package com.qingshixun.project.eshop.module.seckill.service;

import com.qingshixun.project.eshop.dto.*;
import com.qingshixun.project.eshop.module.order.dao.OrderDaoMyBatis;
import com.qingshixun.project.eshop.module.order.service.OrderItemServiceImpl;
import com.qingshixun.project.eshop.module.order.service.OrderServiceImpl;
import com.qingshixun.project.eshop.module.receiver.service.ReceiverServiceImpl;
import com.qingshixun.project.eshop.module.seckill.dao.SeckillOrderDaoMyBatis;
import com.qingshixun.project.eshop.web.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SeckillOrderServiceImpl extends BaseService {
    @Autowired
    private SeckillOrderDaoMyBatis dao;

    @Autowired
    private OrderDaoMyBatis orderDao;

    @Autowired
    private ReceiverServiceImpl receiverService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderItemServiceImpl orderItemService;



    /**
     * 根据会员id和商品id来获取秒杀订单
     * @param memberId
     * @param productId
     * @return
     */
    public SeckillOrderDTO getseckillOrderByMemberIdSeckillProductId ( Long memberId, Long productId){
        return dao.getseckillOrderByMemberIdSeckillProductId(memberId,productId);
    }

    /**
     * 点击秒杀，下订单
     * @param member
     * @param product
     * @param receiverId
     * @return
     */
    public SeckillOrderDTO doSeckill(MemberDTO member, SeckillProductDTO product,Long receiverId){
        SeckillOrderDTO order=new SeckillOrderDTO();
        order.setOrder(new OrderDTO());
        order.getOrder().setMember(member);
        order.getOrder().setProductTotalPrice(product.getProduct().getMarketPrice());
        order.getOrder().setTotalAmount(product.getSeckillPrice());
        order.getOrder().setProductTotalQuantity(1);
        order.getOrder().setStatus(new OrderStatus("ORDS_UnPay"));
        order.getOrder().setReceiver(receiverService.getReceiver(receiverId));
        order.getOrder().setOrderNum(orderService.getOrderNum(member.getId()));
        order.getOrder().setOrderChannel(new OrderChannel("ORDC_Web"));
        orderDao.saveOrUpdateOrder(order.getOrder());

        OrderItemDTO orderItem = new OrderItemDTO();
        orderItem.setProduct(product.getProduct());
        orderItem.setProductQuantity(1);
        orderItem.setTotalPrice(product.getSeckillPrice());
        orderItem.setOrder(orderDao.getOrder(order.getOrder().getId()));
        orderItem.setStatus(new OrderItemStatus("ORIS_UnGrant"));
        orderItemService.saveOrderItem(orderItem);

        order.getOrder().getOrderItems().add(orderItem);
        order.setProductId(product.getProduct().getId());
        dao.insert(order);

        return order;
    }

    /**
     * 获取所有秒杀订单
     * @return
     */
    public List<SeckillOrderDTO> getSeckillOrderByMemberId(Long memberId){
        return dao.getSeckillOrderByMemberId(memberId);
    }
}
