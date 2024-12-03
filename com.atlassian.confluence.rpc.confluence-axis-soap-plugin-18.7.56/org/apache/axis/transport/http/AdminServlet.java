/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServletBase;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class AdminServlet
extends AxisServletBase {
    private static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$AxisServlet == null ? (class$org$apache$axis$transport$http$AxisServlet = AdminServlet.class$("org.apache.axis.transport.http.AxisServlet")) : class$org$apache$axis$transport$http$AxisServlet).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$http$AxisServlet;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=utf-8");
        StringBuffer buffer = new StringBuffer(512);
        buffer.append("<html><head><title>Axis</title></head><body>\n");
        AxisServer server = this.getEngine();
        String cmd = request.getParameter("cmd");
        if (cmd != null) {
            String callerIP = request.getRemoteAddr();
            if (this.isDevelopment()) {
                SOAPService service;
                String name;
                if (cmd.equals("start")) {
                    log.info((Object)Messages.getMessage("adminServiceStart", callerIP));
                    server.start();
                } else if (cmd.equals("stop")) {
                    log.info((Object)Messages.getMessage("adminServiceStop", callerIP));
                    server.stop();
                } else if (cmd.equals("suspend")) {
                    name = request.getParameter("service");
                    log.info((Object)Messages.getMessage("adminServiceSuspend", name, callerIP));
                    service = server.getConfig().getService(new QName("", name));
                    service.stop();
                } else if (cmd.equals("resume")) {
                    name = request.getParameter("service");
                    log.info((Object)Messages.getMessage("adminServiceResume", name, callerIP));
                    service = server.getConfig().getService(new QName("", name));
                    service.start();
                }
            } else {
                log.info((Object)Messages.getMessage("adminServiceDeny", callerIP));
            }
        }
        if (server.isRunning()) {
            buffer.append("<H2>");
            buffer.append(Messages.getMessage("serverRun00"));
            buffer.append("</H2>");
        } else {
            buffer.append("<H2>");
            buffer.append(Messages.getMessage("serverStop00"));
            buffer.append("</H2>");
        }
        if (this.isDevelopment()) {
            Iterator i;
            buffer.append("<p><a href=\"AdminServlet?cmd=start\">start server</a>\n");
            buffer.append("<p><a href=\"AdminServlet?cmd=stop\">stop server</a>\n");
            try {
                i = server.getConfig().getDeployedServices();
            }
            catch (ConfigurationException configException) {
                if (configException.getContainedException() instanceof AxisFault) {
                    throw (AxisFault)configException.getContainedException();
                }
                throw configException;
            }
            buffer.append("<p><h2>Services</h2>");
            buffer.append("<ul>");
            while (i.hasNext()) {
                ServiceDesc sd = (ServiceDesc)i.next();
                StringBuffer sb = new StringBuffer();
                sb.append("<li>");
                String name = sd.getName();
                sb.append(name);
                SOAPService service = server.getConfig().getService(new QName("", name));
                if (service.isRunning()) {
                    sb.append("&nbsp;&nbsp;<a href=\"AdminServlet?cmd=suspend&service=" + name + "\">suspend</a>\n");
                } else {
                    sb.append("&nbsp;&nbsp;<a href=\"AdminServlet?cmd=resume&service=" + name + "\">resume</a>\n");
                }
                sb.append("</li>");
                buffer.append(sb.toString());
            }
            buffer.append("</ul>");
        }
        buffer.append("<p>");
        buffer.append(Messages.getMessage("adminServiceLoad", Integer.toString(AdminServlet.getLoadCounter())));
        buffer.append("\n</body></html>\n");
        response.getWriter().print(new String(buffer));
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

