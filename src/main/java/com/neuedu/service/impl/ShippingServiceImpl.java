package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.entity.Shipping;
import com.neuedu.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    /**
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        if (shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* 添加地址 */
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        Map<String,Integer> map = Maps.newHashMap();
        map.put("Shipping",shipping.getId());
        return ServerResponse.createServerResponseBySuccess(map);
    }

    /**
     * 删除地址
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        /* 非空校验 */
        if (shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* 删除 */
        int result = shippingMapper.deleteShippingByUserIDAndShippingId(userId, shippingId);
        if (result>0){
            return ServerResponse.createServerResponseBySuccess("删除成功");
        }
        return ServerResponse.createServerResponseByError("删除失败");
    }

    /**
     * 修改地址
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse update(Shipping shipping) {
        /* 非空校验 */
        if (shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* 更新 */

        int result = shippingMapper.updateBySelecttiveKey(shipping);
        if (result>0){
            return ServerResponse.createServerResponseBySuccess("更新成功");
        }
        return ServerResponse.createServerResponseByError("更新失败");
    }

    /**
     * 查看地址详情
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        /* 非空校验 */
        if (shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        /* 查看 */
         Shipping shipping= shippingMapper.selectByPrimaryKeyAndUserId(userId,shippingId);

       if (shipping==null){
           return ServerResponse.createServerResponseByError("查询失败");
       }
       return ServerResponse.createServerResponseBySuccess(shipping);
    }

    /**
     * 地址列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectAll();
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }
}
