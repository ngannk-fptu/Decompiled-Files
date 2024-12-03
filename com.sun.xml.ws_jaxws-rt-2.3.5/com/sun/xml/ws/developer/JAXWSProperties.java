/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.developer;

public interface JAXWSProperties {
    @Deprecated
    public static final String CONTENT_NEGOTIATION_PROPERTY = "com.sun.xml.ws.client.ContentNegotiation";
    public static final String MTOM_THRESHOLOD_VALUE = "com.sun.xml.ws.common.MtomThresholdValue";
    public static final String HTTP_EXCHANGE = "com.sun.xml.ws.http.exchange";
    public static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    public static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
    public static final String HTTP_CLIENT_STREAMING_CHUNK_SIZE = "com.sun.xml.ws.transport.http.client.streaming.chunk.size";
    public static final String HOSTNAME_VERIFIER = "com.sun.xml.ws.transport.https.client.hostname.verifier";
    public static final String SSL_SOCKET_FACTORY = "com.sun.xml.ws.transport.https.client.SSLSocketFactory";
    public static final String INBOUND_HEADER_LIST_PROPERTY = "com.sun.xml.ws.api.message.HeaderList";
    public static final String WSENDPOINT = "com.sun.xml.ws.api.server.WSEndpoint";
    public static final String ADDRESSING_TO = "com.sun.xml.ws.api.addressing.to";
    public static final String ADDRESSING_FROM = "com.sun.xml.ws.api.addressing.from";
    public static final String ADDRESSING_ACTION = "com.sun.xml.ws.api.addressing.action";
    public static final String ADDRESSING_MESSAGEID = "com.sun.xml.ws.api.addressing.messageId";
    public static final String HTTP_REQUEST_URL = "com.sun.xml.ws.transport.http.servlet.requestURL";
    public static final String REST_BINDING = "http://jax-ws.dev.java.net/rest";
    public static final String REQUEST_AUTHENTICATOR = "com.sun.xml.ws.request.authenticator";
}

