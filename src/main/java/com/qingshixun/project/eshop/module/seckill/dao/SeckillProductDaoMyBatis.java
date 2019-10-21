package com.qingshixun.project.eshop.module.seckill.dao;

import com.qingshixun.project.eshop.dto.SeckillProductDTO;
import com.qingshixun.project.eshop.web.MyBatisRepository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisRepository
public interface SeckillProductDaoMyBatis {
    /**
     * 获取全部秒杀商品
     */
    List<SeckillProductDTO> getAllSeckillProducts ();

    /**
     * 根据id获取秒杀商品
     * @return
     */
    SeckillProductDTO getSeckillProduct(@Param("productId") Long productId);
}
