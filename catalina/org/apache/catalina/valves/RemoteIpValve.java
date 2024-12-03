/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.http.MimeHeaders
 *  org.apache.tomcat.util.http.parser.Host
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;

public class RemoteIpValve
extends ValveBase {
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final Log log = LogFactory.getLog(RemoteIpValve.class);
    private String hostHeader = null;
    private boolean changeLocalName = false;
    private int httpServerPort = 80;
    private int httpsServerPort = 443;
    private String portHeader = null;
    private boolean changeLocalPort = false;
    private Pattern internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|100\\.6[4-9]{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.[7-9]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.1[0-1]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.12[0-7]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
    private String protocolHeader = "X-Forwarded-Proto";
    private String protocolHeaderHttpsValue = "https";
    private String proxiesHeader = "X-Forwarded-By";
    private String remoteIpHeader = "X-Forwarded-For";
    private boolean requestAttributesEnabled = true;
    private Pattern trustedProxies = null;

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

    public RemoteIpValve() {
        super(true);
    }

    public String getHostHeader() {
        return this.hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }

    public void setChangeLocalName(boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }

    public int getHttpServerPort() {
        return this.httpServerPort;
    }

    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }

    public String getPortHeader() {
        return this.portHeader;
    }

    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }

    public void setChangeLocalPort(boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }

    public String getInternalProxies() {
        if (this.internalProxies == null) {
            return null;
        }
        return this.internalProxies.toString();
    }

    public String getProtocolHeader() {
        return this.protocolHeader;
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

    public String getTrustedProxies() {
        if (this.trustedProxies == null) {
            return null;
        }
        return this.trustedProxies.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        boolean isInternal;
        String originalRemoteAddr = request.getRemoteAddr();
        String originalRemoteHost = request.getRemoteHost();
        String originalScheme = request.getScheme();
        boolean originalSecure = request.isSecure();
        String originalServerName = request.getServerName();
        String originalLocalName = this.isChangeLocalName() ? request.getLocalName() : null;
        int originalServerPort = request.getServerPort();
        int originalLocalPort = request.getLocalPort();
        String originalProxiesHeader = request.getHeader(this.proxiesHeader);
        String originalRemoteIpHeader = request.getHeader(this.remoteIpHeader);
        boolean bl = isInternal = this.internalProxies != null && this.internalProxies.matcher(originalRemoteAddr).matches();
        if (isInternal || this.trustedProxies != null && this.trustedProxies.matcher(originalRemoteAddr).matches()) {
            String hostHeaderValue;
            String protocolHeaderValue;
            int idx;
            String remoteIp = null;
            ArrayDeque<String> proxiesHeaderValue = new ArrayDeque<String>();
            StringBuilder concatRemoteIpHeaderValue = new StringBuilder();
            Enumeration<String> e = request.getHeaders(this.remoteIpHeader);
            while (e.hasMoreElements()) {
                if (concatRemoteIpHeaderValue.length() > 0) {
                    concatRemoteIpHeaderValue.append(", ");
                }
                concatRemoteIpHeaderValue.append(e.nextElement());
            }
            String[] remoteIpHeaderValue = RemoteIpValve.commaDelimitedListToStringArray(concatRemoteIpHeaderValue.toString());
            if (!isInternal) {
                proxiesHeaderValue.addFirst(originalRemoteAddr);
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
            ArrayDeque<String> newRemoteIpHeaderValue = new ArrayDeque<String>();
            while (idx >= 0) {
                String currentRemoteIp = remoteIpHeaderValue[idx];
                newRemoteIpHeaderValue.addFirst(currentRemoteIp);
                --idx;
            }
            if (remoteIp != null) {
                request.setRemoteAddr(remoteIp);
                if (request.getConnector().getEnableLookups()) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        request.setRemoteHost(inetAddress.getCanonicalHostName());
                    }
                    catch (UnknownHostException e2) {
                        log.debug((Object)sm.getString("remoteIpValve.invalidRemoteAddress", new Object[]{remoteIp}), (Throwable)e2);
                        request.setRemoteHost(remoteIp);
                    }
                } else {
                    request.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.proxiesHeader);
                } else {
                    String commaDelimitedListOfProxies = StringUtils.join(proxiesHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.proxiesHeader).setString(commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.remoteIpHeader);
                } else {
                    String commaDelimitedRemoteIpHeaderValue = StringUtils.join(newRemoteIpHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.remoteIpHeader).setString(commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null && (protocolHeaderValue = request.getHeader(this.protocolHeader)) != null) {
                if (this.isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                    request.setSecure(true);
                    request.getCoyoteRequest().scheme().setString("https");
                    this.setPorts(request, this.httpsServerPort);
                } else {
                    request.setSecure(false);
                    request.getCoyoteRequest().scheme().setString("http");
                    this.setPorts(request, this.httpServerPort);
                }
            }
            if (this.hostHeader != null && (hostHeaderValue = request.getHeader(this.hostHeader)) != null) {
                try {
                    int portIndex = Host.parse((String)hostHeaderValue);
                    if (portIndex > -1) {
                        log.debug((Object)sm.getString("remoteIpValve.invalidHostWithPort", new Object[]{hostHeaderValue, this.hostHeader}));
                        hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                    }
                    request.getCoyoteRequest().serverName().setString(hostHeaderValue);
                    if (this.isChangeLocalName()) {
                        request.getCoyoteRequest().localName().setString(hostHeaderValue);
                    }
                }
                catch (IllegalArgumentException iae) {
                    log.debug((Object)sm.getString("remoteIpValve.invalidHostHeader", new Object[]{hostHeaderValue, this.hostHeader}));
                }
            }
            request.setAttribute("org.apache.tomcat.request.forwarded", Boolean.TRUE);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + originalRemoteAddr + "], originalRemoteHost=[" + originalRemoteHost + "], originalSecure=[" + originalSecure + "], originalScheme=[" + originalScheme + "], originalServerName=[" + originalServerName + "], originalServerPort=[" + originalServerPort + "] will be seen as newRemoteAddr=[" + request.getRemoteAddr() + "], newRemoteHost=[" + request.getRemoteHost() + "], newSecure=[" + request.isSecure() + "], newScheme=[" + request.getScheme() + "], newServerName=[" + request.getServerName() + "], newServerPort=[" + request.getServerPort() + "]"));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("Skip RemoteIpValve for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'"));
        }
        if (this.requestAttributesEnabled) {
            request.setAttribute("org.apache.catalina.AccessLog.RemoteAddr", request.getRemoteAddr());
            request.setAttribute("org.apache.tomcat.remoteAddr", request.getRemoteAddr());
            request.setAttribute("org.apache.catalina.AccessLog.RemoteHost", request.getRemoteHost());
            request.setAttribute("org.apache.catalina.AccessLog.Protocol", request.getProtocol());
            request.setAttribute("org.apache.catalina.AccessLog.ServerName", request.getServerName());
            request.setAttribute("org.apache.catalina.AccessLog.ServerPort", request.getServerPort());
        }
        try {
            this.getNext().invoke(request, response);
        }
        finally {
            if (!request.isAsync()) {
                request.setRemoteAddr(originalRemoteAddr);
                request.setRemoteHost(originalRemoteHost);
                request.setSecure(originalSecure);
                request.getCoyoteRequest().scheme().setString(originalScheme);
                request.getCoyoteRequest().serverName().setString(originalServerName);
                if (this.isChangeLocalName()) {
                    request.getCoyoteRequest().localName().setString(originalLocalName);
                }
                request.setServerPort(originalServerPort);
                request.setLocalPort(originalLocalPort);
                MimeHeaders headers = request.getCoyoteRequest().getMimeHeaders();
                if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                    headers.removeHeader(this.proxiesHeader);
                } else {
                    headers.setValue(this.proxiesHeader).setString(originalProxiesHeader);
                }
                if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                    headers.removeHeader(this.remoteIpHeader);
                } else {
                    headers.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
                }
            }
        }
    }

    private boolean isForwardedProtoHeaderValueSecure(String protocolHeaderValue) {
        if (!protocolHeaderValue.contains(",")) {
            return this.protocolHeaderHttpsValue.equalsIgnoreCase(protocolHeaderValue);
        }
        String[] forwardedProtocols = RemoteIpValve.commaDelimitedListToStringArray(protocolHeaderValue);
        if (forwardedProtocols.length == 0) {
            return false;
        }
        for (String forwardedProtocol : forwardedProtocols) {
            if (this.protocolHeaderHttpsValue.equalsIgnoreCase(forwardedProtocol)) continue;
            return false;
        }
        return true;
    }

    private void setPorts(Request request, int defaultPort) {
        int port;
        block4: {
            String portHeaderValue;
            port = defaultPort;
            if (this.portHeader != null && (portHeaderValue = request.getHeader(this.portHeader)) != null) {
                try {
                    port = Integer.parseInt(portHeaderValue);
                }
                catch (NumberFormatException nfe) {
                    if (!log.isDebugEnabled()) break block4;
                    log.debug((Object)sm.getString("remoteIpValve.invalidPortHeader", new Object[]{portHeaderValue, this.portHeader}), (Throwable)nfe);
                }
            }
        }
        request.setServerPort(port);
        if (this.changeLocalPort) {
            request.setLocalPort(port);
        }
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
}

