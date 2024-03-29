package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
/*
 * 
 * 展示登录和注册页面
 */
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class PageController {

	@RequestMapping("/page/register")
	public String showRegister(){
		return "register";
	}
	
	@RequestMapping("/page/login")
	public String showLogin(String url,Model model){
		System.out.println(url);
		model.addAttribute("redirect",url);
		return "login";
	}
}
