package com.fm.user.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fm.user.model.UserDto;
import com.fm.util.BmiCalc;
import com.fm.util.PointAdd;

public interface UserDao {

	public List<Map<String, Object>> pointHistoryList(int uNo);

	public UserDto userExist(String email, String password);

	public int userInsertOne(UserDto userDto, String address);

	public int bmiInsertOne(BmiCalc bmiCalc);

	public Map<String, Object> userSelectInfo(int uNo);

	public int checkEmail(String email);

	public int addPoint(UserDto userDto, int point);

	public int checkNickName(String nickName);

	public int pointHisoty(PointAdd pointAdd, int point);

	public int myPointChk(int uNo);

	public void userDelete(UserDto userDto) throws Exception;

	public void userUpdate(UserDto userDto, String nickName, String password, int salt);

	public String myNickNameChk(String nickName);

	public String fintUserId(String userPhoneNumber);

	public String resultUserpwd(String userEmail);

	public void userBmiDelete(UserDto userDto);

	public void addRecommendItem(UserDto userDto);

	public int getUserTotalCount(int uNo);

	public List<Map<String, Object>> viewUserList(int uNo, int start, int end);

	public List<Map<String, Object>> viewRecommendItemList(int uNo);

	public List<Map<String, Object>> viewBestItemList();

	public List<Map<String, Object>> viewReviewList();

	public List<Map<String, Object>> viewPointList(int uNo, int start, int end);

}
