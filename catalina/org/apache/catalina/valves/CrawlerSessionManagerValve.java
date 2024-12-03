/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CrawlerSessionManagerValve
extends ValveBase {
    private static final Log log = LogFactory.getLog(CrawlerSessionManagerValve.class);
    private final Map<String, String> clientIdSessionId = new ConcurrentHashMap<String, String>();
    private final Map<String, String> sessionIdClientId = new ConcurrentHashMap<String, String>();
    private String crawlerUserAgents = ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
    private Pattern uaPattern = null;
    private String crawlerIps = null;
    private Pattern ipPattern = null;
    private int sessionInactiveInterval = 60;
    private boolean isHostAware = true;
    private boolean isContextAware = true;

    public CrawlerSessionManagerValve() {
        super(true);
    }

    public void setCrawlerUserAgents(String crawlerUserAgents) {
        this.crawlerUserAgents = crawlerUserAgents;
        this.uaPattern = crawlerUserAgents == null || crawlerUserAgents.length() == 0 ? null : Pattern.compile(crawlerUserAgents);
    }

    public String getCrawlerUserAgents() {
        return this.crawlerUserAgents;
    }

    public void setCrawlerIps(String crawlerIps) {
        this.crawlerIps = crawlerIps;
        this.ipPattern = crawlerIps == null || crawlerIps.length() == 0 ? null : Pattern.compile(crawlerIps);
    }

    public String getCrawlerIps() {
        return this.crawlerIps;
    }

    public void setSessionInactiveInterval(int sessionInactiveInterval) {
        this.sessionInactiveInterval = sessionInactiveInterval;
    }

    public int getSessionInactiveInterval() {
        return this.sessionInactiveInterval;
    }

    public Map<String, String> getClientIpSessionId() {
        return this.clientIdSessionId;
    }

    public boolean isHostAware() {
        return this.isHostAware;
    }

    public void setHostAware(boolean isHostAware) {
        this.isHostAware = isHostAware;
    }

    public boolean isContextAware() {
        return this.isContextAware;
    }

    public void setContextAware(boolean isContextAware) {
        this.isContextAware = isContextAware;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.uaPattern = Pattern.compile(this.crawlerUserAgents);
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        boolean isBot = false;
        String sessionId = null;
        String clientIp = request.getRemoteAddr();
        String clientIdentifier = this.getClientIdentifier(request.getHost(), request.getContext(), clientIp);
        if (log.isDebugEnabled()) {
            log.debug((Object)(request.hashCode() + ": ClientIdentifier=" + clientIdentifier + ", RequestedSessionId=" + request.getRequestedSessionId()));
        }
        if (request.getSession(false) == null) {
            Enumeration<String> uaHeaders = request.getHeaders("user-agent");
            String uaHeader = null;
            if (uaHeaders.hasMoreElements()) {
                uaHeader = uaHeaders.nextElement();
            }
            if (uaHeader != null && !uaHeaders.hasMoreElements()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)(request.hashCode() + ": UserAgent=" + uaHeader));
                }
                if (this.uaPattern.matcher(uaHeader).matches()) {
                    isBot = true;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)(request.hashCode() + ": Bot found. UserAgent=" + uaHeader));
                    }
                }
            }
            if (this.ipPattern != null && this.ipPattern.matcher(clientIp).matches()) {
                isBot = true;
                if (log.isDebugEnabled()) {
                    log.debug((Object)(request.hashCode() + ": Bot found. IP=" + clientIp));
                }
            }
            if (isBot && (sessionId = this.clientIdSessionId.get(clientIdentifier)) != null) {
                request.setRequestedSessionId(sessionId);
                if (log.isDebugEnabled()) {
                    log.debug((Object)(request.hashCode() + ": SessionID=" + sessionId));
                }
            }
        }
        this.getNext().invoke(request, response);
        if (isBot) {
            if (sessionId == null) {
                HttpSession s = request.getSession(false);
                if (s != null) {
                    this.clientIdSessionId.put(clientIdentifier, s.getId());
                    this.sessionIdClientId.put(s.getId(), clientIdentifier);
                    s.setAttribute(this.getClass().getName(), (Object)new CrawlerHttpSessionBindingListener(this.clientIdSessionId, clientIdentifier));
                    s.setMaxInactiveInterval(this.sessionInactiveInterval);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)(request.hashCode() + ": New bot session. SessionID=" + s.getId()));
                    }
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)(request.hashCode() + ": Bot session accessed. SessionID=" + sessionId));
            }
        }
    }

    private String getClientIdentifier(Host host, Context context, String clientIp) {
        StringBuilder result = new StringBuilder(clientIp);
        if (this.isHostAware) {
            result.append('-').append(host.getName());
        }
        if (this.isContextAware && context != null) {
            result.append(context.getName());
        }
        return result.toString();
    }

    private static class CrawlerHttpSessionBindingListener
    implements HttpSessionBindingListener,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final transient Map<String, String> clientIdSessionId;
        private final transient String clientIdentifier;

        private CrawlerHttpSessionBindingListener(Map<String, String> clientIdSessionId, String clientIdentifier) {
            this.clientIdSessionId = clientIdSessionId;
            this.clientIdentifier = clientIdentifier;
        }

        public void valueUnbound(HttpSessionBindingEvent event) {
            if (this.clientIdentifier != null && this.clientIdSessionId != null) {
                this.clientIdSessionId.remove(this.clientIdentifier, event.getSession().getId());
            }
        }
    }
}

