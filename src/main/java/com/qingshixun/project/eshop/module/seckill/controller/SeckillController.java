package com.qingshixun.project.eshop.module.seckill.controller;

import com.qingshixun.project.eshop.core.Constants;
import com.qingshixun.project.eshop.dto.MemberDTO;
import com.qingshixun.project.eshop.dto.OrderDTO;
import com.qingshixun.project.eshop.dto.SeckillOrderDTO;
import com.qingshixun.project.eshop.dto.SeckillProductDTO;
import com.qingshixun.project.eshop.module.order.service.OrderServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductCategoryServiceImpl;
import com.qingshixun.project.eshop.module.receiver.service.ReceiverServiceImpl;
import com.qingshixun.project.eshop.module.seckill.service.SeckillOrderServiceImpl;
import com.qingshixun.project.eshop.module.seckill.service.SeckillProductServiceImpl;
import com.qingshixun.project.eshop.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/seckill")
public class SeckillController extends BaseController {

    @Autowired
    private SeckillProductServiceImpl seckillProductService;

    @Autowired
    private SeckillOrderServiceImpl seckillOrderService;

    @Autowired
    private ProductCategoryServiceImpl productCategoryService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ReceiverServiceImpl receiverService;


    @RequestMapping(value = "/")
    public String list(Model model){
        List<SeckillProductDTO> seckillProductsList= seckillProductService.getAllSeckillProducts();
        //System.out.println("seckillProductsList:"+seckillProductsList.size());

        model.addAttribute("seckillProductsList",seckillProductsList);

        return "/seckill/list";
    }

    @RequestMapping(value = "/to_detail")
    public String toDetail(@RequestParam Long productId, Model model){
        SeckillProductDTO seckillProduct=seckillProductService.getSeckillProduct(productId);
        MemberDTO member= this.getCurrentUser();
        model.addAttribute("seckillProduct",seckillProduct);
        model.addAttribute("member",member);

        long startAt = seckillProduct.getStartDate().getTime();
        long endAt = seckillProduct.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "/seckill/detail";
    }

    @RequestMapping(value = "/do_seckill")
    public String doSeckill(@RequestParam Long productId, Model model){
        SeckillProductDTO seckillProduct = seckillProductService.getSeckillProduct(productId);
        if(seckillProduct.getStockCount() <= 0){
        model.addAttribute("errmsg", Constants.SECKILL_OVER);
        return "/seckill/fail";
        }
        MemberDTO member=getCurrentUser();
        SeckillOrderDTO seckillOrder = seckillOrderService.getseckillOrderByMemberIdSeckillProductId(member.getId(),productId);
        if(seckillOrder != null){
        model.addAttribute("errmsg",Constants.REPEATE_seckill);
            return "/seckill/fail";
        }

        seckillOrder = seckillOrderService.doSeckill(member,seckillProduct,member.getDefaultReceiverId());

        model.addAttribute("order",seckillOrder);
        model.addAttribute("product",seckillProduct);
        return "/seckill/order_detail";

/*        model.addAttribute("receivers", receiverService.getReceiversByMember(member.getId()));
        model.addAttribute("orderItems", seckillOrder.getOrder().getOrderItems());
        model.addAttribute("productTotalPrice", seckillOrder.getOrder().getTotalAmount());
        model.addAttribute("totalAmount", seckillOrder.getOrder().getTotalAmount());
        model.addAttribute("orderId", seckillOrder.getOrder().getId());
        model.addAttribute("member", member);
        return "/order/main";*/
    }
}
