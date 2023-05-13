package com.lwy.sys.service;

import com.lwy.sys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2023-05-02
 */
public interface IUserService extends IService<User> {


    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);


    void logout(String token);
}
