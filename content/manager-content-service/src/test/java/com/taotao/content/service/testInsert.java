package com.taotao.content.service;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-*.xml"})
public class testInsert {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Test
	public void testIndsert() {
		
		long parentId = 87;
		String name = "test新建";
		//创建一个pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全对象属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)'
		contentCategory.setStatus(1);
		//排序默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);//id自动更新在contentCategory的id字段中
		System.out.println("contentCategory id = "+contentCategory.getId());
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
		if(!parent.getIsParent()) {
			//如果父节点为叶子节点需要改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		
	}
}
