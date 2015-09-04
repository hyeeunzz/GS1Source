<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"%>${ResponseString}

<%
	String mac = "${payloadMac}";
	if(mac != "0")
		response.setHeader("GS1-MAC", mac);
%>