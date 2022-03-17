package com.project.iotproject.CoAPServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import it.unipr.netsec.mjcoap.coap.message.CoapMessageFactory;
import it.unipr.netsec.mjcoap.coap.message.CoapMessageType;
import it.unipr.netsec.mjcoap.coap.message.CoapRequest;
import it.unipr.netsec.mjcoap.coap.message.CoapRequestMethod;
import it.unipr.netsec.mjcoap.coap.message.CoapResponse;
import it.unipr.netsec.mjcoap.coap.message.CoapResponseCode;
import it.unipr.netsec.mjcoap.coap.server.AbstractCoapServer;
import it.unipr.netsec.mjcoap.coap.server.CoapResource;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.project.iotproject.Util.timeCalculations;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CoAPServer extends AbstractCoapServer {

    timeCalculations sensorTimeCalculation = new timeCalculations("sensorTimeCalculation.txt", 50, true);
    timeCalculations totalTimeCalculation = new timeCalculations("totalTimeCalculation.txt", 50, true);

	public CoAPServer() throws IOException, InterruptedException, URISyntaxException{
		System.out.println("CoAPServer Start!");
		handleGetRequest(new CoapRequest(CoapMessageType.NON, CoapRequestMethod.GET, 1));
	}

  	@Override
	protected void handleGetRequest(CoapRequest req) {
		List<String> resourceList = new ArrayList<>(Arrays.asList("/cputemp", "/temp", "humidity", "/time", "/rand", "/message"));
		String resource_name = req.getRequestUriPath(); //Token UriPath /humidity
		if(resourceList.contains(resource_name)){
			//sensorTimeCalculation.startTimer();
			//totalTimeCalculation.globalStartTimer();
			CoapResponse resp = CoapMessageFactory.createResponse(req, CoapResponseCode._2_05_Content);
			if (resource_name.equals("/cputemp")) {
				String payload_Content1 = "CPUTemperature: " + getCpuTemp() + "C";
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content1.getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			if (resource_name.equals("/temp")) {
				String payload_Content2 = "Temperature, Svall: " + makeWeatherAPICall("temp") + "C";
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content2.toString().getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			if (resource_name.equals("/humidity")) {
				  String payload_Content3 = "Humidity, Svall: " + makeWeatherAPICall("humidity") + "%";
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content3.toString().getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			if (resource_name.equals("/time")) {
				 String payload_Content4 = "Current Time(CET): " + getTimeNow();
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content4.getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			if (resource_name.equals("/rand")) {
				 String payload_Content5 = "Random Number: " + ThreadLocalRandom.current().nextInt(0, 100);
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content5.getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			if (resource_name.equals("/message")) {
				String payload_Content6 = "PayloadMessage: " + new String(req.getPayload());
				resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, payload_Content6.getBytes());
				sensorTimeCalculation.startTimer();
				totalTimeCalculation.globalStartTimer();
				respond(req, resp);
			}
			sensorTimeCalculation.stopTimer();
		}
		else {
			//totalTimeCalculation.globalStartTimer();
			respond(req, CoapMessageFactory.createResponse(req, CoapResponseCode._4_04_Not_Found));
		}
	}


  	public static String getTimeNow(){
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
    	LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
  	}

 	public static Long makeWeatherAPICall(String weather){
		String apiEndPoint="http://api.openweathermap.org/data/2.5/weather?q=Sundsvall,se&units=metric";
		String apiKey="f25a8938524df06fdf4a776588b0496a";
		StringBuilder requestBuilder=new StringBuilder(apiEndPoint);
		URIBuilder builder;
    try {
		builder = new URIBuilder(requestBuilder.toString());
		builder.setParameter("appid", apiKey);
		HttpGet get = new HttpGet(builder.build());
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(get);    
		String responseString = new BasicResponseHandler().handleResponse(response);
		JSONObject myObject = new JSONObject(responseString);
		return Math.round(myObject.getJSONObject("main").getDouble(weather));
    } catch (URISyntaxException | IOException e) {
      	e.printStackTrace();
    }
    return null;
  }

  	public static String getCpuTemp(){
    	List<Cpu> cpus = JSensors.get.components().cpus;
    	if (cpus.size() > 0) {
      		Cpu cpu = cpus.get(0);
      		if (cpu.sensors.temperatures != null && cpu.sensors.temperatures.size() > 0) {
        		for (final Temperature temp : cpu.sensors.temperatures) {
          			if (temp.value != null) {
            			return String.valueOf(temp.value.intValue());
          			}
        		}	
      		}
    	}
    	return "NOT_DETECTED";
  	}


  	public static void timelineRequestHttpClient() throws Exception {
		//set up the end point
		String apiEndPoint="https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
		String location="Sundsvall, SE";
		String unitGroup="metric"; //us,metric,uk 
		String apiKey="CFHSJ6LJXQ3QK74LDMHHESW3T";
	
		StringBuilder requestBuilder=new StringBuilder(apiEndPoint);
		requestBuilder.append(URLEncoder.encode(location, StandardCharsets.UTF_8.toString()));
		

		URIBuilder builder = new URIBuilder(requestBuilder.toString());
		builder.setParameter("unitGroup", unitGroup)
			.setParameter("key", apiKey);

		HttpGet get = new HttpGet(builder.build());
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(get);    
		String rawResult=null;

		try {
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.printf("Bad response status code:%d%n", response.getStatusLine().getStatusCode());
				return;
			}
			HttpEntity entity = response.getEntity();
		    if (entity != null) {
		    	rawResult=EntityUtils.toString(entity, Charset.forName("utf-8"));
		    } 
		} finally {
			response.close();
		}
		
		parseTimelineJson(rawResult);
		
	}

  	private static void parseTimelineJson(String rawResult) {
		if (rawResult==null || rawResult.isEmpty()) {
			System.out.printf("No raw data%n");
			return;
		}
		JSONObject timelineResponse = new JSONObject(rawResult);
		ZoneId zoneId=ZoneId.of(timelineResponse.getString("timezone"));
		System.out.printf("Weather data for: %s%n", timelineResponse.getString("resolvedAddress"));
		JSONArray values=timelineResponse.getJSONArray("days");
		System.out.printf("Date\tMaxTemp\tMinTemp\tPrecip\tSource%n");
		for (int i = 0; i < values.length(); i++) {
			JSONObject dayValue = values.getJSONObject(i);
          	ZonedDateTime datetime=ZonedDateTime.ofInstant(Instant.ofEpochSecond(dayValue.getLong("datetimeEpoch")), zoneId);
          	double maxtemp=dayValue.getDouble("tempmax");
          	double mintemp=dayValue.getDouble("tempmin");
          	double pop=dayValue.getDouble("precip");
          	String source=dayValue.getString("source");
          	System.out.printf("%s\t%.1f\t%.1f\t%.1f\t%s%n", datetime.format(DateTimeFormatter.ISO_LOCAL_DATE), maxtemp, mintemp, pop,source );
        }
	}


}
