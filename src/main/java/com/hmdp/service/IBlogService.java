package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IBlogService extends IService<Blog> {

    /**
     * 查询博客内容
     */
    Result queryBlogById(Long id);

    Result queryHotBlog(Integer current);

    /**
     * 查询博客点赞
     */
    Result queryBlogLikes(Long id);

    /**
     * 点赞博客

     */
    Result likeBlog(Long id);

    /**
     * 查询当前用户的博客
     */
    Result queryBlogByUserId(Integer current, Integer id);

    /**
     * 保存博客
     */
    Result saveBlog(Blog blog);

    Result queryBlogOfFollow(Long max, Integer offset);
}
