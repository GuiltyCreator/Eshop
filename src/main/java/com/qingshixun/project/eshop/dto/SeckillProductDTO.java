package com.qingshixun.project.eshop.dto;

import com.qingshixun.project.eshop.util.DateUtils;

import java.util.Date;

/**
 * 秒杀商品实体类
 *
 * @author 肖晟鹏
 * @version 1.0
 */
public class SeckillProductDTO extends BaseDTO{

    //商品
    private ProductDTO product;

    //秒杀价格
    private Double seckillPrice;

    //秒杀数量
    private int stockCount;

    //开始时间
    private Date startDate ;

    //结束时间
    private Date endDate ;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date starDate) {
        this.startDate = starDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
