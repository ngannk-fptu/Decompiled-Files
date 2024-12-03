/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.axis.transport.http;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AbstractQueryStringHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

public class QSListHandler
extends AbstractQueryStringHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        boolean enableList = (Boolean)msgContext.getProperty("transport.http.plugin.enableList");
        AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
        PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
        HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
        if (enableList) {
            Document doc = Admin.listConfig(engine);
            if (doc != null) {
                response.setContentType("text/xml");
                XMLUtils.DocumentToWriter(doc, writer);
            } else {
                response.setStatus(404);
                response.setContentType("text/html");
                writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
                writer.println("<p>" + Messages.getMessage("noDeploy00") + "</p>");
            }
        } else {
            response.setStatus(403);
            response.setContentType("text/html");
            writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
            writer.println("<p><i>?list</i> " + Messages.getMessage("disabled00") + "</p>");
        }
    }
}

