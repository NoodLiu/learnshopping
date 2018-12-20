package com.neuedu.controller.manage;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/manage/product")
public class UploadController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public String upload(){
        return "from";
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload2(@RequestParam(value = "upload_file",required = false)MultipartFile file){

        String url="D:\\files";
        return productService.upload(file,url);
    }
}
