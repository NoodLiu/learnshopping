package com.neuedu.controller.portal;


import com.neuedu.common.ServerResponse;
import com.neuedu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 前台
 */

@RestController
@RequestMapping("/portal/product")
public class ProductPortalController {

    @Autowired
    private ProductService productService;

    /**
     * 商品详情
     *
     * @param
     * @param productId
     * @return
     */
    @RequestMapping("/detail.do")
    public ServerResponse detail(Integer productId) {

        return productService.detail_protal(productId);
    }

    /**
     * 商品列表
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(@RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1",required = false) Integer pageNum,
                               @RequestParam(defaultValue = "10",required = false)Integer pageSize,
                               @RequestParam(defaultValue = "",required = false)String orderBy) {
        return productService.list_portal(categoryId, keyword, pageNum, pageSize, orderBy);
    }

}
