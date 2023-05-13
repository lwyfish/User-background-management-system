package com.lwy.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwy.sys.entity.User;
import com.lwy.sys.service.IUserService;
import com.lwy.sys.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2023-05-02
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")

//    public List<User> getAllUser(){
//        List<User> list = userService.list();
//        return list;
//    }

    public Result<List<User>> getAllUser() {
        List<User> list = userService.list();
//        ！也可以，就是略显啰嗦。所以在result中创建静态方法即可！
//        return new Result<>(20000,"success",list);
        return Result.success(list, "查询成功");
    }

    @PostMapping("/login")
    //@RequestBody : 将JSON转换成对象
    public Result<Map<String, Object>> getAllUser(@RequestBody User user) {
        //想用传进来的user对象，来从数据库里看有没有这个用户名和密码
        Map<String, Object> data = userService.login(user);
        if (data != null) {
            return Result.success(data);
        } else {
            return Result.fail(20002,"用户名或密码错误");
        }
    }


    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam("token") String token){
        //根据token获取用户信息，用redis
        Map<String, Object> data = userService.getUserInfo(token);
        if (data != null) {
            return Result.success(data);
        } else {
            return Result.fail(20003,"用户登录信息无效，请重新登录");
        }
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token){//接收到token
        userService.logout(token);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Map<String,Object>> getUserList(@RequestParam(value = "username",required = false) String username,
                                              @RequestParam(value = "phone",required = false) String phone,
                                              @RequestParam(value = "pageNo") Long pageNo,//当前页
                                              @RequestParam(value = "pageSize") Long pageSize){


        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(username),User::getUsername,username);
        wrapper.eq(StringUtils.hasLength(phone),User::getPhone,phone);

        Page<User> page = new Page<User>(pageNo,pageSize);
        userService.page(page,wrapper);//?需要分页拦截器设置，具体在config中的MpConfig
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());//?
        data.put("rows",page.getRecords());//返回结果集
        return Result.success(data);

    }

    @PostMapping
    public Result<?> addUser(@RequestBody User user){//注解将json转为对象

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.save(user);
        return Result.success("新增用户成功");//有个问题，密码不能明文处理

    }

    @PutMapping
    public Result<?> updateUser(@RequestBody User user){//注解将json转为对象

        user.setPassword(null);
        userService.updateById(user);
        return Result.success("修改用户成功");//有个问题，密码不能明文处理
    }

    @GetMapping("/{id}")
    public Result<?> getUserID(@PathVariable("id") Integer id){//

        User user = userService.getById(id);
        return Result.success(user);
    }

    @DeleteMapping ("/{id}")
    public Result<?> deleteUserID(@PathVariable("id") Integer id){//

        userService.removeById(id);
        return Result.success("删除用户成功");
    }

}
