/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.Cookie
 *  org.apache.commons.httpclient.Credentials
 *  org.apache.commons.httpclient.Header
 *  org.apache.commons.httpclient.HostConfiguration
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpConnectionManager
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.HttpMethodBase
 *  org.apache.commons.httpclient.HttpState
 *  org.apache.commons.httpclient.HttpVersion
 *  org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
 *  org.apache.commons.httpclient.NTCredentials
 *  org.apache.commons.httpclient.UsernamePasswordCredentials
 *  org.apache.commons.httpclient.auth.AuthScope
 *  org.apache.commons.httpclient.methods.GetMethod
 *  org.apache.commons.httpclient.methods.PostMethod
 *  org.apache.commons.httpclient.methods.RequestEntity
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.CommonsHTTPClientProperties;
import org.apache.axis.components.net.CommonsHTTPClientPropertiesFactory;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NetworkUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;

public class CommonsHTTPSender
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$CommonsHTTPSender == null ? (class$org$apache$axis$transport$http$CommonsHTTPSender = CommonsHTTPSender.class$("org.apache.axis.transport.http.CommonsHTTPSender")) : class$org$apache$axis$transport$http$CommonsHTTPSender).getName());
    protected HttpConnectionManager connectionManager;
    protected CommonsHTTPClientProperties clientProperties;
    boolean httpChunkStream = true;
    static /* synthetic */ Class class$org$apache$axis$transport$http$CommonsHTTPSender;

    public CommonsHTTPSender() {
        this.initialize();
    }

    protected void initialize() {
        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
        this.clientProperties = CommonsHTTPClientPropertiesFactory.create();
        cm.getParams().setDefaultMaxConnectionsPerHost(this.clientProperties.getMaximumConnectionsPerHost());
        cm.getParams().setMaxTotalConnections(this.clientProperties.getMaximumTotalConnections());
        if (this.clientProperties.getDefaultConnectionTimeout() > 0) {
            cm.getParams().setConnectionTimeout(this.clientProperties.getDefaultConnectionTimeout());
        }
        if (this.clientProperties.getDefaultSoTimeout() > 0) {
            cm.getParams().setSoTimeout(this.clientProperties.getDefaultSoTimeout());
        }
        this.connectionManager = cm;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        GetMethod method = null;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("enter00", "CommonsHTTPSender::invoke"));
        }
        try {
            String webMethod;
            URL targetURL = new URL(msgContext.getStrProp("transport.url"));
            HttpClient httpClient = new HttpClient(this.connectionManager);
            httpClient.getParams().setConnectionManagerTimeout((long)this.clientProperties.getConnectionPoolTimeout());
            HostConfiguration hostConfiguration = this.getHostConfiguration(httpClient, msgContext, targetURL);
            boolean posting = true;
            if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS && (webMethod = msgContext.getStrProp("soap12.webmethod")) != null) {
                posting = webMethod.equals("POST");
            }
            if (posting) {
                Message reqMessage = msgContext.getRequestMessage();
                method = new PostMethod(targetURL.toString());
                method.getParams().setBooleanParameter("http.protocol.expect-continue", false);
                this.addContextInfo((HttpMethodBase)method, httpClient, msgContext, targetURL);
                ((PostMethod)method).setRequestEntity((RequestEntity)new MessageRequestEntity((HttpMethodBase)method, reqMessage, this.httpChunkStream));
            } else {
                method = new GetMethod(targetURL.toString());
                this.addContextInfo((HttpMethodBase)method, httpClient, msgContext, targetURL);
            }
            String httpVersion = msgContext.getStrProp("axis.transport.version");
            if (httpVersion != null && httpVersion.equals(HTTPConstants.HEADER_PROTOCOL_V10)) {
                method.getParams().setVersion(HttpVersion.HTTP_1_0);
            }
            if (msgContext.getMaintainSession()) {
                HttpState state = httpClient.getState();
                method.getParams().setCookiePolicy("compatibility");
                String host = hostConfiguration.getHost();
                String path = targetURL.getPath();
                boolean secure = hostConfiguration.getProtocol().isSecure();
                this.fillHeaders(msgContext, state, "Cookie", host, path, secure);
                this.fillHeaders(msgContext, state, "Cookie2", host, path, secure);
                httpClient.setState(state);
            }
            int returnCode = httpClient.executeMethod(hostConfiguration, (HttpMethod)method, null);
            String contentType = CommonsHTTPSender.getHeader((HttpMethodBase)method, "Content-Type");
            String contentLocation = CommonsHTTPSender.getHeader((HttpMethodBase)method, "Content-Location");
            String contentLength = CommonsHTTPSender.getHeader((HttpMethodBase)method, "Content-Length");
            if (!(returnCode > 199 && returnCode < 300 || msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS || contentType != null && !contentType.equals("text/html") && returnCode > 499 && returnCode < 600)) {
                String statusMessage = method.getStatusText();
                AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
                try {
                    fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, method.getResponseBodyAsString()));
                    fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(returnCode));
                    throw fault;
                }
                catch (Throwable throwable) {
                    method.releaseConnection();
                    throw throwable;
                }
            }
            InputStream releaseConnectionOnCloseStream = this.createConnectionReleasingInputStream((HttpMethodBase)method);
            Message outMsg = new Message(releaseConnectionOnCloseStream, false, contentType, contentLocation);
            Header[] responseHeaders = method.getResponseHeaders();
            MimeHeaders responseMimeHeaders = outMsg.getMimeHeaders();
            for (int i = 0; i < responseHeaders.length; ++i) {
                Header responseHeader = responseHeaders[i];
                responseMimeHeaders.addHeader(responseHeader.getName(), responseHeader.getValue());
            }
            outMsg.setMessageType("response");
            msgContext.setResponseMessage(outMsg);
            if (log.isDebugEnabled()) {
                if (null == contentLength) {
                    log.debug((Object)("\n" + Messages.getMessage("no00", "Content-Length")));
                }
                log.debug((Object)("\n" + Messages.getMessage("xmlRecd00")));
                log.debug((Object)"-----------------------------------------------");
                log.debug((Object)outMsg.getSOAPPartAsString());
            }
            if (msgContext.getMaintainSession()) {
                Header[] headers = method.getResponseHeaders();
                for (int i = 0; i < headers.length; ++i) {
                    if (headers[i].getName().equalsIgnoreCase("Set-Cookie")) {
                        this.handleCookie("Cookie", headers[i].getValue(), msgContext);
                        continue;
                    }
                    if (!headers[i].getName().equalsIgnoreCase("Set-Cookie2")) continue;
                    this.handleCookie("Cookie2", headers[i].getValue(), msgContext);
                }
            }
            if (msgContext.isPropertyTrue("axis.one.way")) {
                method.releaseConnection();
            }
        }
        catch (Exception e) {
            log.debug((Object)e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("exit00", "CommonsHTTPSender::invoke"));
        }
    }

    public void handleCookie(String cookieName, String cookie, MessageContext msgContext) {
        int keyIndex = (cookie = this.cleanupCookie(cookie)).indexOf("=");
        String key = keyIndex != -1 ? cookie.substring(0, keyIndex) : cookie;
        ArrayList<String> cookies = new ArrayList<String>();
        Object oldCookies = msgContext.getProperty(cookieName);
        boolean alreadyExist = false;
        if (oldCookies != null) {
            if (oldCookies instanceof String[]) {
                String[] oldCookiesArray = (String[])oldCookies;
                for (int i = 0; i < oldCookiesArray.length; ++i) {
                    String anOldCookie = oldCookiesArray[i];
                    if (key != null && anOldCookie.indexOf(key) == 0) {
                        anOldCookie = cookie;
                        alreadyExist = true;
                    }
                    cookies.add(anOldCookie);
                }
            } else {
                String oldCookie = (String)oldCookies;
                if (key != null && oldCookie.indexOf(key) == 0) {
                    oldCookie = cookie;
                    alreadyExist = true;
                }
                cookies.add(oldCookie);
            }
        }
        if (!alreadyExist) {
            cookies.add(cookie);
        }
        if (cookies.size() == 1) {
            msgContext.setProperty(cookieName, cookies.get(0));
        } else if (cookies.size() > 1) {
            msgContext.setProperty(cookieName, cookies.toArray(new String[cookies.size()]));
        }
    }

    private void fillHeaders(MessageContext msgContext, HttpState state, String header, String host, String path, boolean secure) {
        Object ck1 = msgContext.getProperty(header);
        if (ck1 != null) {
            if (ck1 instanceof String[]) {
                String[] cookies = (String[])ck1;
                for (int i = 0; i < cookies.length; ++i) {
                    this.addCookie(state, cookies[i], host, path, secure);
                }
            } else {
                this.addCookie(state, (String)ck1, host, path, secure);
            }
        }
    }

    private void addCookie(HttpState state, String cookie, String host, String path, boolean secure) {
        int index = cookie.indexOf(61);
        state.addCookie(new Cookie(host, cookie.substring(0, index), cookie.substring(index + 1), path, null, secure));
    }

    private String cleanupCookie(String cookie) {
        int index = (cookie = cookie.trim()).indexOf(59);
        if (index != -1) {
            cookie = cookie.substring(0, index);
        }
        return cookie;
    }

    protected HostConfiguration getHostConfiguration(HttpClient client, MessageContext context, URL targetURL) {
        TransportClientProperties tcp = TransportClientPropertiesFactory.create(targetURL.getProtocol());
        int port = targetURL.getPort();
        boolean hostInNonProxyList = this.isHostInNonProxyList(targetURL.getHost(), tcp.getNonProxyHosts());
        HostConfiguration config = new HostConfiguration();
        if (port == -1) {
            port = 80;
        }
        if (hostInNonProxyList) {
            config.setHost(targetURL.getHost(), port, targetURL.getProtocol());
        } else if (tcp.getProxyHost().length() == 0 || tcp.getProxyPort().length() == 0) {
            config.setHost(targetURL.getHost(), port, targetURL.getProtocol());
        } else {
            if (tcp.getProxyUser().length() != 0) {
                UsernamePasswordCredentials proxyCred = new UsernamePasswordCredentials(tcp.getProxyUser(), tcp.getProxyPassword());
                int domainIndex = tcp.getProxyUser().indexOf("\\");
                if (domainIndex > 0) {
                    String domain = tcp.getProxyUser().substring(0, domainIndex);
                    if (tcp.getProxyUser().length() > domainIndex + 1) {
                        String user = tcp.getProxyUser().substring(domainIndex + 1);
                        proxyCred = new NTCredentials(user, tcp.getProxyPassword(), tcp.getProxyHost(), domain);
                    }
                }
                client.getState().setProxyCredentials(AuthScope.ANY, (Credentials)proxyCred);
            }
            int proxyPort = new Integer(tcp.getProxyPort());
            config.setProxy(tcp.getProxyHost(), proxyPort);
        }
        return config;
    }

    private void addContextInfo(HttpMethodBase method, HttpClient httpClient, MessageContext msgContext, URL tmpURL) throws Exception {
        Hashtable userHeaderTable;
        MimeHeaders mimeHeaders;
        Message msg;
        String action;
        if (msgContext.getTimeout() != 0) {
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(msgContext.getTimeout());
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(msgContext.getTimeout());
        }
        String string = action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
        if (action == null) {
            action = "";
        }
        if ((msg = msgContext.getRequestMessage()) != null) {
            method.setRequestHeader(new Header("Content-Type", msg.getContentType(msgContext.getSOAPConstants())));
        }
        method.setRequestHeader(new Header("SOAPAction", "\"" + action + "\""));
        String userID = msgContext.getUsername();
        String passwd = msgContext.getPassword();
        if (userID == null && tmpURL.getUserInfo() != null) {
            String info = tmpURL.getUserInfo();
            int sep = info.indexOf(58);
            if (sep >= 0 && sep + 1 < info.length()) {
                userID = info.substring(0, sep);
                passwd = info.substring(sep + 1);
            } else {
                userID = info;
            }
        }
        if (userID != null) {
            UsernamePasswordCredentials proxyCred = new UsernamePasswordCredentials(userID, passwd);
            int domainIndex = userID.indexOf("\\");
            if (domainIndex > 0) {
                String domain = userID.substring(0, domainIndex);
                if (userID.length() > domainIndex + 1) {
                    String user = userID.substring(domainIndex + 1);
                    proxyCred = new NTCredentials(user, passwd, NetworkUtils.getLocalHostname(), domain);
                }
            }
            httpClient.getState().setCredentials(AuthScope.ANY, (Credentials)proxyCred);
        }
        if ((mimeHeaders = msg.getMimeHeaders()) != null) {
            Iterator i = mimeHeaders.getAllHeaders();
            while (i.hasNext()) {
                MimeHeader mimeHeader = (MimeHeader)i.next();
                String headerName = mimeHeader.getName();
                if (headerName.equals("Content-Type") || headerName.equals("SOAPAction")) continue;
                method.addRequestHeader(mimeHeader.getName(), mimeHeader.getValue());
            }
        }
        if ((userHeaderTable = (Hashtable)msgContext.getProperty("HTTP-Request-Headers")) != null) {
            Iterator e = userHeaderTable.entrySet().iterator();
            while (e.hasNext()) {
                Map.Entry me = e.next();
                Object keyObj = me.getKey();
                if (null == keyObj) continue;
                String key = keyObj.toString().trim();
                String value = me.getValue().toString().trim();
                if (key.equalsIgnoreCase("Expect") && value.equalsIgnoreCase("100-continue")) {
                    method.getParams().setBooleanParameter("http.protocol.expect-continue", true);
                    continue;
                }
                if (key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)) {
                    String val = me.getValue().toString();
                    if (null == val) continue;
                    this.httpChunkStream = JavaUtils.isTrue(val);
                    continue;
                }
                method.addRequestHeader(key, value);
            }
        }
    }

    protected boolean isHostInNonProxyList(String host, String nonProxyHosts) {
        if (nonProxyHosts == null || host == null) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|\"");
        while (tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("match00", new String[]{"HTTPSender", host, pattern}));
            }
            if (!CommonsHTTPSender.match(pattern, host, false)) continue;
            return true;
        }
        return false;
    }

    protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
        char ch;
        int i;
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (i = 0; i < patArr.length; ++i) {
            if (patArr[i] != '*') continue;
            containsStar = true;
            break;
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (i = 0; i <= patIdxEnd; ++i) {
                char ch2 = patArr[i];
                if (isCaseSensitive && ch2 != strArr[i]) {
                    return false;
                }
                if (isCaseSensitive || Character.toUpperCase(ch2) == Character.toUpperCase(strArr[i])) continue;
                return false;
            }
            return true;
        }
        if (patIdxEnd == 0) {
            return true;
        }
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxStart]) {
                return false;
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
                return false;
            }
            ++patIdxStart;
            ++strIdxStart;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                return false;
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i2 = patIdxStart + 1; i2 <= patIdxEnd; ++i2) {
                if (patArr[i2] != '*') continue;
                patIdxTmp = i2;
                break;
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
                continue;
            }
            int patLength = patIdxTmp - patIdxStart - 1;
            int strLength = strIdxEnd - strIdxStart + 1;
            int foundIdx = -1;
            block8: for (int i3 = 0; i3 <= strLength - patLength; ++i3) {
                for (int j = 0; j < patLength; ++j) {
                    ch = patArr[patIdxStart + j + 1];
                    if (isCaseSensitive && ch != strArr[strIdxStart + i3 + j] || !isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i3 + j])) continue block8;
                }
                foundIdx = strIdxStart + i3;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }
        for (i = patIdxStart; i <= patIdxEnd; ++i) {
            if (patArr[i] == '*') continue;
            return false;
        }
        return true;
    }

    private static String getHeader(HttpMethodBase method, String headerName) {
        Header header = method.getResponseHeader(headerName);
        return header == null ? null : header.getValue().trim();
    }

    private InputStream createConnectionReleasingInputStream(final HttpMethodBase method) throws IOException {
        return new FilterInputStream(method.getResponseBodyAsStream()){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void close() throws IOException {
                try {
                    super.close();
                }
                finally {
                    method.releaseConnection();
                }
            }
        };
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class MessageRequestEntity
    implements RequestEntity {
        private HttpMethodBase method;
        private Message message;
        boolean httpChunkStream = true;

        public MessageRequestEntity(HttpMethodBase method, Message message) {
            this.message = message;
            this.method = method;
        }

        public MessageRequestEntity(HttpMethodBase method, Message message, boolean httpChunkStream) {
            this.message = message;
            this.method = method;
            this.httpChunkStream = httpChunkStream;
        }

        public boolean isRepeatable() {
            return true;
        }

        public void writeRequest(OutputStream out) throws IOException {
            try {
                this.message.writeTo(out);
            }
            catch (SOAPException e) {
                throw new IOException(e.getMessage());
            }
        }

        public long getContentLength() {
            if (this.method.getParams().getVersion() == HttpVersion.HTTP_1_0 || !this.httpChunkStream) {
                try {
                    return this.message.getContentLength();
                }
                catch (Exception e) {
                    return -1L;
                }
            }
            return -1L;
        }

        public String getContentType() {
            return null;
        }
    }
}

