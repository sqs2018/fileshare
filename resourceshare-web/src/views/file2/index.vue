<template>
  
  <div class="file-list-container">
  
    <transition>
        <router-view :key="$route.fullPath"></router-view>
    </transition>
    <div class="operater">
      <el-button type="primary" icon="el-icon-upload" @click="uploadFileTrigger">上传个人文件</el-button>
      <el-button type="primary" icon="el-icon-upload" @click="uploadText">上传个人文本</el-button>
      <el-button icon="el-icon-folder-add" plain @click="createFolder">新增文件夹</el-button>     
      <el-button-group class="extend-btn">
        <el-button type="primary" icon="el-icon-upload" v-if="selection.length === 1" @click="shareToUser">分享给指定用户</el-button>
        <el-button icon="el-icon-edit" v-if="selection.length === 1" @click="renameFile(selection[0])" plain>重命名</el-button>
        <el-button icon="el-icon-download" v-if="canDownload()" @click="downloadFiles(selection)" plain>下载</el-button>
        <el-button icon="el-icon-delete" v-if="selection.length > 0" plain @click="deleteFiles(selection)">删除</el-button>
       <!-- <el-button plain v-if="selection.length > 0" @click="moveTo(selection)">移动到</el-button>
        <el-button plain v-if="selection.length > 0" @click="copyTo(selection)">复制到</el-button>-->
      </el-button-group>
    </div>
    <div class="navigation">
      <div class="previous-level" v-if="navigation.length > 1">
        <span @click="goFile(navigation[navigation.length - 2].id)">返回上一级</span>
        <span class="navigation-separator">|</span>
      </div>
      <div class="breadcrumb">
        <div class="breadcrumb-item" v-for="(nav, index) in navigation" :key="'navigation-' + index">
          <span class="breadcrumb-name" @click="goFile(nav.id)">{{ nav.fileName }}</span>
          <span class="navigation-separator" v-if="index !== navigation.length - 1">/</span>
        </div>
      </div>
    </div>
    <div class="file-list">
      <el-table
        :data="fileList"
        style="width: 100%"
        height="100%"
        :default-sort = "{prop: 'fileName', order: 'ascending'}"
        @selection-change="handleSelectionChange">
        <el-table-column
          type="selection"
          show-overflow-tooltip
          min-width="2">
        </el-table-column>
        <el-table-column
          prop="fileName"
          label="文件名"
          sortable
          show-overflow-tooltip
          min-width="33">
          <template slot-scope="scope">
            <div class="file-icon" :class="getFileType(scope.row.type)"></div>
            <!-- <router-link class="file-name" :to="'/folder2/' + scope.row.id"   v-if="getFileType(scope.row.type) === 'folder'">{{ scope.row.fileName }}</router-link>
            <router-link target="_blank" class="file-name" :to="'/video/' + scope.row.id" v-else-if="getFileType(scope.row.type) === 'video'">{{ scope.row.fileName }}</router-link>
            <router-link class="file-name" to="" v-else>{{ scope.row.fileName }}</router-link> -->
            <span @click="goFile(scope.row.id)" v-if="getFileType(scope.row.type) === 'folder'" class="link">{{ scope.row.fileName }}</span>
            <span v-else>{{ scope.row.fileName }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="size"
          label="大小"
          sortable
          :formatter="sizeFormatter"
          min-width="12">
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="修改日期"
          sortable
          show-overflow-tooltip
          :formatter="dateFormatter"
          min-width="15">
        </el-table-column>
        <el-table-column
          prop="shareCode"
          label="文件共享码"
          sortable
          show-overflow-tooltip        
          min-width="15">
        </el-table-column>

        <el-table-column
          min-width="28"
          label="操作">
         
          <template slot-scope="scope" style="padding: 5px;">
            <!--<el-button type="primary" icon="el-icon-s-unfold" circle   @click="moveTo([scope.row])" title="移动" ></el-button>
            <el-button type="primary" icon="el-icon-document-copy" circle    @click="copyTo([scope.row])"  title="复制"></el-button>  -->       
             <el-button type="primary" icon="el-icon-s-check" circle   @click="renameFile([scope.row])" title="重命名" ></el-button>
            <el-button type="primary" icon="el-icon-download" circle   @click="downloadFiles([scope.row])" title="下载"></el-button>
            <el-button type="primary" icon="el-icon-share" circle   @click="shareFiles([scope.row])" title="共享"></el-button>
             <el-button type="danger" icon="el-icon-delete" circle  @click="deleteFiles([scope.row])" title="删除"></el-button>   
             <el-button type="primary" icon="el-icon-user" circle  @click="showDownloadDetail([scope.row])" title="下载用户"></el-button>           
          </template>
        </el-table-column>
      </el-table>
    </div>
    <FolderTreeDialog v-if="folderTreeVisiable" v-bind="folderTreeProps"
      v-on:return="dealReturn"/>

       <!-- 新增弹框 -->
    <el-dialog
      title="上传文本"
      id="edit_box"
      :visible.sync="dialogText"
      align="left"
      width="370px"
      height='400px'
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-form>
        <el-form-item label="文本内容" style='margin-right:0px'>
          <el-input type='textarea' v-model="uptext" autocomplete="off" placeholder="输入文本内容"></el-input>
        </el-form-item>
      </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogText = false">取 消</el-button>
            <el-button type="primary" @click="textSave()">确 定</el-button>
        </div>
    </el-dialog>  



    <!--显示共享码-->
    <el-dialog
      title=""
      id="shareCode"
      :visible.sync="ShowshareCode"
      align="left"
      width="370px"
      height='400px'
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-form>
        <el-form-item  style='margin-right:0px'>
          <div style="text-align: center; vertical-align: middle;">
          <span style="font-size: 30px;">文件共享码:{{shareCode}}</span><br/>
          <span style="font-size: 15px;">请使用本系统APP扫描二维码下载文件</span>
          <img :src="shareCodeUrl"  style="height: 250px;width: 250px;"/>
        </div>
        </el-form-item>
      </el-form>
       
    </el-dialog>  



    <!--显示下载用户-->
    <el-dialog
      title="文件下载用户列表"
      id="downloadUser"
      :visible.sync="showDownloadUser"
      align="center"
      width="870px"
      height='800px'
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-table
        :data="downloadUserList"
        style="width: 100%"
        height="100%">
        <el-table-column
          prop="userName"
          label="下载用户"
          sortable
          show-overflow-tooltip>
            <template v-slot="scope">
              {{(scope.row.userName == null||scope.row.userName == "")?'匿名用户':scope.row.userName}}
            </template>
         </el-table-column>
        <el-table-column
          prop="createTime"
          label="下载时间"
          sortable
         :formatter="dateFormatterCreateTime"
          show-overflow-tooltip>
        </el-table-column>
        
      </el-table>
    </el-dialog>  



    <!--显示共享用户列表-->
    <el-dialog
      title="共享给用户"
      id="userList"
      :visible.sync="showShareUser"
      align="center"
      width="400px"
      height="400px"
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-table
        :data="userList"
         style="width: 100%"
          @selection-change="userHandleSelectionChange"
        height="100%">
       <el-table-column
      type="selection"
      width="50">
    </el-table-column>
        <el-table-column
          prop="userName"
          label="下载用户"
          sortable
          show-overflow-tooltip>
            <template v-slot="scope">
              {{scope.row.userName}}
            </template>
         </el-table-column>     
       
      </el-table>
      <div slot="footer" class="dialog-footer">
            <el-button @click="showShareUser = false">取 消</el-button>
            <el-button type="primary" @click="shareFileToUser()">确 定</el-button>
        </div>
    </el-dialog>  


 <div id="app">
  <router-view :key="key" />
</div>
  </div>

</template>

<script>
import {getFileList, createFile, renameFile, deleteFiles, moveOrCopyFiles, downloadFileUrl} from '@/apis/personfile'
import {formatSize, formatDateTime} from '@/utils'
import FolderTreeDialog from '@/components/FolderTreeDialog'

export default {
  name: 'FileList',
  props: ['folderId'],
 computed:{
   key(){
     let result=this.$route.name?this.$route.name+new Date():this.$route+new Date();
      console.log("result====="+result);
      return result;
   }
 },
 
  components: {
    FolderTreeDialog
  },
  data() {
    return {
      dialogText:false,
      ShowshareCode:false,
      showDownloadUser:false,
      shareCode:'001',
      shareCodeUrl:'',
      folderTreeVisiable: false,
      uptext:'',
      folderTreeProps: {
        type: 'move', // 标示操作类型，move和copy
        title: '提示',
        sourceFiles: []
      },
      fileList: [],
      navigation: [],
      selection: [],
      userselection:[],
      userName:'',
      downloadUserList:[],
      showShareUser:false,
      userList:[],
    }
  },
  watch: {
    folderId () {
      console.log("folderId====="+this.folderId);
      this.$store.commit('changeFolderId', this.folderId)
      this.renderFileList()
    }
    
  },

  methods: {
    textSave(){
      this.dialogText = false;
      let urlTemp=process.env.VUE_APP_API_URI +`/v1/resource/chunkByText`;
     this.$axios.post(urlTemp,{
        text:this.uptext,
        parentId:this.folderId,
        owner:this.userName            
      }).then((res) => {
        console.log(res)
        this.$message.success('上传成功');
        this.renderFileList()
      }).catch(err=>{
        console.log(err)
        this.$message.success('上传失败')
      })

    },
    goFile(folderId){
     // :to="'/folder/' + scope.row.id"
      window.localStorage.setItem('folder',folderId);
      // this.$router.push('/folder2/'+folderId,'folder2')
      this.folderId = folderId;
      this.renderFileList();
    },
    uploadText(){
      this.dialogText = true;
      this.uptext = '';
    },
    uploadFileTrigger () {
      window.eventBus.$emit('openUploader')
    },
    renderFileList () {
      getFileList(this.folderId,this.userName).then(response => {
        this.fileList = response.data; 
        console.log("fileList=="+this.fileList);
        this.navigation = response.extra
      }).catch(() => {})
    },
    //显示下载用户
    showDownloadDetail(row){
        this.showDownloadUser=true;
       // console.log(row)
     let urlTemp=process.env.VUE_APP_API_URI +`/v1/downloadLog/`+row[0].id;
     this.$axios.get(urlTemp).then((res) => {
        console.log(res.data.data)
        this.downloadUserList=res.data.data;
      }).catch(err=>{
        console.log(err)
      })
    },
    shareFiles (row){
      let urlTemp=process.env.VUE_APP_API_URI +`/v1/file/share/`+row[0].id;
     this.$axios.get(urlTemp).then((res) => {
        console.log(res)
        this.ShowshareCode=true;
        this.shareCode=res.data.data;
        this.shareCodeUrl=process.env.VUE_APP_API_URI +"/v1/file/generateQRCode/"+this.shareCode;
        console.log("this.shareCodeUrl="+this.shareCodeUrl)
        this.$message.success('共享成功');        
        this.renderFileList()
      }).catch(err=>{
        console.log(err)
        this.$message.success('共享失败')
      })
    },
    createFolder () {
      this.$prompt('请输入文件夹名', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{1,100}$/,
        inputErrorMessage: '文件夹名长度必须小于100'
      }).then(({ value }) => {
        createFile(this.folderId, value, 'folder',this.userName).then(() => {
          this.$message({
            type: 'success',
            message: '创建成功'
          })
          this.renderFileList()
        }).catch(error => {
          if (error.data) {
            this.$message({
              type: 'error',
              message: error.data.msg
            })
          }
        })
      }).catch(() => {});
    },
    renameFile (row) {
      this.$prompt('请输入文件夹名', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{1,100}$/,
        inputValue: row[0].fileName,
        inputErrorMessage: '文件夹名长度必须小于100'
      }).then(({ value }) => {
        renameFile(row[0].id, value).then(() => {
          this.$message({
            type: 'success',
            message: '文件重命名成功'
          })
          this.renderFileList()
        }).catch(error => {
          if (error.data) {
            this.$message({
              type: 'error',
              message: error.data.msg
            })
          }
        })
      }).catch(() => {})
    },
    deleteFiles (arr) {
      let ids = new Array()
      for (let i = 0; i < arr.length; i++) {
        ids.push(arr[i].id)
      }
      this.$confirm('确定删除文件?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteFiles(ids).then(() => {
          this.$message({
            type: 'success',
            message: '文件删除成功'
          })
          this.renderFileList()
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '文件删除失败'
          })
          this.renderFileList()
        })
      }).catch(() => {});
    },
    moveTo (arr) {
      this.folderTreeProps = {
        type: 'move',
        title: '移动到',
        sourceFiles: arr
      }
      this.$nextTick(() => {
        this.folderTreeVisiable = true
      })
    },
    copyTo (arr) {
      this.folderTreeProps = {
        type: 'copy',
        title: '复制到',
        sourceFiles: arr
      }
      this.$nextTick(() => {
        this.folderTreeVisiable = true
      })
    },
    dealReturn (val) {
      if (val.type === 'cancel') {
        this.folderTreeVisiable = false
        return
      } else {
        let arr = val.value, that = this,
            sourceIds = this.folderTreeProps.sourceFiles.map(f => f.id)
        let operate = function (sourceIds, targetIds, type, successCallback) {
          moveOrCopyFiles(sourceIds, targetIds, type).then(() => {
            successCallback()
            that.folderTreeVisiable = false
            that.renderFileList()
          }).catch(() => {})
        }
        if (this.folderTreeProps.type === 'move') { // 移动文件到
          console.log("移动到=------")
          if (arr.length !== 1) {
            this.$message({
              type: 'error',
              message: '必须选择一个目标文件夹'
            })
          } else {
            operate(sourceIds, arr, 'move', () => {
              that.$message({
                type: 'success',
                message: '移动文件成功'
              })
            })
          }
        } else if (this.folderTreeProps.type === 'copy') { // 复制文件到
          if (arr.length !== 1) {
            this.$message({
              type: 'error',
              message: '请选择文件夹'
            })
          } else {
            operate(sourceIds, arr, 'copy', () => {
              that.$message({
                type: 'success',
                message: '复制文件成功'
              })
            })
          }
        }
      }
    },
    handleSelectionChange(val) {
      this.selection = val
    },
    userHandleSelectionChange(val){
      this.userselection=val
    },
    canDownload () {
      let hasFolder = false
      for (let i = 0; i < this.selection.length; i++) {
        if (this.selection[i].type === 'folder') {
          hasFolder = true
          break
        }
      }
      return !hasFolder && this.selection.length > 0
    },
    downloadFiles (arr = []) {
      console.log("开始下载。。。。。")
      function download(url) {
        let tag = document.createElement('a')
        tag.setAttribute('href', url)
        tag.click()
      }
      let i = 0
      if (i < arr.length) {
        //如果是下载文件夹，提示正在后打包
       if(this.getFileType(arr[0].type)== 'folder')
       this.$message('后台正在打包，可能需要一段时间，请耐心等待');
      
        download(downloadFileUrl(arr[i].id,this.userName));
        i++
      }
      let s = setInterval(() => {
            if (i < arr.length) {
              download(downloadFileUrl(arr[i].id,this.userName))
              i++
            } else {
              clearInterval(s)
            }
          }, 2000);
    },
    getFileType (originType){
      console.log("=====:"+originType.toLowerCase());
      let supportedType = ['default', 'folder', 'pdf', 'compress_file', "web",
            'video', 'audio', 'picture', 'doc', 'txt', 'torrent', 'ppt', 'code'];
      if (supportedType.indexOf(originType.toLowerCase()) !== -1){
        return originType.toLowerCase();
      }
      return 'default'
    },
    sizeFormatter (row) {
      let size = formatSize(row.size, 2)
      if (row.type === 'folder' || size === undefined) {
        return '-'
      }
      return size
    },
    dateFormatter(row) {
      return formatDateTime(row.updateTime)
    },
    dateFormatterCreateTime(row) {
      return formatDateTime(row.createTime)
    },
    //显示共享组指定用户对话框
    shareToUser(row){
      console.log(row);
      let userName="";
      let user=window.localStorage.getItem('user');
      if(user!=undefined&&user!="undefined"){
        user=JSON.parse(user);   
        userName=user.username        
      }
      let urlTemp=process.env.VUE_APP_API_URI +`/v1/getNormalUser/`+userName;
     this.$axios.get(urlTemp).then((res) => {
        console.log(res)
       
        this.showShareUser=true;
        this.userList=res.data;

      }).catch(err=>{
        console.log(err)
        this.$message.success('获取用户失败')
      })
    },
    //选择共享用户后，真正共享
    shareFileToUser(){
      let userName="";
      let user=window.localStorage.getItem('user');
      if(user!=undefined&&user!="undefined"){
        user=JSON.parse(user);   
        userName=user.username        
      }
      let urlTemp=process.env.VUE_APP_API_URI +`/v1/file/shareFileToUser`;
     // console.log("选中文件"+this.selection[0])
     var shareUser="";
     for(var i=0;i<this.userselection.length;i++){
        shareUser+=this.userselection[i].userName;
        shareUser+=",";
     }

     this.$axios.post(urlTemp,{
        fileId:this.selection[0].id+"",
        users:shareUser,
        shareUser:userName
      }).then((res) => {
        console.log(res)
        this.$message.success('分享成功')
        this.showShareUser=false;        
      }).catch(err=>{
        console.log(err)
        this.$message.success('分享失败')
      })


    }
  },
  mounted () {
    let that = this
   console.log("file2==mounted");

    let user=window.localStorage.getItem('user');
        if(user!=undefined&&user!="undefined"){
          user=JSON.parse(user);
          this.userName=user.username;     
        }

      this.folderId= window.localStorage.getItem('folder');


    this.renderFileList()
    window.eventBus.$on('flushFileList', query => {
      this.params = query || {};
      that.renderFileList()
    })
  },
  destroyed () {
    window.eventBus.$off('flushFileList')
  }
  
}
</script>

<style lang="scss" scoped>
.file-list-container {
  height: 100%;
  position: relative;
  a {
    color: #000;
    text-decoration: none;
    &:visited {
      color: #000;
    }
    &:hover {
      color: #3794ff;
    }
  }
  .extend-btn {
    margin-left: 30px;
  }
  .navigation {
    font-size: 14px;
    font-weight: 450;
    margin: 10px 0px;
    .previous-level {
      cursor: pointer;
      display: inline-block;
      span:first-child:hover {
        color: #3794ff;
      }
    }
    .breadcrumb, .breadcrumb-item {
      display: inline-block;
      .breadcrumb-name {
        cursor: pointer;
        &:hover {
          color: #3794ff;
        }
      }
    }
    .navigation-separator {
      padding: 0 5px;
    }
  }
  .file-list {
    width: 100%;
    position: absolute;
    top: 70px;
    left: 0;
    bottom: 0;
  }
}
.el-icon-more{
  cursor: pointer;
  font-size: 18px;
  &:hover {
    color: #3794ff;
  }
}
.file-icon{
  display: inline-block;
  background-image: url(../../assets/file_icons.png);
  background-repeat: no-repeat;
  width: 26px;
  height: 23px;
}
.file-name{
  display: inline-block;
  padding-left: 8px;
  text-decoration: none;
  color: #000;
  cursor: pointer;
}
.default{
  background-position: -596px -566px;
}
.folder{
  background-position: -594px -862px;
}
.pdf{
  background-position: -596px -136px;
}
.compress_file{
  background-position: -596px -1664px;
}
.video{
  background-position: -596px -1630px;
}
.audio{
  background-position: -596px -442px;
}
.picture{
  background-position: -596px -306px;
}
.doc {
  background-position: -596px -170px;
}
.txt {
  background-position: -596px -102px;
}
.ppt {
  background-position: -596px -204px;
}
.torrent {
  background-position: -596px 0px;
}
.web{
  background-position: -594px -1458px;
}
.code {
  background-position: -596px -1424px;
}
.link{
  cursor: pointer;
}
.link:hover{
  color: #3794ff;
}
</style>