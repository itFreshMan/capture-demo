package cn.edu.ahpu.capture.demo.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Message {
	private String message;// ok
	private String nu;// 订单号:968646983186
	private Integer ischeck;// 1
	private String com;// 快递类型:shentong
	private String status;// 200
	private String condition;// F00
	private String state;// 3
	private List<MessageData> data;

	public Message() {
		super();
	}

	public Message(String message, String nu, Integer ischeck,
			String com, String status, String condition, String state,
			List<MessageData> data) {
		super();
		this.message = message;
		this.nu = nu;
		this.ischeck = ischeck;
		this.com = com;
		this.status = status;
		this.condition = condition;
		this.state = state;
		
		
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNu() {
		return nu;
	}

	public void setNu(String nu) {
		this.nu = nu;
	}

	public Integer getIscheck() {
		return ischeck;
	}

	public void setIscheck(Integer ischeck) {
		this.ischeck = ischeck;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<MessageData> getData() {
		return data;
	}

	public void setData(List<MessageData> data) {
		this.data = data;
	}

	/*class StoResponseMessageData {
		private Date time;
		private String context;
		private String ftime;

		public StoResponseMessageData() {
			super();
		}

		@Override
		public String toString() {
			return "StoResponseMessageData [time=" + time + ", context=" + context
					+ ", ftime=" + ftime + "]";
		}

		public StoResponseMessageData(Date time, String context, String ftime) {
			super();
			this.time = time;
			this.context = context;
			this.ftime = ftime;
		}

		public Date getTime() {
			return time;
		}

		public void setTime(Date time) {
			this.time = time;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public String getFtime() {
			return ftime;
		}

		public void setFtime(String ftime) {
			this.ftime = ftime;
		}

	}*/
}
