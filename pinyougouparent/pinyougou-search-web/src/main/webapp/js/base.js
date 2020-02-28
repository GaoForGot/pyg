//创建模块
var app = angular.module("pinyougou", []);
//过滤器
app.filter("trustHtml",["$sce", function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);
app.config(['$compileProvider',function ($compileProvider) {
    $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file|sms|data|localhost):/);
}]);