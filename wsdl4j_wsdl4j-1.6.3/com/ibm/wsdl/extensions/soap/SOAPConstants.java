/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import javax.xml.namespace.QName;

public class SOAPConstants {
    public static final String NS_URI_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String ELEM_BODY = "body";
    public static final String ELEM_HEADER = "header";
    public static final String ELEM_HEADER_FAULT = "headerfault";
    public static final String ELEM_ADDRESS = "address";
    public static final QName Q_ELEM_SOAP_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
    public static final QName Q_ELEM_SOAP_BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "body");
    public static final QName Q_ELEM_SOAP_HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "header");
    public static final QName Q_ELEM_SOAP_HEADER_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "headerfault");
    public static final QName Q_ELEM_SOAP_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
    public static final QName Q_ELEM_SOAP_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "operation");
    public static final QName Q_ELEM_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "fault");
    public static final String ATTR_TRANSPORT = "transport";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_SOAP_ACTION = "soapAction";
    public static final String ATTR_PARTS = "parts";
    public static final String ATTR_USE = "use";
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";
    public static final String ATTR_PART = "part";
}

