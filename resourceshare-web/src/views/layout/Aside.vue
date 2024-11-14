<template>
  <el-menu :default-active="active" class="navbar-menu">
    <el-menu-item index="folder" @click="linkToFileList">
      <i class="el-icon-document"></i>
      <span slot="title">公共文件</span>
    </el-menu-item>
    <el-menu-item index="folder2" @click="personFileList">
      <i class="el-icon-document"></i>
      <span slot="title">私密文件</span>
    </el-menu-item>
    <el-menu-item index="system" @click="linkTo('/system')">
      <i class="el-icon-setting"></i>
      <span slot="title">系统信息</span>
    </el-menu-item>
    <!--<el-menu-item index="4" @click="personInfomation">
      <i class="el-icon-document"></i>
      <span slot="title">个人信息</span>
    </el-menu-item>-->
    <el-menu-item index="userSetting" @click="userSetting">
      <i class="el-icon-document"></i>
      <span slot="title">用户管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'Aside',
  data () {
    return {
      active: '1',
      navbar: [{
        name: 'folder',
        index: '1',
      }, {
        name: 'system',
        index: '2',
      }]
    }
  },
  watch: {
    '$route' () {
        this.active = this.$route.name
    }
  },
  methods: {
    //公共文件
    linkToFileList() {
      this.$router.push('/folder/0','1')
    },
    //个人文件
    personFileList(){
      let user=window.localStorage.getItem('user');
      if(user==undefined||"undefined"==user){
        this.$message.error('请登录系统')
          this.$router.push('/login','login')
        }else{
           window.localStorage.setItem('folder',0);
          this.$router.push('/folder2/0','2')
        }      
    },
    //系统信息
    linkTo(path) {
      this.$router.push(path,'3')
    },
    //个人信息
    personInfomation(){
      //this.$router.push('/personinfo/' + this.$store.state.folderId)
      this.$router.push('/login/')
    }, // 用户管理
    userSetting(){
      let user=window.localStorage.getItem('user');
        if(user==undefined||user=="undefined"){
          this.$message.error('请登录系统')
          this.$router.push('/login','login')
        }else{
          user=JSON.parse(user);
          if(user.type==0||'0'==user.type){
            this.$message.error('当前为普通用户，请使用管理员帐号')
            return;
          }
          this.$router.push('/userSetting','5')
        }     
    }
  },
  mounted(){
    console.log(this.$route);
    this.active = this.$route.name
  }
}
</script>

<style lang="scss" scoped>
.navbar-menu {
  padding-top: 20px;
}
</style>