package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.SHOP_GEO_KEY;


@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IShopService shopService;

//    @Test
//    public void loadShopData() {
//        //查询店铺信息，然后按照typeID进⾏分组，放到⼀个集合中
//        List<Shop> list = shopService.list();
//        Map<Long, List<Shop>> map =
//                list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
//        //分批写⼊Redis
//        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
//            //类型Id
//            Long typeId = entry.getKey();
//            //相同类型的店铺集合
//            List<Shop> value = entry.getValue();
//            String key = SHOP_GEO_KEY + typeId;
//            //⽅式⼀：⼀个⼀个点写⼊redis GEOADD key 经度 纬度 member
//            //value.forEach(shop->{
//            // stringRedisTemplate.opsForGeo().add(key,new
//            // Point(shop.getX(),shop.getY()),shop.getId().toString());
//            //});
//            //⽅式⼆： 计算出每个店铺的经纬度后的出⼀个GeoLocation集合，然后统⼀写⼊redis
//            List<RedisGeoCommands.GeoLocation<String>> geoLocationList =
//                    value.stream().map(shop -> {
//                        return new RedisGeoCommands.GeoLocation<String>
//                                (shop.getId().toString(),
//                                        new Point(shop.getX(), shop.getY()));
//                    }).collect(Collectors.toList());
//            stringRedisTemplate.opsForGeo().add(key, geoLocationList);
//        }
//    }

    @Test
    void testHyperLogLog(){
        String[] values=new String[1000];
        int count=0;
        for (int i = 0; i < 1000000; i++) {
            count = i%1000;
            values[count] ="user_"+i;
            if(count==999){
                //发送到redis
                stringRedisTemplate.opsForHyperLogLog().add("testHyperLogLog",values);
            }
        }
        //统计数量
        Long res = stringRedisTemplate.opsForHyperLogLog().size("testHyperLogLog");
        log.debug("数量为：{}",res);
    }

}
