app.controller("contentController", function ($scope, contentService) {
    $scope.contentList=[]
    $scope.findByCatId= function (id) {
        contentService.findByCatId(id).success(
            function (response) {
                $scope.contentList[id] = response;
            }
        )
    }
})