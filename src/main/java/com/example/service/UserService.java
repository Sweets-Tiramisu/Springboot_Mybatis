package com.example.service;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @return:
 * @author misu
 * @date: 2021/8/9 10:11
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User Sel(int id){
        return userMapper.Sel(id);
    }

    public List<User> findAllUser(){
        return userMapper.findAllUser();
    }

    public PageInfo getUserPage() {
        PageHelper.startPage(0,3);
        List<User> list=userMapper.findAllUser();
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
