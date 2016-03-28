package com.diy.mlMailDigger;

import java.io.IOException;
//import java.io.InputStream;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;

import org.apache.commons.io.input.ReaderInputStream;

import com.diy.helpers.Utils;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.RequestBody;

public class MarkLogicRESTServiceUtility {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// test 1
		String vars = "{\"word1\":\"hello\",\"word2\":\"world\"}";
		String xquery = "xquery version \"1.0-ml\";"
				+ "declare variable $word1 as xs:string external;"
				+ "declare variable $word2 as xs:string external;"
				+ "fn:concat($word1, \" \", $word2)";
		// test 2
		xquery = "<test>{fn:doc(\"/outlook/0000000023400920E89EEF49930B237B81A9BDB30700B6F469E65D34E04689CA80A0468500390003E06301FC00001E84F2DA634558479B5DB0EC5103632D0009AB8858A10000.xml\")/Metadata/Subject}</test>";
		// test 3
		xquery = "import module namespace search = \"http://marklogic.com/appservices/search\" at \"/MarkLogic/appservices/search/search.xqy\"; "
				+ "let $options := "
				+ "<options xmlns=\"http://marklogic.com/appservices/search\"> "
				+ "<page-length>5</page-length> "
				+ "<sort-order type=\"xs:date\" direction=\"descending\"> "
				+ "<element ns=\"\" name=\"SentOnDate\"/> "
				+ "</sort-order> "
				+ "</options> "
				+ "return "
				+ "<result> "
				+ "{ "
				+ "for $r in search:search(\"\", $options)/search:result "
				+ "return "
				+ "<item> "
				+ "{ fn:doc(data($r/@uri))/Metadata/Subject, fn:doc(data($r/@uri))/Metadata/SentOnDate } "
				+ "</item> "
				+ "} "
				+ "</result>";
		xquery = "import module namespace search = \"http://marklogic.com/appservices/search\" at \"/MarkLogic/appservices/search/search.xqy\"; "
				+ "let $options := "
				+ "<options xmlns=\"http://marklogic.com/appservices/search\"> "
				+ "<page-length>5</page-length> "
				+ "<sort-order type=\"xs:date\" direction=\"descending\"> "
				+ "<element ns=\"\" name=\"SentOnDate\"/> "
				+ "</sort-order> "
				+ "<extract-document-data> "
				+ "<extract-path>/Metadata/Subject</extract-path> "
				+ "<extract-path>/Metadata/SentOnDate</extract-path> "
				+ "</extract-document-data> "
				+ "</options> "
				+ "return "
				+ "search:search(\"\", $options)";
		String password = "Pa55word";
		String user ="svinchon";
		new MarkLogicRESTServiceUtility().getAllMails();
		/*new MarkLogicRESTServiceUtility().callEvalService(
				xquery,
				vars,
				user,
				password
		);*/
	}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType XML = MediaType.parse("application/xml");
	public static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");

	public String getAllMails() {
		Utils.log("getAllMails", this);
		String ret = null;
		try {
			/* url 					*/ String url = "http://localhost:8000/LATEST/search?q=Documentum";
			/* body					*/ //RequestBody body;
			/* byte array body 		*/ //byte[] bBody = null; if (bBody!=null) body = RequestBody.create(OCTET_STREAM, bBody);
			/* string/json body		*/ //String sBody = null; sBody = RequestBody.create(JSON, sBody); 
			/* prepare get request	*/ //Request.Builder rb = new Request.Builder().url(url).get(); //.put(body);
			/* put request 			*/ //Request.Builder rb = new Request.Builder().url(url).put(body);
			/* authentication 		*/ String credential = Credentials.basic("svinchon", "Pa55word");
			String sOptions = ""
			+ "<search xmlns=\"http://marklogic.com/appservices/search\">"
			+ "<options> "
			+ "<page-length>5</page-length> "
			+ "<sort-order type=\"xs:date\" direction=\"descending\"> "
			+ "<element ns=\"\" name=\"SentOnDate\"/> "
			+ "</sort-order> "
			+ "<extract-document-data> "
			+ "<extract-path>/Metadata/Subject</extract-path> "
			+ "<extract-path>/Metadata/SentOnDate</extract-path> "
			+ "</extract-document-data> "
			+ "</options> "
			+ "</search>";
			/* string/xml body		*/ RequestBody body  = RequestBody.create(XML, sOptions);
			/* post request			*/ Request.Builder rb = new Request.Builder().url(url).post(body);
			/* authentication 		*/ rb.addHeader("Authorization", credential);
			/* content type 		*/ rb.addHeader("Content-type", "application/xml");
			/* accept 				*/ rb.addHeader("Accept", "application/json");
			/* build request 		*/ Request request = rb.build();
			
			Utils.log("request: "+request.urlString(), this);
			
			/* client 				*/ OkHttpClient client = new OkHttpClient();
			/* client timeouts		*/ client.setConnectTimeout(3600, TimeUnit.SECONDS);
			/* client timeouts		*/ client.setReadTimeout(3600, TimeUnit.SECONDS);
			/* run request 			*/ Response response = client.newCall(request).execute();

			/* extract body			*/ ret = response.body().string();
			
			Utils.log("response: "+ret, this);
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return ret;
	}
	
	public String callService(String url, String method, String body, String user, String password, String contentType, String accept) {
		Utils.log("callService", this);
		Utils.log("url: "+url, this);
		Utils.log("method: "+method, this);
		Utils.log("contentType: "+contentType, this);
		Utils.log("accept: "+accept, this);
		
		String ret = null;
		try {

			/* create request		*/ Request.Builder rb = new Request.Builder().url(url);
			/* authentication 		*/ String credential = Credentials.basic(user, password);
			/* authentication 		*/ rb.addHeader("Authorization", credential);
			/* content type 		*/ rb.addHeader("Content-type", contentType);
			/* accept 				*/ rb.addHeader("Accept", accept);

			if (method.toLowerCase().equals("get")) {
				/* prepare get request	*/ rb.get();		
			} else {
				/* body					*/ RequestBody requestBody;
				if (contentType.toLowerCase().equals("application/json")) {
					requestBody = RequestBody.create(JSON, body);
				} else if (contentType.toLowerCase().equals("application/xml")) {
					requestBody = RequestBody.create(XML, body);
				} else {
					//if (contentType.toLowerCase().equals("application/octet-stream")) {
					requestBody = RequestBody.create(OCTET_STREAM, body);
				}
				/* prepare post request	*/ rb.post(requestBody);
				/* prepare put request 	*/ //rb.put(body);
			}

			/*  build request 		*/ Request request = rb.build();
			
			Utils.log("request: "+request.urlString(), this);
			
			/* client 				*/ OkHttpClient client = new OkHttpClient();
			/* client timeouts		*/ client.setConnectTimeout(3600, TimeUnit.SECONDS);
			/* client timeouts		*/ client.setReadTimeout(3600, TimeUnit.SECONDS);
			/* run request 			*/ Response response = client.newCall(request).execute();

			if (accept.indexOf("multipart/mixed") >= 0) {
				/* extract body			*/ ret = response.body().string();
				StringReader sr = new StringReader(ret);
				ByteArrayDataSource ds = new ByteArrayDataSource(
						new ReaderInputStream(sr),
						"multipart/mixed"
				);
				
				MimeMultipart multipart = new MimeMultipart(ds);
				
				Utils.log("part count: "+multipart.getCount(), this);
				
				for (int i=0;i<multipart.getCount();i++) {
					BodyPart part = multipart.getBodyPart(i);
					SharedByteArrayInputStream is = (SharedByteArrayInputStream)part.getContent();
					byte[] bDoc = new byte[is.available()];
					is.read(bDoc);
					String partContent = new String(bDoc);//""+part.getContent();//.getContentType();//.getContent().toString();
					
					Utils.log("partContent: "+partContent.substring(0, 100), this);
				}			
			} else {
				/* extract body			*/ ret = response.body().string();
				Utils.log("response: "+ret, this);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}						
		return ret;
	}
	
	public String callEvalService(
			//String url,
			//String method,
			String xquery,
			String vars,
			String user,
			String password//,
			//String contentType,
			//String accept
		) {
		String url = "http://localhost:8000/LATEST/eval";
		String contentType = "application/x-www-form-urlencoded";
		//String accept = "multipart/mixed";
		String accept = "application/json";
		
		Utils.log("callEvalService", this);
		Utils.log("url: "+url, this);
		Utils.log("contentType: "+contentType, this);
		Utils.log("accept: "+accept, this);
		
		String ret = null;
		
		try {

			/* create request		*/ Request.Builder rb = new Request.Builder().url(url);
			/* authentication 		*/ String credential = Credentials.basic(user, password);
			/* authentication 		*/ rb.addHeader("Authorization", credential);
			/* content type 		*/ rb.addHeader("Content-type", contentType);
			/* accept 				*/ rb.addHeader("Accept", accept);

			RequestBody formBody = new FormEncodingBuilder()
					.add("xquery", xquery)
		            .add("vars", vars)
		            .build();
			
			Request request = rb
		            .post(formBody)
		            .build();

			Utils.log("request: "+request.urlString(), this);
			
			/* client 				*/ OkHttpClient client = new OkHttpClient();
			/* client timeouts		*/ client.setConnectTimeout(3600, TimeUnit.SECONDS);
			/* client timeouts		*/ client.setReadTimeout(3600, TimeUnit.SECONDS);
			/* run request 			*/ Response response = client.newCall(request).execute();

			/* extract body			*/ ret = response.body().string();

			Utils.log("response: "+ret, this);

			StringReader sr = new StringReader(ret);
			ByteArrayDataSource ds = new ByteArrayDataSource(
					new ReaderInputStream(sr),
					"multipart/mixed"
			);
			MimeMultipart multipart = new MimeMultipart(ds);
			
			Utils.log("part count: "+multipart.getCount(), this);
			
			for (int i=0;i<multipart.getCount();i++) {
				BodyPart part = multipart.getBodyPart(i);
				SharedByteArrayInputStream is = (SharedByteArrayInputStream)part.getContent();
				byte[] bDoc = new byte[is.available()];
				is.read(bDoc);
				String partContent = new String(bDoc);//""+part.getContent();//.getContentType();//.getContent().toString();
				
				Utils.log("partContent: "+partContent.substring(0, 50), this);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
