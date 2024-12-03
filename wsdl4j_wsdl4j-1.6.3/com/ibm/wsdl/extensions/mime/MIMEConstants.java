/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

import javax.xml.namespace.QName;

public class MIMEConstants {
    public static final String NS_URI_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
    public static final String ELEM_CONTENT = "content";
    public static final String ELEM_MULTIPART_RELATED = "multipartRelated";
    public static final String ELEM_MIME_XML = "mimeXml";
    public static final QName Q_ELEM_MIME_CONTENT = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "content");
    public static final QName Q_ELEM_MIME_MULTIPART_RELATED = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "multipartRelated");
    public static final QName Q_ELEM_MIME_PART = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "part");
    public static final QName Q_ELEM_MIME_MIME_XML = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "mimeXml");
    public static final String ATTR_PART = "part";
}

