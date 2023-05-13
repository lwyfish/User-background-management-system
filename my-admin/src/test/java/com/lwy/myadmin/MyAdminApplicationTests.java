package com.lwy.myadmin;

import com.lwy.sys.entity.User;
import com.lwy.sys.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MyAdminApplicationTests {
    @Autowired
    private UserMapper userMapper;


    @Test
    void testMapper() {
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);//Java8
//        for(User use:users){
//            System.out.println(use);
//        }
    }

}
