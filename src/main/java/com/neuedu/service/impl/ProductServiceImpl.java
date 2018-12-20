package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neuedu.common.ProductStatusEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.entity.Category;
import com.neuedu.entity.Product;
import com.neuedu.service.CategoryService;
import com.neuedu.service.ProductService;
import com.neuedu.util.DateUtils;
import com.neuedu.util.PropertiesUtils;
import com.neuedu.vo.ProductListVo;
import com.neuedu.vo.ProductVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService categoryService;

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

    /**
     * 商品的增加或修改
     *
     * @param product
     * @return
     */
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
        if (product.getId() == null) {
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

    /**
     * 商品上下架
     * @param productId
     * @param status
     * @return
     */
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
        int result = productMapper.updateProductKeySelective(product);
        /* step3 结果判断 */
        if (result > 0) {
            if (product.getStatus()==ProductStatusEnum.UP.getCode()) {
                return ServerResponse.createServerResponseBySuccess("状态修改成功","当前上架");
            }else if (product.getStatus()==ProductStatusEnum.DOWN.getCode()){
                return ServerResponse.createServerResponseBySuccess("状态修改成功","当前下架");
            }else {
                return ServerResponse.createServerResponseBySuccess("状态修改成功","当前删除");
            }

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

    private ProductVo Product2ProductVo(Product product) {
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
        productVo.setStatus(product.getStatus());
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
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        return productListVo;
    }

    /**
     * 搜索商品
     *
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isBlank(productName) && productId == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        productName = "%" + productName + "%";
        List<Product> productList = productMapper.findByProductIdAndProductName(productId, productName);
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

    /**
     * 图片上传
     *
     * @param file
     * @param path
     * @return
     */
    @Override
    public ServerResponse upload(MultipartFile file, String path) {
        /* step1 非空判断 */
        if (file == null) {
            return ServerResponse.createServerResponseByError();
        }
        /* 获取图片名称 */
        String originFileName = file.getOriginalFilename();
        /* 获取文件扩展名 */
        String exName = originFileName.substring(originFileName.lastIndexOf("."));
        /* 生成新的名字 */
        String newFileName = UUID.randomUUID().toString() + exName;
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.setWritable(true);
            pathFile.mkdirs();
        }
        File file1 = new File(path, newFileName);
        try {
            file.transferTo(file1);
            /* 上传到图片服务器 */
            Map<String, String> map = Maps.newHashMap();
            map.put("uri", newFileName);
            map.put("url", PropertiesUtils.readKey("imagehost") + "/" + newFileName);
            return ServerResponse.createServerResponseBySuccess(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 前台-商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse detail_protal(Integer productId) {
        /* step1 非空校验 */
        if (productId == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        /* step2 根据商品id查询商品 */
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        /* step3 商品状态 */
        System.out.println(product.getStatus());
        System.out.println(ProductStatusEnum.UP.getCode());
        if (product.getStatus() != ProductStatusEnum.UP.getCode()) {
            return ServerResponse.createServerResponseByError("商品下架");
        }
        /* step3 product-->productVo */
        ProductVo productVo = Product2ProductVo(product);
        /* 结果返回 */
        return ServerResponse.createServerResponseBySuccess(productVo);
    }

    /**
     * 前台-商品搜索
     *
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        /* step1 参数校验 categoryId和keyword不能同时为空 */
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* step2 根据categoryId查询 */
        Set<Integer> integerSet = Sets.newHashSet();
        if (categoryId != null) {
            /* 根据类别id */
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            /* 如果查询出来的类别为空 */
            if (category == null && StringUtils.isBlank(keyword)) {
                /* 没有商品数据 */
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVos);
                return ServerResponse.createServerResponseBySuccess(pageInfo);
            }
            /* 如果类别不为空，查询出所有类别的子类 */
            ServerResponse serverResponse = categoryService.get_deep_name(categoryId);
            /* 查询出来的类别 */
            if (serverResponse.isSuccess()) {
                /* 成功后接收的子类别 */
                integerSet = (Set<Integer>) serverResponse.getData();
            }
        }
            /* step3 根据keyword查询 */
            if (!StringUtils.isBlank(keyword)) {
                keyword = "%" + keyword + "%";
            }
            if (orderBy.equals("")) {
                PageHelper.startPage(pageNum, pageSize);
            } else {
                String[] orderByArr = orderBy.split("_");
                if (orderByArr.length > 1) {
                    PageHelper.startPage(pageNum, pageSize, orderByArr[0] + "" + orderByArr[1]);
                } else {
                    PageHelper.startPage(pageNum, pageSize);
                }
            }

            /* step4 List<Product>-->List<ProductListVo> */
            List<Product> productList = productMapper.searchProduct(integerSet,keyword);
            List<ProductListVo> productListVos = Lists.newArrayList();
            if (productList != null && productList.size() > 0) {
                for (Product product : productList) {
                    ProductListVo productListVo = product2ProductListVo(product);
                    productListVos.add(productListVo);
                }
            }
            /* step5 分页 */
            PageInfo pageInfo = new PageInfo();
            pageInfo.setList(productListVos);
            /* step6 返回结果*/
            return ServerResponse.createServerResponseBySuccess(pageInfo);
        }
    }