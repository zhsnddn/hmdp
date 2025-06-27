package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IVoucherService extends IService<Voucher> {

    /**
     * 查询店铺的优惠券列表
     */
    Result queryVoucherOfShop(Long shopId);

    /**
     * 新增秒杀优惠券
     */
    void addSeckillVoucher(Voucher voucher);

}
