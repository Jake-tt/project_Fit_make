package com.fm.order.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fm.order.dao.OrderDao;
import com.fm.order.model.CartDto;
import com.fm.order.model.OrderDto;
import com.fm.util.FileUtils;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	public OrderDao orderDao;

	@Override
	public List<Map<String, Object>> viewCartList(int uNo) {
		
		return orderDao.viewCartList(uNo);
	}
	
	@Override
	public int addCart(int uNo, int iNo, int iCount) {
		log.debug("uNo: " + uNo, " iNo : " + iNo, "icount : " + iCount);
		return orderDao.addCart(uNo, iNo, iCount);
	}
	
	@Override
	public int deleteCart(int uNo, int cNo) {
		
		return orderDao.deleteCart(uNo, cNo);
	}
	
	@Override
	public List<Map<String, Object>> viewOrderList(int uNo) {
		
		return orderDao.viewOrderList(uNo);
	}
	
	@Override
	public int addOrder(int uNo) {
		
		return orderDao.addOrder(uNo);
	}

	@Override
	public List<Map<String, Object>> viewOrderDetailItem(int oNo) {
		
		return orderDao.viewOrderDetailItem(oNo);
	}

	@Override
	public Map<String, Object> viewOrderDetailMyInfo(int uNo) {
		return orderDao.viewOrderDetailMyInfo(uNo);
	}
	
	@Override
	public int addOrderDetail(int uNo, int iNo, int iCount, int price) {
		
		return orderDao.addOrderDetail(uNo, iNo, iCount, price);
	}

	@Override
	public int updateCart(CartDto cartDto) {
		
		return orderDao.updateCart(cartDto);
	}

}
