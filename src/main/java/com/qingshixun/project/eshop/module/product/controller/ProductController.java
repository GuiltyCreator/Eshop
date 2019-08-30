package com.qingshixun.project.eshop.module.product.controller;

import com.qingshixun.project.eshop.core.Constants;
import com.qingshixun.project.eshop.dto.*;
import com.qingshixun.project.eshop.module.brand.service.BrandServiceImpl;
import com.qingshixun.project.eshop.module.cart.service.CartItemServiceImpl;
import com.qingshixun.project.eshop.module.evaluate.service.EvaluateServiceImpl;
import com.qingshixun.project.eshop.module.order.service.OrderServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductCategoryServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductTypeAttributeServiceImpl;
import com.qingshixun.project.eshop.module.product.service.ProductTypeValueServiceImpl;
import com.qingshixun.project.eshop.web.BaseController;
import com.qingshixun.project.eshop.web.ResponseData;
import com.qingshixun.project.eshop.web.SimpleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/front/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductCategoryServiceImpl productCategoryService;

    @Autowired
    private ProductTypeAttributeServiceImpl productTypeAttributeService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private EvaluateServiceImpl evaluateService;

    @Autowired
    private CartItemServiceImpl cartItemService;

    @Autowired
    private OrderServiceImpl orderService;


    /**
     * 商品列表页
     */
    @RequestMapping("/list")
    public String list(Model model, @RequestParam Long categoryId,
                       @RequestParam(required = false)String[] searchItems,
                       @RequestParam(required = false)Long brandId,
                       @RequestParam(required = false)String  brandName,
                       @RequestParam(required = false)String[] aId,
                       HttpServletRequest request) {


        List<String> selecteds=new ArrayList<>();
        List<Long> aIds=new ArrayList<>();
        List<ProductDTO> products=productService.getProductsByCategory(categoryId,brandId,null);

        //添加URL和参数到model
        String url="/front/product/list";
        String query=request.getQueryString();
        if(query!=null){
            url=url+"?"+query;
        }
        model.addAttribute("url",url);

        //将品牌Id添加到model中，用于判断是否已经选择
        if (brandId != null) {
            model.addAttribute("brandId",brandId);
            selecteds.add(brandName);
        }else {
            model.addAttribute("brandId",null);
        }

        //将筛选条件加入到model中，用于判断是否已经选择该条件
        if(searchItems!=null) {
            for(int i=0;i<searchItems.length;i++){
                if(i==0){
                    products=productService.getProductsByCategory(categoryId,brandId,searchItems[0]);
                }
                else {
                    List<ProductDTO> p=productService.getProductsByCategory(categoryId,brandId,searchItems[i]);
                    products=getResultList(products,p);

                }
                selecteds.add(searchItems[i]);
                aIds.add(Long.parseLong(aId[i]));
            }
        }

        model.addAttribute("aIds",aIds);

        model.addAttribute("selecteds",selecteds);




        //List<ProductDTO> products = productService.getProductsByCategory(categoryId,brandId,searchItem);


        MemberDTO member = getCurrentUser();
        // 非空验证
        if (!products.isEmpty()) {
            // 获取第一个商品的类型id
            Long typeId = products.get(0).getProductType().getId();

            model.addAttribute("brands", brandService.getBrandsByCategory(categoryId));
            model.addAttribute("productTypeAttributes", productTypeAttributeService.getProductTypeAttributesByProductType(typeId));
        }

        model.addAttribute("productCategories", productCategoryService.getProductCategories());
        model.addAttribute("totalCartCount", cartItemService.getTotalCartCount(member, getSession()));
        model.addAttribute("products", products);
        model.addAttribute("categoryId", categoryId);

        return "/product/list";
    }

    /**
     * 返回两个集合中相同的元素
     * @param list1
     * @param list2
     * @return
     */
    public List<ProductDTO> getResultList(List<ProductDTO> list1,List<ProductDTO> list2){
        List<ProductDTO> result = new ArrayList<>();
        if (list1 != null && list2 != null) {

            for (ProductDTO item : list1) {
                if (list2.contains(item)) ;
                result.add(item);
            }
        }
        return result;
    }


    @RequestMapping("/main")
    public String main(Model model, @RequestParam Long productId) {
        ProductDTO product = productService.getProduct(productId);

        MemberDTO member = getCurrentUser();

        model.addAttribute("product", product);
        model.addAttribute("cart", new CartItemDTO());
        model.addAttribute("imagePath", Constants.PRODUCT_IMAGE_PATH);
        model.addAttribute("products", productService.getProductsByPrice(product.getPrice()));
        model.addAttribute("score", evaluateService.getAverageEvaluateScoreByProduct(productId));
        model.addAttribute("totalEvaluateCount", evaluateService.getEvaluateCountByProduct(productId));
        model.addAttribute("satisfiedEvaluateCount", evaluateService.getEvaluateCountByStatusAndProduct("满意", product.getId()));
        model.addAttribute("commonlyEvaluateCount", evaluateService.getEvaluateCountByStatusAndProduct("一般", product.getId()));
        model.addAttribute("disSatisfiedEvaluateCount", evaluateService.getEvaluateCountByStatusAndProduct("不满意", product.getId()));
        model.addAttribute("totalCartCount", cartItemService.getTotalCartCount(member, getSession()));

        return "/product/main";
    }

    @RequestMapping("/evaluate/list")
    public String evaluates(Model model, @RequestParam(required = false, defaultValue = "") String status, @RequestParam Long productId) {
        model.addAttribute("evaluates", evaluateService.getEvaluatesByStatusAndProduct(status, productId));

        return "/product/evaluate/list";
    }

    /**
     * 商品评论页面
     *
     * @param productId 商品ID
     *
     * @throws Exception
     */
    @RequestMapping("/evaluate/{productId}/{orderId}")
    public String productEvaluate(Model model, @PathVariable Long productId, @PathVariable Long orderId) throws Exception {
        // 获取当前的登录用户
        MemberDTO dbMember = this.getCurrentUser();
        model.addAttribute("product", productService.getProduct(productId));
        model.addAttribute("productId", productId);
        model.addAttribute("totalCartCount", cartItemService.getTotalCartCount(dbMember, getSession()));
        model.addAttribute("time", orderService.getOrder(orderId).getUpdateTime());
        model.addAttribute("orderId", orderId);
        model.addAttribute("member", dbMember);
        model.addAttribute("evaluate", evaluateService.getEvaluateByMemberAndProductAndOrder(dbMember.getId(), productId, orderId));
        return "/product/evaluate";
    }

    /**
     * 保存商品评论
     *
     * @param model
     * @param evaluate 评论实体
     */
    @RequestMapping(value = "/evaluate/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData receiverSave(Model model, EvaluateDTO evaluate) {
        MemberDTO member = this.getCurrentUser();

        return new SimpleHandler(request) {
            @Override
            protected void doHandle(ResponseData responseData) throws Exception {
                evaluateService.saveOrUpdateEvaluate(evaluate, member);
            }
        }.handle();
    }

}
