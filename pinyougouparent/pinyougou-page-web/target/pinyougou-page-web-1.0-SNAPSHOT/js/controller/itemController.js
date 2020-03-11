app.controller("itemController", function ($scope, $http) {

    $scope.num = 1;
    //用户选择的规格
    $scope.specs = {};
    //当前页面的sku对象
    $scope.sku = {};
    //更改数量
    $scope.addItem = function (x) {
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }

    //判断规格是否已选中, 用于样式的更改
    $scope.isSelected = function (specName, specItem) {
        if ($scope.specs[specName] == specItem) {
            return true;
        } else {
            return false;
        }
    }

    //将默认sku选项加载到页面
    $scope.loadSku = function () {
        //深克隆js对象
        //$scope.sku= JSON.parse(JSON.stringify(skuList[0]));//itemList是item页面的全局js对象
        $scope.sku = skuList[0];
        $scope.specs = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //选择规格
    $scope.addSpec = function (specName, specItem) {
        $scope.specs[specName] = specItem;
        findSku();

    }

    //查询sku列表, 已选择的规格在sku列表中是否存在
    findSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchSku(skuList[i].spec, $scope.specs)) {//如果用户选择的规格组合匹配到了一个存在的sku, 则赋值给页面绑定的sku数据模型
                $scope.sku = skuList[i];
                return;
            }
        }
        //如果没有匹配到, 就将sku赋空值
        $scope.sku = {
            id: 0,
            title: '-',
            price: '-',
            spec: '-'
        };

    };

    //比较两个spec列表中的数据是否完全一致
    matchSku = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }

        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }
        return true;
    };

    //加入购物车预留方法
    $scope.addToCart = function () {
        /*alert("SKU ID: " + $scope.sku.id);*/
        $http.get("http://localhost:9107/cart/addToCart.do?itemId=" + $scope.sku.id + "&num=" + $scope.num,
            {'withCredentials': true}).success(
            function (response) {
                if (response.success) {
                    location.href = "http://localhost:9107";
                } else {
                    alert(response.message);
                }
            }
        )
    };
});