<template>
    <div>
        <!--搜索栏-->
        <el-card id="search">
            <el-row>
                <el-col :span="20">
                    <el-input v-model="searchModel.username" placeholder="用户名"></el-input>
                    <el-input v-model="searchModel.phone" placeholder="电话"></el-input>
                    <el-button @click="getUserList" type="primary" round icon="el-icon-search">查询</el-button>
                    <!-- 按钮可以加入icon图标 -->
                </el-col>
                <el-col :span="4" align="right">
                    <el-button @click="openEditUI(null)" type="primary" icon="el-icon-plus" circle></el-button>
                </el-col>
            </el-row>
        </el-card>


        <!-- 结果列表 -->
        <el-card>
            <!-- <el-table :data="userList" stripe style="width: 100%">
                <el-table-column prop="date" label="日期" width="180">
                </el-table-column>
                <el-table-column prop="name" label="姓名" width="180">
                </el-table-column>
                <el-table-column prop="address" label="地址">
                </el-table-column>
            </el-table> -->
            <el-table :data="userList" stripe style="width: 100%">
                <!-- <el-table-column type="index" label="#" width="80">
                </el-table-column> -->
                <el-table-column type="index" label="#" width="80">
                    <!-- (pageNo-1)*pageSize+(index+1) 因为index是从0开始的 -->
                    <template slot-scope="scope">
                        {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
                    </template>
                </el-table-column>
                <el-table-column prop="id" label="用户ID" width="180">
                </el-table-column>
                <el-table-column prop="username" label="用户名" width="180">
                </el-table-column>
                <el-table-column prop="phone" label="电话" width="180">
                </el-table-column>
                <el-table-column prop="status" label="状态" width="180">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == 1">正常</el-tag>
                        <el-tag v-else type="danger">禁用</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="email" label="电子邮件">
                </el-table-column>
                <el-table-column label="操作" width="180">
                    <template slot-scope="scope">
                        <el-button @click="openEditUI(scope.row.id)" type="primary" icon="el-icon-edit" circle></el-button>
                        <el-button @click="deleteUser(scope.row)" type="danger" icon="el-icon-delete" circle></el-button>
                    </template>
                </el-table-column>
            </el-table>

        </el-card>
        <!-- 分页组件 -->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>

        <!-- 用户新增和修改的共用对话框 -->
        <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="userForm" ref="userFormRef" :rules="rules">
                <el-form-item label="用户名" prop="username" :label-width="formLabelWidth">
                    <el-input v-model="userForm.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="登录密码" prop="password"
                    :label-width="formLabelWidth">
                    <el-input type="password" v-model="userForm.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="联系电话" :label-width="formLabelWidth">
                    <el-input v-model="userForm.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户状态" :label-width="formLabelWidth">
                    <el-switch v-model="userForm.status" :activate-value="1" :inactivate="0">
                    </el-switch>
                </el-form-item>
                <el-form-item label="电子邮件" prop="email" :label-width="formLabelWidth">
                    <el-input v-model="userForm.email" autocomplete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveUser">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import userApi from '@/api/userManage'//导入新写的方法，userApi是起的名字
import { title } from '@/settings';
export default {
    name: '',
    data() {
        var checkEmail = (rule, value, callback) => {
            var reg = /^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\.[a-z]{2,}$/
            if (!reg.test(value)) {
                return callback(new Error('邮箱不能为空'));
            }
            callback();
        };
        return {
            total: 0,//后台传来的，先默认
            searchModel: {
                pageNo: 1,
                pageSize: 10
            },
            userList: [],

            title: "",
            dialogFormVisible: false,//控制表单的可不可见
            formLabelWidth: '130px',
            userForm: {},
            rules: {//表单验证
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },//失去焦点的时候触发
                    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入初始密码', trigger: 'blur' },//失去焦点的时候触发
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入电子邮件', trigger: 'blur' },//失去焦点的时候触发
                    { validator: checkEmail, trigger: 'blur' }
                ],

            },

        }
    },
    created() {
        this.getUserList();
    },//页面加载，用到钩子函数created
    methods: {
        handleSizeChange(pageSize) {
            this.searchModel.pageSize = pageSize;
            this.getUserList();//重新查询
        },
        handleCurrentChange(pageNo) {
            this.searchModel.pageNo = pageNo;
            this.getUserList();//重新查询
        },
        getUserList() {
            userApi.getUserList(this.searchModel).then(response => {
                this.userList = response.data.rows;//这个与后端回调后的属性有关，data.rows
                this.total = response.data.total;
            }
            );//then是回调，目的是给total和userList里面赋值
        },


        openEditUI(id) {
            if (id == null) {
                this.title = '新增用户';
            } else {
                this.title = '修改用户';
                //根据id查询用户数据
                userApi.getUserById(id).then(response => {
                    this.userForm = response.data;
                })
            }
            this.dialogFormVisible = true;
        },
        clearForm() {  //使用close回调来清理当窗口关闭时的表单
            this.userForm = {};
            this.$refs.userFormRef.clearValidate();//官网上清除验证错误信息的
        },

        saveUser() {
            //触发表单验证
            this.$refs.userFormRef.validate((valid) => {
                if (valid) {
                    //提交请求给后台
                    // alert('submit!');
                    userApi.saveUser(this.userForm).then(response => {
                        //成功提示

                        this.$message({
                            message: response.message,
                            type: 'success'
                        });

                        //关闭对话框
                        this.dialogFormVisible = false;
                        //刷新表格数据
                        this.getUserList();
                    })

                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },

        deleteUser(user) {
            this.$confirm(`您确认删除用户${user.username}?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                userApi.deleteUserById(user.id).then(response => {
                    this.$message({
                        type: 'success',
                        message: response.message
                    });
                    this.getUserList();
                })

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: response
                });
            });
        }


    }

}


</script>


<!-- 同名选择器el-input -->
<style>
#search .el-input {

    width: 200px;
    margin-right: 10px;
}

.el-dialog .el-input {
    width: 85%;

}
</style>
