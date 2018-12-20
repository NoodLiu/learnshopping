package com.neuedu.service.impl;

import com.google.common.collect.Sets;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.entity.Category;
import com.neuedu.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceimpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 获取所有类别节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse get_category(Integer categoryId) {
        /* 非空校验 */
        if (categoryId==null){
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

    /**
     * 增加类别节点
     * @param parentId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse add_category(Integer parentId, String categoryName) {
        /* step1 校验是否为空 */
        if (StringUtils.isBlank(categoryName)){
            return ServerResponse.createServerResponseByError("类别名不能为空");
        }
        /* step2 添加节点 */
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(1);
        int result = categoryMapper.insert(category);
        /* 返回结果 */
        if (result>0){
            return ServerResponse.createServerResponseBySuccess("添加成功");
        }

        return ServerResponse.createServerResponseByError("添加失败");
    }

    /**
     * 修改类别节点信息
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse set_category_name(Integer categoryId, String categoryName) {
        /* step1 校验是否为空 */
        if (categoryId==null){
            return ServerResponse.createServerResponseByError("类目id不能为空");
        }
        if (StringUtils.isBlank(categoryName)){
            return ServerResponse.createServerResponseByError("类别名不能为空");
        }
        /* step2 根据categoryId查询 */
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.createServerResponseByError("修改的类别不存在");
        }
        /* step3 修改节点 */
        category.setName(categoryName);
        int result = categoryMapper.updateByPrimaryKey(category);
        /* 返回结果 */
        if (result>0){
            return ServerResponse.createServerResponseBySuccess("修改成功");
        }

        return ServerResponse.createServerResponseByError("修改失败");
    }

    /**
     * 获取当前分类节点及其子节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse get_deep_name(Integer categoryId) {

        /* step1 非空校验 */
        if (categoryId==null){
            return ServerResponse.createServerResponseByError("类别id不能为空");
        }
        /* step2 查询 */
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = findAllChildCategory(categorySet,categoryId);

        Set<Integer> integerSet = Sets.newHashSet();

        Iterator<Category> categoryIterator = categorySet.iterator();
        while (categoryIterator.hasNext()){
            Category category = categoryIterator.next();
            /* 获取类别的子节点 */
            integerSet.add(category.getId());
        }
        /* step3 返回结果 */
        return ServerResponse.createServerResponseBySuccess("成功",integerSet);
    }
    /*
    * 查询到的节点     子节点     子节点
    *                          子节点
    *                子节点
    *
    *                子节点
    *
    * */
    private Set<Category> findAllChildCategory(Set<Category> categories,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            /* 把根据id查询到的类别（当前的相当于根节点）存放到集合里 */
            categories.add(category);
        }
        /* 查找categoryId下的子节点（平级） */
        List<Category> categoryList =  categoryMapper.findChildCategory(categoryId);
        if (categoryList!=null&&categories.size()>0){ /* 如果当前节点还有子节点 */
            for (Category category1 : categoryList) {   /* 遍历节点 */
                findAllChildCategory(categories,category1.getId()); /* 递归执行查询 */
            }
        }
        return categories;
    }
}
