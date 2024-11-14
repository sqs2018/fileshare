<template>
    <div class="all">
    <div class="reg">
    
    <img src="@/assets/login_bg.jpg"/>
      </div>
          <div class="log">
            <div class="register">
              <form>
                <div class="register-top-grid">
                  <span style="font-size: 30px; text-align: center;">多终端资源共享系统</span><br/>
                  <span style="font-size: 20px; text-align: center;"> 用户登录</span>
                  <div class="input">
                    <span>用户名 <label style="color: red">* </label></span>
                    <input
                      type="text"
                      v-model="username"
                      placeholder="请输入用户名"
                      class="input-text"
                      autocomplete="off"
                    />
                  </div>
                  <div class="input">
                    <span>密码 <label style="color: red">*</label></span>
                    <input
                      type="password"
                      v-model="password"
                      placeholder="请输入密码"
                      class="input-text"
                      autocomplete="off"
                    />
                  </div>
                  <div class="input">
                    <span>类型 <label style="color: red">*</label></span>
                    <select  class="input-text"  v-model="type">
                        <option value="0">普通用户</option>
                        <option value="1">管理员用户</option>
                    </select>
                  </div>
                </div>
                <div class="text-center">
                  <input
                    type="button"
                    value="登录"
                    class="tijiao"
                    @click="denglu"
                  />
                </div>
              </form>
            </div>
          </div>
          
        </div>
    
    </template>
    <script>     
     import request from '../apis/request'

    export default {
      name: 'app',
      data () {
        return {
          username:'',
          password:'',
          type: 0
        }
      },
      methods:{
        denglu(){

          if(this.username!=='' && this.password!==''){
            let urlTemp=process.env.VUE_APP_API_URI +`/v1/login?username=`+this.username+`&password=`+this.password+`&type=`+this.type;
            console.log(urlTemp)
           request({ 
            url: urlTemp,
            method: 'post',
            }).then((res)=>{

              console.log(typeof(res));
              if(typeof(res)!='object'&&res.indexOf("error")!=-1){
              this.$message.error(res);
              return;
              }
              this.$message.success('登录成功');
              var user=new Object();
              user.username=this.username;
              user.type=this.type;
              window.localStorage.setItem('user',JSON.stringify(user));
              this.$router.push( '/folder/0','folder');

              });
            
            
           
            
           
           
        }else{
            this.$message.error('账号跟密码不能为空')
        }



        }
      
    }
  }
    </script>
    
    <style lang="scss" scoped>
    .all {
          width: 800px;
          box-shadow: -10px 10px 25px rgba(210, 210, 210, 0.9);
          margin: auto;
          margin-top: 5%;
          display: flex;
          border-radius: 35px;
          background-color: #ffffff;
          height: 550px;
        }
        .log {
          width: 50%;
          margin: auto;
        }
        .reg {
          width: 50%;
          height: 100%;
          margin: auto;
          background-color: #20b2aa;
          border-radius: 35px;
          color: #ffffff;
        }
        .reg  img{
          width: 100%;
          height: 100%;
        }
        .reg_1 {
          text-align: center;
          margin: auto;
          margin-top: 50%;
        }
        .reg_1 h2 {
          font-weight: 700;
        }
        .reg_1 p {
          margin: 15px 0px 25px 0px;
        }
        .sig {
          width: 70px;
          height: 30px;
          border-radius: 12px;
          background-color: #20b2aa;
          border-color: #fff;
          color: #ffffff;
        }
        #tiao {
          padding: 0em 0;
        }
        .reg_1 a {
          color: #ffffff;
        }
        h3 {
          font-size: 3em;
          color: black;
          padding-bottom: 1em;
          margin: 0;
          text-align: center;
          font-family: "Marvel-Regular";
        }
        .input {
          margin: 10px 50px;
          width: 300px;
          height: 70px;
        }
        span {
          color: #999;
          font-size: 0.85em;
          padding-bottom: 0.2em;
          display: block;
          text-transform: uppercase;
          margin-bottom: 4px;
        }
        .input-text {
          border: 1px solid #555;
          outline-color: #fd9f3e;
          width: 90%;
          font-size: 1em;
          padding: 0.5em;
          line-height: inherit;
        }
        .register-top-grid {
          color: black;
          padding-bottom: 1em;
          margin: 0;
          font-family: "Marvel-Regular";
          margin: 10px 0;
        }
        .text-center {
          text-align: center;
        }
        .tijiao {
          color: rgb(255, 253, 253);
          width: 80px;
          height: 35px;
          background-color: rgb(241, 52, 10);
          border: none;
        }
    
    </style>