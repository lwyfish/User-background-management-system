<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>
<!-- 设置全局的，让app-main的内边距为10px -->
<style>
.app-main{
  padding: 10px;
}
.el-card{
  margin-bottom: 10px;
}
</style>