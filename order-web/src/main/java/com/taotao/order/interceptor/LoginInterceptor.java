package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.Handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
/*
 * 
 * 判断用户是否登录的拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		//ModelAndView返回之后 异常处理

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		//handler执行之后  ModelAndView返回之前

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 执行handler之前执行此方法
		//1、从cookie中取token
		String token = CookieUtils.getCookieValue(request, TT_TOKEN);
		//2、取不到token 跳转到sso登录页面，需要把当前请求的url作为参数传递给sso sso登录成功后跳转回请求的页面
		if(StringUtils.isBlank(token)) {
			//跳转到登录页面 取当前请求的url作为参数传递给sso
			String requestURL = request.getRequestURL().toString();
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			//拦截
			return false;
		}
		//3、取到token 调用sso系统的服务判断用户是否登录 
		TaotaoResult taotaoResult = userService.getUserByToken(token);
		//4、如果用户未登录 即没取到用户信息，跳转到sso页面  需要把当前请求的url作为参数传递给sso sso登录成功后跳转回请求的页面
		if(taotaoResult.getStatus() != 200) {
			String requestURL = request.getRequestURL().toString();
			System.out.println("requestURL"+requestURL);
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			//拦截
			return false;
		}
		//如果取到用户信息 放行 存放在request中
		request.setAttribute("user",(TbUser)taotaoResult.getData());
		//返回true 放行
		return true;
	}

}
