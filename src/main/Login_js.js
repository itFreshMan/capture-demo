(function() {
	var c = null;
	var d;
	jQuery
			.extend({
				initLoginForm : function() {
					d = $("#loginForm")
							.validate(
									{
										wrapper : "li",
										onkeyup : false,
										rules : {
											"loginUserDTO.user_name" : {
												requiredUserName : true,
												checkLoginUserName : true
											},
											"userDTO.password" : {
												required : true,
												minlength : 6
											}
										},
										messages : {
											"loginUserDTO.user_name" : {
												requiredUserName : login_messages.userNameEmpty,
												checkLoginUserName : login_messages.userNameFormat
											},
											"userDTO.password" : {
												required : login_messages.passwordEmpty,
												minlength : login_messages.passwordLength
											}
										},
										submitHandler : function(b) {
											var a = dhtmlx
													.modalbox({
														targSrc : '<div style="z-index: 20000; position: fixed; left: 750.5px; top: 237px;"><img src="'
																+ ctx
																+ 'resources/images/loading.gif"></img></div>'
													});
											$(b)
													.ajaxSubmit(
															{
																url : ctx
																		+ "login/loginAysnSuggest",
																type : "post",
																async : false,
																success : function(
																		f) {
																	if (f.status) {
																		if (f.data.loginCheck == "Y") {
																			$
																					.submitLogin()
																		} else {
																			if ("Y" == openRandCodeCheck) {
																				refreshImg(
																						"login",
																						"sjrand");
																				$(
																						"#randCode")
																						.val(
																								"")
																			}
																			dhtmlx.modalbox
																					.hide(a);
																			$(
																					"#password")
																					.val(
																							"");
																			return false
																		}
																	}
																}
															})
										},
										errorPlacement : function(b, a) {
										}
									});
					$("#loginSub").on("click", function(a) {
						$("#loginForm").submit()
					})
				},
				verifyLoginUser : function(b) {
					if (b == "用户名／邮箱" || "" == b || null == b) {
						return login_messages.userNameEmpty
					}
					var a = /^[A-Za-z]{1}([A-Za-z0-9]|[_]){0,29}$/;
					var f = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
					if (!a.test(b) && !f.test(b)) {
						return login_messages.userNameFormat
					}
					return true
				},
				verifyLoginPassword : function(b) {
					var a = true;
					if ("" == b || null == b) {
						a = login_messages.passwordEmpty
					} else {
						if (6 > b.length) {
							a = login_messages.passwordLength
						}
					}
					return a
				},
				onBlurCheck : function() {
					var b = false;
					var a = false;
					$("#username").on("blur", function() {
						var g = $("#username").val();
						b = $.verifyLoginUser(g);
						var h = $("#randCode")[0];
						if (typeof (b) !== "boolean") {
							showError(h, b)
						} else {
							if (b === true) {
								showError(h).hide()
							}
						}
					});
					$("#password").on("blur", function() {
						var i = $("#password").val();
						var j = $("#randCode")[0];
						var h = $.verifyLoginPassword(i);
						if (b === true && typeof (h) !== "boolean") {
							showError(j, h)
						} else {
							if (h === true) {
								showError(j).hide()
							}
						}
					})
				},
				loginClick : function() {
					$("#loginSub")
							.on(
									"click",
									function(m) {
										var b = $("#username").val();
										var k = $("#password").val();
										var a = $("#randCode").val();
										if ("undefined" == typeof (submitForm)) {
										} else {
											submitForm()
										}
										var n = $.verifyLoginUser(b);
										var e = typeof (n) === "boolean" ? $
												.verifyLoginPassword(k) : n;
										if ("Y" == openRandCodeCheck
												&& !verifyRandCode(
														$("#randCode")[0], e)) {
											return
										}
										var l = dhtmlx
												.modalbox({
													targSrc : '<div style="z-index: 20000; position: fixed; left: 750.5px; top: 237px;"><img src="'
															+ ctx
															+ 'resources/images/loading.gif"></img></div>'
												});
										$("#loginForm")
												.ajaxSubmit(
														{
															url : ctx
																	+ "login/loginAysnSuggest",
															type : "post",
															async : false,
															success : function(
																	f) {
																if (f.status) {
																	if (f.data.loginCheck == "Y") {
																		$
																				.submitLogin()
																	} else {
																		if ("Y" == openRandCodeCheck) {
																			refreshImg(
																					"login",
																					"sjrand");
																			$(
																					"#randCode")
																					.val(
																							"")
																		}
																		dhtmlx.modalbox
																				.hide(l);
																		$(
																				"#password")
																				.val(
																						"");
																		return false
																	}
																}
															}
														})
									})
				},
				forgetMyPassword : function() {
					$("#forget_password_id")
							.on(
									"click",
									function(a) {
										otsRedirect(
												"post",
												ctx
														+ "forgetPassword/initforgetMyPassword")
									})
				},
				sendCheck : function() {
					$("#send_check").on("click", function(a) {
						$.ajax({
							url : ctx + "login/sendCheck",
							type : "post",
							data : {
								mobileNo : $("#mobileNo").val()
							},
							async : false,
							success : function(b) {
							}
						})
					})
				},
				submitLogin : function() {
					otsRedirect("post", ctx + "login/userLogin")
				},
				alertErrorMsg : function(b, a) {
					dhtmlx.alert({
						title : messages["message.error"],
						ok : messages["button.ok"],
						text : a,
						type : "alert-error",
						callback : function() {
							if (b) {
								obj.focus()
							}
						}
					})
				},
				showCheckMobileDialog : function() {
					var a = dhtmlx.createWin({
						winId : "checkMobile",
						closeWinId : [ "back_edit" ],
						callback : function() {
						},
						okId : "qr_submit",
						okCallBack : function() {
							$.submitLogin()
						},
						checkConfirm : function() {
							$.ajax({
								url : ctx + "login/checkMobile",
								type : "post",
								data : {
									mobileNo : $("#mobileNo").val(),
									valueCheck : $("#checkCode").val()
								},
								async : false,
								success : function(g) {
									if (g.status) {
										var h = g.data.msg;
										var b = g.data.checkFlag;
										if (!b) {
											$("#msg_error").html(h);
											return false
										} else {
											alert("成功!");
											return true
										}
									}
								}
							});
							return false
						}
					});
					$(".dhx_modal_cover").css("background-color", "#EEEEEE")
				},
				styleSet : function() {
					$("#username").css("color", "#333");
					$("#password").css("color", "#333");
					$("#randCode").css("color", "#333");
					if ($("#username").val() == ""
							|| $("#username").val() == "用户名／邮箱"
							|| $("#username").val() == null) {
						$("#username").css("color", "#999");
						$("#username").val("用户名／邮箱")
					}
					$("#username").focus(function() {
						var a = $("#username").val();
						if (a == "用户名／邮箱") {
							$("#username").css("color", "#333");
							$("#username").val("")
						}
					}).blur(function() {
						var a = $("#username").val();
						if (a == "") {
							$("#username").css("color", "#999");
							$("#username").val("用户名／邮箱")
						}
					})
				}
			});
	$(document).ready(function() {
		if ("undefined" != typeof (activeSuc)) {
			if ("Y" == activeSuc) {
				dhtmlx.createWin({
					winId : "dialog_active_succ",
					closeWinId : [ "dialog_active_close" ],
					okId : "dialog_active_ok",
					okCallBack : function() {
					}
				})
			}
		}
		if ("undefined" != typeof (resetPwdSucFlag)) {
			if ("Y" == resetPwdSucFlag) {
				dhtmlx.createWin({
					winId : "dialog_restPwd_succ",
					closeWinId : [ "dialog_restPwd_close" ],
					okId : "dialog_restPwd_ok",
					okCallBack : function() {
					}
				})
			}
		}
		if ("undefined" != typeof (noticeSessionCollect)) {
			if ("Y" == noticeSessionCollect) {
				dhtmlx.createWin({
					winId : "dialog_sessionCollect",
					closeWinId : [ "dialog_sessionCollect_close" ],
					okId : "dialog_sessionCollect_ok",
					okCallBack : function() {
					}
				})
			}
		}
		$.onBlurCheck();
		$.loginClick();
		$.styleSet();
		$.forgetMyPassword()
	})
})();
function showInfoMsg(b) {
	dhtmlx.alert({
		title : messages["message.info"],
		ok : messages["button.ok"],
		text : b,
		type : "alert-error",
		callback : function() {
		}
	})
};
jQuery.validator
		.addMethod(
				"checkLoginUserName",
				function(j, g) {
					var i = false;
					var h = /^[A-Za-z]{1}([A-Za-z0-9]|[_]){0,29}$/;
					var f = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
					if (h.test(j) || f.test(j)) {
						i = true
					}
					return this.optional(g) || i
				}, "wrong username.");
jQuery.validator.addMethod("requiredUserName", function(c, d) {
	if ("用户名／邮箱" == c) {
		return false
	}
	if (c == null || "" == c) {
		return false
	}
	return true
}, "wrong username.");
jQuery.validator.addMethod("requiredSchoolName", function(c, d) {
	if ("简码／汉字" == c) {
		return false
	}
	if (c == null || "" == c) {
		return false
	}
	return true
}, "wrong schoolname.");
jQuery.validator.addMethod("randCodeRequired", function(c, d) {
	$("#i-ok").css("display", "none");
	return c.length > 0
}, "验证码错误!");
jQuery.validator.addMethod("randCodeFormat", function(e, f) {
	$("#i-ok").css("display", "none");
	var d = /^[a-zA-Z0-9]+$/;
	return this.optional(f) || d.test(e)
}, "验证码错误!");
jQuery.validator.addMethod("randCodeLength", function(c, d) {
	$("#i-ok").css("display", "none");
	return c.length == 4
}, "验证码错误!.");
jQuery.validator.addMethod("integrationCheck", function(f, d) {
	var e = /^\d{6}$/;
	return this.optional(d) || e.test(f)
}, "wrong integrationpassword");
jQuery.validator.addMethod("integrationPwdCheck", function(f, d, e) {
	if ($("#" + e[0]).val() == $("#" + e[1]).val()) {
		return true
	}
	return false
}, "两次输入密码不一致!.");
jQuery.validator.addMethod("checkRandCode", function(f, g, e) {
	var h = true;
	if (f && f.length == 4) {
		$.ajax({
			url : ctx + "passcodeNew/checkRandCodeAnsyn",
			type : "post",
			data : {
				randCode : f,
				rand : e
			},
			async : false,
			success : function(a) {
				if (a.data == "N") {
					h = false;
					$("#i-ok").css("display", "none")
				} else {
					h = true;
					$("#i-ok").css("display", "block")
				}
			}
		})
	} else {
		h = false;
		$("#i-ok").css("display", "none")
	}
	return h
}, "验证码错误!.");
jQuery.validator.addMethod("validateUsersName", function(c, d) {
	return this.optional(d) || /^[A-Za-z]{1}([A-Za-z0-9]|[_]){0,29}$/.test(c)
}, "用户名只能由字母、数字或_组成");
jQuery.validator.addMethod("checkWriteSpace", function(e, f) {
	for ( var d = 0; d < e.length; d++) {
		if (e.charCodeAt(d) == 32) {
			return false
		}
	}
	return true
}, "contain writespace");
jQuery.validator.addMethod("validateRandCode", function(c, d) {
	return this.optional(d) || /^[a-zA-Z0-9]+$/.test(c)
}, "验证码错误!.");
jQuery.validator.addMethod("checkPassward", function(g, h, j) {
	var f = true;
	for ( var i = 0; i < g.length; i++) {
		if (g.charCodeAt(i) == 39 || g.charCodeAt(i) == 60
				|| g.charCodeAt(i) == 62) {
			f = false
		}
		if (!f) {
			break
		}
	}
	return this.optional(h) || f
}, "Passward wrong");
function validateSecIdCard(n) {
	var d = 0;
	var l = n;
	var i = {
		11 : "北京",
		12 : "天津",
		13 : "河北",
		14 : "山西",
		15 : "内蒙",
		21 : "辽宁",
		22 : "吉林",
		23 : "黑龙",
		31 : "上海",
		32 : "江苏",
		33 : "浙江",
		34 : "安徽",
		35 : "福建",
		36 : "江西",
		37 : "山东",
		41 : "河南",
		42 : "湖北",
		43 : "湖南",
		44 : "广东",
		45 : "广西",
		46 : "海南",
		50 : "重庆",
		51 : "四川",
		52 : "贵州",
		53 : "云南",
		54 : "西藏",
		61 : "陕西",
		62 : "甘肃",
		63 : "青海",
		64 : "宁夏",
		65 : "新疆",
		71 : "台湾",
		81 : "香港",
		82 : "澳门",
		91 : "国外"
	};
	if (!/^\d{17}(\d|x)$/i.test(l)) {
		return false
	}
	l = l.replace(/x$/i, "a");
	if (i[parseInt(l.substr(0, 2))] == null) {
		return false
	}
	var j = l.substr(6, 4) + "-" + Number(l.substr(10, 2)) + "-"
			+ Number(l.substr(12, 2));
	var m = new Date(j.replace(/-/g, "/"));
	if (j != (m.getFullYear() + "-" + (m.getMonth() + 1) + "-" + m.getDate())) {
		return false
	}
	for ( var k = 17; k >= 0; k--) {
		d += (Math.pow(2, k) % 11) * parseInt(l.charAt(17 - k), 11)
	}
	if (d % 11 != 1) {
		return false
	}
	return true
}
function validateFirIdCard(n) {
	var d = 0;
	var l;
	var i = {
		11 : "北京",
		12 : "天津",
		13 : "河北",
		14 : "山西",
		15 : "内蒙",
		21 : "辽宁",
		22 : "吉林",
		23 : "黑龙",
		31 : "上海",
		32 : "江苏",
		33 : "浙江",
		34 : "安徽",
		35 : "福建",
		36 : "江西",
		37 : "山东",
		41 : "河南",
		42 : "湖北",
		43 : "湖南",
		44 : "广东",
		45 : "广西",
		46 : "海南",
		50 : "重庆",
		51 : "四川",
		52 : "贵州",
		53 : "云南",
		54 : "西藏",
		61 : "陕西",
		62 : "甘肃",
		63 : "青海",
		64 : "宁夏",
		65 : "新疆",
		71 : "台湾",
		81 : "香港",
		82 : "澳门",
		91 : "国外"
	};
	if (n.length == 15) {
		l = idCardUpdate(n)
	} else {
		l = n
	}
	if (!/^\d{17}(\d|x)$/i.test(l)) {
		return false
	}
	l = l.replace(/x$/i, "a");
	if (i[parseInt(l.substr(0, 2))] == null) {
		return false
	}
	var j = l.substr(6, 4) + "-" + Number(l.substr(10, 2)) + "-"
			+ Number(l.substr(12, 2));
	var m = new Date(j.replace(/-/g, "/"));
	if (j != (m.getFullYear() + "-" + (m.getMonth() + 1) + "-" + m.getDate())) {
		return false
	}
	for ( var k = 17; k >= 0; k--) {
		d += (Math.pow(2, k) % 11) * parseInt(l.charAt(17 - k), 11)
	}
	if (d % 11 != 1) {
		return false
	}
	return true
}
function idCardUpdate(m) {
	var k;
	var n = /^(\d){15}$/;
	if (n.test(m)) {
		var h = 0;
		var l = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
		var i = new Array("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2");
		m = m.substr(0, 6) + "19" + m.substr(6, m.length - 6);
		for ( var j = 0; j < m.length; j++) {
			h += parseInt(m.substr(j, 1)) * l[j]
		}
		m += i[h % 11];
		k = m
	} else {
		k = "#"
	}
	return k
}
jQuery.validator.addMethod("checkBorth", function(d, g) {
	var h = d;
	if (h == "") {
		return true
	} else {
		var i = h.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
		if (i == null) {
			return false
		}
		var j = new Date(i[1], i[3] - 1, i[4]);
		return (j.getFullYear() == i[1] && (j.getMonth() + 1) == i[3] && j
				.getDate() == i[4])
	}
}, "日期格式不合法");
jQuery.validator.addMethod("byteRangeLength", function(f, h, j) {
	var g = f.length;
	for ( var i = 0; i < f.length; i++) {
		if (f.charCodeAt(i) > 127) {
			g++
		}
	}
	return this.optional(h) || (g >= j[0] && g <= j[1])
}, "length wrong");
jQuery.validator.addMethod("checkNameCharBlank", function(f, g, e) {
	var h = e.split("@");
	if ($("#" + h[1]).val() == "") {
		return true
	} else {
		if ($("#" + h[0]).val() == "1" || $("#" + h[0]).val() == "2") {
			return this.optional(g) || /^[a-zA-Z·.．\u3400-\u9FFF]+$/.test(f)
		} else {
			if ($("#" + h[0]).val() == "B" || $("#" + h[0]).val() == "H") {
				if (/^[-]+$/.test(f)) {
					return false
				}
				return this.optional(g)
						|| /^[a-z A-Z·.．\u3400-\u9FFF\-]+$/.test(f)
			} else {
				return this.optional(g)
						|| /^[a-z A-Z·.．\u3400-\u9FFF]+$/.test(f)
			}
		}
	}
}, "wrong name.");
jQuery.validator.addMethod("checkIdValidStr", function(e, f) {
	var d = /^[a-zA-Z0-9\_\-\(\)]+$/;
	return this.optional(f) || (d.test(e))
}, "wrong id");
jQuery.validator.addMethod("isSecIDCard", function(f, d, e) {
	if (!checkIfSecIdCard($(e).val())) {
		return true
	}
	return validateSecIdCard(f)
}, "wrong");
function checkIfSecIdCard(b) {
	if (b == "1") {
		return true
	}
	return false
}
function checkIfFirIdCard(b) {
	if (b == "2") {
		return true
	}
	return false
}
function checkCardForHKorTW(b) {
	if (b == "C" || b == "G") {
		return true
	}
	return false
}
jQuery.validator.addMethod("isFirIDCard", function(f, d, e) {
	if (!checkIfFirIdCard($(e).val())) {
		return true
	}
	return validateFirIdCard(f)
}, "wrong");
jQuery.validator.addMethod("checkHkongMacao", function(f, g, e) {
	if ($(e).val() == "C") {
		var h = /^[HMhm]{1}([0-9]{10}|[0-9]{8})$/;
		return this.optional(g) || (h.test(f))
	} else {
		return true
	}
}, "wrong format.");
jQuery.validator.addMethod("checkTaiw", function(g, i, j) {
	if ($(j).val() == "G") {
		var f = /^[0-9]{8}$/;
		var h = /^[0-9]{10}$/;
		return this.optional(i) || (f.test(g)) || (h.test(g))
	} else {
		return true
	}
}, "wrong format.");
jQuery.validator.addMethod("checkPassport", function(f, h, j) {
	if ($(j).val() == "B") {
		var g = /^[a-zA-Z]{5,17}$/;
		var i = /^[a-zA-Z0-9]{5,17}$/;
		return this.optional(h) || (i.test(f)) || g.test(f)
	} else {
		return true
	}
}, "wrong format.");
jQuery.validator.addMethod("checkWork", function(f, h, j) {
	if ($(j).val() == "H") {
		var g = /^[a-zA-Z]{5,17}$/;
		var i = /^[a-zA-Z0-9]{5,17}$/;
		return this.optional(h) || (i.test(f)) || g.test(f)
	} else {
		return true
	}
}, "wrong format.");
jQuery.validator.addMethod("isMobile", function(e, d) {
	var f = e.length;
	return this.optional(d) || (f == 11 && /^[0-9]+$/.test(e))
}, "wrong mobile phone ");
jQuery.validator
		.addMethod(
				"isTelePhone",
				function(f, d) {
					var e = /(^[0-9]{3,4}\-[0-9]{3,8}$)|(^[0-9]{3,8}$)|(^[0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}#)/;
					return this.optional(d) || (e.test(f))
				}, "wrong telePhone ");
jQuery.validator.addMethod("illegalChar", function(g, h, j) {
	var f = true;
	if (g.indexOf("$") >= 0) {
		return false
	}
	for ( var i = 0; i < g.length; i++) {
		if (g.charCodeAt(i) == 39 || g.charCodeAt(i) == 60
				|| g.charCodeAt(i) == 62 || g.charCodeAt(i) == 34
				|| g.charCodeAt(i) == 63) {
			f = false
		}
		if (!f) {
			break
		}
	}
	return this.optional(h) || f
}, "Illegal char wrong");
jQuery.validator.addMethod("isZipCode", function(e, f) {
	var d = /^[0-9]{6}$/;
	return this.optional(f) || (d.test(e))
}, "wrong zipcode");
jQuery.validator.addMethod("isEmail", function(e, d) {
	var f = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	return this.optional(d) || (f.test(trim(e)))
}, "wrong email");
function replaceChar(c) {
	var d = c.value.replace(/['"<> ?]/g, "");
	c.value = d
}
function checkNameChar1(b) {
	return /^[a-zA-Z0-9\u3400-\u9FFF]+$/.test(b)
}
function trim(b) {
	return b.replace(/(^\s*)|(\s*$)/g, "")
}
function ltrim(b) {
	return b.replace(/(^\s*)/g, "")
}
function rtrim(b) {
	return b.replace(/(\s*$)/g, "")
}
jQuery.validator.addMethod("validateName", function(c, d) {
	return this.optional(d) || /^[a-zA-Z\u3400-\u9FFF0-9\_]+$/.test(c)
}, "wrong username.");
jQuery.validator.addMethod("studentRequired", function(f, d, e) {
	if ($(e).val() == "3") {
		return f && trim(f) != ""
	}
	return true
}, "wrong studentRequired.");
jQuery.validator.addMethod("studentStationRequired", function(f, d, e) {
	if ($(e).val() == "3") {
		return f && trim(f) != "简拼/全拼/汉字" && trim(f) != ""
	}
	return true
}, "wrong studentStationRequired.");
jQuery.validator.addMethod("studentValidateName", function(f, d, e) {
	if ($(e).val() == "3") {
		return this.optional(d) || /^[a-zA-Z\u3400-\u9FFF0-9\_]+$/.test(f)
	}
	return true
}, "wrong username.");
jQuery.validator.addMethod("checkStudentName", function(f, d, e) {
	if ($(e).val() == "3") {
		if ((!f || trim(f) == "" || trim(f) == "简码/汉字")) {
			return false
		}
	}
	return true
}, "wrong username.");
jQuery.validator.addMethod("isQuestionNull", function(f, d, e) {
	if (jQuery.trim(f) != "") {
		if (jQuery.trim($(e[0]).val()) == "customQuestion"
				&& jQuery.trim($(e[1]).val()) == ""
				|| jQuery.trim($(e[0]).val()) == "") {
			return false
		}
	}
	return true
}, "you should input the question");
jQuery.validator.addMethod("isAnswerNull", function(f, d, e) {
	if ((jQuery.trim($(e[0]).val()) == "customQuestion" && jQuery.trim($(e[1])
			.val()) != "")
			|| (jQuery.trim($(e[0]).val()) != "")) {
		if (jQuery.trim(f) == "") {
			return false
		}
	}
	return true
}, "you should input the answer");
function checkSex(e, f, d) {
	if (!checkSexByCardId(e, f, d)) {
		if (!confirm("性别与身份证中的性别不符，是否继续?")) {
			return false
		} else {
			return true
		}
	} else {
		return true
	}
}
function checkSexByCardId(g, j, i) {
	function h(a, e) {
		var b = null;
		if (e.length == 15) {
			b = e.substring(14, 15)
		} else {
			if (e.length == 18) {
				b = e.substring(16, 17)
			} else {
				return true
			}
		}
		if (b == "x" || b == "X") {
			b = "10"
		}
		var c = parseInt(b);
		var d = c % 2;
		if (d === 0 && a === "F") {
			return true
		} else {
			if (d === 1 && a === "M") {
				return true
			} else {
				return false
			}
		}
	}
	var f = $(i).val();
	if (checkIfSecIdCard($(j).val()) && validateSecIdCard(f)) {
		if (f !== "") {
			return h(g, f)
		} else {
			return true
		}
	} else {
		if (checkIfFirIdCard($(j).val()) && validateFirIdCard(f)) {
			if (f !== "") {
				return h(g, f)
			} else {
				return true
			}
		} else {
			return true
		}
	}
}
function checkBirdDateByCardId(g, j, h) {
	var i = null;
	var f = $(h).val();
	if (checkIfSecIdCard($(j).val()) && f !== "" && validateSecIdCard(f)) {
		i = f.substring(6, 14)
	} else {
		if (checkIfFirIdCard($(j).val()) && f !== "" && validateFirIdCard(f)) {
			if (f.length == 15) {
				i = "19" + f.substring(6, 12)
			} else {
				if (f.length == 18) {
					i = f.substring(6, 14)
				}
			}
		} else {
			return true
		}
	}
	if (g !== "") {
		g = g.replace(/-/g, "");
		if (g != i) {
			return false
		} else {
			return true
		}
	} else {
		return true
	}
}
function checkBirdate(e, f, d) {
	if (!checkBirdDateByCardId(e, f, d)) {
		if (!confirm("出生日期与身份证中的出生日期不符，是否继续?")) {
			return false
		} else {
			return true
		}
	} else {
		return true
	}
}
jQuery.validator.addMethod("checkPwdValidate", function(c, d) {
	return this.optional(d)
			|| /(?![a-z]+$|[0-9]+$|_+$)^[a-zA-Z0-9_]{6,}$/.test(c)
}, "contain writespace");
jQuery.validator.addMethod("checkConfirmPassWard", function(f, d, e) {
	if ($(e).val() != null) {
		return $(e).val() == f
	}
	return true
}, "contain writespace");
jQuery.validator.addMethod("IVR_passwd_format", function(f, d) {
	var e = /^[0-9]{6}$/;
	return this.optional(d) || e.test(f)
}, "验证码错误!.");
jQuery.validator
		.addMethod(
				"checkStation",
				function(c, d) {
					if ((!c || trim(c) == "" || trim(c) == "简拼/全拼/汉字" || trim(c) == "简拼/全拼/汉字或↑↓")) {
						return false
					}
					return true
				}, "wrong username.");
jQuery.validator.addMethod("checkAnsyUserName", function(l, h, k) {
	var i = k[0];
	var g = $("#" + k[1]).val();
	var j = true;
	$.ajax({
		url : i + "?user_name=" + l,
		type : "get",
		async : false,
		success : function(b, a) {
			if (b.data == true) {
				j = false
			} else {
				j = true
			}
		},
		error : function(a, b, c) {
			j = false
		}
	});
	return j
}, "wrong cardNo");
function checkPwdRank(j, i, f) {
	var h = $(j);
	var g = h.val();
	if (g.length <= 6 || new RegExp("^[a-zA-Z]{6,}$").test(g)
			|| new RegExp("^[0-9]{6,}$").test(g)
			|| new RegExp("^[_]{6,}$").test(g)) {
		$("#" + i).attr("title", "危险");
		$("#" + f).html("危险");
		$("#" + i).removeClass("rank-a");
		$("#" + i).removeClass("rank-b");
		$("#" + i).removeClass("rank-c");
		$("#" + i).addClass("rank-a")
	} else {
		if (g.length > 6 && new RegExp("[a-zA-Z]").test(g)
				&& new RegExp("[0-9]").test(g) && new RegExp("[_]").test(g)) {
			$("#" + i).attr("title", "安全");
			$("#" + f).html("安全");
			$("#" + i).removeClass("rank-a");
			$("#" + i).removeClass("rank-b");
			$("#" + i).removeClass("rank-c");
			$("#" + i).addClass("rank-c")
		} else {
			$("#" + i).attr("title", "一般");
			$("#" + f).html("一般");
			$("#" + i).removeClass("rank-a");
			$("#" + i).removeClass("rank-b");
			$("#" + i).removeClass("rank-c");
			$("#" + i).addClass("rank-b")
		}
	}
}
Array.prototype.unique = function() {
	var f = {}, d = this.length;
	for ( var e = 0; e < d; e++) {
		if (typeof f[this[e]] == "undefined") {
			f[this[e]] = 1
		}
	}
	this.length = 0;
	d = 0;
	for ( var e in f) {
		this[d++] = e
	}
	return this
};
function checkSearchPwdRank(p, m, i) {
	var k = $(p);
	var j = k.val();
	if (j.length < 6) {
		$("#" + m).attr("title", "危险");
		$("#" + i).html("危险");
		$("#" + m).removeClass("rank-a");
		$("#" + m).removeClass("rank-b");
		$("#" + m).removeClass("rank-c");
		$("#" + m).addClass("rank-a")
	} else {
		var o = [];
		for ( var n = 0; n < 6; n++) {
			o.push(j.charAt(n))
		}
		o = o.unique();
		var l = o.length;
		if (l == 1) {
			$("#" + m).attr("title", "危险");
			$("#" + i).html("危险");
			$("#" + m).removeClass("rank-a");
			$("#" + m).removeClass("rank-b");
			$("#" + m).removeClass("rank-c");
			$("#" + m).addClass("rank-a")
		} else {
			if (l > 1 && l < 5) {
				$("#" + m).attr("title", "一般");
				$("#" + i).html("一般");
				$("#" + m).removeClass("rank-a");
				$("#" + m).removeClass("rank-b");
				$("#" + m).removeClass("rank-c");
				$("#" + m).addClass("rank-b")
			} else {
				$("#" + m).attr("title", "安全");
				$("#" + i).html("安全");
				$("#" + m).removeClass("rank-a");
				$("#" + m).removeClass("rank-b");
				$("#" + m).removeClass("rank-c");
				$("#" + m).addClass("rank-c")
			}
		}
	}
}
jQuery.validator.addMethod("checkDetailAddress", function(c, d) {
	return this.optional(d) || /^[0-9a-zA-Z\u3400-\u9FFF\#]+$/.test(c)
}, "wrong name.");
jQuery.validator.addMethod("checkAddressName", function(c, d) {
	if (/^[-]+$/.test(c)) {
		return false
	}
	return this.optional(d) || /^[a-z A-Z·.．\u3400-\u9FFF\-]+$/.test(c)
			|| /^[a-zA-Z·.．\u3400-\u9FFF]+$/.test(c)
}, "wrong name.");
jQuery.validator.addMethod("checkAddressSelect", function(c, d) {
	if ("" == c) {
		return false
	}
	if (c) {
		return true
	}
	return this.optional(d)
}, "wrong name.");
var login_messages = {
	randCodeError : "验证码错误!",
	randCodeExpired : "验证码失效",
	randCodeLentgh : "验证码长度为4位!",
	randCodeFormat : "验证码只能由数字或字母组成!",
	randCodeEmpty : "验证码不能为空!",
	userNameEmpty : "登录名必须填写!",
	userNameFormat : "登录名格式不正确，请重新输入!",
	passwordEmpty : "密码必须填写,且不少于6位!",
	passwordLength : "密码长度不能少于6位!",
	pleaseClickCaptcha : "请点击验证码",
	pleaseClickLeftCaptcha : "请点击左侧验证码",
	pleaseClickCaptchaRight : "请正确点击验证码",
	loginError : "当前访问用户过多,请稍候重试!"
};
