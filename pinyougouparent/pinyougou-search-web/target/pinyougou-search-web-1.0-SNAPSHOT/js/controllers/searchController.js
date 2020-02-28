app.controller("searchController", function ($scope, $location,searchService) {

    //初始化搜索条件集合searchMap
    $scope.searchMap = {
        "keywords": "", "category": "", "brand": "", "spec": {}, "price": "", "pageNo": 1,
        "pageSize": 40, "sort": "", "sortField": ""
    };


    //发送ajax请求, 执行搜索
    $scope.searchKeywords = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.searchKeywords($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPageLabel();
                delayKeywords = $scope.searchMap.keywords;//防止双向绑定影响品牌列表展示
            }
        )
    };


    //构建分页栏
    buildPageLabel = function () {
        $scope.pageLabel = [];
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点

        if ($scope.resultMap.totalPages > 5) {//如果总页数大于五页, 才展示省略号
            if ($scope.searchMap.pageNo <= 3) {//如果是前三页
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.searchMap.pageNo >= (lastPage - 2)) {//如果是后三页
                firstPage = lastPage - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {//如果总页数小于等于五页, 就不展示省略号
            $scope.firstDot = false;//前面没点
            $scope.lastDot = false;//后面没点
        }

        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };


    //调用搜索相关方法--Start

    //绑定搜索框的回车事件
    $scope.searchEnter = function (e) {
        var keycode = window.event ? e.keyCode : e.which;
        if (keycode == 13) {
            $scope.searchMap.pageNo = 1;
            $scope.searchKeywords();
        }
    };

    //添加搜索条件
    $scope.addSearchCondition = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.searchMap.pageNo = 1;
        $scope.searchKeywords();
    };

    //删除搜索条件
    $scope.removeSearchCondition = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo = 1;
        $scope.searchKeywords();
    };

    //按页码查询
    $scope.searchByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {//如果页码小于1或大于搜索结果的总页码数
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.searchKeywords();
    };

    //排序
    $scope.searchBySort = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.pageNo = 1;
        $scope.searchKeywords();
    };

    //从首页跳转过来的初始化方法
    $scope.searchFromPortal = function () {
        $scope.searchMap.keywords = $location.search()["keyword"];
        $scope.searchKeywords();
    };

    //调用搜索相关方法--End


    //是否是第一页
    $scope.isFirstPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }

    //是否是最后一页
    $scope.isLastPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }

    //判断关键字中是否有品牌
    var delayKeywords = "";//防止双向绑定影响品牌列表展示, 仅在执行搜索请求后判断关键字是否包含品牌
    $scope.keywordsHaveBrand = function () {
        for (var i = 0; i < $scope.resultMap.brands.length; i++) {
            if (delayKeywords.indexOf($scope.resultMap.brands[i].text) >= 0) {
                return true;
            }
        }
        return false;
    };


});