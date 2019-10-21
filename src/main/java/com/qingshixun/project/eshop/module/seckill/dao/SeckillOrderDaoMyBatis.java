package com.qingshixun.project.eshop.module.seckill.dao;

import com.qingshixun.project.eshop.dto.SeckillOrderDTO;
import com.qingshixun.project.eshop.web.MyBatisRepository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisRepository
public interface SeckillOrderDaoMyBatis {

    Long insert (SeckillOrderDTO seckillOrder);


    SeckillOrderDTO getseckillOrderByMemberIdSeckillProductId (@Param("memberId") Long memberId,@Param("productId") Long productId);

    List<SeckillOrderDTO> getSeckillOrderByMemberId(@Param("memberId") Long memberId);
}
