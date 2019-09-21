app.controller("baseController", function ($scope) {


    //分页插件配置对象
    $scope.paginationConf = {
        currentPage: 1,                         //当前页
        totalItems: 10,                         //总数据数
        itemsPerPage: 10,                       //每页显示条数
        perPageOptions: [10, 20, 30, 40, 50],   //更改每页显示条数
        onChange: function () {
            $scope.reloadList();
            //清空选中框的选中id列表
            $scope.selectIds = [];
        }
    };

    //封装调用分页查询方法
    //如果不封装的话, 每一个controller的实现可能都不一样, 会很麻烦
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //删除选定
    //声明数组变量, 被选中的id数组
    $scope.selectIds = [];

    //id被选中后调用, 更新数组
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            //删除一个在index上的元素
            $scope.selectIds.splice(index, 1);
        }

    };

    //json转字符串
    $scope.jsonToString = function (jsonString, key) {
        var parsed = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < parsed.length; i++) {
            /*if (i == parsed.length - 1) {
                value += parsed[i][key];
            }else{
                value += parsed[i][key] + ", ";
            }*/
            if (i > 0) {
                value+=","
            }
            value += parsed[i][key];

        }
        return value;
    }

    //搜索list中的每一个map中的指定key是否有指定name
    $scope.searchForName=function (list,key,name) {
        //遍历list
        for (var i = 0; i < list.length; i++) {
            //判断每一个map中的key是否有传入的name
            if (list[i][key] == name) {
                //如果有, 表示之前已经插入过一次
                //返回这个map, 在外面直接插入元素
                return list[i];
            }
        }
        //没有, 返回null
        return null;
    }

});