package com.neuedu.service;


import com.neuedu.common.ServerResponse;

public interface CategoryService {
    /**
     * 获取类别节点
     * @param categoryId
     * @return
     */
    ServerResponse get_category(Integer categoryId);

    /**
     * 增加类别节点
     * @param parentId
     * @param categoryName
     * @return
     */
    ServerResponse add_category(Integer parentId, String categoryName);

    /**
     * 修改类别节点
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse set_category_name(Integer categoryId,String categoryName);

    /**
     * 获取类别节点及子节点
     * @param categoryId
     * @return
     */
    ServerResponse get_deep_name(Integer categoryId);
}
