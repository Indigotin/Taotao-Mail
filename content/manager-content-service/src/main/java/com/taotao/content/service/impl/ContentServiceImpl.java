package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;

	@Override
	public List<TbContent> getContentList(long categoryId) {

		//先查询缓存
		//添加缓存不能影响正常业务逻辑
		try {
			
			//查询缓存
			String json = jedisClient.hget(INDEX_CONTENT, categoryId+"");
			//查询到结果，把json转换成list返回
			if(StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//缓存中没有命中 需要查询数据库 
		//执行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = contentMapper.selectByExample(example);
		//把结果添加到缓存
		try {
			jedisClient.hset(INDEX_CONTENT, categoryId+"", JsonUtils.objectToJson(list));
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//返回结果
		return list;
	}
	
	@Override
	public TaotaoResult addContent(TbContent tbContent) {
		tbContent.setCreated( new Date());
		tbContent.setUpdated(new Date());
		contentMapper.insert(tbContent);
		//同步缓存 (删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult updateContent(TbContent tbContent) {
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(tbContent.getId());
		tbContent.setUpdated(new Date());
		contentMapper.updateByExampleSelective(tbContent, example);
		//同步缓存 (删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteContent(String ids) {
		String[] str = ids.split(",");
		for(String temp:str) {
			Long id = Long.parseLong(temp);
			contentMapper.deleteByPrimaryKey(id);
			//同步缓存 (删除对应的缓存信息
			jedisClient.hdel(INDEX_CONTENT, contentMapper.selectByPrimaryKey(id).getCategoryId().toString());
		}
		return TaotaoResult.ok();
	}

}
