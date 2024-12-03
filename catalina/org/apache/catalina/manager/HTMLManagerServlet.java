/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.Part
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.manager.Constants;
import org.apache.catalina.manager.DummyProxySession;
import org.apache.catalina.manager.JspHelper;
import org.apache.catalina.manager.ManagerServlet;
import org.apache.catalina.manager.util.SessionUtils;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public final class HTMLManagerServlet
extends ManagerServlet {
    private static final long serialVersionUID = 1L;
    static final String APPLICATION_MESSAGE = "message";
    static final String APPLICATION_ERROR = "error";
    static final String sessionsListJspPath = "/WEB-INF/jsp/sessionsList.jsp";
    static final String sessionDetailJspPath = "/WEB-INF/jsp/sessionDetail.jsp";
    static final String connectorCiphersJspPath = "/WEB-INF/jsp/connectorCiphers.jsp";
    static final String connectorCertsJspPath = "/WEB-INF/jsp/connectorCerts.jsp";
    static final String connectorTrustedCertsJspPath = "/WEB-INF/jsp/connectorTrustedCerts.jsp";
    private boolean showProxySessions = false;
    private static final String APPS_HEADER_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"6\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-left\"><small>{1}</small></td>\n <td class=\"header-left\"><small>{2}</small></td>\n <td class=\"header-center\"><small>{3}</small></td>\n <td class=\"header-center\"><small>{4}</small></td>\n <td class=\"header-left\"><small>{5}</small></td>\n <td class=\"header-left\"><small>{6}</small></td>\n</tr>\n";
    private static final String APPS_ROW_DETAILS_SECTION = "<tr>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{0}</small></td>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{1}</small></td>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{2}</small></td>\n <td class=\"row-center\" bgcolor=\"{6}\" rowspan=\"2\"><small>{3}</small></td>\n <td class=\"row-center\" bgcolor=\"{6}\" rowspan=\"2\"><small><a href=\"{4}\">{5}</a></small></td>\n";
    private static final String MANAGER_APP_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  <small>\n  &nbsp;{1}&nbsp;\n  &nbsp;{3}&nbsp;\n  &nbsp;{5}&nbsp;\n  &nbsp;{7}&nbsp;\n  </small>\n </td>\n</tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STARTED_DEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  &nbsp;<small>{1}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">  <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{4}\">  <small><input type=\"submit\" value=\"{5}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{6}\">  &nbsp;&nbsp;<small><input type=\"submit\" value=\"{7}\"></small>  </form>\n </td>\n </tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STOPPED_DEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\" rowspan=\"2\">\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">  <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  &nbsp;<small>{3}</small>&nbsp;\n  &nbsp;<small>{5}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{6}\">  <small><input type=\"submit\" value=\"{7}\"></small>  </form>\n </td>\n</tr>\n<tr></tr>\n";
    private static final String STARTED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  &nbsp;<small>{1}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">  <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{4}\">  <small><input type=\"submit\" value=\"{5}\"></small>  </form>\n  &nbsp;<small>{7}</small>&nbsp;\n </td>\n </tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STOPPED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\" rowspan=\"2\">\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">  <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  &nbsp;<small>{3}</small>&nbsp;\n  &nbsp;<small>{5}</small>&nbsp;\n  &nbsp;<small>{7}</small>&nbsp;\n </td>\n</tr>\n<tr></tr>\n";
    private static final String DEPLOY_SECTION = "</table>\n<br>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployPath\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{4}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployVersion\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{5}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployConfig\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{6}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployWar\" size=\"40\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{7}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n";
    private static final String UPLOAD_SECTION = "<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{0}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{1}\" enctype=\"multipart/form-data\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{2}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"file\" name=\"deployWar\" size=\"40\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{3}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>\n\n";
    private static final String CONFIG_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"tlsHostName\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{4}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>";
    private static final String DIAGNOSTICS_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{2}\">\n   <input type=\"submit\" value=\"{4}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{3}</small>\n </td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{5}</small></td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{6}\">\n   <input type=\"submit\" value=\"{7}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{8}</small>\n </td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{9}\">\n   <input type=\"submit\" value=\"{10}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{11}</small>\n </td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{12}\">\n   <input type=\"submit\" value=\"{13}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{14}</small>\n </td>\n</tr>\n</table>\n<br>";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && !command.equals("/") && !command.equals("/list")) {
            if (command.equals("/sessions")) {
                try {
                    this.doSessions(cn, request, response, smClient);
                    return;
                }
                catch (Exception e) {
                    this.log(sm.getString("htmlManagerServlet.error.sessions", new Object[]{cn}), e);
                    message = smClient.getString("managerServlet.exception", new Object[]{e.toString()});
                }
            } else if (command.equals("/sslConnectorCiphers")) {
                this.sslConnectorCiphers(request, response, smClient);
            } else if (command.equals("/sslConnectorCerts")) {
                this.sslConnectorCerts(request, response, smClient);
            } else if (command.equals("/sslConnectorTrustedCerts")) {
                this.sslConnectorTrustedCerts(request, response, smClient);
            } else {
                message = command.equals("/upload") || command.equals("/deploy") || command.equals("/reload") || command.equals("/undeploy") || command.equals("/expire") || command.equals("/start") || command.equals("/stop") ? smClient.getString("managerServlet.postCommand", new Object[]{command}) : smClient.getString("managerServlet.unknownCommand", new Object[]{command});
            }
        }
        this.list(request, response, message, smClient);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        String deployPath = request.getParameter("deployPath");
        String deployWar = request.getParameter("deployWar");
        String deployConfig = request.getParameter("deployConfig");
        ContextName deployCn = null;
        if (deployPath != null && deployPath.length() > 0) {
            deployCn = new ContextName(deployPath, request.getParameter("deployVersion"));
        } else if (deployConfig != null && deployConfig.length() > 0) {
            deployCn = ContextName.extractFromPath(deployConfig);
        } else if (deployWar != null && deployWar.length() > 0) {
            deployCn = ContextName.extractFromPath(deployWar);
        }
        String tlsHostName = request.getParameter("tlsHostName");
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && command.length() != 0) {
            if (command.equals("/upload")) {
                message = this.upload(request, smClient);
            } else if (command.equals("/deploy")) {
                message = this.deployInternal(deployConfig, deployCn, deployWar, smClient);
            } else if (command.equals("/reload")) {
                message = this.reload(cn, smClient);
            } else if (command.equals("/undeploy")) {
                message = this.undeploy(cn, smClient);
            } else if (command.equals("/expire")) {
                message = this.expireSessions(cn, request, smClient);
            } else if (command.equals("/start")) {
                message = this.start(cn, smClient);
            } else if (command.equals("/stop")) {
                message = this.stop(cn, smClient);
            } else if (command.equals("/findleaks")) {
                message = this.findleaks(smClient);
            } else if (command.equals("/sslReload")) {
                message = this.sslReload(tlsHostName, smClient);
            } else {
                this.doGet(request, response);
                return;
            }
        }
        this.list(request, response, message, smClient);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String upload(HttpServletRequest request, StringManager smClient) {
        String message;
        block12: {
            message = "";
            try {
                File file;
                Part warPart = request.getPart("deployWar");
                if (warPart == null) {
                    message = smClient.getString("htmlManagerServlet.deployUploadNoFile");
                    break block12;
                }
                String filename = warPart.getSubmittedFileName();
                if (filename == null || !filename.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    message = smClient.getString("htmlManagerServlet.deployUploadNotWar", new Object[]{filename});
                    break block12;
                }
                if (filename.lastIndexOf(92) >= 0) {
                    filename = filename.substring(filename.lastIndexOf(92) + 1);
                }
                if (filename.lastIndexOf(47) >= 0) {
                    filename = filename.substring(filename.lastIndexOf(47) + 1);
                }
                if ((file = new File(this.host.getAppBaseFile(), filename)).exists()) {
                    message = smClient.getString("htmlManagerServlet.deployUploadWarExists", new Object[]{filename});
                    break block12;
                }
                ContextName cn = new ContextName(filename, true);
                String name = cn.getName();
                if (this.host.findChild(name) != null && !this.isDeployed(name)) {
                    message = smClient.getString("htmlManagerServlet.deployUploadInServerXml", new Object[]{filename});
                    break block12;
                }
                if (this.tryAddServiced(name)) {
                    try {
                        warPart.write(file.getAbsolutePath());
                    }
                    finally {
                        this.removeServiced(name);
                    }
                    this.check(name);
                    break block12;
                }
                message = smClient.getString("managerServlet.inService", new Object[]{name});
            }
            catch (Exception e) {
                message = smClient.getString("htmlManagerServlet.deployUploadFail", new Object[]{e.getMessage()});
                this.log(message, e);
            }
        }
        return message;
    }

    protected String deployInternal(String config, ContextName cn, String war, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.deploy(printWriter, config, cn, war, false, smClient);
        return stringWriter.toString();
    }

    protected void list(HttpServletRequest request, HttpServletResponse response, String message, StringManager smClient) throws IOException {
        if (this.debug >= 1) {
            this.log("list: Listing contexts for virtual host '" + this.host.getName() + "'");
        }
        PrintWriter writer = response.getWriter();
        Object[] args = new Object[]{this.getServletContext().getContextPath(), smClient.getString("htmlManagerServlet.title")};
        writer.print(MessageFormat.format(Constants.HTML_HEADER_SECTION, args));
        writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        args = new Object[3];
        args[0] = smClient.getString("htmlManagerServlet.messageLabel");
        args[1] = message == null || message.length() == 0 ? "OK" : Escape.htmlElementContent((String)message);
        writer.print(MessageFormat.format(Constants.MESSAGE_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.manager"), response.encodeURL(this.getServletContext().getContextPath() + "/html/list"), smClient.getString("htmlManagerServlet.list"), this.getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpHtmlManagerFile"), smClient.getString("htmlManagerServlet.helpHtmlManager"), this.getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpManagerFile"), smClient.getString("htmlManagerServlet.helpManager"), response.encodeURL(this.getServletContext().getContextPath() + "/status"), smClient.getString("statusServlet.title")};
        writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.appsTitle"), smClient.getString("htmlManagerServlet.appsPath"), smClient.getString("htmlManagerServlet.appsVersion"), smClient.getString("htmlManagerServlet.appsName"), smClient.getString("htmlManagerServlet.appsAvailable"), smClient.getString("htmlManagerServlet.appsSessions"), smClient.getString("htmlManagerServlet.appsTasks")};
        writer.print(MessageFormat.format(APPS_HEADER_SECTION, args));
        Container[] children = this.host.findChildren();
        Object[] contextNames = new String[children.length];
        for (int i = 0; i < children.length; ++i) {
            contextNames[i] = children[i].getName();
        }
        Arrays.sort(contextNames);
        String appsStart = smClient.getString("htmlManagerServlet.appsStart");
        String appsStop = smClient.getString("htmlManagerServlet.appsStop");
        String appsReload = smClient.getString("htmlManagerServlet.appsReload");
        String appsUndeploy = smClient.getString("htmlManagerServlet.appsUndeploy");
        String appsExpire = smClient.getString("htmlManagerServlet.appsExpire");
        String noVersion = "<i>" + smClient.getString("htmlManagerServlet.noVersion") + "</i>";
        boolean isHighlighted = true;
        boolean isDeployed = true;
        String highlightColor = null;
        for (Object contextName : contextNames) {
            Context ctxt = (Context)this.host.findChild((String)contextName);
            if (ctxt == null) continue;
            isHighlighted = !isHighlighted;
            highlightColor = isHighlighted ? "#C3F3C3" : "#FFFFFF";
            String contextPath = ctxt.getPath();
            String displayPath = contextPath;
            if (displayPath.equals("")) {
                displayPath = "/";
            }
            StringBuilder tmp = new StringBuilder();
            tmp.append("path=");
            tmp.append(URLEncoder.DEFAULT.encode(displayPath, StandardCharsets.UTF_8));
            String webappVersion = ctxt.getWebappVersion();
            if (webappVersion != null && webappVersion.length() > 0) {
                tmp.append("&version=");
                tmp.append(URLEncoder.DEFAULT.encode(webappVersion, StandardCharsets.UTF_8));
            }
            String pathVersion = tmp.toString();
            try {
                isDeployed = this.isDeployed((String)contextName);
            }
            catch (Exception e) {
                isDeployed = false;
            }
            args = new Object[7];
            args[0] = "<a href=\"" + URLEncoder.DEFAULT.encode(contextPath + "/", StandardCharsets.UTF_8) + "\" " + "rel=\"noopener noreferrer\"" + ">" + Escape.htmlElementContent((String)displayPath) + "</a>";
            args[1] = webappVersion == null || webappVersion.isEmpty() ? noVersion : Escape.htmlElementContent((String)webappVersion);
            args[2] = ctxt.getDisplayName() == null ? "&nbsp;" : Escape.htmlElementContent((String)ctxt.getDisplayName());
            args[3] = ctxt.getState().isAvailable();
            args[4] = Escape.htmlElementContent((String)response.encodeURL(this.getServletContext().getContextPath() + "/html/sessions?" + pathVersion));
            Manager manager = ctxt.getManager();
            args[5] = manager instanceof DistributedManager && this.showProxySessions ? Integer.valueOf(((DistributedManager)((Object)manager)).getActiveSessionsFull()) : (manager != null ? Integer.valueOf(manager.getActiveSessions()) : Integer.valueOf(0));
            args[6] = highlightColor;
            writer.print(MessageFormat.format(APPS_ROW_DETAILS_SECTION, args));
            args = new Object[]{Escape.htmlElementContent((String)response.encodeURL(request.getContextPath() + "/html/start?" + pathVersion)), appsStart, Escape.htmlElementContent((String)response.encodeURL(request.getContextPath() + "/html/stop?" + pathVersion)), appsStop, Escape.htmlElementContent((String)response.encodeURL(request.getContextPath() + "/html/reload?" + pathVersion)), appsReload, Escape.htmlElementContent((String)response.encodeURL(request.getContextPath() + "/html/undeploy?" + pathVersion)), appsUndeploy, Escape.htmlElementContent((String)response.encodeURL(request.getContextPath() + "/html/expire?" + pathVersion)), appsExpire, smClient.getString("htmlManagerServlet.expire.explain"), manager == null ? smClient.getString("htmlManagerServlet.noManager") : Integer.valueOf(ctxt.getSessionTimeout()), smClient.getString("htmlManagerServlet.expire.unit"), highlightColor};
            if (ctxt.getName().equals(this.context.getName())) {
                writer.print(MessageFormat.format(MANAGER_APP_ROW_BUTTON_SECTION, args));
                continue;
            }
            if (ctxt.getState().isAvailable() && isDeployed) {
                writer.print(MessageFormat.format(STARTED_DEPLOYED_APPS_ROW_BUTTON_SECTION, args));
                continue;
            }
            if (ctxt.getState().isAvailable() && !isDeployed) {
                writer.print(MessageFormat.format(STARTED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION, args));
                continue;
            }
            if (!ctxt.getState().isAvailable() && isDeployed) {
                writer.print(MessageFormat.format(STOPPED_DEPLOYED_APPS_ROW_BUTTON_SECTION, args));
                continue;
            }
            writer.print(MessageFormat.format(STOPPED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION, args));
        }
        args = new Object[]{smClient.getString("htmlManagerServlet.deployTitle"), smClient.getString("htmlManagerServlet.deployServer"), response.encodeURL(this.getServletContext().getContextPath() + "/html/deploy"), smClient.getString("htmlManagerServlet.deployPath"), smClient.getString("htmlManagerServlet.deployVersion"), smClient.getString("htmlManagerServlet.deployConfig"), smClient.getString("htmlManagerServlet.deployWar"), smClient.getString("htmlManagerServlet.deployButton")};
        writer.print(MessageFormat.format(DEPLOY_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.deployUpload"), response.encodeURL(this.getServletContext().getContextPath() + "/html/upload"), smClient.getString("htmlManagerServlet.deployUploadFile"), smClient.getString("htmlManagerServlet.deployButton")};
        writer.print(MessageFormat.format(UPLOAD_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.configTitle"), smClient.getString("htmlManagerServlet.configSslReloadTitle"), response.encodeURL(this.getServletContext().getContextPath() + "/html/sslReload"), smClient.getString("htmlManagerServlet.configSslHostName"), smClient.getString("htmlManagerServlet.configReloadButton")};
        writer.print(MessageFormat.format(CONFIG_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.diagnosticsTitle"), smClient.getString("htmlManagerServlet.diagnosticsLeak"), response.encodeURL(this.getServletContext().getContextPath() + "/html/findleaks"), smClient.getString("htmlManagerServlet.diagnosticsLeakWarning"), smClient.getString("htmlManagerServlet.diagnosticsLeakButton"), smClient.getString("htmlManagerServlet.diagnosticsSsl"), response.encodeURL(this.getServletContext().getContextPath() + "/html/sslConnectorCiphers"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCipherButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCipherText"), response.encodeURL(this.getServletContext().getContextPath() + "/html/sslConnectorCerts"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCertsButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCertsText"), response.encodeURL(this.getServletContext().getContextPath() + "/html/sslConnectorTrustedCerts"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorTrustedCertsButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorTrustedCertsText")};
        writer.print(MessageFormat.format(DIAGNOSTICS_SECTION, args));
        args = new Object[]{smClient.getString("htmlManagerServlet.serverTitle"), smClient.getString("htmlManagerServlet.serverVersion"), smClient.getString("htmlManagerServlet.serverJVMVersion"), smClient.getString("htmlManagerServlet.serverJVMVendor"), smClient.getString("htmlManagerServlet.serverOSName"), smClient.getString("htmlManagerServlet.serverOSVersion"), smClient.getString("htmlManagerServlet.serverOSArch"), smClient.getString("htmlManagerServlet.serverHostname"), smClient.getString("htmlManagerServlet.serverIPAddress")};
        writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, args));
        args = new Object[8];
        args[0] = ServerInfo.getServerInfo();
        args[1] = System.getProperty("java.runtime.version");
        args[2] = System.getProperty("java.vm.vendor");
        args[3] = System.getProperty("os.name");
        args[4] = System.getProperty("os.version");
        args[5] = System.getProperty("os.arch");
        try {
            InetAddress address = InetAddress.getLocalHost();
            args[6] = address.getHostName();
            args[7] = address.getHostAddress();
        }
        catch (UnknownHostException e) {
            args[6] = "-";
            args[7] = "-";
        }
        writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        writer.print(Constants.HTML_TAIL_SECTION);
        writer.flush();
        writer.close();
    }

    protected String reload(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.reload(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String undeploy(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.undeploy(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String sessions(ContextName cn, int idle, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.sessions(printWriter, cn, idle, smClient);
        return stringWriter.toString();
    }

    protected String start(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.start(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String stop(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.stop(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String findleaks(StringManager smClient) {
        StringBuilder msg = new StringBuilder();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.findleaks(false, printWriter, smClient);
        String writerText = stringWriter.toString();
        if (writerText.length() > 0) {
            if (!writerText.startsWith("FAIL -")) {
                msg.append(smClient.getString("htmlManagerServlet.findleaksList"));
            }
            msg.append(writerText);
        } else {
            msg.append(smClient.getString("htmlManagerServlet.findleaksNone"));
        }
        return msg.toString();
    }

    protected String sslReload(String tlsHostName, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.sslReload(printWriter, tlsHostName, smClient);
        return stringWriter.toString();
    }

    protected void sslConnectorCiphers(HttpServletRequest request, HttpServletResponse response, StringManager smClient) throws ServletException, IOException {
        request.setAttribute("cipherList", this.getConnectorCiphers(smClient));
        this.getServletContext().getRequestDispatcher(connectorCiphersJspPath).forward((ServletRequest)request, (ServletResponse)response);
    }

    protected void sslConnectorCerts(HttpServletRequest request, HttpServletResponse response, StringManager smClient) throws ServletException, IOException {
        request.setAttribute("certList", this.getConnectorCerts(smClient));
        this.getServletContext().getRequestDispatcher(connectorCertsJspPath).forward((ServletRequest)request, (ServletResponse)response);
    }

    protected void sslConnectorTrustedCerts(HttpServletRequest request, HttpServletResponse response, StringManager smClient) throws ServletException, IOException {
        request.setAttribute("trustedCertList", this.getConnectorTrustedCerts(smClient));
        this.getServletContext().getRequestDispatcher(connectorTrustedCertsJspPath).forward((ServletRequest)request, (ServletResponse)response);
    }

    public String getServletInfo() {
        return "HTMLManagerServlet, Copyright (c) 1999-2023, The Apache Software Foundation";
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String value = null;
        value = this.getServletConfig().getInitParameter("showProxySessions");
        this.showProxySessions = Boolean.parseBoolean(value);
    }

    protected String expireSessions(ContextName cn, HttpServletRequest req, StringManager smClient) {
        int idle = -1;
        String idleParam = req.getParameter("idle");
        if (idleParam != null) {
            try {
                idle = Integer.parseInt(idleParam);
            }
            catch (NumberFormatException e) {
                this.log(sm.getString("managerServlet.error.idleParam", new Object[]{idleParam}));
            }
        }
        return this.sessions(cn, idle, smClient);
    }

    protected void doSessions(ContextName cn, HttpServletRequest req, HttpServletResponse resp, StringManager smClient) throws ServletException, IOException {
        req.setAttribute("path", (Object)cn.getPath());
        req.setAttribute("version", (Object)cn.getVersion());
        String action = req.getParameter("action");
        if (this.debug >= 1) {
            this.log("sessions: Session action '" + action + "' for web application '" + cn.getDisplayName() + "'");
        }
        if ("sessionDetail".equals(action)) {
            String sessionId = req.getParameter("sessionId");
            this.displaySessionDetailPage(req, resp, cn, sessionId, smClient);
            return;
        }
        if ("invalidateSessions".equals(action)) {
            String[] sessionIds = req.getParameterValues("sessionIds");
            int i = this.invalidateSessions(cn, sessionIds, smClient);
            req.setAttribute(APPLICATION_MESSAGE, (Object)("" + i + " sessions invalidated."));
        } else if ("removeSessionAttribute".equals(action)) {
            String name;
            String sessionId = req.getParameter("sessionId");
            boolean removed = this.removeSessionAttribute(cn, sessionId, name = req.getParameter("attributeName"), smClient);
            String outMessage = removed ? "Session attribute '" + name + "' removed." : "Session did not contain any attribute named '" + name + "'";
            req.setAttribute(APPLICATION_MESSAGE, (Object)outMessage);
            this.displaySessionDetailPage(req, resp, cn, sessionId, smClient);
            return;
        }
        this.displaySessionsListPage(cn, req, resp, smClient);
    }

    protected List<Session> getSessionsForName(ContextName cn, StringManager smClient) {
        if (cn == null || !cn.getPath().startsWith("/") && !cn.getPath().equals("")) {
            String path = null;
            if (cn != null) {
                path = cn.getPath();
            }
            throw new IllegalArgumentException(smClient.getString("managerServlet.invalidPath", new Object[]{Escape.htmlElementContent((String)path)}));
        }
        Context ctxt = (Context)this.host.findChild(cn.getName());
        if (null == ctxt) {
            throw new IllegalArgumentException(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)cn.getDisplayName())}));
        }
        Manager manager = ctxt.getManager();
        ArrayList<Session> sessions = new ArrayList<Session>(Arrays.asList(manager.findSessions()));
        if (manager instanceof DistributedManager && this.showProxySessions) {
            Set<String> sessionIds = ((DistributedManager)((Object)manager)).getSessionIdsFull();
            for (Session session : sessions) {
                sessionIds.remove(session.getId());
            }
            for (String sessionId : sessionIds) {
                sessions.add(new DummyProxySession(sessionId));
            }
        }
        return sessions;
    }

    protected Session getSessionForNameAndId(ContextName cn, String id, StringManager smClient) {
        List<Session> sessions = this.getSessionsForName(cn, smClient);
        if (sessions.isEmpty()) {
            return null;
        }
        for (Session session : sessions) {
            if (!session.getId().equals(id)) continue;
            return session;
        }
        return null;
    }

    protected void displaySessionsListPage(ContextName cn, HttpServletRequest req, HttpServletResponse resp, StringManager smClient) throws ServletException, IOException {
        List<Session> sessions = this.getSessionsForName(cn, smClient);
        String sortBy = req.getParameter("sort");
        String orderBy = null;
        if (null != sortBy && !"".equals(sortBy.trim())) {
            Comparator<Session> comparator = this.getComparator(sortBy);
            if (comparator != null) {
                orderBy = req.getParameter("order");
                if ("DESC".equalsIgnoreCase(orderBy)) {
                    comparator = Collections.reverseOrder(comparator);
                    orderBy = "ASC";
                } else {
                    orderBy = "DESC";
                }
                try {
                    sessions.sort(comparator);
                }
                catch (IllegalStateException ise) {
                    req.setAttribute(APPLICATION_ERROR, (Object)"Can't sort session list: one session is invalidated");
                }
            } else {
                this.log(sm.getString("htmlManagerServlet.error.sortOrder", new Object[]{sortBy}));
            }
        }
        req.setAttribute("sort", (Object)sortBy);
        req.setAttribute("order", orderBy);
        req.setAttribute("activeSessions", sessions);
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
        resp.setDateHeader("Expires", 0L);
        this.getServletContext().getRequestDispatcher(sessionsListJspPath).include((ServletRequest)req, (ServletResponse)resp);
    }

    protected void displaySessionDetailPage(HttpServletRequest req, HttpServletResponse resp, ContextName cn, String sessionId, StringManager smClient) throws ServletException, IOException {
        Session session = this.getSessionForNameAndId(cn, sessionId, smClient);
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
        resp.setDateHeader("Expires", 0L);
        req.setAttribute("currentSession", (Object)session);
        this.getServletContext().getRequestDispatcher(resp.encodeURL(sessionDetailJspPath)).include((ServletRequest)req, (ServletResponse)resp);
    }

    protected int invalidateSessions(ContextName cn, String[] sessionIds, StringManager smClient) {
        if (null == sessionIds) {
            return 0;
        }
        int nbAffectedSessions = 0;
        for (String sessionId : sessionIds) {
            Session session = this.getSessionForNameAndId(cn, sessionId, smClient);
            if (null == session) {
                if (this.debug < 1) continue;
                this.log("Cannot invalidate null session " + sessionId);
                continue;
            }
            try {
                session.getSession().invalidate();
                ++nbAffectedSessions;
                if (this.debug < 1) continue;
                this.log("Invalidating session id " + sessionId);
            }
            catch (IllegalStateException ise) {
                if (this.debug < 1) continue;
                this.log("Cannot invalidate already invalidated session id " + sessionId);
            }
        }
        return nbAffectedSessions;
    }

    protected boolean removeSessionAttribute(ContextName cn, String sessionId, String attributeName, StringManager smClient) {
        boolean wasPresent;
        block4: {
            Session session = this.getSessionForNameAndId(cn, sessionId, smClient);
            if (null == session) {
                if (this.debug >= 1) {
                    this.log("Cannot remove attribute '" + attributeName + "' for null session " + sessionId);
                }
                return false;
            }
            HttpSession httpSession = session.getSession();
            wasPresent = null != httpSession.getAttribute(attributeName);
            try {
                httpSession.removeAttribute(attributeName);
            }
            catch (IllegalStateException ise) {
                if (this.debug < 1) break block4;
                this.log("Cannot remote attribute '" + attributeName + "' for invalidated session id " + sessionId);
            }
        }
        return wasPresent;
    }

    protected Comparator<Session> getComparator(String sortBy) {
        Comparator<Session> comparator = null;
        if ("CreationTime".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingLong(Session::getCreationTime);
        }
        if ("id".equalsIgnoreCase(sortBy)) {
            return HTMLManagerServlet.comparingNullable(Session::getId);
        }
        if ("LastAccessedTime".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingLong(Session::getLastAccessedTime);
        }
        if ("MaxInactiveInterval".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingInt(Session::getMaxInactiveInterval);
        }
        if ("new".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(s -> s.getSession().isNew());
        }
        if ("locale".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(JspHelper::guessDisplayLocaleFromSession);
        }
        if ("user".equalsIgnoreCase(sortBy)) {
            return HTMLManagerServlet.comparingNullable(JspHelper::guessDisplayUserFromSession);
        }
        if ("UsedTime".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingLong(SessionUtils::getUsedTimeForSession);
        }
        if ("InactiveTime".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingLong(SessionUtils::getInactiveTimeForSession);
        }
        if ("TTL".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingLong(SessionUtils::getTTLForSession);
        }
        return comparator;
    }

    private static <U extends Comparable<? super U>> Comparator<Session> comparingNullable(Function<Session, ? extends U> keyExtractor) {
        return (s1, s2) -> {
            Comparable c1 = (Comparable)keyExtractor.apply((Session)s1);
            Comparable c2 = (Comparable)keyExtractor.apply((Session)s2);
            return c1 == null ? (c2 == null ? 0 : -1) : (c2 == null ? 1 : c1.compareTo(c2));
        };
    }
}

