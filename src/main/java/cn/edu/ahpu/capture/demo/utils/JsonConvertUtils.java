package cn.edu.ahpu.capture.demo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.junit.Test;

import cn.edu.ahpu.capture.demo.DateJsonValueProcessor;
import cn.edu.ahpu.capture.demo.model.Message;
import cn.edu.ahpu.capture.demo.model.MessageData;

/**
 * json �� object ֮���ת��
 * @author <a href="jhuaishuang@gmail.com">JHS</a>
 * @datetime 2015-3-13 ����11:11:08 
 * @description:
 */
public class JsonConvertUtils {

	@Test
	//javaBeanת��Ϊjson
	public void javaBean2JsonTest01() throws ParseException{
		String message = "ok";
		String nu = "968646983186";
		Integer ischeck = 1;
		String com = "shentong";
		String status = "200";
		String condition ="F00";
		String state = "3";
		List<MessageData> data = new ArrayList<MessageData>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		data.add(new MessageData(sdf.parse("2015-01-11 21:03:05"),"����",sdf.parse("2015-01-11 21:03:05")));
		data.add(new MessageData(sdf.parse("2015-01-12 09:58:05"),"�Ϻ��ֶ��Ž���˾ ���ɼ�Ա �ۿ� �����ɼ�",sdf.parse("2015-01-12 09:58:05")));
		data.add(new MessageData(sdf.parse("2015-01-12 13:20:00"),"�ͻ�ǩ��",sdf.parse("2015-01-12 13:20:00")));
		Message entity = new Message(message, nu, ischeck, com, status, condition, state, data);
		
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" )); //������������
		JSONObject  json= JSONObject.fromObject(entity,config);
		System.out.println(json);
	}
	
	@Test
	//java����listת��Ϊjson����
	public void javaListBean2JsonTest02() throws ParseException{
		List<MessageData> list = new ArrayList<MessageData>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		list.add(new MessageData(sdf.parse("2015-01-11 21:03:05"),"����",sdf.parse("2015-01-11 21:03:05")));
		list.add(new MessageData(sdf.parse("2015-01-12 09:58:05"),"�Ϻ��ֶ��Ž���˾ ���ɼ�Ա �ۿ� �����ɼ�",sdf.parse("2015-01-12 09:58:05")));
		list.add(new MessageData(sdf.parse("2015-01-12 13:20:00"),"�ͻ�ǩ��",sdf.parse("2015-01-12 13:20:00")));
		
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" )); //������������
		JSONArray  jsonArr= JSONArray.fromObject(list,config);
		System.out.println(jsonArr);
	}
	
	@Test
	//JSONObjectת��Ϊjava����
	public void json2JavaBeanTest03() throws ParseException{
		String url = "http://www.kuaidi100.com/query?type=shentong";
		Map<String, String> params = new HashMap<String, String>();  
		params.put("postid","968646983186");
		String infos =  CaptureUtils.ajaxCrossDomain(url,params);
		System.out.printf("ץȡ����json����:%s\n",infos);
		
		Map<String, Class > classMap = new HashMap<String, Class>(); 
		classMap.put( "data", MessageData. class);

		//JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher( new String[] {"yyyy-MM-dd HH:mm:ss" }) );//����Date����
		Class clazz = Message.class;
		Message bean = (Message) JSONObject.toBean(JSONObject.fromObject(infos), clazz,classMap);
		
		List<MessageData> data = bean.getData();
		if(data != null && data.size() > 0){
			for(MessageData temp : data){
				System.out.println(temp);
			}
		}
		
	}
	
	@Test
	//JSONArrayת��Ϊlist����
	public void json2JavaListBeanTest04() throws ParseException{
		String infos = "[{\"time\":\"2015-01-12 20:15:47\",\"location\":\"\",\"context\":\"��ǩ��,ǩ��������ǩ��\",\"ftime\":\"2015-01-12 20:15:47\"},"+
					"{\"time\":\"2015-01-11 18:17:40\",\"location\":\"\",\"context\":\"���հ�����˾ ���ռ�Ա ���������ռ�\",\"ftime\":\"2015-01-11 18:17:40\"}]";
		System.out.println(infos);
		JsonConfig config= new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss" ));
		
		JSONArray jsonArray = JSONArray.fromObject(infos, config);
		List<MessageData> list = JSONArray.toList(jsonArray, MessageData.class);
		if(list != null && list.size() > 0){
			for(MessageData temp : list){
				System.out.println(temp);
			}
		}
	}
}
