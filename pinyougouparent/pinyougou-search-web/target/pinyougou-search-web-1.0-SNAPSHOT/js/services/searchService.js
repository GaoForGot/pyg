app.service("searchService", function ($http) {
    this.searchKeywords=function (searchMap) {
        return $http.post("search/searchKeywords.do", searchMap);
    }
});