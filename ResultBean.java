package com.qf.common.vo;

public class ResultBean {

	private int code;//编码  1 成功 2失败
	private String msg;//内容
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	//成功
	public static ResultBean setSuccess(String msg){
		ResultBean bean=new ResultBean();
		bean.setCode(1);
		bean.setMsg(msg);
		return bean;
	}
	//失败
	public static ResultBean setError(String msg){
		ResultBean bean=new ResultBean();
		bean.setCode(2);
		bean.setMsg(msg);
		return bean;
	}
	

}
