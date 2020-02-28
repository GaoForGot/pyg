package com.pinyougou.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.grouppojo.Goods;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import entities.Message;
import entities.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemService itemService;

	//activeMQ消息队列对象-添加索引库队列
	@Autowired
	private Destination queueSolrInsertItemsDestination;

	//activeMQ消息队列对象-批量删除索引库队列
	@Autowired
	private Destination queueSolrDeleteItemsDestination;

	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){			
		return goodsService.findPage(page, rows);
	}

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Message update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Message(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Message delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//itemSearchService.deleteItems(ids);
			//发送批量删除消息到activeMQ的批量删除队列
			jmsTemplate.send(queueSolrDeleteItemsDestination, new MessageCreator() {
				@Override
				public javax.jms.Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Message(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param 
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows){
		return goodsService.findPage(goods, page, rows);		
	}



	/**
	 * SPU更改审核状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Message updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);
			//同步更新solr服务器, 如果将status改为1, 就上传, 如果status为其他, 就删除
			if ("1".equals(status)) {
				final List<TbItem> itemList = itemService.findItemsBySpuAndStatus(ids);
				//itemSearchService.importItems(itemList);
				//将itemList转换成JSON字符串
				final String itemListJson = JSON.toJSONString(itemList);
				//发送activeMQ的消息
				jmsTemplate.send(queueSolrInsertItemsDestination, new MessageCreator() {
					@Override
					public javax.jms.Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(itemListJson);
					}
				});
				//生成静态页面
				for (Long goodsId : ids) {
					itemPageService.genItemHtml(goodsId);
				}
			}
			return new Message(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "审核失败");
		}
	}

	@Reference(timeout = 30000)
	private ItemPageService itemPageService;

	
}
