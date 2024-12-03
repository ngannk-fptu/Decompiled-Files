/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.QSHandler;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

public abstract class AbstractQueryStringHandler
implements QSHandler {
    private boolean development;
    protected Log exceptionLog;
    protected Log log;

    protected boolean isDevelopment() {
        return this.development;
    }

    protected void configureFromContext(MessageContext msgContext) {
        this.development = (Boolean)msgContext.getProperty("transport.http.plugin.isDevelopment");
        this.exceptionLog = (Log)msgContext.getProperty("transport.http.plugin.exceptionLog");
        this.log = (Log)msgContext.getProperty("transport.http.plugin.log");
    }

    protected void processAxisFault(AxisFault fault) {
        Element runtimeException = fault.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        if (runtimeException != null) {
            this.exceptionLog.info((Object)Messages.getMessage("axisFault00"), (Throwable)fault);
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        } else if (this.exceptionLog.isDebugEnabled()) {
            this.exceptionLog.debug((Object)Messages.getMessage("axisFault00"), (Throwable)fault);
        }
        if (!this.isDevelopment()) {
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        }
    }

    protected void configureResponseFromAxisFault(HttpServletResponse response, AxisFault fault) {
        int status = this.getHttpServletResponseStatus(fault);
        if (status == 401) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"AXIS\"");
        }
        response.setStatus(status);
    }

    protected Message convertExceptionToAxisFault(Exception exception, Message responseMsg) {
        this.logException(exception);
        if (responseMsg == null) {
            AxisFault fault = AxisFault.makeFault(exception);
            this.processAxisFault(fault);
            responseMsg = new Message(fault);
        }
        return responseMsg;
    }

    private int getHttpServletResponseStatus(AxisFault af) {
        return af.getFaultCode().getLocalPart().startsWith("Server.Unauth") ? 401 : 500;
    }

    private void logException(Exception e) {
        this.exceptionLog.info((Object)Messages.getMessage("exception00"), (Throwable)e);
    }

    protected void writeFault(PrintWriter writer, AxisFault axisFault) {
        String localizedMessage = XMLUtils.xmlEncodeString(axisFault.getLocalizedMessage());
        writer.println("<pre>Fault - " + localizedMessage + "<br>");
        writer.println(axisFault.dumpToString());
        writer.println("</pre>");
    }
}

