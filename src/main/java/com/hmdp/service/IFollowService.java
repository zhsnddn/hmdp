package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IFollowService extends IService<Follow> {

    /**
     * 是否关注
     */
    Result isFollow(Long id);

    /**
     * 关注或取关
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * 共同关注
     */
    Result followCommons(Long id);
}
