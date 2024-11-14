import request from './request'

/**
 * 根据parentId获取所有的文件
 * @param {Number} parentId 
 */
export const getFileList = (parentId,owner) => {
  return request({
    url: `/v1/personfile/parent/${parentId}/${owner}`
  })
}

/**
 * 根据fileId获取文件
 * @param {Number} fileId 文件id
 */
export const getFile = (fileId) => {
  return request({
    url: `/v1/personfile/${fileId}`
  })
}

/**
 * 下载文件URL
 * @param {Number} fileId fileId
 */
export const downloadFileUrl = (fileId,owner) => {
  let userName="";
  let user=window.localStorage.getItem('user');
  if(user!=undefined&&user!="undefined"){
    user=JSON.parse(user);   
    userName=user.username        
  }
  if (process.env.NODE_ENV === 'production') {
    return window.location.origin + `/v1/personfile/${fileId}/download/${owner}?downloadUser=`+userName
  } else {
    return process.env.VUE_APP_API_URI + `/v1/personfile/${fileId}/download/${owner}?downloadUser=`+userName
  }
}

/**
 * 创建新的文件
 * @param {Number}} parentId 父文件夹id
 * @param {String} fileName 文件名
 * @param {String} type 文件类型：folder/txt/pdf...
 */
export const createFile = (parentId, fileName, type,owner) => {
  return request({
    url: `/v1/personfile`,
    method: 'post',
    data: {
      parentId, fileName, type,owner
    }
  })
}

/**
 * 文件重命名
 * @param {Number} fileId 文件id
 * @param {String} fileName 文件名
 */
export const renameFile = (fileId, fileName) => {
  return request({
    url: `/v1/personfile/${fileId}/rename`,
    method: 'put',
    data: {
      fileName
    }
  })
}

/**
 * 根据文件id删除文件
 * @param {Array} fileIds 文件ids
 */
export const deleteFiles = (fileIds) => {
  return request({
    url: `/v1/personfile`,
    method: 'delete',
    data: fileIds
  })
}

/**
 * 移动或复制文件
 * @param {Array} fileIds 源文件ids
 * @param {Array} targetIds 目标文件ids
 * @param {String} type 类型
 */
export const moveOrCopyFiles = (fileIds, targetIds, type) => {
  console.log("moveOrCopyFiles----");
  return request({
    url: '/v1/personfile',
    method: 'put',
    data: {
      fileIds, targetIds, type
    }
  })
}