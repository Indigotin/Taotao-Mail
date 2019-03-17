package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
/*
 * 
 * 订单确认页面Controller
 */
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
@Controller
public class OrderCartController {
	
	@Value("${CART_KEY}")
	private String CART_KEY;
	
	//展示订单确认页面
	@RequestMapping("order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		//用户必须是登录状态
		//取用户id
		TbUser user = (TbUser)request.getAttribute("user");
		System.out.println(user.getUsername());
		//根据用户信息取收货地址列表，使用静态数据模拟
		//把收货地址列表取出传递给页面
		//从cookie中取商品列表展示到页面(如果实现将购物车信息储存到redis需要从redis中取
		//取购物车商品列表
		List <TbItem> cartList = getCartItemList(request);
		//返回逻辑视图
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	private List<TbItem> getCartItemList(HttpServletRequest request){
		
		//从cookie中取购物车商品列表,需要转码，明文存储不安全
		String json = CookieUtils.getCookieValue(request, CART_KEY, true); 
		if(StringUtils.isBlank(json)) {
			//如果没有内容返回一个空的list
			return new ArrayList<>();
		}
		//不为空的话返回list
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
}
