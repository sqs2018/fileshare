<template>
  <div class="file-list-container">
    <div class="operater">
      <el-button icon="el-icon-folder-add" plain @click="add()">新增用户</el-button>
    </div>
    <div class="file-list">
      <el-table
        :data="list"
        style="width: 100%"
        height="100%">
        <el-table-column
          prop="xh"
          label="序号"
          sortable
          min-width="12">
        </el-table-column>
        <el-table-column
          prop="userName"
          label="用户名"
          sortable
          show-overflow-tooltip
          min-width="33">
        </el-table-column>
        
        <el-table-column
          prop="type"
          label="类型"
          sortable
          show-overflow-tooltip
          min-width="22">
        </el-table-column>
        <el-table-column
          min-width="15"
          label="操作">
         
          <template slot-scope="scope" style="padding: 5px;">
            <el-button type="primary" icon="el-icon-s-unfold" circle   @click="edit(scope)" title="编辑" ></el-button>
             <el-button type="danger" icon="el-icon-delete" circle  @click="delet(scope)" title="删除"></el-button>            
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 新增弹框 -->
    <el-dialog
      title="用户信息"
      id="edit_box"
      :visible.sync="dialogFormVisible"
      align="left"
      width="370px"
      height='400px'
      :show-close="true"
      :lock-scroll="true"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
    > 
      <el-form>
        <el-form-item label="用户名称" style='margin-right:0px'>
          <el-input v-model="form.userName" autocomplete="off" :disabled='formState == "edit"'></el-input>
        </el-form-item>      
        <el-form-item label="密码" prop='metadata.remark'>
            <el-input v-model="form.password"   type="password" autocomplete="off"></el-input>
        </el-form-item> 
        <el-form-item label="用户类型" prop='metadata.remark'>
            <el-select v-model="form.type" autocomplete="off">
              <el-option  
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"></el-option>
            </el-select>
        </el-form-item>       
      </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="save()">确 定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
 import request from '../../apis/request'



export default {
  name: 'FileList',
  components: {
    
  },
  props: ['folderId'],
  data() {
    return {
      
      options: [ // 下拉列表的选项
        { value: '0', label: '普通用户' },
        { value: '1', label: '管理员' }
      ],
      form:{
        userName:'',
        password:'',
        type:''
      },
      formState:'',
      dialogFormVisible:false,
      list: [
        /*{
            userName:'张三',
            user:'zhangsan',
            updateTime:'2025-09-01 12:21',
            password:'123456'
        },
        {
            userName:'李四',
            user:'lisi',
            updateTime:'2025-09-01 12:21',
            password:'123456'
        }*/
      ],
      selection: []
    }
  },
  watch: {
    folderId () {
      this.$store.commit('changeFolderId', this.folderId)
      this.renderFileList()
    }
  },
  methods: {
    initdata(){
      let urlTemp=process.env.VUE_APP_API_URI +`/v1/getUserAll`;
      console.log(urlTemp)
      request({ 
            url: urlTemp,
            method: 'get',
            }).then((res)=>{

              console.log(res);
              for(let i=0;i<res.length;i++){
                let temp=res[i];
                var showJson={
                  xh:i+1,
                  userName:temp.userName,
                  type:temp.type==0?'普通用户':'管理员',
                 
              };
                this.list.push(showJson);
              }
              

              });
    },
    delet(scope) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
          //删除数据
          let urlTemp=process.env.VUE_APP_API_URI +`/v1/deleteUser?userName=`+scope.row.userName;
          console.log(urlTemp)
          request({ 
            url: urlTemp,
            method: 'get',
            }).then((res)=>{

              console.log(res);
              this.$message.success('删除成功');
              this.list=[];
              this.initdata();

              });
        
        
        
        
       // console.log(scope);
        //this.list.splice(scope.$index,1);
        //this.$message.success('删除成功');
        // this.$axios.get('http://123123').then((res)=>{
        //   console.log(res);
        //   if(res.data.state = 200){
        //    
        //   }else{
        //    
        //   }
        // }).catch((err)=>{
        //   console.log(err);
        // })
      }).catch(() => {});
    },
    handleSelectionChange(val) {
      this.selection = val
    },
    edit(scope){
      console.log(scope)
      this.formState = 'edit';
      this.dialogFormVisible = true;
      this.form = JSON.parse(JSON.stringify(scope.row));
    },
    add(){
      this.dialogFormVisible = true;
      this.formState = 'add';
      this.form = {
        userName:'',
        user:'',
        password:'',
        updateTime:''
      }
    },
    save(){
      if(this.form.userName !== '' && this.form.password !== ''||this.form.type !== ''){
        //this.form.updateTime = '2025-09-02 16:42';
        if(this.formState == 'edit'){
         /* this.list.forEach((o,i) => {
            if(o.user == this.form.user){
              this.$set(this.list,i,this.form)
            }
          });*/

          //更新数据
          let urlTemp=process.env.VUE_APP_API_URI +`/v1/updateuser?userName=`+this.form.userName+`&password=`+this.form.password+`&type=`+this.form.type;
          console.log(urlTemp)
          request({ 
            url: urlTemp,
            method: 'post',
            }).then((res)=>{

              console.log(res);
              this.$message.success('更新成功');
              this.list=[];
              this.initdata();

              });


         
        }else{
         
         
          //新增数据
          let urlTemp=process.env.VUE_APP_API_URI +`/v1/adduser?userName=`+this.form.userName+`&password=`+this.form.password+`&type=`+this.form.type;
          console.log(urlTemp)
          request({ 
            url: urlTemp,
            method: 'post',
            }).then((res)=>{

              console.log(res);
              this.$message.success('新增成功');
              this.list=[];
              this.initdata();

              });
         
         
        
        }
        this.dialogFormVisible = false;
        // this.$axios.post('http://123123',this.form).then((res)=>{
        //   console.log(res);
        //   if(res.data.state = 200){
        //   }else{
        //   }
        // }).catch((err)=>{
        //   console.log(err);
        // })
      }else{
        this.$message.error('请检查是否全部填写')
      }
      
    }
  },
  mounted () {
    this.initdata();
  },
  destroyed () {
   
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
</style>