package com.taotao.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;
/*
 * 
 * 网页静态化处理
 */
@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String genHtml() throws Exception {
		//生成静态页面
		Configuration configuration = freeMarkerConfig.getConfiguration();
		Template template = configuration.getTemplate("hello.ftl");
		Map data = new HashMap<>();
		data.put("hello", "hello freemarker test");
		Writer out = new FileWriter(new File("D:\\eclipse-workspace\\freemarkerOut\\out\\student.html"));
		template.process(data, out);
		out.close();
		//返回结果
		return "OK";
	}
	
}
