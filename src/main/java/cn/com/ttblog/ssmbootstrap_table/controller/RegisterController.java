package cn.com.ttblog.ssmbootstrap_table.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.ttblog.ssmbootstrap_table.exception.CustomGenericException;
import cn.com.ttblog.ssmbootstrap_table.model.User;
import cn.com.ttblog.ssmbootstrap_table.model.UserListModel;
import cn.com.ttblog.ssmbootstrap_table.service.IUserService;
import cn.com.ttblog.ssmbootstrap_table.validator.UserValidator;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@Resource
	private IUserService userService;
	
	@Autowired(required=false)  
    private Validator validator;  
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * springmvc数据校验 需要为实体加@Validated标记 
	 * @param binder
	 */
//	@InitBinder
//	public void initBinder(DataBinder binder) {
//		binder.setValidator(new UserValidator());
//	}

	@RequestMapping(value = { "", "/", "/index" })
	public String index() {
		return "redirect:/registerinfo.html";
	}

	@RequestMapping("/save")
	public String save(User user,BindingResult result,Model model) {
		validator.validate(user, result);
		logger.warn("校验结果:{}",result);
		if(result.hasErrors()){
			logger.info("校验user出错:"+ToStringBuilder.reflectionToString(result));
			model.addAttribute("result", result);
			return "500";
		}
		user.setAdddate((int) (System.currentTimeMillis() / 1000));
		try {
			userService.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/register-error.html";
		}
		return "redirect:/register-success.html";
	}
	
	@RequestMapping("/saves")
	public String save(UserListModel users) {
		logger.debug("user lists-size:{}",users.getUsers().size());
		for(User u:users.getUsers()){
			logger.debug("userinfo:{}",u);
			userService.addUser(u);
		}
		return "redirect:/register-success.html";
	}
	
	@RequestMapping("/savesjson")
	public String savesjson(@RequestBody List<User> users) {
		logger.debug("user lists-size:{}",users.size());
		for(User u:users){
			logger.debug("userinfo:{}",u);
			userService.addUser(u);
		}
		return "redirect:/register-success.html";
	}
	
	@RequestMapping("/testerror1")
	public String testError(){
		throw new CustomGenericException(500,"错误测试");
	}
	
	@RequestMapping("/testerror2")
	public String testError2(){
		int i=1/0;
		return "index";
	}
	@RequestMapping("/testredirect")
	public String testRedirect(Model model){
		model.addAttribute("param", "test");
		return "redirect:/register-success.html?id={param}";
	}
}