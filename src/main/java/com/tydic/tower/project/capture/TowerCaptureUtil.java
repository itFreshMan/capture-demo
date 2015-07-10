 package com.tydic.tower.project.capture;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import cn.edu.ahpu.capture.demo.DateJsonValueProcessor;
public class TowerCaptureUtil {
	private String a_project_design_url = "http://123.126.34.173:13456/pms/design/appoint/cn.chinatowercom.pms.design.showDetails.flow?desId=D4&prjType=A&pageType=check";
	private String b_project_construction_url = "http://123.126.34.173:13456/pms/construction/construction/TIniInfoForm.jsp";
	private String c_project_acceptance_url = "http://123.126.34.173:13456/pms/acceptance/acceptance/TAccInfoForm.jsp?prjType=A";
	private String d_project_finalaccount_url = "http://123.126.34.173:13456/pms/finalaccount/TFinInfoForm.jsp";

	private String prjId = "394822";
	private String username = "***";
	private String password = "***";
	private String sso_form_id = "#fm1";

	private String ssoUrl = "http://sso.chinatowercom.cn/cas/login?service=http%3A%2F%2Feip.chinatowercom.cn%2Fc%2Fportal%2Flogin%3Fredirect%3D%252F%26p_l_id%3D10617";
	private String crmUrl = "http://app.chinatowercom.cn/bridge/GenerateToken?url=http%3a%2f%2f123.126.34.63%3a36080%2fdefault%2fcn.chinatowercom.crm.main.login.login.flow";
	private String pmsUrl = "http://app.chinatowercom.cn/bridge/GenerateToken?url=http%3a%2f%2f123.126.34.173%3a13456%2fpms%2fcn.chinatowercom.pms.sso.ssoAuth.flow";

	
	private Map<SystemEnum ,Map<String,String>> sessionMap = new HashMap<SystemEnum,Map<String,String>>();
	private Map<String, String> paramData = new HashMap<String, String>();
	
	@Before
	public void setUp(){
//		login();
		 Map<String, String> pmsCookies = new HashMap<String, String>();
		 pmsCookies.put("JSESSIONID", "4B5B2D2D86636B48FF874B1587D178A4");
		 pmsCookies.put("X-Mapping-chmfolic", "5A2FD788564AD1DE25A615678D7DD575");
		 sessionMap.put(SystemEnum.PMS, pmsCookies);
	}

	public Map<String, String> login() {
		Map<String, String> loginMap = new HashMap<String, String>();

		Document document = null;
		Map<String, String> cookies = null;
		try {
			//1.根据页面中的登录formid 获取登录form
			Response response = Jsoup.connect(ssoUrl).ignoreContentType(true)
					.userAgent("Mozilla").method(Method.POST).execute();
			cookies = response.cookies();

			document = Jsoup.parse(response.body());
			List<Element> forms = document.select(sso_form_id);
			Element loginForm = forms == null ? null : forms.get(0);

			// 页面中登录的form的所有初始值都给赋到 loginMap中
			for (Element e : loginForm.getAllElements()) {
				if (e.tagName().equalsIgnoreCase("input")) {
					loginMap.put(e.attr("name"), e.attr("value"));
				}
			}
			loginMap.put("username", username);
			loginMap.put("password", password);

			//2.登陆sso
			response = Jsoup.connect(ssoUrl).ignoreContentType(true)
					.cookies(cookies).data(loginMap).method(Method.POST)
					.execute();
			cookies = response.cookies();
			sessionMap.put(SystemEnum.SSO, cookies);
			
			//3. pms
			response = Jsoup.connect(pmsUrl).ignoreContentType(true)
					.cookies(cookies).method(Method.POST).timeout(16000)
					.execute();
			cookies = response.cookies();
			sessionMap.put(SystemEnum.PMS, cookies);
			
			//4.crm
			response = Jsoup.connect(crmUrl).ignoreContentType(true)
					.cookies(cookies).method(Method.POST).timeout(16000)
					.execute();
			cookies = response.cookies();
			sessionMap.put(SystemEnum.CRM, cookies);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cookies;
	}
	

	@Test
	public void a_project_design(){
		Map<String, String> cookies = null;
		Document document = null;
		try{
			if(sessionMap == null|| sessionMap.get(SystemEnum.PMS) == null){
				login();
			}
			cookies = sessionMap.get(SystemEnum.PMS);
			
			paramData.put("prjId", prjId);
			Response response = Jsoup
						.connect(a_project_design_url).ignoreContentType(true).userAgent("Mozilla")
						.data(paramData).cookies(cookies)
						.method(Method.POST).execute();
			
			System.out.println(response.body());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private String fileListUrl01="http://123.126.34.173:13456/pms/cn.chinatowercom.pms.wordtemplate.dao.newcomponent.getDocCode.biz.ext";
	
	//http://123.126.34.173:13456/pms/cn.chinatowercom.pms.wordtemplate.service.output.flow?doc_id=19451e1277da7b50f47eb8d99982ddc5
	private String downloadFileUrlPrefix01 = "/cn.chinatowercom.pms.wordtemplate.service.output.flow";
	@Test
	public void a_project_design_fileList01(){
		Map<String, String> cookies = null;
		try{
			if(sessionMap == null|| sessionMap.get(SystemEnum.PMS) == null){
				login();
			}
			cookies = sessionMap.get(SystemEnum.PMS);
			
			paramData.put("prj_id", prjId);
			Response response = Jsoup
						.connect(fileListUrl01).ignoreContentType(true).userAgent("Mozilla")
						.data(paramData).cookies(cookies)
						.method(Method.POST).execute();
			
		String fileList01Json = response.body();
		
		if(StringUtils.isEmpty(fileList01Json)) return ;
			
		JsonConfig config= new JsonConfig();
//		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" ));
		JSONObject jsonObject = JSONObject.fromObject(fileList01Json, config);
		
		if(jsonObject == null) return ;

		Object obj = jsonObject.get("resList");
		
		if(obj == null ) return ;
		
		JSONArray jsonArray = JSONArray.fromObject(obj.toString(), config);
		List<Map> list = JSONArray.toList(jsonArray,Map.class);
		for(Map map : list){
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry e = (Entry) it.next();
				System.out.print(e.getKey()+":"+e.getValue()+",");
			}
			System.out.println();
			String downloadUrl = downloadFileUrlPrefix01 +"?doc_id="+map.get("DOC_ID");
			System.out.println(downloadUrl);
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//http://123.126.34.173:13456/pms/design/appoint/cn.chinatowercom.pms.design.TypeManager.queryDesImprotInfo.biz.ext?prjId=394822
	private String fileListUrl02="http://123.126.34.173:13456/pms/design/appoint/cn.chinatowercom.pms.design.TypeManager.queryDesImprotInfo.biz.ext";
	//http://123.126.34.173:13456/pms/design/despreparation/Download.jsp?fileName=%E6%9D%A8%E5%B2%97.xlsx&filePath=/app/files/MAIN_D/2015/6/1433128269562.xlsx
	private String downloadFileUrlPrefix02 =  "/design/despreparation/Download.jsp";
	@Test
	public void a_project_design_fileList02(){
		Map<String, String> cookies = null;
		try{
			if(sessionMap == null|| sessionMap.get(SystemEnum.PMS) == null){
				login();
			}
			cookies = sessionMap.get(SystemEnum.PMS);
			
			paramData.put("prjId", prjId);
			Response response = Jsoup
						.connect(fileListUrl02).ignoreContentType(true).userAgent("Mozilla")
						.data(paramData).cookies(cookies)
						.method(Method.POST).execute();
			
		String fileList02Json = response.body();
		
		if(StringUtils.isEmpty(fileList02Json)) return ;
			
		JsonConfig config= new JsonConfig();
//		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" ));
		JSONObject jsonObject = JSONObject.fromObject(fileList02Json, config);
		
		if(jsonObject == null) return ;

		Object obj = jsonObject.get("desImportInfo");
		
		if(obj == null ) return ;
		
		JSONArray jsonArray = JSONArray.fromObject(obj.toString(), config);
		List<Map> list = JSONArray.toList(jsonArray,Map.class);
		for(Map map : list){
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry e = (Entry) it.next();
				System.out.print(e.getKey()+":"+e.getValue()+",");
			}
			System.out.println();
			String fileName = map.get("docName") == null ? "" : map.get("docName").toString();
			String filePath = map.get("docPath") == null ? "" : map.get("docPath").toString();
			if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(filePath)) return ;
			
			filePath = "/app/files"+filePath;
			
			String downloadUrl = downloadFileUrlPrefix02 +"?fileName="+fileName+"&filePath="+filePath;
			System.out.println(downloadUrl);
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//http://123.126.34.173:13456/pms/design/appoint/cn.chinatowercom.pms.initiation.fileList.fileList.biz.ext?criteria/_entity=cn.chinatowercom.pms.initiation.newproinfodraft.FileList&criteria/_expr[0]/prjId=394822
	private String fileListUrl03="http://123.126.34.173:13456/pms/design/appoint/cn.chinatowercom.pms.initiation.fileList.fileList.biz.ext";
	
	//http://123.126.34.173:13456/pms/initiation/newpage/Download.jsp?filePath=cf67aa98bf472ac21646ab5fe165b524%2F6f3fc13cdf0e734ef932b889f300eb35%2F8ea62b17382acf1cdb130ca7978428af%2Fe8d02beca25496cdef340b46a2c576fd.pdf&fileName=%E5%90%88%E8%82%A5%E9%93%81%E5%A1%94%E5%BA%90%E6%B1%9F%E5%8E%BF%E6%9D%A8%E5%B2%97%E5%9F%BA%E7%AB%9945%E7%B1%B3%E6%99%AF%E8%A7%82%E5%A1%94%E4%B8%80%E4%BD%93%E5%8C%96%E6%9C%BA%E6%9F%9C0427%20Model%20(1).pdf
	private String downloadFileUrlPrefix03 =  "/initiation/newpage/Download.jsp";
	@Test
	public void a_project_design_fileList03(){
		Map<String, String> cookies = null;
		try{
			if(sessionMap == null|| sessionMap.get(SystemEnum.PMS) == null){
				login();
			}
			cookies = sessionMap.get(SystemEnum.PMS);
			

			fileListUrl03 += "?criteria/_entity=cn.chinatowercom.pms.initiation.newproinfodraft.FileList&criteria/_expr[0]/prjId="+prjId;
			Response response = Jsoup
						.connect(fileListUrl03).ignoreContentType(true).userAgent("Mozilla")
						.cookies(cookies).timeout(16000)
						.method(Method.POST).execute();
			
		String fileList03Json = response.body();
		
		if(StringUtils.isEmpty(fileList03Json)) return ;
			
		JsonConfig config= new JsonConfig();
//		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" ));
		JSONObject jsonObject = JSONObject.fromObject(fileList03Json, config);
		
		if(jsonObject == null) return ;

		Object obj = jsonObject.get("fileList");
		
		if(obj == null ) return ;
		
		JSONArray jsonArray = JSONArray.fromObject(obj.toString(), config);
		List<Map> list = JSONArray.toList(jsonArray,Map.class);
		for(Map map : list){
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry e = (Entry) it.next();
				System.out.print(e.getKey()+":"+e.getValue()+",");
			}
			System.out.println();
			String fileName = map.get("docName") == null ? "" : map.get("docName").toString();
			String filePath = map.get("docPath") == null ? "" : map.get("docPath").toString();
			if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(filePath)) return ;
			
			filePath = "/app/files"+filePath;
			
			String downloadUrl = downloadFileUrlPrefix03 +"?fileName="+fileName+"&filePath="+filePath;
			System.out.println(downloadUrl);
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	@After
	public void tearDown(){
	}
}
