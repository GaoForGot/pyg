package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map searchKeywords(Map searchMap);

    public void importItems(List<TbItem> itemList);

    public void deleteItems(Long[] ids);
}
