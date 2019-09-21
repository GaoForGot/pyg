//创建控制器
app.controller("brandController", function ($scope, $controller,brandService) {

    $controller("baseController",{$scope:$scope})

    //查询所有
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }

    //分页查询
    $scope.findPage = function (page, size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }

    //添加品牌
    $scope.add = function () {
        brandService.add($scope.brand).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    //修改品牌
    //1. 回显数据

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.brand = response;
            }
        )
    };

    //2. 执行修改
    $scope.update = function () {
        brandService.update($scope.brand).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    //保存按键应根据情况执行不同的方法
    //如果brand为空对象, 调用add方法
    //如果brand不是空对象, 调用update方法
    $scope.save = function () {
        if ($scope.brand.id != null) {
            $scope.update();
        } else {
            $scope.add();
        }
    };

    //get请求删除选中的品牌
    $scope.del = function () {
        brandService.del($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
                $scope.selectIds = [];
            }
        )
    }

    //searchBrand要给空赋值, 否则null会报错
    $scope.searchBrand = {};
    //条件查询
    $scope.search = function (page, size) {
        brandService.search(page,size,$scope.searchBrand).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };
})
