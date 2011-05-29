package com.yfzhang.utils.net;

import org.json.JSONObject;

/**
 * Holds Http response data.
 * 
 * @see com.yfzhang.utils.net.HttpClient HttpClient
 * @see com.yfzhang.utils.net.HttpRequest HttpRequest
 * @author yifanz
 */
public class HttpResponse {
	private boolean reqSuccessful;
	private int responseCode;
	private JSONObject json;
	
	/**
	 * Creates a new HttpResponse instance.
	 * 
	 * @param successful Http request executed successfully or not
	 * @param responseCode Http response code
	 * @param json {@link org.json.JSONObject JSONObject} object reference
	 */
	public HttpResponse(boolean successful, int responseCode, JSONObject json) {
		this.reqSuccessful = successful;
		this.json = json;
		this.responseCode = responseCode;
	}

	/**
	 * Call to check if Http request proccess executed without problems on client side
	 * 
	 * @return true if client side proccess executed without problems, false otherwise
	 */
	public boolean isReqSuccessful() {
		return reqSuccessful;
	}
	
	/**
	 * Returns Http server response code.
	 * 
	 * @return Http response code from server. Returns 0 if response code is uninitialized.
	 */
	public int getResponseCode() {
		return responseCode;
	}
	
	/**
	 * Returns JSONObject result from server
	 * 
	 * @return {@link org.json.JSONObject JSONObject} from server
	 */
	public JSONObject getJSON() {
		return json;
	}
}
