package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private TbUserMapper tbUserMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${SESSION_EXPIRE}")
	private int SESSION_EXPIRE;
	
	@Override
	public TaotaoResult checkUserData(String data, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		//1判断用户名是否可用
		if(type == 1) {
			criteria.andUsernameEqualTo(data);
		//判断手机号是否可用
		}else if(type == 2){
			criteria.andPhoneEqualTo(data);
		//判断用户邮箱是否可用
		}else if (type == 3) {
			criteria.andEmailEqualTo(data);
		}else {
			return TaotaoResult.build(400, "参数中包含非法数据");
		}
		//执行查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list.size() == 0 || list == null) {
			//查询不到数据，数据可用返回true
			return TaotaoResult.ok(true);
		}
		//数据不可用
		return TaotaoResult.ok(false);
	}
	
	
	@Override
	public TaotaoResult register(TbUser user) {
		//检查数据的有效性
		if(StringUtils.isBlank(user.getUsername())) {
			return TaotaoResult.build(400, "用户名不能为空");
		}
		//判断用户名是否重复
		TaotaoResult taotaoResult = checkUserData(user.getUsername(),1);
		if(!(boolean)taotaoResult.getData()) {
			return TaotaoResult.build(400, "用户名重复");
		}
		//判断密码是否为空
		if(StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		//判断手机是否为空
		if(StringUtils.isBlank(user.getPhone())) {
			return TaotaoResult.build(400, "手机号码不能为空");
		}
		//判断手机是否重复
		taotaoResult = checkUserData(user.getPhone(),2);
		if(!(boolean)taotaoResult.getData()) {
			return TaotaoResult.build(400, "手机号码重复");
		}
		//邮箱不为空判断是否重复
		if(StringUtils.isNoneBlank(user.getEmail())) {
			taotaoResult = checkUserData(user.getEmail(),3);
			if(!(boolean)taotaoResult.getData()) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}
		//补全pojo属性
		user.setUpdated(new Date());
		user.setCreated(new Date());
		//密码要进行MD5加密
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		//插入数据
		user.setPassword(md5Pass);
		tbUserMapper.insert(user);
		//返回注册成功
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult login(String username, String password) {
		//检查数据的有效性
		if(StringUtils.isBlank(username)) {
			return TaotaoResult.build(400, "用户名不能为空");
		}
		//判断用户名是否存在
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list.size() == 0 || list == null) {
			return TaotaoResult.build(400, "用户名或密码错误！");
		}
		//判断密码是否为空
		if(StringUtils.isBlank(password)) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		TbUser user = list.get(0);
		if(DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			//生成token，使用uuid
			String token = UUID.randomUUID().toString();
			//清空密码
			user.setPassword(null);
			jedisClient.set(USER_SESSION+":"+token, JsonUtils.objectToJson(user));
			//设置key的过期时间
			jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
			return TaotaoResult.ok(token);
		}
		return TaotaoResult.build(400, "用户名或密码错误！");
	}


	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(USER_SESSION+":"+token);
		if(StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "用户未登录或登录已过期");
		}
		//重置session时间
		jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
		//把json转换为user对象
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
	}


	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(USER_SESSION+":"+token, 0);
		return TaotaoResult.ok();
	}

}
