package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IShopService extends IService<Shop> {

    /**
     * 根据id查询店铺
     */
    Result queryById(Long id);

    /**
     * 更新店铺信息
     */
    Result updateShop(Shop shop);

    /**
     * 根据类型分页查询店铺信息
     */
    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
