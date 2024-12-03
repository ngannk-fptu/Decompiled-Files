/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts.action.SecurePlugInInterface
 *  org.apache.struts.config.ModuleConfig
 *  org.apache.struts.config.SecureActionConfig
 */
package org.apache.velocity.tools.struts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.SecurePlugInInterface;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.SecureActionConfig;
import org.apache.velocity.tools.generic.ValueParser;
import org.apache.velocity.tools.struts.StrutsUtils;
import org.apache.velocity.tools.view.LinkTool;

public class SecureLinkTool
extends LinkTool {
    protected ServletContext application;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String STD_HTTP_PORT = "80";
    private static final String STD_HTTPS_PORT = "443";

    @Override
    protected void configure(ValueParser props) {
        super.configure(props);
        this.application = (ServletContext)props.getValue("servletContext");
    }

    public SecureLinkTool setAction(String action) {
        String link = StrutsUtils.getActionMappingURL(this.application, this.request, action);
        return (SecureLinkTool)this.absolute(this.computeURL(this.request, this.application, link));
    }

    public SecureLinkTool setForward(String forward) {
        String url = StrutsUtils.getForwardURL(this.request, this.application, forward);
        if (url == null) {
            return null;
        }
        return (SecureLinkTool)this.absolute(url);
    }

    public String computeURL(HttpServletRequest request, ServletContext app, String link) {
        StringBuilder url = new StringBuilder(link);
        String contextPath = request.getContextPath();
        SecurePlugInInterface securePlugin = (SecurePlugInInterface)app.getAttribute("org.apache.struts.action.SecurePlugIn-Instance");
        if (securePlugin.getSslExtEnable() && url.toString().startsWith(contextPath)) {
            String usingScheme = request.getScheme();
            String usingPort = String.valueOf(request.getServerPort());
            String linkString = url.toString().substring(contextPath.length());
            SecureActionConfig secureConfig = SecureLinkTool.getActionConfig(app, linkString);
            if (secureConfig != null && !"any".equalsIgnoreCase(secureConfig.getSecure())) {
                String desiredPort;
                String desiredScheme = Boolean.valueOf(secureConfig.getSecure()) != false ? HTTPS : HTTP;
                String string = desiredPort = Boolean.valueOf(secureConfig.getSecure()) != false ? securePlugin.getHttpsPort() : securePlugin.getHttpPort();
                if (!desiredScheme.equals(usingScheme) || !desiredPort.equals(usingPort)) {
                    url.insert(0, SecureLinkTool.startNewUrlString(request, desiredScheme, desiredPort));
                    if (securePlugin.getSslExtAddSession() && url.toString().indexOf(";jsessionid=") < 0) {
                        url = new StringBuilder(this.toEncoded(url.toString(), request.getSession().getId()));
                    }
                }
            }
        }
        return url.toString();
    }

    private static SecureActionConfig getActionConfig(ServletContext app, String linkString) {
        ModuleConfig moduleConfig = StrutsUtils.selectModule(linkString, app);
        linkString = linkString.substring(moduleConfig.getPrefix().length());
        SecurePlugInInterface spi = (SecurePlugInInterface)app.getAttribute("org.apache.struts.action.SecurePlugIn-Instance");
        for (String servletMapping : spi.getServletMappings()) {
            int anchor;
            int question;
            int starIndex;
            int n = starIndex = servletMapping != null ? servletMapping.indexOf(42) : -1;
            if (starIndex == -1) continue;
            String prefix = servletMapping.substring(0, starIndex);
            String suffix = servletMapping.substring(starIndex + 1);
            int jsession = linkString.indexOf(";jsessionid=");
            if (jsession >= 0) {
                linkString = linkString.substring(0, jsession);
            }
            if ((question = linkString.indexOf(63)) >= 0) {
                linkString = linkString.substring(0, question);
            }
            if ((anchor = linkString.indexOf(35)) >= 0) {
                linkString = linkString.substring(0, anchor);
            }
            if (!linkString.startsWith(prefix) || !linkString.endsWith(suffix)) continue;
            linkString = linkString.substring(prefix.length());
            if (!(linkString = linkString.substring(0, linkString.length() - suffix.length())).startsWith("/")) {
                linkString = "/" + linkString;
            }
            SecureActionConfig secureConfig = (SecureActionConfig)moduleConfig.findActionConfig(linkString);
            return secureConfig;
        }
        return null;
    }

    private static StringBuilder startNewUrlString(HttpServletRequest request, String desiredScheme, String desiredPort) {
        StringBuilder url = new StringBuilder();
        String serverName = request.getServerName();
        url.append(desiredScheme).append("://").append(serverName);
        if (HTTP.equals(desiredScheme) && !STD_HTTP_PORT.equals(desiredPort) || HTTPS.equals(desiredScheme) && !STD_HTTPS_PORT.equals(desiredPort)) {
            url.append(":").append(desiredPort);
        }
        return url;
    }

    public String toEncoded(String url, String sessionId) {
        StringBuilder sb;
        int question;
        if (url == null || sessionId == null) {
            return url;
        }
        String path = url;
        String query = "";
        String anchor = "";
        int pound = url.indexOf(35);
        if (pound >= 0) {
            path = url.substring(0, pound);
            anchor = url.substring(pound);
        }
        if ((question = path.indexOf(63)) >= 0) {
            query = path.substring(question);
            path = path.substring(0, question);
        }
        if ((sb = new StringBuilder(path)).length() > 0) {
            sb.append(";jsessionid=");
            sb.append(sessionId);
        }
        sb.append(query);
        sb.append(anchor);
        return sb.toString();
    }
}

