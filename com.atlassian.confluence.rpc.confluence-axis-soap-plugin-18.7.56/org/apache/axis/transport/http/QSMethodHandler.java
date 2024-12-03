/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.axis.transport.http;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AbstractQueryStringHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;

public class QSMethodHandler
extends AbstractQueryStringHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        this.configureFromContext(msgContext);
        AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
        PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
        HttpServletRequest request = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
        String method = null;
        String args = "";
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String param = (String)enumeration.nextElement();
            if (param.equalsIgnoreCase("method")) {
                method = request.getParameter(param);
                continue;
            }
            args = args + "<" + param + ">" + request.getParameter(param) + "</" + param + ">";
        }
        if (method == null) {
            response.setContentType("text/html");
            response.setStatus(400);
            writer.println("<h2>" + Messages.getMessage("error00") + ":  " + Messages.getMessage("invokeGet00") + "</h2>");
            writer.println("<p>" + Messages.getMessage("noMethod01") + "</p>");
        } else {
            this.invokeEndpointFromGet(msgContext, response, writer, method, args);
        }
    }

    private void invokeEndpointFromGet(MessageContext msgContext, HttpServletResponse response, PrintWriter writer, String method, String args) throws AxisFault {
        String body = "<" + method + ">" + args + "</" + method + ">";
        String msgtxt = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
        ByteArrayInputStream istream = new ByteArrayInputStream(msgtxt.getBytes());
        Message responseMsg = null;
        try {
            AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
            Message msg = new Message((Object)istream, false);
            msgContext.setRequestMessage(msg);
            engine.invoke(msgContext);
            responseMsg = msgContext.getResponseMessage();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            if (responseMsg == null) {
                throw new Exception(Messages.getMessage("noResponse01"));
            }
        }
        catch (AxisFault fault) {
            this.processAxisFault(fault);
            this.configureResponseFromAxisFault(response, fault);
            if (responseMsg == null) {
                responseMsg = new Message(fault);
                responseMsg.setMessageContext(msgContext);
            }
        }
        catch (Exception e) {
            response.setStatus(500);
            responseMsg = this.convertExceptionToAxisFault(e, responseMsg);
        }
        response.setContentType("text/xml");
        writer.println(responseMsg.getSOAPPartAsString());
    }
}

