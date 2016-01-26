package org.gs1.source.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.gs1.source.service.mongo.MongoInsert;
import org.gs1.source.service.mongo.ServerKey;
import org.gs1.source.service.util.MacEncode;
import org.gs1.source.service.util.MacUrlGenerator;
import org.gs1.source.service.util.XmlValidation;
import org.gs1.source.tsd.CountryCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Data Aggregator home page
	 * @param locale
	 * @param model
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws IOException {
		
		logger.info("Welcome FSS Data Aggregator! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	/**
	 * Product Data query page
	 * @param gtin
	 * @param targetMarketString
	 * @param dataVersion
	 * @param clientGln
	 * @param mac
	 * @return
	 * @throws Exception
	 */
	@Async
	@RequestMapping(value = "/v1/ProductData/gtin/{gtin:.+}", method = RequestMethod.GET)
	public Callable<ModelAndView> queryByGTIN(@PathVariable("gtin") final String gtin,
			@RequestParam(value = "targetMarket") final String targetMarketString,
			@RequestParam(value = "dataVersion", defaultValue = "1.1") final String dataVersion,
			@RequestParam(value = "clientGln", defaultValue = "0") final String clientGln,
			@RequestParam(value = "mac", defaultValue = "0") final String mac) throws Exception {

		return new Callable<ModelAndView>() {

			@Override
			public ModelAndView call() throws Exception {
				
				logger.info("Start query...");
				
				ModelAndView model = new ModelAndView();
				model.setViewName("queryByGTIN");

				CountryCodeType targetMarket = new CountryCodeType();
				targetMarket.setCodeListVersion(dataVersion);
				targetMarket.setValue(targetMarketString);

				DataQueryFactory dataQueryFactory = new DataQueryFactory();
				QueryService queryService = new QueryService(dataQueryFactory, "mongo");
				String str = queryService.query(gtin, targetMarket, dataVersion);

				//There is no product data of such gtin & target market
				if(str == null){
					model.addObject("ResponseString", "There is no product of GTIN " + gtin + ".");
					model.addObject("payloadMac", "0");
					
					logger.info("Complete query...(No data)");

					return model;
				}

				//case 1) AAQI interface
				if(clientGln.compareTo("0") != 0){
					ServerKey server = new ServerKey();
					String key = server.queryKey(clientGln);
					
					MacUrlGenerator macUrlGenerator = new MacUrlGenerator(gtin, targetMarket, dataVersion, clientGln);
					String mac_url = macUrlGenerator.getMacUrl();
					
					MacEncode macEncode = new MacEncode();
					String mac_check = macEncode.encode(key, mac_url);

					if(mac.compareTo(mac_check) != 0){
						model.addObject("ResponseString", "Exception: mac is not identical.");
						model.addObject("payloadMac", "0");
						
						logger.info("Complete query...(Not Authenticated)");
						
						return model;
					}

					String mac_payload = macEncode.encode(key, str);

					model.addObject("ResponseString", str);
					model.addObject("payloadMac", mac_payload);
				}
				//case 2) Not AAQI interface, just present product data in web
				else{
					model.addObject("ResponseString", str);
					model.addObject("payloadMac", "0");
				}
				
				logger.info("Complete query...");
				
				return model;
				
			}
			
		};
		
	}
	
	/**
	 * Product data register page
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Locale locale, Model model) {

		return "register";

	}
	
	/**
	 * Product data registered page
	 * @param request
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/registered", method = RequestMethod.POST)
	public String registered(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
		
		request.setCharacterEncoding("UTF-8");
		return "registered";
		
	}
	
	@RequestMapping(value = "/postdata", method = RequestMethod.POST, consumes = "application/xml")
	public ResponseEntity<String> postData(@RequestBody String xmldata) throws Exception {
		
//		if (userID.compareTo("kaist") != 0 || password.compareTo("reslresl") != 0) {
//			System.out.println("Not Authenticated");
//			return new ResponseEntity<String>(new String("Not Authenticated"), HttpStatus.BAD_REQUEST);
//		}
		
		XmlValidation validation = new XmlValidation();

		if (xmldata == ""){
			System.out.println("The string is empty.");
			return new ResponseEntity<String>(new String("The string is empty."), HttpStatus.OK);
		}
		else if (validation.xmlValidation(xmldata) == false){
		
			System.out.println("The xml data is not valid.");
			return new ResponseEntity<String>(new String("The xml data is not valid."), HttpStatus.OK);
		}
		else {
			MongoInsert mongoInsert = new MongoInsert();
			String s = mongoInsert.insertData(xmldata);
			if (s == null) {
				
				System.out.println("The product is already exists.");
				return new ResponseEntity<String>(new String("The product is already exists."), HttpStatus.OK);
				
			}else {
				
				System.out.println("Product Data of GTIN " + s + " is registered at Aggregator.");
				return new ResponseEntity<String>(new String("Product Data of GTIN " + s + " is registered at Aggregator."), HttpStatus.OK);
			}
		}

	}

}
