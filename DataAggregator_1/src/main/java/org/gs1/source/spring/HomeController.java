package org.gs1.source.spring;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome FSS Data Aggregator no.1! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "home";
	}

	@RequestMapping(value = "/v1/ProductData/gtin/{gtin:.+}", method = RequestMethod.GET)
	public ModelAndView queryByGTIN(@PathVariable("gtin") String gtin,
			@RequestParam(value = "targetMarket") String targetMarketString,
			@RequestParam(value = "dataVersion", defaultValue = "1.1") String dataVersion,
			@RequestParam(value = "clientGln", defaultValue = "0") String clientGln,
			@RequestParam(value = "mac", defaultValue = "0") String mac) throws Exception {

		ModelAndView model = new ModelAndView();
		model.setViewName("queryByGTIN");

		MongoDataBase mongo = new MongoDataBase();

		CountryCodeType targetMarket = new CountryCodeType();
		targetMarket.setCodeListVersion(dataVersion);
		targetMarket.setValue(targetMarketString);

		String str = mongo.findData(gtin, targetMarket);

		if(str == null){
			model.addObject("ResponseString", "There is no product of GTIN " + gtin + ".");
			model.addObject("payloadMac", "0");

			return model;
		}

		if(clientGln != mac){
			String key = mongo.findKeyServer(clientGln);
			String mac_url = "v1/ProductData/gtin/" + gtin + "?targetMarket=" + targetMarketString
					+ "&dataVersion=" + dataVersion + "&clientGln=" + clientGln;
			MacEncode macEncode = new MacEncode();
			String mac_check = macEncode.encode(key, mac_url);

			if(mac != mac_check){
				model.addObject("ResponseString", "Exception: mac is not identical.");
				model.addObject("payloadMac", "0");
				return model;
			}

			String mac_payload = macEncode.encode(key, str);

			model.addObject("ResponseString", str);
			model.addObject("payloadMac", mac_payload);
		}
		else{
			model.addObject("ResponseString", str);
			model.addObject("payloadMac", "0");
		}
		
		return model;

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Locale locale, Model model) {

		return "register";

	}

	@RequestMapping(value = "/registered", method = RequestMethod.POST)
	public String registered(@Validated TSDQueryByGTINResponseType rs, Model model) {

		model.addAttribute("productData", rs.getProductData());
		return "registered";

	}

}
