package cn.edu.ahpu.capture.demo.work.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;


public class TowerCaptureUtil {
	
	private String ssoUrl = "http://sso.chinatowercom.cn/cas/login?service=http%3A%2F%2Feip.chinatowercom.cn%2Fc%2Fportal%2Flogin%3Fredirect%3D%252F%26p_l_id%3D10617";
	private String crmUrl = "http://app.chinatowercom.cn/bridge/GenerateToken?url=http%3a%2f%2f123.126.34.63%3a36080%2fdefault%2fcn.chinatowercom.crm.main.login.login.flow";
	private String pmsUrl="http://app.chinatowercom.cn/bridge/GenerateToken?url=http%3a%2f%2f123.126.34.173%3a13456%2fpms%2fcn.chinatowercom.pms.sso.ssoAuth.flow";
	
	private String dataUrl = "http://123.126.34.173:13456/pms/acceptance/acceptance/TAccInfoForm.jsp?prjId=394822&prjType=A";
	private String formId = "#fm1";
	@Test
	public void a_login(){
		Map<String, String> loginMap = new HashMap<String, String>();
		
		Document document = null;
		try {
			Response response = Jsoup.connect(ssoUrl).ignoreContentType(true)
					.userAgent("Mozilla").method(Method.POST)
					.execute();
			Map<String, String> cookies = response.cookies();
			
			document = Jsoup.parse(response.body());
			
			//根据页面中的登录formid 获取登录form
			List<Element> forms = document.select(formId);
			Element loginForm = forms == null ? null : forms.get(0);
			
			//讲页面中登录的form的所有初始值都给赋到 loginMap中
			for(Element e : loginForm.getAllElements()){
				if(e.tagName().equalsIgnoreCase("input")){
					loginMap.put(e.attr("name"), e.attr("value"));
				}
			}
			loginMap.put("username", "");
			loginMap.put("password", "");
			
			response = Jsoup.connect(ssoUrl).ignoreContentType(true)
					.cookies(cookies).data(loginMap).method(Method.POST)
					.execute();
			
			cookies = response.cookies();
			
			
//			System.out.println(document);
			
			/*response = Jsoup.connect(crmUrl).ignoreContentType(true)
					.cookies(cookies).method(Method.POST)
					.execute();
			
			cookies = response.cookies();*/
			
			response = Jsoup.connect(pmsUrl).ignoreContentType(true)
					.cookies(cookies).method(Method.POST).timeout(16000)
					.execute();
			cookies = response.cookies();
			
			response = Jsoup.connect(dataUrl).ignoreContentType(true)
					.cookies(cookies).method(Method.POST).timeout(16000)
					.execute();
			
			document = Jsoup.parse(response.body());
			System.out.println(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
