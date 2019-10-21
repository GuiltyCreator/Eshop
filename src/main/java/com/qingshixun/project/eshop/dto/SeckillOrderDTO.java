package com.qingshixun.project.eshop.dto;

public class SeckillOrderDTO extends BaseDTO{

    //订单
    private OrderDTO order;

    private Long productId;

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
