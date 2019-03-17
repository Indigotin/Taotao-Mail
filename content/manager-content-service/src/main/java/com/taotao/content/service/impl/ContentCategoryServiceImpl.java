package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.pojo.TbContentExample;

/**
 * 内容分类管理service
 * @author Len
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<>();
		
		for(TbContentCategory tbContentCategory:list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			resultList.add(node);
		}
		return resultList;
	}
	
	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
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
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
		if(!parent.getIsParent()) {
			//如果父节点为叶子节点需要改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}
	
	
	@Override
	public void updateContentCategory(long id, String name) {
		
		TbContentCategory record = new TbContentCategory();
		record.setName(name);
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		contentCategoryMapper.updateByExampleSelective(record, example);
	}
	
	
	
	
	@Override
	public TaotaoResult deleteContentCategory(long id) {
		deleteContentCategoryTemp(id);
		return TaotaoResult.ok();
	}
	
	public void deleteContentCategoryTemp(long id) {
		
		System.out.println("---当前id = "+id);
		//判断结点状态
		TbContentCategory node = contentCategoryMapper.selectByPrimaryKey(id);
		//是父节点，将此节点下的结点递归删除
		if(node != null && node.getIsParent()) {
			//查询id的子节点列表
			TbContentCategoryExample example = new TbContentCategoryExample();
			//设置查询条件
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			//执行查询
			List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
			System.out.println("-------当前结点的子节点.size = "+list.size());
			for(TbContentCategory temp:list) {
				int i=1;
				System.out.println("id = "+id+" 的第"+i+"个子结点："+temp.toString());
				deleteContentCategory(temp.getId());
			}
		}
		contentCategoryMapper.deleteByPrimaryKey(id);
		System.out.println("已经删除了结点id = "+id);
		//如果父节点已经没有子节点了就更新父节点状态
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(node.getParentId());
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		System.out.println("-------父节点剩余的子结点.size = "+list.size());
		if(list.size() == 0) {
			TbContentCategory record = new TbContentCategory();
			record.setIsParent(false);
			TbContentCategoryExample example1 = new TbContentCategoryExample();
			Criteria criteria1 = example1.createCriteria();
			criteria1.andIdEqualTo(node.getParentId());
			contentCategoryMapper.updateByExampleSelective(record, example1);
			System.out.println("更改父节点 id = "+node.getParentId()+" 的状态");
		}
	}


}
