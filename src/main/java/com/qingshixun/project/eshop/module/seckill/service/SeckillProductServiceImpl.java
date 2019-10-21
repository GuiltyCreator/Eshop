package com.qingshixun.project.eshop.module.seckill.service;

import com.qingshixun.project.eshop.dto.SeckillProductDTO;
import com.qingshixun.project.eshop.module.seckill.dao.SeckillProductDaoMyBatis;
import com.qingshixun.project.eshop.web.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillProductServiceImpl extends BaseService {

    @Autowired
    private SeckillProductDaoMyBatis dao;

    /**
     * 获取秒杀商品列表
     * @return
     */
    @Cacheable(value = "SeckillProducts" ,key = "0",unless = "#result eq null ")
    public List<SeckillProductDTO> getAllSeckillProducts(){
        return dao.getAllSeckillProducts();
    }

    /**
     * 获取秒杀商品
     * @param productId
     * @return
     */
    @Cacheable(value = "SeckillProduct" ,key = "#root.args[0]",unless = "#result eq null ")
    public SeckillProductDTO getSeckillProduct(Long productId){
        return dao.getSeckillProduct(productId);
    }

}
