package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.ProductStatusEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.entity.Category;
import com.neuedu.entity.Product;
import com.neuedu.service.ProductService;
import com.neuedu.util.DateUtils;
import com.neuedu.util.PropertiesUtils;
import com.neuedu.vo.ProductListVo;
import com.neuedu.vo.ProductVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private static CategoryMapper categoryMapper;

    @Override
    public ServerResponse producr_list() {
        List<Product> products = productMapper.selectAll();
        return ServerResponse.createServerResponseBySuccess(products);
    }

    @Override
    public ServerResponse product_up_list() {
        List<Product> products = productMapper.selectAll();
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.getStatus() == ProductStatusEnum.UP.getCode()) {
                productList.add(product);
            }
        }
        return ServerResponse.createServerResponseBySuccess(productList);
    }

    @Override
    public ServerResponse saveOrUpdate(Product product) {
        /* step1 参数校验 */
        if (product == null) {
            return ServerResponse.createServerResponseByError("商品不能为空");
        }
        /* step2 设置商品主图 sub-image-->1.png 2.png*/
        String subimages = product.getSubImages();
        if (subimages != null && !subimages.equals("")) {
            String[] images = subimages.split(",");
            if (images.length > 0) {
                //设置商品主图
                product.setMainImage(images[0]);
            }
        }
        /* step3 判断添加还是修改 */
        if (product.getId() != null) {
            /* 添加 */
            int result = productMapper.insert(product);
            if (result > 0) {
                return ServerResponse.createServerResponseBySuccess("添加成功");
            }
            return ServerResponse.createServerResponseByError("添加失败");
        } else {
            /* 修改 */
            int result = productMapper.updateByPrimaryKey(product);
            if (result > 0) {
                return ServerResponse.createServerResponseBySuccess("修改成功");
            }
            return ServerResponse.createServerResponseByError("修改失败");
        }
    }

    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {
        /* step1 非空校验 */
        if (productId == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        if (status == null) {
            return ServerResponse.createServerResponseByError("商品状态不存在");
        }
        /* step2 更新商品状态*/
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateProductKeySelective(product);
        /* step3 结果判断 */
        if (result > 0) {
            return ServerResponse.createServerResponseBySuccess("状态修改成功");
        }
        return ServerResponse.createServerResponseByError("状态修改失败");
    }

    /**
     * 商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse detail(Integer productId) {
        /* step1 非空校验 */
        if (productId == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        /* step2 根据商品id查询商品 */
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        /* step3 product-->productVo */
        ProductVo productVo = Product2ProductVo(product);
        /* 结果返回 */
        return ServerResponse.createServerResponseBySuccess(productVo);
    }

    private static ProductVo Product2ProductVo(Product product) {

        ProductVo productVo = new ProductVo();
        productVo.setId(product.getId());
        productVo.setCategoryId(product.getCategoryId());
        productVo.setDetail(product.getDetail());
        productVo.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productVo.setImageHost(PropertiesUtils.readKey("imagehost"));
        productVo.setName(product.getName());
        productVo.setMainImage(product.getMainImage());
        productVo.setPrice(product.getPrice());
        productVo.setStock(product.getStock());
        productVo.setSubImages(product.getSubImages());
        productVo.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        productVo.setSubtitle(product.getSubtitle());
        /* 设置商品父类id */
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productVo.setParentCategoryId(0);
        } else {
            productVo.setParentCategoryId(category.getParentId());
        }
        return productVo;
    }

    /**
     * 商品列表-分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        /* step1 查询商品*/
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectAll();
        List<ProductListVo> productListVos = Lists.newArrayList();
        if (productList != null && productList.size() > 0) {
            for (Product product : productList) {
                ProductListVo productListVo = product2ProductListVo(product);
                productListVos.add(productListVo);
            }
        }
        PageInfo pageInfo = new PageInfo(productListVos);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }

    private ProductListVo product2ProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        return productListVo;
    }

    /**
     * 搜索商品
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isBlank(productName)){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        productName = "%"+productName+"%";
        List<Product> productList = productMapper.findByProductIdAndProductName(productId,productName);
        List<ProductListVo> productListVos = Lists.newArrayList();
        if (productList != null && productList.size() > 0) {
            for (Product product : productList) {
                ProductListVo productListVo = product2ProductListVo(product);
                productListVos.add(productListVo);
            }
        }
        PageInfo pageInfo = new PageInfo(productListVos);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse upload(MultipartFile file, String url) {
        /* step1 非空判断 */
        if (file==null){
            return ServerResponse.createServerResponseByError();
        }
        return null;
    }
}
