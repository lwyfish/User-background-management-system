package com.lwy.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lwy.sys.entity.User;
import com.lwy.sys.mapper.UserMapper;
import com.lwy.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2023-05-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource//@Resource就不报错了
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(User user) {
        //登录逻辑
        //根据用户名和密码查询
        //结果不为空，则生成token（前后端分离用到），并将用户信息存入redis
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();//用于查询
        wrapper.eq(User::getUsername, user.getUsername());
//        wrapper.eq(User::getPassword, user.getPassword());

        //结果不为空，且密码和传入的密码匹配
        User loginUser = userMapper.selectOne(wrapper);
        if (loginUser != null && passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {

            //暂时用UUID，终极方案是jwt
            String key = "user:" + UUID.randomUUID();
            //把登录对象存入redis,先注入配置类
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key, loginUser, 30, TimeUnit.MINUTES);//30分钟失效

            //返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", key);
            return data;
        }
        return null;
    }
//    老的登录逻辑
//    @Override
//    public Map<String, Object> login(User user) {
//        //登录逻辑
//        //根据用户名和密码查询
//        //结果不为空，则生成token（前后端分离用到），并将用户信息存入redis
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();//用于查询
//        wrapper.eq(User::getUsername, user.getUsername());
//        wrapper.eq(User::getPassword, user.getPassword());
//
//        User loginUser = userMapper.selectOne(wrapper);
//        if (loginUser != null) {
//
//            //暂时用UUID，终极方案是jwt
//            String key = "user:" + UUID.randomUUID();
//            //把登录对象存入redis,先注入配置类
//            loginUser.setPassword(null);
//            redisTemplate.opsForValue().set(key, loginUser, 30, TimeUnit.MINUTES);//30分钟失效
//
//            //返回数据
//            Map<String, Object> data = new HashMap<>();
//            data.put("token", key);
//            return data;
//        }
//        return null;
//    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        //根据token获取用户信息，用redis
        Object obj = redisTemplate.opsForValue().get(token);//类似hashmap对象，不是json对象

        //之前存入是经过序列化的，现在要反序列化
        if (obj != null) {
            String s = JSON.toJSONString(obj);//变成json对象
            User loginUser = JSON.parseObject(s, User.class);//反序列化
            Map<String, Object> data = new HashMap<>();
            data.put("name", loginUser.getUsername());
            data.put("avatar", loginUser.getAvatar());//用户头像地址


            //角色
            List<String> roleList = userMapper.getRoleNameByUserId(loginUser.getId());
            data.put("roles", roleList);
            return data;

        }
        return null;
    }

    @Override
    public void logout(String token) {
        //在redis中把token删掉
        redisTemplate.delete(token);
    }


}
