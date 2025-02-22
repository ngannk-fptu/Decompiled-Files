/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

public class HTTPConstants {
    public static final String HEADER_PROTOCOL_10 = "HTTP/1.0";
    public static final String HEADER_PROTOCOL_11 = "HTTP/1.1";
    public static final String HEADER_PROTOCOL_V10 = "1.0".intern();
    public static final String HEADER_PROTOCOL_V11 = "1.1".intern();
    public static final String HEADER_POST = "POST";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_CONTENT_DESCRIPTION = "Content-Description";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    public static final String HEADER_CONTENT_TYPE_JMS = "ContentType";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_LOCATION = "Content-Location";
    public static final String HEADER_CONTENT_ID = "Content-Id";
    public static final String HEADER_SOAP_ACTION = "SOAPAction";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String HEADER_EXPECT = "Expect";
    public static final String HEADER_EXPECT_100_Continue = "100-continue";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_CACHE_CONTROL_NOCACHE = "no-cache";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_LOCATION = "Location";
    public static final String REQUEST_HEADERS = "HTTP-Request-Headers";
    public static final String RESPONSE_HEADERS = "HTTP-Response-Headers";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding".intern();
    public static final String HEADER_TRANSFER_ENCODING_CHUNKED = "chunked".intern();
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_CONNECTION_CLOSE = "close".intern();
    public static final String HEADER_CONNECTION_KEEPALIVE = "Keep-Alive".intern();
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_TEXT_ALL = "text/*";
    public static final String HEADER_ACCEPT_APPL_SOAP = "application/soap+xml";
    public static final String HEADER_ACCEPT_MULTIPART_RELATED = "multipart/related";
    public static final String HEADER_ACCEPT_APPLICATION_DIME = "application/dime";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_COOKIE2 = "Cookie2";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_SET_COOKIE2 = "Set-Cookie2";
    public static String MC_HTTP_STATUS_CODE = "transport.http.statusCode";
    public static String MC_HTTP_STATUS_MESSAGE = "transport.http.statusMessage";
    public static String MC_HTTP_SERVLET = "transport.http.servlet";
    public static String MC_HTTP_SERVLETREQUEST = "transport.http.servletRequest";
    public static String MC_HTTP_SERVLETRESPONSE = "transport.http.servletResponse";
    public static String MC_HTTP_SERVLETLOCATION = "transport.http.servletLocation";
    public static String MC_HTTP_SERVLETPATHINFO = "transport.http.servletPathInfo";
    public static String MC_HTTP_SOAPACTION = "javax.xml.rpc.soap.http.soapaction.uri";
    public static final String HEADER_DEFAULT_CHAR_ENCODING = "iso-8859-1";
    public static final String PLUGIN_NAME = "transport.http.plugin.pluginName";
    public static final String PLUGIN_SERVICE_NAME = "transport.http.plugin.serviceName";
    public static final String PLUGIN_IS_DEVELOPMENT = "transport.http.plugin.isDevelopment";
    public static final String PLUGIN_ENABLE_LIST = "transport.http.plugin.enableList";
    public static final String PLUGIN_ENGINE = "transport.http.plugin.engine";
    public static final String PLUGIN_WRITER = "transport.http.plugin.writer";
    public static final String PLUGIN_LOG = "transport.http.plugin.log";
    public static final String PLUGIN_EXCEPTION_LOG = "transport.http.plugin.exceptionLog";
}

