package com.neuedu.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/* 购物车 */
public class CarVo implements Serializable {
    /* 购物车商品集合 */
    private List<CarProductVo> carProductVoList;
    /* 是否全选 */
    private boolean ischeckedAll;
    /* 总价格 */
    private BigDecimal cartTotalPrice;

    public List<CarProductVo> getCarProductVoList() {
        return carProductVoList;
    }

    public void setCarProductVoList(List<CarProductVo> carProductVoList) {
        this.carProductVoList = carProductVoList;
    }

    public boolean isIscheckedAll() {
        return ischeckedAll;
    }

    public void setIscheckedAll(boolean ischeckedAll) {
        this.ischeckedAll = ischeckedAll;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}
