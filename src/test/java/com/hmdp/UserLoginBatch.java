package com.hmdp;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.service.IUserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.LOGIN_CODE_KEY;

@SpringBootTest
@AutoConfigureMockMvc
public class UserLoginBatch {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Resource
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("批量创建用户并获取登录凭证")
    @SneakyThrows
    public void testBatchUserLogin() {
        // 要创建的用户数量
        final int USER_COUNT = 1000;
        
        // 创建文件保存token
        File tokenFile = new File("token.txt");
        if (!tokenFile.exists()) {
            tokenFile.createNewFile();
        }
        
        // 线程池
        ExecutorService executorService = ThreadUtil.newExecutor(20);
        
        // 线程安全的结果集合
        CopyOnWriteArrayList<String> tokens = new CopyOnWriteArrayList<>();
        
        // 使用CountDownLatch等待所有任务完成
        CountDownLatch latch = new CountDownLatch(USER_COUNT);
        
        // 创建1000个用户并获取登录凭证
        for (int i = 0; i < USER_COUNT; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    // 用户手机号，确保唯一性
                    String phone = "1356789" + String.format("%04d", finalI);
                    
                    // 1. 发送验证码
                    mockMvc.perform(
                            MockMvcRequestBuilders.post("/user/code")
                                    .param("phone", phone)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(MockMvcResultMatchers.status().isOk());
                    
                    // 2. 从Redis中获取验证码
                    String codeKey = LOGIN_CODE_KEY + phone;
                    String code = stringRedisTemplate.opsForValue().get(codeKey);
                    
                    // 3. 创建登录请求参数
                    LoginFormDTO loginFormDTO = new LoginFormDTO();
                    loginFormDTO.setPhone(phone);
                    loginFormDTO.setCode(code);
                    
                    // 4. 执行登录请求
                    String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/user/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(loginFormDTO))
                    ).andExpect(MockMvcResultMatchers.status().isOk())
                            .andReturn().getResponse().getContentAsString();
                    
                    // 5. 提取token
                    Result result = objectMapper.readValue(response, Result.class);
                    if (result.getSuccess()) {
                        String token = result.getData().toString();
                        tokens.add(token);
                        System.out.println("用户 " + phone + " 登录成功，token: " + token);
                    } else {
                        System.err.println("用户 " + phone + " 登录失败: " + result.getErrorMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        latch.await(5, TimeUnit.MINUTES);
        
        // 关闭线程池
        executorService.shutdown();
        
        // 打印结果统计
        System.out.println("成功创建用户数量: " + tokens.size());
        
        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tokenFile), StandardCharsets.UTF_8))) {
            for (String token : tokens) {
                writer.write(token);
                writer.newLine();
            }
            writer.flush();
        }
        
        System.out.println("所有用户token已保存到: " + tokenFile.getAbsolutePath());
    }
}