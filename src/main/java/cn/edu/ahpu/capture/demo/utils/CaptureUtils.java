package cn.edu.ahpu.capture.demo.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * @author <a href="jhuaishuang@gmail.com">JHS</a>
 * @datetime 2015-1-16 上午10:10:32
 * @description:
 */
public class CaptureUtils {
	/**
	 * 链接超时
	 */
	private static final int TIME_OUT = 50000;

	public static void main(String[] args) throws Exception {
		new CaptureUtils().loginWithCaptcha();
	}
	/**
	 * 带有验证码的登陆
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	@Test
	public void loginWithCaptcha() throws Exception {
		//1.获取验证码+sessionid
		Long rnds = System.currentTimeMillis();
		String yzmUrl = "http://220.178.98.86/hfgjj/code.jsp?rnds=" + rnds;
		
		String specialName = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + ".png";
		String fileName = "yzm\\a_original\\"+  specialName;
		String sessionId = getImages(yzmUrl, fileName);
//		System.out.println("sessionId:" + sessionId);

		/**
		 * 后期会增加自动识别验证码的部分;
		 */
		//手动查看验证码后，输入验证码
		String yzm = OcrImageUtils.getCaptcha(specialName, 5, 5, 0, 4);
		System.out.println(fileName+" ---------> "+ yzm);
		
		//2.登陆:利用sessionid+用户名,密码,类别
		String loginActionUrl = "http://220.178.98.86/hfgjj/jsp/web/public/search/grloginAct.jsp";//
		Map<String, String> loginDataMap = new HashMap<String, String>();
		loginDataMap.put("lb", "a");//a:个人公积金帐号(9位),b:身份证号(15或18位),c:公积金黄山卡号(17位)
		loginDataMap.put("hm", "*");//账号
		loginDataMap.put("mm", "*");	//密码
		loginDataMap.put("yzm", yzm);	//验证码
		String dataUrl = "http://220.178.98.86/hfgjj/jsp/web/public/search/grCenter.jsp?rnd="+ System.currentTimeMillis();
		Document document = null;
		try {
			Response response = Jsoup.connect(loginActionUrl).ignoreContentType(true)
					.userAgent("Mozilla").method(Method.POST)
					.data(loginDataMap)
					.cookie("JSESSIONID", sessionId)
					.execute();
			
			Map<String, String> cookies = response.cookies();
			if(cookies != null){
				Iterator<?> it = cookies.entrySet().iterator();
				while(it.hasNext()){
					Entry<?, ?> entry = (Entry<?, ?>) it.next();
					System.out.println(entry.getKey()+":"+entry.getValue());
				}
			}else{
				System.out.println("登陆过后cookies为空");
			}
			

			//3.发起数据请求;
			document = Jsoup.connect(dataUrl).ignoreContentType(true)
					.cookies(cookies).data(loginDataMap).post();
			
	 System.out.println("============================================");
			Elements spans =  document.getElementsByTag("span").attr("class", "tc");
			String welcomeStr = "";
			if(spans.size() > 4){
				List<Element> spansList = spans.subList(0, 4);
				for(Element e : spansList){
					welcomeStr += e.html().replaceAll("&nbsp;", "")+"\n";
				 }
			}else{
				for(Element e : spans){
					welcomeStr += e.html().replaceAll("&nbsp;", "")+"\n";
				 }
			}
			
			// 尊敬的用户，您好！
			if(welcomeStr.indexOf("尊敬的用户") < 0){
				System.out.println("登陆失败!");
//				return ;
			}else{
				System.out.println(welcomeStr);
			}

			 loginDataMap.put("url", "2");
			 loginDataMap.put("dkzh", "");
			 document = Jsoup.connect(dataUrl).ignoreContentType(true)
						.cookies(cookies).data(loginDataMap).post();
			 
			 if(document.body().html().contains("<script>alert('登陆超时，请重新登陆');window.location='grlogin.jsp';</script>")){
				 System.out.println("登陆超时，请重新登陆");
				 return;
			 }
			 
			 Elements tds =  document.select("td[background$=fzdt_Bg.jpg]");
				Element table = null;
				 for(Element td : tds){
					 table = td.parent().parent(); //td -> tr -> table;
					 break;
				 }
				 if(table != null){
					 System.out.println("职工基本信息");
					 Elements trs = table.getElementsByTag("tr");
					 int count = 0 ;
					 for(Element tr : trs){
						 if(count >= 28){
							 return ;
						 }
						 tds = tr.getElementsByTag("td");
						 for(Element td : tds){
							 count++;
							 String html = td.html().replaceAll("&nbsp;", "")+" ";
							 if(count % 2 == 0){
								 html +=  " | ";
							 }
							 System.out.print(html);
						 }
						 System.out.print(" \n");
					 }
				 }
			 
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	/**
	 * @param urlPath
	 *            图片路径
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static String getImages(String urlPath, String fileName)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
		HttpGet get = new HttpGet(urlPath);
		get.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,TIME_OUT);

		try {
			HttpResponse resonse = client.execute(get);
			if (resonse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity entity = resonse.getEntity();
				if (entity != null) {
					byte[] data = EntityUtils.toByteArray(entity);
					if (data.length > 1024) {
						FileOutputStream outputStream = new FileOutputStream(
								fileName);
						outputStream.write(data);
						outputStream.close();
						System.out.println("图片" + fileName + "下载成功");
					}
				}
			}

			return getJsessionId(resonse);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 获取sessionid
	 * 
	 * @param resonse
	 * @return
	 */
	public static String getJsessionId(HttpResponse resonse) {
		Header[] rspHeaders = resonse.getAllHeaders();
		Header setCookieHeader = null;
		String jsessionId = null;
		for (Header header : rspHeaders) {
			String headerName = header.getName();
			if (headerName.equalsIgnoreCase("Set-Cookie")) {
				setCookieHeader = header;
				break;
			}
		}

		if (setCookieHeader != null) {
			 System.out.println(setCookieHeader);
			HeaderElement[] headerElements = setCookieHeader.getElements();
			if (headerElements != null && headerElements.length > 0) {
				for (HeaderElement headerElement : headerElements) {
					jsessionId = headerElement.getValue();
					break;
				}
			}
		}

		return jsessionId;
	}

	/**
	 * 读取url中数据，并以字节的形式返回
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		inputStream.close();
		return outputStream.toByteArray();
	}

	public static Document login(String loginUrl, Map<String, String> loginMap,
			String dataUrl, Map<String, String> dataMap) {
		Document document = null;
		try {
			Response response = Jsoup.connect(loginUrl).ignoreContentType(true)
					.data(loginMap).userAgent("Mozilla").method(Method.POST)
					.execute();
			Map<String, String> cookies = response.cookies();
		
			document = Jsoup.connect(dataUrl).ignoreContentType(true)
					.cookies(cookies).data(dataMap).post();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	@Test
	public void loginTest() {
		String loginUrl = "http://10.160.134.10:8088/j_spring_security_check";
		Map<String, String> loginMap = new HashMap<String, String>();
		loginMap.put("j_username", "*");
		loginMap.put("j_password", "*");

		String dataUrl = "http://10.160.134.10:8088/select/tjb_IpasTjbCkftplr";
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("limit", "20");
		dataMap.put("start", "0");
		dataMap.put("tjrqStr", "201412");

		Document document = login(loginUrl, loginMap, dataUrl, dataMap);
		System.out.println(document.html());
	}

	/**
	 * 解决ajax请求跨域访问:
	 */
	public static String ajaxCrossDomain(String url, Map<String, String> params) {
		String infos = null;
		Document document;
		try {
			document = Jsoup.connect(url).data(params).userAgent("Mozilla")
					.get();
			Element body = document.body();
			infos = body.html();
			if (infos != null && infos.trim().length() > 0) {
				infos = infos.replaceAll("&quot;", "\"");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infos;
	}

	@Test
	public void testAjaxCrossDomain01() {
		String url = "http://www.kuaidi100.com/query?type=shentong";
		Map<String, String> params = new HashMap<String, String>();
		params.put("postid", "968646983186");
		String infos = ajaxCrossDomain(url, params);
		System.out.println(infos);
	}
}
