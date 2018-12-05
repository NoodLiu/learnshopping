package com.neuedu.controller;


import com.neuedu.dao.CategoryMapper;
import com.neuedu.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/*  商品类目  */
@RestController
public class CategoryController {

    /**
     *  ###### 添加类别
     *  ###### 修改类别
     *  ###### 删除类别
     *  #####  查看类别
     *  ###### 查看子类
     */
    @Autowired
    public CategoryMapper categoryMapper;

    @GetMapping
    public void save(Category category){

        categoryMapper.insert(category);
    }

}
