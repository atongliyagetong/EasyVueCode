package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Chen on 2019/3/1.
 */
@Controller
public class PathController {

    @RequestMapping("dataImport")
    public String toDataInput(ModelMap modelMap) {
        User user = new User();
        user.setId("007");
        user.setName("大内密探");
        modelMap.put("user", user);
        return "dataImport";
    }

    @RequestMapping("newDataImport")
    public String toNewDataInput(ModelMap modelMap) {
        User user = new User();
        user.setId("008");
        user.setName("模板新方法");
        modelMap.put("user", user);
        return "newDataImport";
    }
}

