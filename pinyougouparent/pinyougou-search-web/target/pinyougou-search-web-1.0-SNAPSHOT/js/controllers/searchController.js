app.controller("searchController", function ($scope, searchService) {

    //初始化搜索条件集合searchMap
    $scope.searchMap = {"keywords": "", "category": "", "brand": "", "spec": {}};

    $scope.searchKeywords = function () {
        searchService.searchKeywords($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
            }
        )
    };

    //添加搜索条件
    $scope.addSearchCondition = function (key,value) {
        if (key == "category" || key == "brand") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

    };
});