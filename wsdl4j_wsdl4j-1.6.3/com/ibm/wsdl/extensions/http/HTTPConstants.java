/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.http;

import javax.xml.namespace.QName;

public class HTTPConstants {
    public static final String NS_URI_HTTP = "http://schemas.xmlsoap.org/wsdl/http/";
    public static final String ELEM_ADDRESS = "address";
    public static final String ELEM_URL_ENCODED = "urlEncoded";
    public static final String ELEM_URL_REPLACEMENT = "urlReplacement";
    public static final QName Q_ELEM_HTTP_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/http/", "binding");
    public static final QName Q_ELEM_HTTP_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/http/", "operation");
    public static final QName Q_ELEM_HTTP_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/http/", "address");
    public static final QName Q_ELEM_HTTP_URL_ENCODED = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlEncoded");
    public static final QName Q_ELEM_HTTP_URL_REPLACEMENT = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlReplacement");
    public static final String ATTR_VERB = "verb";
}

