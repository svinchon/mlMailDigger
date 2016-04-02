package com.diy.mlMailDigger;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import java.util.concurrent.TimeUnit;
//import javax.ws.rs.QueryParam;
//import com.sun.jersey.core.util.Base64;

import com.diy.helpers.Utils;

@Path("/services")
public class RESTServices {
	
	public static void main(String[] args) {
		//new RESTServices().getMostRecentMails();
		new RESTServices().getMail("Corp.Pgm.2015.MinorUIDev-Sent@20150709-Exp@1603251410-Idx@98.xml.json");
	}

	@Context ServletContext context;

	@GET
	@Path("/getMostRecentMails.json")
	//@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/json;charset=utf-8")//MediaType.APPLICATION_JSON)
	// @QueryParam("Scenario") String scenario
	public Response getMostRecentMails() {
		ResourceBundle r = ResourceBundle.getBundle("mlMailDigger");
		String sHomeFolder = r.getString("HomeFolder");
		byte[] output = null;
		int code = 200;
		try {
			MarkLogicRESTServiceUtility mlUtil = new MarkLogicRESTServiceUtility();
			Utils.log("getMostRecentMails", this);
			//output = mlrsu.getAllMails().getBytes();
			
			String sBody = new String(
				Utils.convertFile2Byte(
						//context.getRealPath("/")+"\\sqy\\MostRecentMails.sqy"
						sHomeFolder+"/sqy/MostRecentMails.sqy"
				)
			);
			Utils.log("sqy:"+sBody.replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", ""), this);	
			output = mlUtil.callService(
					/* url 			*/	"http://localhost:8000/LATEST/search",
					/* method		*/	"post",
					/* body			*/	sBody,
					/* user			*/	"svinchon",
					/* password		*/	"Pa55word",
					/* contentType	*/	"application/xml",
					/* accept		*/	"application/json; charset=utf-8" //multipart/mixed; boundary=BOUNDARY"
			).getBytes("utf-8");
			//Utils.log("response:" + new String(output), this);
		} catch (Exception e) {
			e.printStackTrace();
			String str = "{ error: true }";
			output = str.getBytes();
			code = 400;
		}
		return Response
				.status(code)
				.entity(output)
				//.header("Access-Control-Allow-Origin", "http://localhost:18080")
				.header("Access-Control-Allow-Origin", "http://localhost:8000")
				.build();
	}
	
	@GET
	@Path("/getMail.json")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/json;charset=utf-8")//MediaType.APPLICATION_JSON)
	public Response getMail(
			@QueryParam("id") String id
	) {
		byte[] output = null;
		int code = 200;
		try {
			MarkLogicRESTServiceUtility mlUtil = new MarkLogicRESTServiceUtility();
			Utils.log("getMail", this);
			//output = mlrsu.getAllMails().getBytes();
			output = mlUtil.callService(
					/* url 			*/	"http://localhost:8000/LATEST/documents?uri=/mlMailDigger/"+id,
					/* method		*/	"get",
					/* body			*/	"",
					/* user			*/	"svinchon",
					/* password		*/	"Pa55word",
					/* contentType	*/	"application/xml",
					/* accept		*/	"application/json; charset=utf-8"
			).getBytes("utf-8");
			Utils.log("response:" + new String(output), this);
		} catch (Exception e) {
			e.printStackTrace();
			String str = "{ error: true }";
			output = str.getBytes();
			code = 400;
		}
		return Response
				.status(code)
				.entity(output)
				.header("Access-Control-Allow-Origin", "http://localhost:8000")
				//.header("Access-Control-Allow-Origin", "http://localhost:18080")
				.build();
	}

	@GET
	@Path("/getMailsSearchResults.json")
	@Produces("application/json;charset=utf-8")//MediaType.APPLICATION_JSON)
	public Response getMailsSearchResults(
		@QueryParam("Contains") String Contains,
		@QueryParam("From") String From,
		@QueryParam("Tags") String Tags
	
	) {
		ResourceBundle r = ResourceBundle.getBundle("mlMailDigger");
		String sHomeFolder = r.getString("HomeFolder");
		byte[] output = null;
		int code = 200;
		try {
			String sSearch = new String(Utils.convertFile2Byte(sHomeFolder+"/sqy/fragments.search-options.sqy"));
			String sContains = new String(Utils.convertFile2Byte(sHomeFolder+"/sqy/fragments.term-query.sqy"));
			String sFrom = new String(Utils.convertFile2Byte(sHomeFolder+"/sqy/fragments.word-query.sqy"));
			String sTags = new String(Utils.convertFile2Byte(sHomeFolder+"/sqy/fragments.properties-query.sqy"));
			String sQuery = "";
			if (!Contains.equals("")) { sQuery += sContains.replace("--VALUE--", Contains); }
			if (!From.equals("")) { sQuery += sFrom.replace("--FIELD--", "From").replace("--VALUE--", From); }
			if (!Tags.equals("")) { sQuery += sTags.replace("--VALUE--", Tags); }
			sSearch = sSearch.replace("--QUERY--", "\n"+sQuery);
			MarkLogicRESTServiceUtility mlUtil = new MarkLogicRESTServiceUtility();
			Utils.log("getMailsSearchResults", this);
			//output = mlrsu.getAllMails().getBytes();
			
			String sBody = sSearch;
			Utils.log("sqy:"+sBody.replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", ""), this);	
			output = mlUtil.callService(
					/* url 			*/	"http://localhost:8000/LATEST/search",
					/* method		*/	"post",
					/* body			*/	sBody,
					/* user			*/	"svinchon",
					/* password		*/	"Pa55word",
					/* contentType	*/	"application/xml",
					/* accept		*/	"application/json; charset=utf-8" //multipart/mixed; boundary=BOUNDARY"
			).getBytes("utf-8");
			//Utils.log("response:" + new String(output), this);
		} catch (Exception e) {
			e.printStackTrace();
			String str = "{ error: true }";
			output = str.getBytes();
			code = 400;
		}
		return Response
				.status(code)
				.entity(output)
				//.header("Access-Control-Allow-Origin", "http://localhost:18080")
				.header("Access-Control-Allow-Origin", "http://localhost:8000")
				.build();
	}
	
	/*
	@GET
	@Path("/getAllMails.json")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	// @QueryParam("Scenario") String scenario
	public Response getAllMails() {
		byte[] output = null;
		int code = 200;
		try {
			MarkLogicRESTServiceUtility mlUtil = new MarkLogicRESTServiceUtility();
			Utils.log("getAllMails", this);
			//output = mlrsu.getAllMails().getBytes();
			output = mlUtil.callService(
					/* url 			*	"http://localhost:8000/LATEST/search",
					/* method		*	"get",
					/* body			*	"",
					/* user			*	"svinchon",
					/* password		*	"Pa55word",
					/* contentType	*	"application/xml",
					/* accept		*	"application/json"
			).getBytes();
			Utils.log("response:" + new String(output), this);
		} catch (Exception e) {
			e.printStackTrace();
			String str = "{ error: true }";
			output = str.getBytes();
			code = 400;
		}
		return Response.status(code).entity(output).build();
	}
	*/

}
