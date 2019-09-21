//上传文件服务层
app.service('uploadService',function($http){

    this.upload=function () {
        var formData = new FormData();
        //固定写法
        //第一个参数: 发送请求时参数名为file, 接收参数名也必须是file 第二个参数: 相当于document.getById("#file")
        formData.append("file",file.files[0]);
        return $http({
            //访问路径
            url:"../upload.do",
            //访问方式
            method: "POST",
            //数据类型
            data: formData,
            //上传文件格式, 默认为json, 设置为undefined, 浏览器会自动更改为multipart
            headers:{"Content-Type":undefined},
            //angularjs二进制序列化封装formData对象
            transformRequest:angular.identity
        })
    };

});
