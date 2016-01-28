<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registered Page</title>
</head>
<body>
<%@page import="org.gs1.source.service.mongo.MongoInsert" %>
<%@page import="java.util.*" %>
<%@page import="javax.xml.datatype.Duration" %>
<%@page import="org.gs1.source.tsd.*" %>
<%@page import="org.gs1.source.service.util.XmlValidator" %>
<%
	String xmldata = request.getParameter("xmldata");
	XmlValidator validation = new XmlValidator();

	if (xmldata == "")
		out.println("<h2>Please fill the blank.<h2>");
	else if (validation.xmlValidation(xmldata) == false)
		out.println("<h2>The xml data is not valid.<h2>");
	else {
		MongoInsert mongo = new MongoInsert();
		String s = mongo.insertData(xmldata);
		if (s == null)
	out.println("<h1>The product is already exists.<h1>");
		else
	out.println("<h1>Product Data of GTIN " + s + " is registered at Aggregator.<h1>");
	}
%>
</body>
</html>