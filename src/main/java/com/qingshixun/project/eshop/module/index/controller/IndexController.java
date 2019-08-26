package com.qingshixun.project.eshop.module.index.controller;

import com.qingshixun.project.eshop.dto.MemberDTO;
import com.qingshixun.project.eshop.module.advertisement.service.AdvertisementServiceImpl;
import com.qingshixun.project.eshop.module.cart.service.CartItemServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductCategoryServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductServiceImpl;
import com.qingshixun.project.eshop.util.CaptchaUtil;
import com.qingshixun.project.eshop.web.BaseController;
import com.qingshixun.project.eshop.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private ProductCategoryServiceImpl productCategoryService;

    @Autowired
    private AdvertisementServiceImpl advertisementService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CartItemServiceImpl cartItemService;

    @RequestMapping(value = {"/front/index", ""})
    public String index(Model model) {
        MemberDTO member = getCurrentUser();

        model.addAttribute("productCategories", productCategoryService.getProductCategories());
        model.addAttribute("advertisements", advertisementService.getAdvertisements());
        model.addAttribute("hotProducts", productService.getHotProducts());
        model.addAttribute("newProducts", productService.getNewProducts());
        model.addAttribute("bestProducts", productService.getBestProducts());
        model.addAttribute("totalCartCount", cartItemService.getTotalCartCount(member, getSession()));
        // 当前登录用户数据保存到session中
        setSessionAttribute("member", member);

        return "/index";
    }

    /**
     * 注册页面
     *
     * @return
     */
    @RequestMapping("/front/register")
    public String register(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "/register";
    }

    /**
     * 生成验证码
     *
     * @param param
     */
    @RequestMapping(value = "/captcha/generate")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 生成验证码
            CaptchaUtil.generateCaptcha(request, response);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 验证码校验
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/captcha/check")
    @ResponseBody
    public ResponseData checkCaptcha(Model model, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        String code = request.getParameter("captcha");

        // 比较输入的验证码是否正确
        if (code.equalsIgnoreCase((String) request.getSession().getAttribute("captcha"))) {
            responseData.setStatus("0");
        } else {
            responseData.setStatus("-1");
        }
        // 返回处理结果（json 格式）
        return responseData;
    }


    /**
     * 登录页面
     *
     * @return
     */
    @RequestMapping("/front/login")
    public String login() {
        return "/login";
    }

}
