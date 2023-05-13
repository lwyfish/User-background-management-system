import request from '@/utils/request'

// export function login(data) {
//   return request({
//     // url: '/vue-admin-template/user/login',
//     url: '/user/login',
//     method: 'post',
//     data
//   })
// }

export default {
  getUserList(searchModel) {//定义函数
    return request({
      url: '/user/list',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,//属性名称和属性值
        pageSize: searchModel.pageSize,
        username: searchModel.username,
        phone: searchModel.phone,
      }
    });
  },


  addUser(user) {//定义函数
    return request({
      url: '/user',
      method: 'post',
      data: user,//用json给它传过去

    });
  },
  getUserById(id) {//定义函数
    return request({
      // url:'/user/'+id,
      url: `/user/${id}`,
      method: 'get',
    });
  },
  updateUser(user) {//定义函数
    return request({
      url: '/user',
      method: 'put',
      data: user,//用json给它传过去 

    });
  },
  saveUser(user) {//定义函数
    if (user.id == null && user.id == undefined) {//新增的情况
      return this.addUser(user);
    } else {
      return this.updateUser(user);
    }
  },

  deleteUserById(id) {//定义删除
    return request({
      // url:'/user/'+id,
      url: `/user/${id}`,
      method: 'delete',
    });
  },



}