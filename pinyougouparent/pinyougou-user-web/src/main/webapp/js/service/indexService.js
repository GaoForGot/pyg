//服务层
app.service('indexService',function($http){

    //读取列表数据绑定到表单中
    this.findUserName=function(){
        return $http.get('../user/findUserName.do');
    }

});
