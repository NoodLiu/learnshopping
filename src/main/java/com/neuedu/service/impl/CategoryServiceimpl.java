package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.entity.Category;
import com.neuedu.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;


    @Override
    public ServerResponse get_category(Integer categoryId) {
        /* 非空校验 */
        if (categoryId!=null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        /* 根据id查询类别 */
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.createServerResponseByError("类别不存在");
        }
        /* 查询子类别 */
        List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        /* 返回结果 */
        return ServerResponse.createServerResponseBySuccess("成功",categoryList);
    }
}
