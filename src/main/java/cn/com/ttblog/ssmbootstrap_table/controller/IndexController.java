package cn.com.ttblog.ssmbootstrap_table.controller;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.octo.captcha.service.image.ImageCaptchaService;

import cn.com.ttblog.ssmbootstrap_table.filter.CusAuthenticationFilter;
import cn.com.ttblog.ssmbootstrap_table.model.User;
import cn.com.ttblog.ssmbootstrap_table.service.IUserService;
import cn.com.ttblog.ssmbootstrap_table.util.BeanMapUtil;
import cn.com.ttblog.ssmbootstrap_table.util.POIExcelUtil;

@Controller(value="mainindex")
@RequestMapping("/")
public class IndexController {

	@Resource
	private IUserService userService;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	http://www.tuicool.com/articles/rMzAFj
//	@RequestMapping("/login")
//	public String login(HttpSession session, HttpServletRequest request,
//			HttpServletResponse response, String username, String captcha, String password) {
//		logger.info("进入username:{},pwd:{},session captcha:{},captcha:{}", username, password,
//				imageCaptchaService.getChallengeForID(request.getSession().getId().toString(), request.getLocale()),captcha);
//		/*if(!imageCaptchaService.validateResponseForID(request.getSession().getId(), captcha)){
//			logger.info("验证码未通过!");
//			return "redirect:/index.html";
//		}*/
//		if (username.equals(ConfigConstant.VAL_USERNAME)
//				&& password.equals(ConfigConstant.VAL_PWD)) {
//			session.setAttribute(ConfigConstant.ISLOGIN, true);
//			session.setAttribute(ConfigConstant.USERNAME, username);
//			Cookie c = new Cookie(ConfigConstant.USERNAME, username);
//			c.setMaxAge(86400);
//			response.addCookie(c);
//			return "redirect:/manage.html";
//		} else {
//			return "redirect:/index.html";
//		}
//	}
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(){
		if(SecurityUtils.getSubject().isAuthenticated()){
			return "redirect:/manage.html";
		}
		return "redirect:/index.html";
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public void logout(){
		SecurityUtils.getSubject().logout();
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@RequestParam(value=CusAuthenticationFilter.DEFAULT_USERNAME_PARAM,required=true) String username) {
		logger.info("username:{}", username);
		Subject subject=SecurityUtils.getSubject();
		if(subject.isAuthenticated()){
			logger.debug("登陆成功,跳转管理页面");
			return "redirect:/manage.html";
		}else{
			logger.debug("登陆失败");
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping("/exit")
	public String exit(HttpSession session,HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("用户{}退出系统",session.getAttribute(ConfigConstant.USERNAME));
		//删除cookie
		Cookie cookie = new Cookie(ConfigConstant.USERNAME, null); 
		cookie.setMaxAge(0);
		session.invalidate();
		logger.debug("exit c2:{}",cookie);
		response.addCookie(cookie);
		return "redirect:/index.html";
	}
	
	@RequestMapping("/newdata")
	public String newdata(HttpSession session, Model model) {
		DecimalFormat df = new DecimalFormat("0.00");
		// Display the total amount of memory in the Java virtual machine.
		long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		System.out.println(df.format(totalMem) + " MB");
		// Display the maximum amount of memory that the Java virtual machine
		// will attempt to use.
		long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		System.out.println(df.format(maxMem) + " MB");
		// Display the amount of free memory in the Java Virtual Machine.
		long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		System.out.println(df.format(freeMem) + " MB");
		logger.info("执行前:{}", model);
		int newcount = userService.getNewData();
		String username = session.getAttribute(ConfigConstant.USERNAME)
				.toString();
		model.addAttribute("newcount", newcount);
		model.addAttribute("username", username);
		logger.info("执行后:{}", model);
		return "newdata";
	}

	@RequestMapping("teststr")
	public @ResponseBody String teststr() {
		return "this is str";
	}

	@RequestMapping("/datacount")
	public @ResponseBody Map<String, Object> datacount(HttpSession session,
			Model model) {
		logger.debug("获取datacount");
		List<Map<String, Object>> counts = userService.getDataSum();
		JSONArray categorys = new JSONArray();
		JSONArray nums = new JSONArray();
		for (Map<String, Object> m : counts) {
			categorys.add(m.get("adddate").toString());
			nums.add(m.get("num").toString());
		}
		logger.debug("categorys:{},nums:{}", categorys, nums);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("c", categorys);
		data.put("d", nums);
		return data;
	}

	@RequestMapping("/export")
	public ResponseEntity<byte[]> export(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		List<User> users = userService.getUserList("desc", 10, 0);
		String projectPath = request.getServletContext().getRealPath("export")
				+ File.separator;
		int userCount = users.size();
		List<Map<String, Object>> mps = new ArrayList<Map<String, Object>>(
				users.size());
		for (int i = 0; i < userCount; i++) {
			Map<String, Object> m = BeanMapUtil.transBean2Map(users.get(i));
			mps.add(m);
		}
		logger.info("users:{}", mps);
		List<String> titles = new ArrayList<String>(mps.get(0).size() - 1);
		titles.add("adddate");
		titles.add("age");
		titles.add("deliveryaddress");
		titles.add("id");
		titles.add("name");
		titles.add("phone");
		titles.add("sex");
		String file = projectPath + format.format(new Date()) + "."
				+ ConfigConstant.EXCELSTR;
		logger.info("文件路径:{}", file);
		POIExcelUtil.export(titles, mps, file);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment",
				file.replace(projectPath, ""));
		try {
			return new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(new File(file)), headers,
					HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/testerror")
	public String testthrowException() {
		throw new RuntimeException("test error");
	}

	@ExceptionHandler
	public ModelAndView handleAllException(Exception ex) {
		ModelAndView mav = new ModelAndView("500");
		mav.addObject("errMsg", ex.getMessage());
		return mav;
	}

	/**
	 * 数据转换处理
	 */
	// @InitBinder
	// public void bind(WebDataBinder binder){
	// //设置不转换name，自行处理
	// binder.setDisallowedFields("name");
	// }
}