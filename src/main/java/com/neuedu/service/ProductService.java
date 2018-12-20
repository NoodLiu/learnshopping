package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {


    ServerResponse producr_list();

    ServerResponse product_up_list();

    ServerResponse saveOrUpdate(Product product);

    ServerResponse set_sale_status(Integer productId, Integer status);

    ServerResponse detail(Integer productId);

    ServerResponse list(Integer pageNum, Integer pageSize);

    ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize);

    ServerResponse upload(MultipartFile file, String path);

    ServerResponse detail_protal(Integer productId);

    ServerResponse list_portal(Integer categoryId,String keyword, Integer pageNum, Integer pageSize, String orderBy);
}
