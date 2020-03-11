package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class FreemarkerGenHtmlListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long goodsId = (Long) objectMessage.getObject();
            itemPageService.genItemHtml(goodsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
