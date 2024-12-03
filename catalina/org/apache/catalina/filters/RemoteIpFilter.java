/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.PushBuilder
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.http.parser.Host
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.RequestUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.res.StringManager;

public class RemoteIpFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    protected static final String HTTP_SERVER_PORT_PARAMETER = "httpServerPort";
    protected static final String HTTPS_SERVER_PORT_PARAMETER = "httpsServerPort";
    protected static final String INTERNAL_PROXIES_PARAMETER = "internalProxies";
    private transient Log log = LogFactory.getLog(RemoteIpFilter.class);
    protected static final StringManager sm = StringManager.getManager(RemoteIpFilter.class);
    protected static final String PROTOCOL_HEADER_PARAMETER = "protocolHeader";
    protected static final String PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER = "protocolHeaderHttpsValue";
    protected static final String HOST_HEADER_PARAMETER = "hostHeader";
    protected static final String PORT_HEADER_PARAMETER = "portHeader";
    protected static final String CHANGE_LOCAL_NAME_PARAMETER = "changeLocalName";
    protected static final String CHANGE_LOCAL_PORT_PARAMETER = "changeLocalPort";
    protected static final String PROXIES_HEADER_PARAMETER = "proxiesHeader";
    protected static final String REMOTE_IP_HEADER_PARAMETER = "remoteIpHeader";
    protected static final String TRUSTED_PROXIES_PARAMETER = "trustedProxies";
    protected static final String ENABLE_LOOKUPS_PARAMETER = "enableLookups";
    private int httpServerPort = 80;
    private int httpsServerPort = 443;
    private Pattern internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|100\\.6[4-9]{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.[7-9]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.1[0-1]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.12[0-7]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
    private String protocolHeader = "X-Forwarded-Proto";
    private String protocolHeaderHttpsValue = "https";
    private String hostHeader = null;
    private boolean changeLocalName = false;
    private String portHeader = null;
    private boolean changeLocalPort = false;
    private String proxiesHeader = "X-Forwarded-By";
    private String remoteIpHeader = "X-Forwarded-For";
    private boolean requestAttributesEnabled = true;
    private Pattern trustedProxies = null;
    private boolean enableLookups;

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return commaDelimitedStrings == null || commaDelimitedStrings.length() == 0 ? new String[]{} : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    @Deprecated
    protected static String listToCommaDelimitedString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Iterator<String> it = stringList.iterator();
        while (it.hasNext()) {
            String element = it.next();
            if (element == null) continue;
            result.append((Object)element);
            if (!it.hasNext()) continue;
            result.append(", ");
        }
        return result.toString();
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean isInternal;
        boolean bl = isInternal = this.internalProxies != null && this.internalProxies.matcher(request.getRemoteAddr()).matches();
        if (isInternal || this.trustedProxies != null && this.trustedProxies.matcher(request.getRemoteAddr()).matches()) {
            String hostHeaderValue;
            String protocolHeaderValue;
            int idx;
            String remoteIp = null;
            ArrayDeque<String> proxiesHeaderValue = new ArrayDeque<String>();
            StringBuilder concatRemoteIpHeaderValue = new StringBuilder();
            Enumeration e = request.getHeaders(this.remoteIpHeader);
            while (e.hasMoreElements()) {
                if (concatRemoteIpHeaderValue.length() > 0) {
                    concatRemoteIpHeaderValue.append(", ");
                }
                concatRemoteIpHeaderValue.append((String)e.nextElement());
            }
            String[] remoteIpHeaderValue = RemoteIpFilter.commaDelimitedListToStringArray(concatRemoteIpHeaderValue.toString());
            if (!isInternal) {
                proxiesHeaderValue.addFirst(request.getRemoteAddr());
            }
            for (idx = remoteIpHeaderValue.length - 1; idx >= 0; --idx) {
                String currentRemoteIp;
                remoteIp = currentRemoteIp = remoteIpHeaderValue[idx];
                if (this.internalProxies != null && this.internalProxies.matcher(currentRemoteIp).matches()) continue;
                if (this.trustedProxies != null && this.trustedProxies.matcher(currentRemoteIp).matches()) {
                    proxiesHeaderValue.addFirst(currentRemoteIp);
                    continue;
                }
                --idx;
                break;
            }
            LinkedList<String> newRemoteIpHeaderValue = new LinkedList<String>();
            while (idx >= 0) {
                String currentRemoteIp = remoteIpHeaderValue[idx];
                newRemoteIpHeaderValue.addFirst(currentRemoteIp);
                --idx;
            }
            XForwardedRequest xRequest = new XForwardedRequest(request);
            if (remoteIp != null) {
                xRequest.setRemoteAddr(remoteIp);
                if (this.getEnableLookups()) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        xRequest.setRemoteHost(inetAddress.getCanonicalHostName());
                    }
                    catch (UnknownHostException e2) {
                        this.log.debug((Object)sm.getString("remoteIpFilter.invalidRemoteAddress", new Object[]{remoteIp}), (Throwable)e2);
                        xRequest.setRemoteHost(remoteIp);
                    }
                } else {
                    xRequest.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.proxiesHeader);
                } else {
                    String commaDelimitedListOfProxies = StringUtils.join(proxiesHeaderValue);
                    xRequest.setHeader(this.proxiesHeader, commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.remoteIpHeader);
                } else {
                    String commaDelimitedRemoteIpHeaderValue = StringUtils.join(newRemoteIpHeaderValue);
                    xRequest.setHeader(this.remoteIpHeader, commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null && (protocolHeaderValue = request.getHeader(this.protocolHeader)) != null) {
                if (this.isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                    xRequest.setSecure(true);
                    xRequest.setScheme("https");
                    this.setPorts(xRequest, this.httpsServerPort);
                } else {
                    xRequest.setSecure(false);
                    xRequest.setScheme("http");
                    this.setPorts(xRequest, this.httpServerPort);
                }
            }
            if (this.hostHeader != null && (hostHeaderValue = request.getHeader(this.hostHeader)) != null) {
                try {
                    int portIndex = Host.parse((String)hostHeaderValue);
                    if (portIndex > -1) {
                        this.log.debug((Object)sm.getString("remoteIpFilter.invalidHostWithPort", new Object[]{hostHeaderValue, this.hostHeader}));
                        hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                    }
                    xRequest.setServerName(hostHeaderValue);
                    if (this.isChangeLocalName()) {
                        xRequest.setLocalName(hostHeaderValue);
                    }
                }
                catch (IllegalArgumentException iae) {
                    this.log.debug((Object)sm.getString("remoteIpFilter.invalidHostHeader", new Object[]{hostHeaderValue, this.hostHeader}));
                }
            }
            request.setAttribute("org.apache.tomcat.request.forwarded", (Object)Boolean.TRUE);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + request.getRemoteAddr() + "], originalRemoteHost=[" + request.getRemoteHost() + "], originalSecure=[" + request.isSecure() + "], originalScheme=[" + request.getScheme() + "], originalServerName=[" + request.getServerName() + "], originalServerPort=[" + request.getServerPort() + "] will be seen as newRemoteAddr=[" + xRequest.getRemoteAddr() + "], newRemoteHost=[" + xRequest.getRemoteHost() + "], newSecure=[" + xRequest.isSecure() + "], newScheme=[" + xRequest.getScheme() + "], newServerName=[" + xRequest.getServerName() + "], newServerPort=[" + xRequest.getServerPort() + "]"));
            }
            if (this.requestAttributesEnabled) {
                request.setAttribute("org.apache.catalina.AccessLog.RemoteAddr", (Object)xRequest.getRemoteAddr());
                request.setAttribute("org.apache.tomcat.remoteAddr", (Object)xRequest.getRemoteAddr());
                request.setAttribute("org.apache.catalina.AccessLog.RemoteHost", (Object)xRequest.getRemoteHost());
                request.setAttribute("org.apache.catalina.AccessLog.Protocol", (Object)xRequest.getProtocol());
                request.setAttribute("org.apache.catalina.AccessLog.ServerName", (Object)xRequest.getServerName());
                request.setAttribute("org.apache.catalina.AccessLog.ServerPort", (Object)xRequest.getServerPort());
            }
            chain.doFilter((ServletRequest)xRequest, (ServletResponse)response);
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Skip RemoteIpFilter for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'"));
            }
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private boolean isForwardedProtoHeaderValueSecure(String protocolHeaderValue) {
        if (!protocolHeaderValue.contains(",")) {
            return this.protocolHeaderHttpsValue.equalsIgnoreCase(protocolHeaderValue);
        }
        String[] forwardedProtocols = RemoteIpFilter.commaDelimitedListToStringArray(protocolHeaderValue);
        if (forwardedProtocols.length == 0) {
            return false;
        }
        for (String forwardedProtocol : forwardedProtocols) {
            if (this.protocolHeaderHttpsValue.equalsIgnoreCase(forwardedProtocol)) continue;
            return false;
        }
        return true;
    }

    private void setPorts(XForwardedRequest xrequest, int defaultPort) {
        String portHeaderValue;
        int port = defaultPort;
        if (this.getPortHeader() != null && (portHeaderValue = xrequest.getHeader(this.getPortHeader())) != null) {
            try {
                port = Integer.parseInt(portHeaderValue);
            }
            catch (NumberFormatException nfe) {
                this.log.debug((Object)("Invalid port value [" + portHeaderValue + "] provided in header [" + this.getPortHeader() + "]"));
            }
        }
        xrequest.setServerPort(port);
        if (this.isChangeLocalPort()) {
            xrequest.setLocalPort(port);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }

    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }

    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }

    public Pattern getInternalProxies() {
        return this.internalProxies;
    }

    public String getProtocolHeader() {
        return this.protocolHeader;
    }

    public String getPortHeader() {
        return this.portHeader;
    }

    public String getProtocolHeaderHttpsValue() {
        return this.protocolHeaderHttpsValue;
    }

    public String getProxiesHeader() {
        return this.proxiesHeader;
    }

    public String getRemoteIpHeader() {
        return this.remoteIpHeader;
    }

    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public Pattern getTrustedProxies() {
        return this.trustedProxies;
    }

    public boolean getEnableLookups() {
        return this.enableLookups;
    }

    public void init() throws ServletException {
        if (this.getInitParameter(INTERNAL_PROXIES_PARAMETER) != null) {
            this.setInternalProxies(this.getInitParameter(INTERNAL_PROXIES_PARAMETER));
        }
        if (this.getInitParameter(PROTOCOL_HEADER_PARAMETER) != null) {
            this.setProtocolHeader(this.getInitParameter(PROTOCOL_HEADER_PARAMETER));
        }
        if (this.getInitParameter(PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER) != null) {
            this.setProtocolHeaderHttpsValue(this.getInitParameter(PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER));
        }
        if (this.getInitParameter(HOST_HEADER_PARAMETER) != null) {
            this.setHostHeader(this.getInitParameter(HOST_HEADER_PARAMETER));
        }
        if (this.getInitParameter(PORT_HEADER_PARAMETER) != null) {
            this.setPortHeader(this.getInitParameter(PORT_HEADER_PARAMETER));
        }
        if (this.getInitParameter(CHANGE_LOCAL_NAME_PARAMETER) != null) {
            this.setChangeLocalName(Boolean.parseBoolean(this.getInitParameter(CHANGE_LOCAL_NAME_PARAMETER)));
        }
        if (this.getInitParameter(CHANGE_LOCAL_PORT_PARAMETER) != null) {
            this.setChangeLocalPort(Boolean.parseBoolean(this.getInitParameter(CHANGE_LOCAL_PORT_PARAMETER)));
        }
        if (this.getInitParameter(PROXIES_HEADER_PARAMETER) != null) {
            this.setProxiesHeader(this.getInitParameter(PROXIES_HEADER_PARAMETER));
        }
        if (this.getInitParameter(REMOTE_IP_HEADER_PARAMETER) != null) {
            this.setRemoteIpHeader(this.getInitParameter(REMOTE_IP_HEADER_PARAMETER));
        }
        if (this.getInitParameter(TRUSTED_PROXIES_PARAMETER) != null) {
            this.setTrustedProxies(this.getInitParameter(TRUSTED_PROXIES_PARAMETER));
        }
        if (this.getInitParameter(HTTP_SERVER_PORT_PARAMETER) != null) {
            try {
                this.setHttpServerPort(Integer.parseInt(this.getInitParameter(HTTP_SERVER_PORT_PARAMETER)));
            }
            catch (NumberFormatException e) {
                throw new NumberFormatException(sm.getString("remoteIpFilter.invalidNumber", new Object[]{HTTP_SERVER_PORT_PARAMETER, e.getLocalizedMessage()}));
            }
        }
        if (this.getInitParameter(HTTPS_SERVER_PORT_PARAMETER) != null) {
            try {
                this.setHttpsServerPort(Integer.parseInt(this.getInitParameter(HTTPS_SERVER_PORT_PARAMETER)));
            }
            catch (NumberFormatException e) {
                throw new NumberFormatException(sm.getString("remoteIpFilter.invalidNumber", new Object[]{HTTPS_SERVER_PORT_PARAMETER, e.getLocalizedMessage()}));
            }
        }
        if (this.getInitParameter(ENABLE_LOOKUPS_PARAMETER) != null) {
            this.setEnableLookups(Boolean.parseBoolean(this.getInitParameter(ENABLE_LOOKUPS_PARAMETER)));
        }
    }

    public void setChangeLocalName(boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }

    public void setChangeLocalPort(boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public void setHttpsServerPort(int httpsServerPort) {
        this.httpsServerPort = httpsServerPort;
    }

    public void setInternalProxies(String internalProxies) {
        this.internalProxies = internalProxies == null || internalProxies.length() == 0 ? null : Pattern.compile(internalProxies);
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public void setProtocolHeader(String protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    public void setProtocolHeaderHttpsValue(String protocolHeaderHttpsValue) {
        this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
    }

    public void setProxiesHeader(String proxiesHeader) {
        this.proxiesHeader = proxiesHeader;
    }

    public void setRemoteIpHeader(String remoteIpHeader) {
        this.remoteIpHeader = remoteIpHeader;
    }

    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    public void setTrustedProxies(String trustedProxies) {
        this.trustedProxies = trustedProxies == null || trustedProxies.length() == 0 ? null : Pattern.compile(trustedProxies);
    }

    public void setEnableLookups(boolean enableLookups) {
        this.enableLookups = enableLookups;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(RemoteIpFilter.class);
    }

    public static class XForwardedRequest
    extends HttpServletRequestWrapper {
        protected final Map<String, List<String>> headers;
        protected String localName;
        protected int localPort;
        protected String remoteAddr;
        protected String remoteHost;
        protected String scheme;
        protected boolean secure;
        protected String serverName;
        protected int serverPort;

        public XForwardedRequest(HttpServletRequest request) {
            super(request);
            this.localName = request.getLocalName();
            this.localPort = request.getLocalPort();
            this.remoteAddr = request.getRemoteAddr();
            this.remoteHost = request.getRemoteHost();
            this.scheme = request.getScheme();
            this.secure = request.isSecure();
            this.serverName = request.getServerName();
            this.serverPort = request.getServerPort();
            this.headers = new HashMap<String, List<String>>();
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = (String)headerNames.nextElement();
                this.headers.put(header, Collections.list(request.getHeaders(header)));
            }
        }

        public long getDateHeader(String name) {
            String value = this.getHeader(name);
            if (value == null) {
                return -1L;
            }
            long date = FastHttpDateFormat.parseDate((String)value);
            if (date == -1L) {
                throw new IllegalArgumentException(value);
            }
            return date;
        }

        public String getHeader(String name) {
            Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null || header.getValue() == null || header.getValue().isEmpty()) {
                return null;
            }
            return header.getValue().get(0);
        }

        protected Map.Entry<String, List<String>> getHeaderEntry(String name) {
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (!entry.getKey().equalsIgnoreCase(name)) continue;
                return entry;
            }
            return null;
        }

        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }

        public Enumeration<String> getHeaders(String name) {
            Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null || header.getValue() == null) {
                return Collections.enumeration(Collections.emptyList());
            }
            return Collections.enumeration((Collection)header.getValue());
        }

        public int getIntHeader(String name) {
            String value = this.getHeader(name);
            if (value == null) {
                return -1;
            }
            return Integer.parseInt(value);
        }

        public String getLocalName() {
            return this.localName;
        }

        public int getLocalPort() {
            return this.localPort;
        }

        public String getRemoteAddr() {
            return this.remoteAddr;
        }

        public String getRemoteHost() {
            return this.remoteHost;
        }

        public String getScheme() {
            return this.scheme;
        }

        public String getServerName() {
            return this.serverName;
        }

        public int getServerPort() {
            return this.serverPort;
        }

        public void removeHeader(String name) {
            Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header != null) {
                this.headers.remove(header.getKey());
            }
        }

        public void setHeader(String name, String value) {
            List<String> values = Collections.singletonList(value);
            Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null) {
                this.headers.put(name, values);
            } else {
                header.setValue(values);
            }
        }

        public void setLocalName(String localName) {
            this.localName = localName;
        }

        public void setLocalPort(int localPort) {
            this.localPort = localPort;
        }

        public void setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }

        public void setRemoteHost(String remoteHost) {
            this.remoteHost = remoteHost;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public void setSecure(boolean secure) {
            super.getRequest().setAttribute("org.apache.catalina.filters.RemoteIpFilter.secure", (Object)secure);
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public StringBuffer getRequestURL() {
            return RequestUtil.getRequestURL((HttpServletRequest)this);
        }

        public PushBuilder newPushBuilder() {
            ServletRequest current = this.getRequest();
            while (current instanceof ServletRequestWrapper) {
                current = ((ServletRequestWrapper)current).getRequest();
            }
            if (current instanceof RequestFacade) {
                return ((RequestFacade)current).newPushBuilder((HttpServletRequest)this);
            }
            return null;
        }
    }
}

