package com.config;


import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.cloud.bus.event.AckRemoteApplicationEvent;
import org.springframework.cloud.bus.event.RefreshListener;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.bus.event.TraceListener;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

@RestController
public class WebController {

	private RefreshScope scope;
	private ConfigurableApplicationContext configCon;
	private TraceListener traceListener;
	
	  
	  @EventListener	
	  @PostMapping("/refreshAll")
	  public String refreshAll(HttpServletRequest servletRequest){		  
		StringBuffer url = servletRequest.getRequestURL();
		String uri = servletRequest.getRequestURI();
		String target = url.toString().replaceAll(uri, "/monitor");
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		LinkedMultiValueMap<String, String> param =  new LinkedMultiValueMap<String, String>();	  
		param.add("path", "*");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);		
		ResponseEntity<String> response = restTemplate.postForEntity(target, request, String.class);	
		

		return "targetUrl "+target;
	  }
	  
	  
	  @GetMapping("/getConfig")
	  public String getConfig(String username, String repo, String rootPath){
		  //file name is not included in "rootPath"
		  String url = "https://api.github.com/repos/"+username+"/"+repo+"/contents"+rootPath;
		  RestTemplate restTemplate = new RestTemplate();		  
		  ResponseEntity<ArrayList> response = restTemplate.getForEntity(url, ArrayList.class);	 
		  LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) response.getBody().get(0);
		  Object name = json.get("name");
		  System.err.println("name "+name);
		  
		  String file_url = url+"/"+name;
		  System.err.println("file url "+file_url);
		  ResponseEntity<LinkedHashMap> fileJson = restTemplate.getForEntity(file_url, LinkedHashMap.class);
		  System.err.println("file json "+fileJson);
		  byte[] decodedContent = Base64.decodeBase64((String) fileJson.getBody().get("content"));
		  String ymlString = new String(decodedContent);
		  System.err.println("content : "+new String(decodedContent));
		  
		  Yaml yaml = new Yaml();
		  LinkedHashMap<String, Object> obj = yaml.load(ymlString);
		  
		  //LinkedHashMap obj is generated from yaml
		  System.err.println(" Obj from Yaml "+obj);
		  
		  String keys = "eureka.client.serviceUrl.defaultZone";
		  String newValue = "http://localhost:8781/eureka/";
		  
		 //Convert keys into ArrayList
		  String[] keyList = keys.split("\\.");
		  LinkedHashMap<String, Object> map = obj;
		  System.err.println("keyList "+keyList);
		  for(int i=0; i<keyList.length; i++) {		
			  System.err.println("keyList[i] "+keyList[i]);
			  if(!(map.get(keyList[i]) instanceof LinkedHashMap<?, ?>)) {
				  map.put(keyList[i], newValue);				  
			  }else {
				  map = (LinkedHashMap<String, Object>) map.get(keyList[i]);
				  System.err.println("map "+map);
			  }			  
		  }
		  
		  //depth 1 Structure
		  obj.put("a", "new aaaaa");
		  //System.err.println("a type :"+obj.get("a").getClass().getSimpleName());  //String
		  //System.err.println("eureka type :"+obj.get("eureka").getClass().getSimpleName()); //LinkedHashMap
		  
		  if(obj.get("eureka") instanceof LinkedHashMap<?, ?>) {
			  System.err.println("yes");
		  }
		  //depth 2
		  LinkedHashMap<String, String> obj2 = (LinkedHashMap<String, String>) obj.get("my");
		  obj2.put("greeting", "new greeting");

		  //depth 3 Structure
		  LinkedHashMap<String, LinkedHashMap<String, Object>> obj3= (LinkedHashMap<String, LinkedHashMap<String, Object>>) obj.get("eureka");		  
		  obj3.get("client").put("fetchRegistry", false);
		  
		  //depth4
		  //LinkedHashMap<String, Object> obj4 = obj3.get("client");
		  //obj4.get("serviceUrl").
		  //put("defaultZone", "http://localhost:8787/eureka/");
		  
		  
		  
		  //conversion result
		  System.err.println(" Obj from Yaml "+obj);
		  
		  return new String(decodedContent);
	  }
	  
	  

}