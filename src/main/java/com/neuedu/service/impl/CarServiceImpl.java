package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.entity.Cart;
import com.neuedu.entity.Product;
import com.neuedu.service.CarService;
import com.neuedu.util.BigdecimalUtils;
import com.neuedu.vo.CarProductVo;
import com.neuedu.vo.CarVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加/修改
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        /* step1 校验参数 */
        if (productId == null || count == null) {
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* 数据库中查询到的购物车 */
        Cart cart = cartMapper.selectCartByUserIdAndproductId(userId, productId);
        if (cart == null) {
            //添加
            Cart cart1 = new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.Checked.ISCHECK.getCode());
            System.out.println(cart1.getChecked() +"-----添加");
            cartMapper.insert(cart1);
        } else {
            //修改
            Cart cart1 = new Cart();
            cart1.setId(cart.getId());
            cart1.setUserId(userId);
            cart1.setChecked(Const.Checked.ISCHECK.getCode());
            cart1.setProductId(productId);
            cart1.setQuantity(cart.getQuantity() + count);
            System.out.println(cart1.getChecked() +"-----修改");
            cartMapper.updateByPrimaryKey(cart1);
        }
        CarVo carVo = getCartVoLimit(userId);
        return ServerResponse.createServerResponseBySuccess(carVo);
    }

    private CarVo getCartVoLimit(Integer userId) {
        CarVo carVo = new CarVo();
        /* step1 根据用户id获取购物车信息 */
        List<Cart> cartList = cartMapper.selectcatByUserId(userId);
        List<CarProductVo> carProductVos = Lists.newArrayList();
        /* step2 List<Cart>--> List<CartVo> */
        /* 购物车总价 */
        BigDecimal carTotalPrice = new BigDecimal("0");
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                CarProductVo carProductVo = new CarProductVo();
                /* 查询购物车  购物车信息--> Vo */
                carProductVo.setId(cart.getId());
                carProductVo.setQuantity(cart.getQuantity());
                carProductVo.setUserId(cart.getUserId());
                carProductVo.setProductChecked(cart.getChecked());
                /* 查询商品 */
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    carProductVo.setProductId(product.getId());
                    carProductVo.setProductMainImage(product.getMainImage());
                    carProductVo.setProductName(product.getName());
                    carProductVo.setPrice(product.getPrice());
                    carProductVo.setProductStatus(product.getStatus());
                    carProductVo.setProductStock(product.getStock());
                    carProductVo.setProductSubtitle(product.getSubtitle());
                    int stock = product.getStock();
                    int limitProductCount = 0;
                    /* 判断商品库存是否足够 */
                    if (stock >= cart.getQuantity()) {
                        limitProductCount = cart.getQuantity();
                        carProductVo.setLimitQuantity("LIMIT_NUM_SUCCESS");
                    } else {
                        /* 库存不足 */
                        limitProductCount = stock;
                        /* 更新购物车中商品的数量 */
                        Cart cart1 = new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(stock);
                        cart1.setProductId(cart.getProductId());
                        cart1.setUserId(userId);
                        cartMapper.updateByPrimaryKey(cart1);
                        carProductVo.setLimitQuantity("LIMIT_NUM_ERROR");
                    }
                    carProductVo.setQuantity(limitProductCount);
                    /* 购物车中单个商品的总价 */
                    if (carProductVo.getProductChecked() == Const.Checked.ISCHECK.getCode()) {
                                                                        /* 价格  *     商品数量  */
                        carProductVo.setProductTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(carProductVo.getQuantity())));
                    } else {
                        carProductVo.setProductTotalPrice(product.getPrice().multiply(BigDecimal.ZERO));
                    }

                }
                /* 购物车中商品总价*/
                carTotalPrice = BigdecimalUtils.add(carTotalPrice.doubleValue(), carProductVo.getProductTotalPrice().doubleValue());
                carProductVos.add(carProductVo);
            }
        }
        carVo.setCarProductVoList(carProductVos);
        /* 计算总价格 */
        carVo.setCartTotalPrice(carTotalPrice);
        /* step3 判断是否全选 */
        int count = cartMapper.countCheckedAll(userId);/* 获取未选中的数量 */
        if (count == 0) {
            /* 如果未选中的商品个数为0 设置全选属性为true*/
            carVo.setIscheckedAll(true);
        } else {
            carVo.setIscheckedAll(false);
        }
        /* step4 返回结果 */
        return carVo;
    }


    /**
     * 购物车列表
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse list(Integer userId) {
        CarVo carVo = getCartVoLimit(userId);
        return ServerResponse.createServerResponseBySuccess(carVo);
    }

    /**
     * 购物车商品数量更新
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        /* step1 参数非空判断 */
        if (productId == null || count == null) {
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* step2 查询商品 */
        Cart cart = cartMapper.selectCartByUserIdAndproductId(userId, productId);
        if (cart != null) {
            /* step3 跟新数量 */
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
            /* step4 返回结果 */
            return ServerResponse.createServerResponseBySuccess(getCartVoLimit(userId));
        }
        return ServerResponse.createServerResponseByError("修改失败");
    }

    /**
     * 移除商品
     *
     * @param userId
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {
        /* step1 参数非空校验 */
        if (StringUtils.isBlank(productIds)) {
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* step2 products --> List<Integer> */
        String[] strings = productIds.split(",");
        List<Integer> integerList = Lists.newArrayList();
        if (strings != null && strings.length > 0) {
            for (String string : strings) {
                integerList.add(Integer.valueOf(string));
            }
        }
        /* step3 调用dao层 */
        int count = cartMapper.deleteByUserIdAndProductIds(userId, integerList);
        /* step4 返回结果 */
        if (count > 0) {
            return ServerResponse.createServerResponseBySuccess();
        }

        return ServerResponse.createServerResponseByError("删除失败");
    }

    /**
     * 选中/取消 购物车中的商品
     *
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public ServerResponse select(Integer userId, Integer productId, Integer check) {
        /* step1 非空校验 */
        if (productId == null) {
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        /* step2 dao层 */
        cartMapper.selectOrUnselectProduct(userId, productId, check);
        return ServerResponse.createServerResponseBySuccess(getCartVoLimit(userId));
    }

    /**
     * 全部选中/取消购物车中的商品
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse select_All(Integer userId, Integer check) {

        /* step2 dao层 */
        cartMapper.selectOrUnselectProduct(userId, null, check);
        return ServerResponse.createServerResponseBySuccess(getCartVoLimit(userId));
    }

    /**
     * 查询购物车中商品数量
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse get_cart_product_count(Integer userId) {
        int resule = cartMapper.get_cart_product_count(userId);
        if (resule < 0) {
            return ServerResponse.createServerResponseByError("数量异常");
        }
        return ServerResponse.createServerResponseBySuccess(resule);
    }


}
