/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.params;

import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpMethodParams
extends DefaultHttpParams {
    private static final Log LOG = LogFactory.getLog(HttpMethodParams.class);
    public static final String USER_AGENT = "http.useragent";
    public static final String PROTOCOL_VERSION = "http.protocol.version";
    public static final String UNAMBIGUOUS_STATUS_LINE = "http.protocol.unambiguous-statusline";
    public static final String SINGLE_COOKIE_HEADER = "http.protocol.single-cookie-header";
    public static final String STRICT_TRANSFER_ENCODING = "http.protocol.strict-transfer-encoding";
    public static final String REJECT_HEAD_BODY = "http.protocol.reject-head-body";
    public static final String HEAD_BODY_CHECK_TIMEOUT = "http.protocol.head-body-timeout";
    public static final String USE_EXPECT_CONTINUE = "http.protocol.expect-continue";
    public static final String CREDENTIAL_CHARSET = "http.protocol.credential-charset";
    public static final String HTTP_ELEMENT_CHARSET = "http.protocol.element-charset";
    public static final String HTTP_URI_CHARSET = "http.protocol.uri-charset";
    public static final String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";
    public static final String COOKIE_POLICY = "http.protocol.cookie-policy";
    public static final String WARN_EXTRA_INPUT = "http.protocol.warn-extra-input";
    public static final String STATUS_LINE_GARBAGE_LIMIT = "http.protocol.status-line-garbage-limit";
    public static final String SO_TIMEOUT = "http.socket.timeout";
    public static final String DATE_PATTERNS = "http.dateparser.patterns";
    public static final String RETRY_HANDLER = "http.method.retry-handler";
    public static final String BUFFER_WARN_TRIGGER_LIMIT = "http.method.response.buffer.warnlimit";
    public static final String VIRTUAL_HOST = "http.virtual-host";
    public static final String MULTIPART_BOUNDARY = "http.method.multipart.boundary";
    private static final String[] PROTOCOL_STRICTNESS_PARAMETERS = new String[]{"http.protocol.unambiguous-statusline", "http.protocol.single-cookie-header", "http.protocol.strict-transfer-encoding", "http.protocol.reject-head-body", "http.protocol.warn-extra-input"};

    public HttpMethodParams() {
        super(HttpMethodParams.getDefaultParams());
    }

    public HttpMethodParams(HttpParams defaults) {
        super(defaults);
    }

    public String getHttpElementCharset() {
        String charset = (String)this.getParameter(HTTP_ELEMENT_CHARSET);
        if (charset == null) {
            LOG.warn((Object)"HTTP element charset not configured, using US-ASCII");
            charset = "US-ASCII";
        }
        return charset;
    }

    public void setHttpElementCharset(String charset) {
        this.setParameter(HTTP_ELEMENT_CHARSET, charset);
    }

    public String getContentCharset() {
        String charset = (String)this.getParameter(HTTP_CONTENT_CHARSET);
        if (charset == null) {
            LOG.warn((Object)"Default content charset not configured, using ISO-8859-1");
            charset = "ISO-8859-1";
        }
        return charset;
    }

    public void setUriCharset(String charset) {
        this.setParameter(HTTP_URI_CHARSET, charset);
    }

    public String getUriCharset() {
        String charset = (String)this.getParameter(HTTP_URI_CHARSET);
        if (charset == null) {
            charset = "UTF-8";
        }
        return charset;
    }

    public void setContentCharset(String charset) {
        this.setParameter(HTTP_CONTENT_CHARSET, charset);
    }

    public String getCredentialCharset() {
        String charset = (String)this.getParameter(CREDENTIAL_CHARSET);
        if (charset == null) {
            LOG.debug((Object)"Credential charset not configured, using HTTP element charset");
            charset = this.getHttpElementCharset();
        }
        return charset;
    }

    public void setCredentialCharset(String charset) {
        this.setParameter(CREDENTIAL_CHARSET, charset);
    }

    public HttpVersion getVersion() {
        Object param = this.getParameter(PROTOCOL_VERSION);
        if (param == null) {
            return HttpVersion.HTTP_1_1;
        }
        return (HttpVersion)param;
    }

    public void setVersion(HttpVersion version) {
        this.setParameter(PROTOCOL_VERSION, version);
    }

    public String getCookiePolicy() {
        Object param = this.getParameter(COOKIE_POLICY);
        if (param == null) {
            return "default";
        }
        return (String)param;
    }

    public void setCookiePolicy(String policy) {
        this.setParameter(COOKIE_POLICY, policy);
    }

    public int getSoTimeout() {
        return this.getIntParameter(SO_TIMEOUT, 0);
    }

    public void setSoTimeout(int timeout) {
        this.setIntParameter(SO_TIMEOUT, timeout);
    }

    public void setVirtualHost(String hostname) {
        this.setParameter(VIRTUAL_HOST, hostname);
    }

    public String getVirtualHost() {
        return (String)this.getParameter(VIRTUAL_HOST);
    }

    public void makeStrict() {
        this.setParameters(PROTOCOL_STRICTNESS_PARAMETERS, Boolean.TRUE);
        this.setIntParameter(STATUS_LINE_GARBAGE_LIMIT, 0);
    }

    public void makeLenient() {
        this.setParameters(PROTOCOL_STRICTNESS_PARAMETERS, Boolean.FALSE);
        this.setIntParameter(STATUS_LINE_GARBAGE_LIMIT, Integer.MAX_VALUE);
    }
}

