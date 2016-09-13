<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加用户</title>
<script src="http://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
<style type="text/css">
.bar { height: 18px; background: green; }
</style>
<script type="text/javascript">
	$(function(){
	});
</script>	
</head>
<body>
	<c:if test="${not empty msg}"><p style="text-align:center;">${msg}</p></c:if>
	<form action="${pageContext.request.contextPath }/user/add" method="post">
			<div class="weui_cell">
				<div class="weui_cell_hd">
					<label class="weui_label">昵称</label>
				</div>
				<div class="weui_cell_bd weui_cell_primary">
					<input type="text" name="name" placeholder="请输入昵称" class="weui_input">
				</div>
			</div>
			<div class="weui_cell weui_vcode">
				<div class="weui_cell_hd">
					<label class="weui_label">年龄</label>
				</div>
				<div class="weui_cell_bd weui_cell_primary">
					<input type="number" name="age" placeholder="请输入年龄" class="weui_input">
				</div>
			</div>
			<div class="weui_cell weui_vcode">
				<div class="weui_cell_hd">
					<label class="weui_label">密码</label>
				</div>
				<div class="weui_cell_bd weui_cell_primary">
					<input type="password" name="password" placeholder="请输入密码" class="weui_input">
				</div>
			</div>
			<div class="weui_cells_title">性别</div>
			<div class="weui_cells weui_cells_radio">
	            <label for="x11" class="weui_cell weui_check_label">
	                <div class="weui_cell_bd weui_cell_primary">
	                    <p>男</p>
	                </div>
	                <div class="weui_cell_ft">
	                    <input type="radio" value="男" id="x11" name="sex" class="weui_check">
	                    <span class="weui_icon_checked"></span>
	                </div>
	            </label>
	            <label for="x12" class="weui_cell weui_check_label">
	                <div class="weui_cell_bd weui_cell_primary">
	                    <p>女</p>
	                </div>
	                <div class="weui_cell_ft">
	                    <input type="radio" value="女" checked="checked" id="x12" name="sex"  class="weui_check">
	                    <span class="weui_icon_checked"></span>
	                </div>
	            </label>
	             <label for="x13" class="weui_cell weui_check_label">
	                <div class="weui_cell_bd weui_cell_primary">
	                    <p>中性</p>
	                </div>
	                <div class="weui_cell_ft">
	                    <input type="radio" value="中性" id="x13" name="sex" class="weui_check">
	                    <span class="weui_icon_checked"></span>
	                </div>
	            </label>
	        </div>
			<div class="weui_cell">
				<div class="weui_cell_hd">
					<label class="weui_label">手机号</label>
				</div>
				<div class="weui_cell_bd weui_cell_primary">
					<input type="tel" placeholder="请输入手机号" name="phone"  class="weui_input">
				</div>
			</div>
			<div class="weui_cells_title">收获地址</div>
			<div class="weui_cell">
	            <div class="weui_cell_bd weui_cell_primary">
	                <textarea rows="3" placeholder="请输入收获地址" name="deliveryaddress" class="weui_textarea"></textarea>
	                <div class="weui_textarea_counter"><span>0</span>/200</div>
	            </div>
	        </div>
	        
	        <div id="files">
			    <input type="file" name="file" id="fileupload">
			    <img src=""/>
			</div>
			<div id="progress"> <div class="bar" style="width: 0%;"></div> </div>
			
<!-- 	        <input name="img" value="img1"> -->
<!-- 	        <input name="img" value="img2"> -->
<!-- 	        <input name="img" value="img3"> -->
	        <div class="weui_btn_area">
		        <button type="submit" class="weui_btn weui_btn_primary">注册</button>
		    </div>
   </form>
</body>
</html>