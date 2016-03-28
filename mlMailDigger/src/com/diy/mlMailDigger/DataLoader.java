package com.diy.mlMailDigger;

import java.io.File;
import java.io.FilenameFilter;
//import java.io.IOException;
import java.util.concurrent.TimeUnit;

//import org.json.JSONException;
//import org.json.JSONObject;

import com.diy.helpers.Utils;
//import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class DataLoader {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
	
	public static void main(String[] args) {
		try {
			String rootFolder = "C:\\zStuff\\OutlookExport";
			
			// retrieve files to import
			File dir = new File(rootFolder);
			int timeOut = 600;
			
			// xmls first			
			FilenameFilter filenameFilterXML = new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".xml");
			    }
			};
			File[] filesXML = dir.listFiles(filenameFilterXML);
			
			for (File file: filesXML) {
				Utils.log(file.getName());
				
				// retrieve data to be uploaded
				byte[] bDoc = Utils.convertFile2Byte(file.getAbsolutePath());
				String sDoc = new String(bDoc, "ISO-8859-1");
				sDoc = Utils.convertXMLToJSON(sDoc);
				Utils.convertByte2File(sDoc.getBytes(), file.getAbsolutePath()+".json");
				bDoc = sDoc.getBytes("UTF-8");
				
				// url
				String url = "http://localhost:8000"+"/LATEST/documents?uri=/mlMailDigger/"+ file.getName() + ".json&prop:test=zzz";
				
				// body
				RequestBody body = RequestBody.create(OCTET_STREAM, bDoc);
				
				// prepare request
				Request.Builder rb = new Request.Builder().url(url).put(body);
				
				// authentication
				String credential = Credentials.basic("svinchon", "Pa55word");
				rb.addHeader("Authorization", credential);
				
				// content type
				//rb.addHeader("Content-type", "application/xml");
				rb.addHeader("Content-type", "application/json");
				
				// build request 
				Request request = rb.build();
				Utils.log(request.toString());
				
				// client
				OkHttpClient client = new OkHttpClient();
				client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
				client.setReadTimeout(timeOut, TimeUnit.SECONDS);
				
				// run request
				Response response = client.newCall(request).execute();
				
				Utils.log("response: "+response.message());				
			}
			
			FilenameFilter filenameFilterMSG = new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".msg");
			    }
			};
			File[] filesMSG = dir.listFiles(filenameFilterMSG);
			
			for (File file: filesMSG) {
				Utils.log(file.getName());
				
				// retrieve data to be uploaded
				byte[] bDoc = Utils.convertFile2Byte(file.getAbsolutePath());
				//String sDoc = new String(bDoc, "ISO-8859-1");
				//bDoc = sDoc.getBytes("UTF-8");
				
				// url
				String url = "http://localhost:8000"+"/LATEST/documents?uri=/mlMailDigger/"+ file.getName();
				
				// body
				RequestBody body = RequestBody.create(OCTET_STREAM, bDoc);
				
				// prepare request
				Request.Builder rb = new Request.Builder().url(url).put(body);
				
				// authentication
				String credential = Credentials.basic("svinchon", "Pa55word");
				rb.addHeader("Authorization", credential);
				
				// content type
				rb.addHeader("Content-type", "application/vnd.ms-outlook");
				
				// build request 
				Request request = rb.build();
				Utils.log(request.toString());
				
				// client
				OkHttpClient client = new OkHttpClient();
				client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
				client.setReadTimeout(timeOut, TimeUnit.SECONDS);
				
				// run request
				Response response = client.newCall(request).execute();
				
				Utils.log("response: "+response.message());				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public static String post(String url, byte[] data, String mediaType, String ticket, int timeOut) throws IOException {
		OkHttpClient client = new OkHttpClient();
		RequestBody body;
		if (mediaType.equals("application/json")) { body = RequestBody.create(JSON, data); } 
		else { body = RequestBody.create(OCTET_STREAM, data); }
		Request.Builder rb = new Request.Builder().url(url).post(body);
		if (ticket != null) { rb.addHeader("CPTV-TICKET", ticket); }
		Request request = rb.build();
		client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
		client.setReadTimeout(timeOut, TimeUnit.SECONDS);
		Response response = client.newCall(request).execute();
		return response.body().string();
	}*/
}
