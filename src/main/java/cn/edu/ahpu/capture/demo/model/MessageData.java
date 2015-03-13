package cn.edu.ahpu.capture.demo.model;

import java.util.Date;

public class MessageData {

	private Date time;
	private String context;
	private Date ftime;

	public MessageData() {
		super();
	}

	@Override
	public String toString() {
		return "MessageData [time=" + time + ", context=" + context
				+ ", ftime=" + ftime + "]";
	}

	public MessageData(Date time, String context, Date ftime) {
		super();
		this.time = time;
		this.context = context;
		this.ftime = ftime;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		System.out.println("setter time£º"+time);
		this.time = time;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Date getFtime() {
		return ftime;
	}

	public void setFtime(Date ftime) {
		this.ftime = ftime;
	}

}
