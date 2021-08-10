package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @return:
 * @author misu
 * @date: 2021/8/9 10:11
 */

@RestController
@RequestMapping("/testBoot")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("getUser/{id}")
    public String GetUser(@PathVariable int id){
        return userService.Sel(id).toString();
    }

    @GetMapping("getUserList")
    public String GetUser(){
        String list = userService.findAllUser().toString();
        return JSON.toJSONString(list);
    }

    @GetMapping("getUserListPage")
    public String getUserListPage(){
        String list = userService.getUserPage().toString();
        return JSON.toJSONString(list);
    }
}
