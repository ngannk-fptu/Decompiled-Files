/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import javax.xml.namespace.QName;

public class SOAP12Constants {
    public static final String NS_URI_SOAP12 = "http://schemas.xmlsoap.org/wsdl/soap12/";
    public static final String ELEM_BODY = "body";
    public static final String ELEM_HEADER = "header";
    public static final String ELEM_HEADER_FAULT = "headerfault";
    public static final String ELEM_ADDRESS = "address";
    public static final QName Q_ELEM_SOAP_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "binding");
    public static final QName Q_ELEM_SOAP_BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "body");
    public static final QName Q_ELEM_SOAP_HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "header");
    public static final QName Q_ELEM_SOAP_HEADER_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "headerfault");
    public static final QName Q_ELEM_SOAP_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
    public static final QName Q_ELEM_SOAP_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "operation");
    public static final QName Q_ELEM_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "fault");
    public static final String ATTR_TRANSPORT = "transport";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_SOAP_ACTION = "soapAction";
    public static final String ATTR_SOAP_ACTION_REQUIRED = "soapActionRequired";
    public static final String ATTR_PARTS = "parts";
    public static final String ATTR_USE = "use";
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";
    public static final String ATTR_PART = "part";
}

