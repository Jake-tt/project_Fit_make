package com.fm.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fm.user.model.UserDto;
import com.fm.user.service.UserService;
import com.fm.util.BmiCalc;
import com.fm.util.Paging;
import com.fm.util.PointAdd;

//어노테이션 드리븐
@Controller
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	/**
	 * index.jsp에서 로그인 페이지로 이동 나중에 없앨듯
	 * 
	 * @return LoginForm.jsp로 이동
	 */
	@RequestMapping(value = "/auth/login.do", method = RequestMethod.GET)
	public String login() {
		logger.info("Welcome UserController login! ");

		return "auth/LoginForm";
	}

	/**
	 * 
	 * @param email    사용자가 입력한 email값
	 * @param password 사용자가 입력한 password값
	 * @param model    alret를 하기 위해 model 사용
	 * @param session  세션에 userDto정보를 담는다 view페이지에서 현재 세션정보를 찾기 위함
	 * @return 가입된 회원 -> 메인페이지, 가입되지 않은 회원 -> 로그인실패 alert(이동후 다시 로그인페이지)
	 */
	@RequestMapping(value = "/auth/loginCtr.do", method = RequestMethod.POST)
	public String loginCtr(HttpSession session, Model model, String email, String password) throws Exception {
		logger.info("Welcome UserController loginCtr! " + email);

		try {
			UserDto userDto = userService.userExist(email, password);
			String viewUrl = "";
			// 회원 확인
			if (userDto == null) {
				model.addAttribute("msg", "아이디 또는 비밀번호가 잘못되었습니다");
				model.addAttribute("url", "../auth/login.do");
				return "auth/LoginFail";
			} else {
				session.setAttribute("_userDto_", userDto);
				viewUrl = "redirect:/main/main.do";
				return viewUrl;
			}
		} catch (Exception e) {
			model.addAttribute("msg", "등록되지 않은 정보입니다.");
			model.addAttribute("url", "../auth/login.do");

			return "common/UserExistAlret";
		}

	}

	/**
	 * 
	 * @param userDto
	 * @param model
	 * @param bmiCalc
	 * @return 회원가입 userDto정보와 bmiCalc정보 DB에 기입
	 */
	@RequestMapping(value = "/user/addCtr.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String userAdd(UserDto userDto, Model model, BmiCalc bmiCalc, String add_1st, String add_Extra,
			String add_Detail) {
		logger.info("Welcome UserController userAdd 신규등록 처리! " + userDto);
		
		try {
			String address = add_1st + add_Extra + add_Detail;
			int salt = userDto.addSalt();
			String password = userDto.setHashpwd(salt, userDto.getPassword());
			userDto.setSalt(salt);
			userDto.setPassword(password);
			userService.userInsertOne(userDto, address);
			userService.bmiInsertOne(bmiCalc);
		
			model.addAttribute("msg", "가입이 완료되었습니다!");
			model.addAttribute("url", "../auth/login.do");
			return "common/UserJoinSuccess";
		
		} catch (Exception e) {
			
			return null;
		}
	}

	/**
	 * 
	 * @return 아이디 찾기 view
	 */
	@RequestMapping(value = "/user/findId.do")
	public String viewFindId() {

		return "/user/FindId";
	}

	/**
	 * 
	 * @return 비밀번호 찾기 view
	 */
	@RequestMapping(value = "/user/findPassword.do")
	public String viewFindpwd() {

		return "/user/FindPassword";
	}

	/**
	 * 
	 * @return 회원탈퇴 view
	 */
	@RequestMapping(value = "/user/userDelete.do")
	public String viewDelete(HttpSession session) {

		return "/user/UserDelete";
	}

	/**
	 * 
	 * @param uNo
	 * @param password
	 * @return 회원탈퇴 기능
	 */
	@RequestMapping(value = "/user/deleteCtr.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String userDelete(UserDto userDto, HttpSession session, RedirectAttributes ra) throws Exception {
		logger.info("회원탈퇴 진행");

		UserDto uPwd = (UserDto) session.getAttribute("_userDto_");

		String oldPwd = uPwd.getPassword();
		String sessionPwd = userDto.getPassword();

		String existPwd = userDto.setHashpwd(uPwd.getSalt(), sessionPwd);
		if (existPwd.equals(oldPwd)) {
			userService.userBmiDelete(userDto);
			userService.userDelete(userDto);
			session.invalidate();
			return "redirect:/auth/login.do";
		} else {
			ra.addFlashAttribute("msg", false);
			return "redirect:/user/userDelete.do";
		}
	}

	/**
	 * 헤더 메인로고 클릭 시 로그인 상태 -> 메인페이지, 비로그인 상태 -> 로그인페이지
	 * 
	 * @param session 회원정보 유무상태를 확인
	 * @return 로그인상태 -> MainPage.jsp, 비로그인 상태 -> LoginForm.jsp
	 */
	@RequestMapping(value = "/main/main.do", method = RequestMethod.GET)
	public String main(HttpSession session, Model model) {
		logger.info("메인로고 클릭! ");
		
		
		String viewPage = "";
		if (session.getAttribute("_userDto_") != null) {
			UserDto userDto = (UserDto) session.getAttribute("_userDto_");
			int uNo = userDto.getuNo();
			
			List<Map<String, Object>> mainRecommendItemList = userService.viewRecommendItemList(uNo);
			List<Map<String, Object>> mainBestItemList = userService.viewBestItemList();
			List<Map<String, Object>> mainReviewList = userService.viewReviewList();
			
			model.addAttribute("mainRecommendItemList", mainRecommendItemList);
			model.addAttribute("mainBestItemList", mainBestItemList);
			model.addAttribute("mainReviewList", mainReviewList);
			
			viewPage = "main/MainPage";
		} else if (session.getAttribute("_userDto_") == null) {
			viewPage = "redirect:/auth/login.do";
		}
		return viewPage;
	}

	/**
	 * 
	 * @param session.invalidate() 를 사용해서 세션 종료
	 * @return 로그인페이지로 다시 이동
	 */
	@RequestMapping(value = "/auth/logout.do", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		logger.info("Welcome UserController logout! ");

		session.invalidate();

		return "redirect:/auth/login.do";
	}

	/**
	 * 회원가입 페이지로 이동
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/add.do", method = RequestMethod.GET)
	public String userAdd(Model model) {
		logger.debug("Welcome UserController memberAdd! ");

		return "/user/JoinForm";
	}

	/**
	 * 
	 * @param email 이메일 중복체크를 위한 밸류값
	 * @ResponseBody View 페이지가 아닌 응답값 그대로 반환하기 위해 사용
	 * @return
	 */
	@RequestMapping(value = "/user/emailCheck.do", method = RequestMethod.POST)
	@ResponseBody
	public String checkEmail(@RequestParam("emailChk") String email) {

		String result = "N";
		int flag = userService.checkEmail(email);
		// 이메일이 있을시 Y 없을시 N 으로 회원가입페이지로 보냄
		if (flag == 1)
			result = "Y";

		return result;
	}

	/**
	 * 
	 * @param nickName 닉네임 중복체크를 위한 값
	 * @ResponseBody View 페이지가 아닌 응답값 그대로 반환하기 위해 사용
	 * @return
	 */
	@RequestMapping(value = "/user/nickNameChk.do", method = RequestMethod.POST)
	@ResponseBody
	public String checkNickName(@RequestParam("nickNameChk") String nickName) {

		String result = "N";
		int flag = userService.checkNickName(nickName);
		// 닉네임이 있을시 Y 없을시 N 으로 회원가입페이지로 보냄
		if (flag == 1)
			result = "Y";

		return result;
	}

	/**
	 * 
	 * @param userPhoneNumber 수신번호
	 * @ResponseBody View 페이지가 아닌 응답값 그대로 반환하기 위해 사용
	 * @return
	 */
	@RequestMapping(value = "/user/phoneCheck.do", method = RequestMethod.GET)
	@ResponseBody
	public String sendSMS(@RequestParam("phone") String userPhoneNumber) { // 휴대폰 문자보내기
		int randomNumber = (int) ((Math.random() * (9999 - 1000 + 1)) + 1000);// 난수 생성

		userService.certifiedPhoneNumber(userPhoneNumber, randomNumber);

		System.out.println(randomNumber);
		return Integer.toString(randomNumber);
	}

	/**
	 * 
	 * @param userPhoneNumber 입력한 핸드폰 번호
	 * @return userPhoneNumber로 DB를 통해 EMAIL값을 가져옴 (EMAIL찾기)
	 */
	@RequestMapping(value = "/user/resultFindId.do", method = RequestMethod.POST)
	@ResponseBody
	public String resultId(@RequestParam("phone") String userPhoneNumber) {

		String result = userService.fintUserId(userPhoneNumber);

		return result;
	}

	@RequestMapping(value = "/user/resultFindPwd.do", method = RequestMethod.POST)
	@ResponseBody
	public String resultPwd(@RequestParam("email") String userEmail) {

		String result = userService.resultUserpwd(userEmail);

		return result;
	}

	/**
	 * 
	 * @param session
	 * @return 헤더 포인트 정보 확인
	 */
	@RequestMapping(value = "/user/pointChk.do", method = RequestMethod.GET)
	@ResponseBody
	public int myPointChk(HttpSession session) {

		UserDto userDto = (UserDto) session.getAttribute("_userDto_");

		if (userDto == null) {
			int myPointChk = 0;
			return myPointChk;
		} else {
			int uNo = (int) userDto.getuNo();

			int myPointChk = userService.myPointChk(uNo);
			return myPointChk;
		}

	}

	/**
	 * 
	 * @param session
	 * @return 헤더 닉네임 정보 확인
	 */
	@RequestMapping(value = "/user/nickNameChk.do", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String myNickNameChk(HttpSession session) {

		UserDto userDto = (UserDto) session.getAttribute("_userDto_");

		if (userDto == null) {
			String myNicknameChk = "";
			return myNicknameChk;
		} else {
			String nickName = userDto.getNickName();

			String myNicknameChk = userService.myNickNameChk(nickName);
			return myNicknameChk;
		}
	}

	/**
	 * 
	 * @param model
	 * @return 내정보 보기
	 */
	@RequestMapping(value = "/user/Info.do")
	public String userInfo(Model model, HttpSession session, @RequestParam(defaultValue = "1") int curPage) {

		UserDto userdto = new UserDto();

		userdto = (UserDto) session.getAttribute("_userDto_");
		logger.info("Welcome UserController userInfo enter! - {}", userdto.getuNo());

		Map<String, Object> myInfomap = userService.userSelectInfo(userdto.getuNo());
		model.addAttribute("myInfomap", myInfomap);
		
		int uNo = (int) userdto.getuNo();
		
		int totalCount = userService.getUserTotalCount(uNo);
		
		Paging userPaging = new Paging(totalCount, curPage);
		
		int start = userPaging.getPageBegin();
		int end = userPaging.getPageEnd();
		
		List<Map<String, Object>> userMapList = userService.viewUserList(uNo, start, end);
		
		Map<String, Object> uPagingMap = new HashMap<String, Object>();
		uPagingMap.put("userPaging", userPaging); 
		uPagingMap.put("totalCount",totalCount);
		uPagingMap.put("start", start);
		uPagingMap.put("end", end);
		
		model.addAttribute("userMapList", userMapList);
		model.addAttribute("uPagingMap", uPagingMap);
		String viewUrl = "";
		
		if(uNo == 1) {
			viewUrl = "user/UserManage";
		} else {
			viewUrl = "user/UserMyInfo";
		}
		
		return viewUrl;
	}

	/**
	 * 
	 * @param userDto
	 * @param model
	 * @param session
	 * @param nickName
	 * @param newpassword
	 * @return 회원정보 수정 기능
	 */
	@RequestMapping(value = "/user/userUpdate.do", method = RequestMethod.POST)
	public String userUpdate(UserDto userDto, Model model, HttpSession session, String nickName, String newpassword) {
		logger.info("회원정보 수정", userDto);

		UserDto uPwd = (UserDto) session.getAttribute("_userDto_");

		String existingPwd = uPwd.getPassword();
		String sessionPwd = userDto.getPassword();

		if (!(sessionPwd.equals(existingPwd))) {
			model.addAttribute("msg", "기존 비밀번호와 일치하지 않습니다.");
			model.addAttribute("url", "redirect:/user/Info.do");

			return "common/UpdateAlert";
		}
		int salt = userDto.addSalt();
		String password = userDto.setHashpwd(salt, newpassword);

		userService.userUpdate(userDto, nickName, password, salt);

		return "redirect:/user/Info.do";
	}

	/**
	 * 
	 * @return 충전버튼 누를시 -> 포인트충전 팝업창 실행
	 */
	@RequestMapping(value = "/user/point.do")
	public String pointView() {
		;
		logger.info("포인트 페이지로 갑니다");

		return "/user/PointPopup";
	}

	/**
	 * 
	 * @param point    사용자가 충전할 포인트 값
	 * @param pointAdd DB에 저장할 충전값 (history)
	 * @param session
	 * @return 포인트 충전 기능
	 */
	@RequestMapping(value = "/user/pointAdd.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public int pointAdd(@RequestParam("priceSelect") int point, PointAdd pointAdd, HttpSession session) {
		logger.info("포인트 충전 - {}", point);

		UserDto userdto = new UserDto();
		userdto = (UserDto) session.getAttribute("_userDto_");
		pointAdd.setuNo(userdto.getuNo());

		userService.addPoint(pointAdd, point);
		userService.pointHistory(pointAdd, point);

		return 1;
	}

	/**
	 * 
	 * @return 충전/사용내역 View
	 */
	@RequestMapping(value = "/user/pointHistory.do")
	public String viewHistory(HttpSession session, Model model, @RequestParam(defaultValue = "1") int curPage) {
		logger.info("충전내역");
		UserDto userDto = (UserDto) session.getAttribute("_userDto_");
		int uNo = (int) userDto.getuNo();
		
		
		int totalCount = userService.getUserTotalCount(uNo);
		
		Paging userPaging = new Paging(totalCount, curPage);
		
		int start = userPaging.getPageBegin();
		int end = userPaging.getPageEnd();
		
		List<Map<String, Object>> pointList = userService.pointHistoryList(uNo, start, end);
		
		model.addAttribute("pointList", pointList);
		
		List<Map<String, Object>> userMapList = userService.viewUserList(uNo, start, end);
		
		List<Map<String, Object>> pointManage = userService.viewPointList(uNo, start, end);
		
		model.addAttribute("pointManage", pointManage);
		
		Map<String, Object> uPagingMap = new HashMap<String, Object>();
		uPagingMap.put("userPaging", userPaging); 
		uPagingMap.put("totalCount",totalCount);
		uPagingMap.put("start", start);
		uPagingMap.put("end", end);
		
		model.addAttribute("userMapList", userMapList);
		model.addAttribute("uPagingMap", uPagingMap);
		
		
		String viewUrl = "";
		
		if(uNo == 1) {
			viewUrl = "user/PointManage";
		} else {
			viewUrl = "user/PointRechargehistory";
		}
		
		return viewUrl;
		
	}

}
