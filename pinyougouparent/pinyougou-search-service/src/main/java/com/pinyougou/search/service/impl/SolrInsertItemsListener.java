package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class SolrInsertItemsListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage text = (TextMessage) message;
        try {
            List<TbItem> itemList = JSON.parseArray(text.getText(), TbItem.class);
            itemSearchService.importItems(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
