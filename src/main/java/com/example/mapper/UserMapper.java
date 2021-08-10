package com.example.mapper;

import com.example.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @return:
 * @author misu
 * @date: 2021/8/9 10:11
 */
@Repository
public interface UserMapper {

    User Sel(int id);

    List<User> findAllUser();
}
