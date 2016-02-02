package org.gs1.source.service.aiqi;

import java.io.IOException;
import java.util.List;

import org.gs1.source.service.type.TSDIndexEntryType;
import org.gs1.source.service.type.TSDQueryIndexByGTINRequestType;
import org.gs1.source.service.type.TSDQueryIndexByGTINResponseType;
import org.gs1.source.service.type.TSDServiceReferenceType;
import org.gs1.source.service.util.CheckBit;
import org.gs1.source.tsd.Description200Type;
import org.gs1.source.tsd.TSDInvalidGTINExceptionType;
import org.gs1.source.tsd.TSDInvalidRequestExceptionType;
import org.gs1.source.tsd.TSDInvalidTargetMarketExceptionType;

public class AIQIProcessor implements AggregatorIndexQueryInterface {

	public TSDQueryIndexByGTINResponseType queryByGtin(TSDQueryIndexByGTINRequestType request) throws IOException {

		TSDQueryIndexByGTINResponseType rs = new TSDQueryIndexByGTINResponseType();

		String gtin = request.getGtin();
		String targetMarketValue = request.getTargetMarket().getValue();

		if(gtin == null || request.getTargetMarket() == null) {
			Description200Type reason = new Description200Type();
			reason.setLanguageCode("en");
			reason.setCodeListVersion("1.1");
			reason.setValue("Invalid Request (No GTIN or No TargetMarket");

			TSDInvalidRequestExceptionType exception = new TSDInvalidRequestExceptionType();
			exception.setExceptionReason(reason);
			rs.setInvalidRequestException(exception);
			return rs;
		}

		CheckBit checkBit = new CheckBit();
		if(gtin.length() < 13 || checkBit.check(gtin) == false) {
			Description200Type reason = new Description200Type();
			reason.setLanguageCode("en");
			reason.setCodeListVersion("1.1");
			reason.setValue("Invalid GTIN");

			TSDInvalidGTINExceptionType exception = new TSDInvalidGTINExceptionType();
			exception.setExceptionReason(reason);
			rs.setInvalidGTINException(exception);
			return rs;
		}

		if(targetMarketValue.length() != 3) {
			Description200Type reason = new Description200Type();
			reason.setLanguageCode("en");
			reason.setCodeListVersion("1.1");
			reason.setValue("Invalid TargetMarket");

			TSDInvalidTargetMarketExceptionType exception = new TSDInvalidTargetMarketExceptionType();
			exception.setExceptionReason(reason);
			rs.setInvalidTargetMarketException(exception);
			return rs;
		}

		ONSQuery onsQuery = new ONSQuery();

		if(gtin.length() < 14){
			gtin = "0" + gtin;

		}

		List<String> queryUrl = onsQuery.query(gtin);

		for(String r : queryUrl){
			if(r.toLowerCase().contains("http://www.ons.gs1.org/tsd/servicetype-aaqi")){
				String str = r.substring(r.lastIndexOf("!^.*$!") + 6, r.lastIndexOf("!"));

				TSDIndexEntryType indexEntry = new TSDIndexEntryType();
				indexEntry.setDataAggregatorService(new TSDServiceReferenceType(str));
				rs.setIndexEntry(indexEntry);
				return rs;
			}
		}

		return null;
	}

}
