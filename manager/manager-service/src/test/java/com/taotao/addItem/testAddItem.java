package com.taotao.addItem;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.utils.IDUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public class testAddItem {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Test
	public void addItem() {
	
		
		System.out.println(itemMapper.toString());
		
		TbItem item = new TbItem();
		
		item.setNum(11);
		item.setTitle("testtest");
		item.setPrice((long) 111);
		item.setSellPoint("testtesttesttesttesttestse");
		String desc="sdfsdds";
		//生成商品ID
		long itemId = IDUtils.genItemId();
		
		System.out.println(itemId);
		
		//补全item的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		item.setCid((long) 4);
		item.setImage("  ");
		item.setBarcode("dvds");
		System.out.println("item"+item==null);
		System.out.println("itemMapper"+itemMapper==null);
		//向商品表插入数据

		//itemMapper.insert(item);
		
		//System.out.println("向商品表插入数据"+itemMapper.insert(item));
		
		
		
		//创建一个商品描述表对应的pojo
		TbItemDesc itemDesc = new TbItemDesc();
		//补全pojo属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		itemDesc.setCreated(new Date());
		//向商品描述表插入数据
		System.out.println(itemDescMapper.insert(itemDesc));
		//返回结果
		//return TaotaoResult.ok();
	}
}
