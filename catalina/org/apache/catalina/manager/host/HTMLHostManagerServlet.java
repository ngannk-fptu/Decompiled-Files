/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.manager.host;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.manager.Constants;
import org.apache.catalina.manager.host.HostManagerServlet;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public final class HTMLHostManagerServlet
extends HostManagerServlet {
    private static final long serialVersionUID = 1L;
    private static final String HOSTS_HEADER_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"5\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-left\"><small>{0}</small></td>\n <td class=\"header-center\"><small>{1}</small></td>\n <td class=\"header-center\"><small>{2}</small></td>\n</tr>\n";
    private static final String HOSTS_ROW_DETAILS_SECTION = "<tr>\n <td class=\"row-left\"><small><a href=\"http://{0}\" rel=\"noopener noreferrer\">{0}</a></small></td>\n <td class=\"row-center\"><small>{1}</small></td>\n";
    private static final String MANAGER_HOST_ROW_BUTTON_SECTION = " <td class=\"row-left\">\n  <small>{4}</small>\n </td>\n</tr>\n";
    private static final String HOSTS_ROW_BUTTON_SECTION = " <td class=\"row-left\" NOWRAP>\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">   <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">   <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n </td>\n</tr>\n";
    private static final String ADD_SECTION_START = "</table>\n<br>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"name\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{4}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"aliases\" size=\"64\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{5}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"appBase\" size=\"64\">\n </td>\n</tr>\n";
    private static final String ADD_SECTION_BOOLEAN = "<tr>\n <td class=\"row-right\">\n  <small>{0}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"checkbox\" name=\"{1}\" {2}>\n </td>\n</tr>\n";
    private static final String ADD_SECTION_END = "<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{0}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>\n\n";
    private static final String PERSIST_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form class=\"inline\" method=\"POST\" action=\"{1}\">   <small><input type=\"submit\" value=\"{2}\"></small>  </form> {3}\n </td>\n</tr>\n</table>\n<br>\n\n";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager.host", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && !command.equals("/list")) {
            message = command.equals("/add") || command.equals("/remove") || command.equals("/start") || command.equals("/stop") || command.equals("/persist") ? smClient.getString("hostManagerServlet.postCommand", new Object[]{command}) : smClient.getString("hostManagerServlet.unknownCommand", new Object[]{command});
        }
        this.list(request, response, message, smClient);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager.host", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        String name = request.getParameter("name");
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null) {
            if (command.equals("/add")) {
                message = this.add(request, name, smClient);
            } else if (command.equals("/remove")) {
                message = this.remove(name, smClient);
            } else if (command.equals("/start")) {
                message = this.start(name, smClient);
            } else if (command.equals("/stop")) {
                message = this.stop(name, smClient);
            } else if (command.equals("/persist")) {
                message = this.persist(smClient);
            } else {
                this.doGet(request, response);
            }
        }
        this.list(request, response, message, smClient);
    }

    protected String add(HttpServletRequest request, String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.add(request, printWriter, name, true, smClient);
        return stringWriter.toString();
    }

    protected String remove(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.remove(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String start(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.start(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String stop(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.stop(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String persist(StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.persist(printWriter, smClient);
        return stringWriter.toString();
    }

    public void list(HttpServletRequest request, HttpServletResponse response, String message, StringManager smClient) throws IOException {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.list", new Object[]{this.engine.getName()}));
        }
        PrintWriter writer = response.getWriter();
        Object[] args = new Object[]{this.getServletContext().getContextPath(), smClient.getString("htmlHostManagerServlet.title")};
        writer.print(MessageFormat.format(Constants.HTML_HEADER_SECTION, args));
        writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        args = new Object[3];
        args[0] = smClient.getString("htmlHostManagerServlet.messageLabel");
        args[1] = message == null || message.length() == 0 ? "OK" : Escape.htmlElementContent((String)message);
        writer.print(MessageFormat.format("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n <tr>\n  <td class=\"row-left\" width=\"10%\"><small><strong>{0}</strong></small>&nbsp;</td>\n  <td class=\"row-left\"><pre>{1}</pre></td>\n </tr>\n</table>\n<br>\n\n", args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.manager"), response.encodeURL(this.getServletContext().getContextPath() + "/html/list"), smClient.getString("htmlHostManagerServlet.list"), this.getServletContext().getContextPath() + "/" + smClient.getString("htmlHostManagerServlet.helpHtmlManagerFile"), smClient.getString("htmlHostManagerServlet.helpHtmlManager"), this.getServletContext().getContextPath() + "/" + smClient.getString("htmlHostManagerServlet.helpManagerFile"), smClient.getString("htmlHostManagerServlet.helpManager"), response.encodeURL("/manager/status"), smClient.getString("statusServlet.title")};
        writer.print(MessageFormat.format("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"4\" class=\"title\">{0}</td>\n</tr>\n <tr>\n  <td class=\"row-left\"><a href=\"{1}\">{2}</a></td>\n  <td class=\"row-center\"><a href=\"{3}\" rel=\"noopener noreferrer\">{4}</a></td>\n  <td class=\"row-center\"><a href=\"{5}\" rel=\"noopener noreferrer\">{6}</a></td>\n  <td class=\"row-right\"><a href=\"{7}\">{8}</a></td>\n </tr>\n</table>\n<br>\n\n", args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.hostName"), smClient.getString("htmlHostManagerServlet.hostAliases"), smClient.getString("htmlHostManagerServlet.hostTasks")};
        writer.print(MessageFormat.format(HOSTS_HEADER_SECTION, args));
        Container[] children = this.engine.findChildren();
        String[] hostNames = new String[children.length];
        for (int i = 0; i < children.length; ++i) {
            hostNames[i] = children[i].getName();
        }
        TreeSet<String> sortedHostNames = new TreeSet<String>();
        sortedHostNames.addAll(Arrays.asList(hostNames));
        String hostsStart = smClient.getString("htmlHostManagerServlet.hostsStart");
        String hostsStop = smClient.getString("htmlHostManagerServlet.hostsStop");
        String hostsRemove = smClient.getString("htmlHostManagerServlet.hostsRemove");
        String hostThis = smClient.getString("htmlHostManagerServlet.hostThis");
        for (String hostName : sortedHostNames) {
            Host host = (Host)this.engine.findChild(hostName);
            if (host == null) continue;
            args = new Object[2];
            args[0] = Escape.htmlElementContent((String)hostName);
            String[] aliases = host.findAliases();
            StringBuilder buf = new StringBuilder();
            if (aliases.length > 0) {
                buf.append(aliases[0]);
                for (int j = 1; j < aliases.length; ++j) {
                    buf.append(", ").append(aliases[j]);
                }
            }
            if (buf.length() == 0) {
                buf.append("&nbsp;");
                args[1] = buf.toString();
            } else {
                args[1] = Escape.htmlElementContent((String)buf.toString());
            }
            writer.print(MessageFormat.format(HOSTS_ROW_DETAILS_SECTION, args));
            args = new Object[5];
            if (host.getState().isAvailable()) {
                args[0] = response.encodeURL(this.getServletContext().getContextPath() + "/html/stop?name=" + URLEncoder.encode(hostName, "UTF-8"));
                args[1] = hostsStop;
            } else {
                args[0] = response.encodeURL(this.getServletContext().getContextPath() + "/html/start?name=" + URLEncoder.encode(hostName, "UTF-8"));
                args[1] = hostsStart;
            }
            args[2] = response.encodeURL(this.getServletContext().getContextPath() + "/html/remove?name=" + URLEncoder.encode(hostName, "UTF-8"));
            args[3] = hostsRemove;
            args[4] = hostThis;
            if (host == this.installedHost) {
                writer.print(MessageFormat.format(MANAGER_HOST_ROW_BUTTON_SECTION, args));
                continue;
            }
            writer.print(MessageFormat.format(HOSTS_ROW_BUTTON_SECTION, args));
        }
        args = new Object[]{smClient.getString("htmlHostManagerServlet.addTitle"), smClient.getString("htmlHostManagerServlet.addHost"), response.encodeURL(this.getServletContext().getContextPath() + "/html/add"), smClient.getString("htmlHostManagerServlet.addName"), smClient.getString("htmlHostManagerServlet.addAliases"), smClient.getString("htmlHostManagerServlet.addAppBase")};
        writer.print(MessageFormat.format(ADD_SECTION_START, args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.addAutoDeploy"), "autoDeploy", "checked"};
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args[0] = smClient.getString("htmlHostManagerServlet.addDeployOnStartup");
        args[1] = "deployOnStartup";
        args[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args[0] = smClient.getString("htmlHostManagerServlet.addDeployXML");
        args[1] = "deployXML";
        args[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args[0] = smClient.getString("htmlHostManagerServlet.addUnpackWARs");
        args[1] = "unpackWARs";
        args[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args[0] = smClient.getString("htmlHostManagerServlet.addManager");
        args[1] = "manager";
        args[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args[0] = smClient.getString("htmlHostManagerServlet.addCopyXML");
        args[1] = "copyXML";
        args[2] = "";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.addButton")};
        writer.print(MessageFormat.format(ADD_SECTION_END, args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.persistTitle"), response.encodeURL(this.getServletContext().getContextPath() + "/html/persist"), smClient.getString("htmlHostManagerServlet.persistAllButton"), smClient.getString("htmlHostManagerServlet.persistAll")};
        writer.print(MessageFormat.format(PERSIST_SECTION, args));
        args = new Object[]{smClient.getString("htmlHostManagerServlet.serverTitle"), smClient.getString("htmlHostManagerServlet.serverVersion"), smClient.getString("htmlHostManagerServlet.serverJVMVersion"), smClient.getString("htmlHostManagerServlet.serverJVMVendor"), smClient.getString("htmlHostManagerServlet.serverOSName"), smClient.getString("htmlHostManagerServlet.serverOSVersion"), smClient.getString("htmlHostManagerServlet.serverOSArch")};
        writer.print(MessageFormat.format("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"6\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-center\"><small>{1}</small></td>\n <td class=\"header-center\"><small>{2}</small></td>\n <td class=\"header-center\"><small>{3}</small></td>\n <td class=\"header-center\"><small>{4}</small></td>\n <td class=\"header-center\"><small>{5}</small></td>\n <td class=\"header-center\"><small>{6}</small></td>\n</tr>\n", args));
        args = new Object[]{ServerInfo.getServerInfo(), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor"), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")};
        writer.print(MessageFormat.format("<tr>\n <td class=\"row-center\"><small>{0}</small></td>\n <td class=\"row-center\"><small>{1}</small></td>\n <td class=\"row-center\"><small>{2}</small></td>\n <td class=\"row-center\"><small>{3}</small></td>\n <td class=\"row-center\"><small>{4}</small></td>\n <td class=\"row-center\"><small>{5}</small></td>\n</tr>\n</table>\n<br>\n\n", args));
        writer.print("<hr size=\"1\" noshade=\"noshade\">\n<center><font size=\"-1\" color=\"#525D76\">\n <em>Copyright &copy; 1999-2023, Apache Software Foundation</em></font></center>\n\n</body>\n</html>");
        writer.flush();
        writer.close();
    }
}

