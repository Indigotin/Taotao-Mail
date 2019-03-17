package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;


/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name="itemAddtopic")
	private Destination destination;
	@Autowired 
	private JedisClient jedisClient;
	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	@Value("${TIME_EXPIRE}")
	private Integer TIME_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		//查询数据库之前先查询缓存
		try {
			
			String json = jedisClient.get(ITEM_INFO+":"+itemId+":BASE");
			if(StringUtils.isNotBlank(json)) {
				TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
				return item;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有去查询数据库
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		//把查询结果添加到缓存
		try {
			jedisClient.set(ITEM_INFO+":"+itemId+":BASE", JsonUtils.objectToJson(item));
			//设置过期时间 提高缓存利用率
			jedisClient.expire(ITEM_INFO+":"+itemId+":BASE", TIME_EXPIRE);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		System.out.println(list.size());
		//返回结果
		return result;
	
	}

	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成商品ID
		long itemId = IDUtils.genItemId();
		
		System.out.println(itemId);
		
		//补全item的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		
		
		//向商品表插入数据
		itemMapper.insert(item);
		//System.out.println("向商品表插入数据"+itemMapper.insert(item));
		
		//创建一个商品描述表对应的pojo
		TbItemDesc itemDesc = new TbItemDesc();
		//补全pojo属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		itemDesc.setCreated(new Date());
		
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//System.out.println("insert(itemDesc):"+itemDescMapper.insert(itemDesc));
		//System.out.println("selectByPrimaryKey(itemId).getTitle()"+itemMapper.selectByPrimaryKey(itemId).getTitle());
		
		//向activemq发送商品添加消息
		jmsTemplate.send(destination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message =  session.createTextMessage(itemId+"");
				return message;
			}
		});
		//返回结果
		return TaotaoResult.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//查询数据库之前先查询缓存
		try {
			String json = jedisClient.get(ITEM_INFO+":"+itemId+":DESC");
			if(StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有去查询数据库
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//把查询结果添加到缓存
		try {
			jedisClient.set(ITEM_INFO+":"+itemId+":DESC", JsonUtils.objectToJson(tbItemDesc));
			//设置过期时间 提高缓存利用率
			jedisClient.expire(ITEM_INFO+":"+itemId+":DESC", TIME_EXPIRE);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tbItemDesc;
	}

}
