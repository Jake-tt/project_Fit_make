package com.fm.order.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fm.order.model.CartDto;
import com.fm.order.model.OrderDto;
import com.fm.user.model.UserDto;

public interface OrderDao {

	public List<Map<String, Object>> viewCartList(int uNo);
	
	public int addCart(int uNo, int iNo, int iCount);
	
	public int deleteCart(int uNo, int cNo);

	public List<Map<String, Object>> viewOrderList(int uNo);

	public int addOrder(int uNo);

	public List<Map<String, Object>> viewOrderDetailItem(int oNo);

	public Map<String, Object> viewOrderDetailMyInfo(int uNo);

	public int addOrderDetail(int uNo, int iNo, int iCount, int price);

	public int updateCart(CartDto cartDto);
	
}
