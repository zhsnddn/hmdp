package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_LIST;
import static java.util.stream.Collectors.toList;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String key = CACHE_SHOP_TYPE_LIST;
        List<String> typeListJson = stringRedisTemplate.opsForList().range(key, 0, -1);
        assert typeListJson != null;
        if (!typeListJson.isEmpty()) {
            List<ShopType> typeList = JSONUtil.toList(typeListJson.toString(), ShopType.class);
            return Result.ok(typeList);
        }

        List<ShopType> typeList = this.query().orderByAsc("sort").list();

        if (typeList.isEmpty()) {
           return Result.fail("店铺类型不存在");
        }

        for(ShopType shopType : typeList) {
            stringRedisTemplate.opsForList().rightPush(key, JSONUtil.toJsonStr(shopType));
        }

        return Result.ok(typeList);
    }
}
