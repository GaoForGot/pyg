package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Reference(timeout = 5000)
    private ItemSearchService searchService;

    @RequestMapping("/searchKeywords")
    public Map searchKeywords(@RequestBody Map map) {
        if (map.get("keywords")==null||map.get("keywords").equals("")) {
            return null;
        }
        return searchService.searchKeywords(map);
    }
}
