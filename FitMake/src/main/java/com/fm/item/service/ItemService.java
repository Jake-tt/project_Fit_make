package com.fm.item.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fm.item.model.ItemDto;


public interface ItemService {
	public List<ItemDto> itemSelectList();
	
//	public void itemInsertOne(ItemDto itemDto, MultipartHttpServletRequest mulRequest) throws Exception;
	public void itemInsertOne(ItemDto itemDto, HttpServletRequest hRequest) throws Exception;
	
	public Map<String, Object> itemSelectOne(int no);
	
	public int itemUpdateOne(ItemDto itemDto) throws Exception;
	
	public void itemDeleteOne(int no);
	


}
