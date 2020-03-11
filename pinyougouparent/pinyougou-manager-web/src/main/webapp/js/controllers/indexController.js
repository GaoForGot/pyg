app.controller("indexController", function ($scope, indexService) {
    $scope.findUserName= function () {
        indexService.findUserName().success(
            function (response) {
                $scope.userName = response.loginName;
            }
        );
    }
});