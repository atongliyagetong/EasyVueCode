package com.example.demo.controller;


import com.example.demo.model.ExcleColumn;
import com.example.demo.service.ImportService;
import com.example.demo.service.NewImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2019/3/2.
 */

@RestController
public class DataController {

    @Autowired
    private ImportService importService;


    @PostMapping("doImport")
    public void doImport(@RequestParam("excel") MultipartFile file, HttpServletRequest request) throws Exception{
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

//        MultipartFile file = multipartRequest.getFile("filename");

        if (file.isEmpty()) {
//            return "文件不能为空";
        }
        List<ExcleColumn> excleColumnList = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        List<List<Object>> list = importService.getListByExcelWithBlank(inputStream, file.getOriginalFilename());
        inputStream.close();

        for (int i = 0; i < list.size(); i++) {

            ExcleColumn excleColumn = new ExcleColumn();
            List<Object> columns = list.get(i);
            //TODO 随意发挥
            excleColumn = importService.getExcleColumn(columns);
            importService.outPutDataFile(excleColumn);

        }

    }


}


