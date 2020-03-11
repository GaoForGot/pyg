package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entities.Message;
import entities.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.PhoneFormatCheckUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	/**
	 * 查询登录用户名
	 */
	@RequestMapping("/findUserName")
	public Map findUserName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = new HashMap<>();
		map.put("loginName", name);
		return map;
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return userService.findPage(page, rows);
	}

	/**
	 * 生成验证码
	 * @param phone
	 * @return
	 */
	@RequestMapping("/createCode")
	public Message createCode(String phone) {
		try {
			//校验手机号
			boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
			if (!phoneLegal) {
				return new Message(false, "非法手机号");
			}
			userService.createCode(phone);
			return new Message(true, "已生成验证码");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "生成验证码失败");
		}
	}

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Message add(String code,@RequestBody TbUser user){
		//验证码非空判断
		if ("".equals(code) || code == null) {
			return new Message(false,"请输入验证码");
		}
		//校验验证码
		if (!userService.checkCode(user.getPhone(), code)) {
			return new Message(false, "验证码错误");
		}
		try {
			userService.add(user);
			return new Message(true, "注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "注册失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Message update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Message delete(Long [] ids){
		try {
			userService.delete(ids);
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
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
