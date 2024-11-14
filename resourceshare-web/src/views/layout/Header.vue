<template>
  <div class="header-container">
    <i v-if="asideVisiable" class="el-icon-s-fold icon"></i>
    <router-link to="/"><span class="name">多终端资源共享系统</span></router-link>
    <el-avatar :size="30" :src="require('@/assets/default_head.png')"></el-avatar>
    <span id="userName">{{ username }}</span>
    &nbsp; &nbsp; &nbsp;
   
                  <input
                    type="button"
                    value="退出登录"
                    v-if="showExit"
                  
                    class="tijiao"
                    @click="exit"
                  />


  <el-dialog
      title="有人向您共享文件"
      id="fileList"
      :visible.sync="showShareFile"
      align="center"
      width="400px"
      height="400px"
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-table
        :data="fileList"
         style="width: 100%"
        height="100%">
        <el-table-column
          prop="fromUser"
          label="分享用户"
          sortable
          show-overflow-tooltip>
                  
         </el-table-column>     

        <el-table-column
          prop="fileName"
          label="文件名"
          sortable
          show-overflow-tooltip>
                  
         </el-table-column>     

         <el-table-column
          label="操作">
          <template slot-scope="scope" style="padding: 5px;">          
            <el-button type="primary" icon="el-icon-download" circle   @click="downloadFiles(scope.row.fileId,scope.row.fromUser)" title="下载"></el-button>           
          </template>
        </el-table-column>
       
      </el-table>     
    </el-dialog>  
                
  </div>
</template>

<script>
import {downloadFileUrl} from '@/apis/personfile'
export default {
  props: {
    asideVisiable: {
      type: Boolean,
      default: true
    }
  },data () {
        return {
          username:'未登录' ,
          showExit:false,
          showShareFile:false,
          fileList:[],
          interval:Object
        }
      },methods:{

    initData(){
       // window.localStorage.setItem('user','fasfas');
        let user=window.localStorage.getItem('user');
        if(user==undefined){
          this.username='未登录';
        }else{
          user=JSON.parse(user);
          this.username=user.username;          
          this.showExit=true;
        }
      },
      exit(){        
        window.localStorage.removeItem('user');
        clearInterval(this.interval);
        this.$router.push('/login','login')
      },queryShareFile(){
        console.log("-------查询后台是否有文件分享--------")
        let userName="";
        let user=window.localStorage.getItem('user');
        if(user!=undefined&&user!="undefined"){
          user=JSON.parse(user);   
          userName=user.username 
          let urlTemp=process.env.VUE_APP_API_URI +`/v1/file/polling/`+userName;
          this.$axios.get(urlTemp).then((res) => {
            console.log(res.data.data)
            if(res.data.data!=null){
              var fromUserList=res.data.data;
              //console.log(fromUserList)
              this.fileList=fromUserList;
              this.showShareFile=true;
              //this.$message.success(res.data.data.'有人向您分享文件')  
            }            
          }).catch(err=>{
            console.log(err)       
          })
       
      }
     },downloadFiles(fileId,fromUser){
      function download(url) {
        let tag = document.createElement('a')
        tag.setAttribute('href', url)
        tag.click()
      }
     
     download(downloadFileUrl(fileId,fromUser));
    
    
     }
  },mounted () {
    this.initData();
    this.interval=window.setInterval(this.queryShareFile,5000);
  }
}
</script>

<style lang="scss" scoped>
@import "src/styles/index.scss";
.header-container {
  width: 96%;
  margin: 0 auto;
  color: #fff;
  height: 100%;
  display: flex;
  align-items: center;
  .icon {
    font-size: 30px;
    cursor: pointer;
  }
  .name {
    padding-left: 6px;
  }
  .el-avatar {
    margin-left: auto;
    cursor: pointer;
  }
  a {
    @include linkTo;
  }
  .tijiao {
          color: rgb(255, 253, 253);
          width: 80px;
          height: 30px;
          background-color: rgb(241, 52, 10);
          border: none;
          cursor: pointer;
        }
    
}


        
</style>

