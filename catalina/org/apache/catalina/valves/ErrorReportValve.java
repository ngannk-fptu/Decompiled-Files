/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  org.apache.coyote.ActionCode
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.valves;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class ErrorReportValve
extends ValveBase {
    private boolean showReport = true;
    private boolean showServerInfo = true;
    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();

    public ErrorReportValve() {
        super(true);
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        this.getNext().invoke(request, response);
        if (response.isCommitted()) {
            if (response.setErrorReported()) {
                AtomicBoolean ioAllowed = new AtomicBoolean(true);
                response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)ioAllowed);
                if (ioAllowed.get()) {
                    try {
                        response.flushBuffer();
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                    }
                    response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("javax.servlet.error.exception"));
                }
            }
            return;
        }
        Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
        if (request.isAsync() && !request.isAsyncCompleting()) {
            return;
        }
        if (throwable != null && !response.isError()) {
            response.reset();
            response.sendError(500);
        }
        response.setSuspended(false);
        try {
            this.report(request, response, throwable);
        }
        catch (Throwable tt) {
            ExceptionUtils.handleThrowable((Throwable)tt);
        }
    }

    protected ErrorPage findErrorPage(int statusCode, Throwable throwable) {
        ErrorPage errorPage = null;
        if (throwable != null) {
            errorPage = this.errorPageSupport.find(throwable);
        }
        if (errorPage == null) {
            errorPage = this.errorPageSupport.find(statusCode);
        }
        if (errorPage == null) {
            errorPage = this.errorPageSupport.find(0);
        }
        return errorPage;
    }

    protected void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0L || !response.setErrorReported()) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)result);
        if (!result.get()) {
            return;
        }
        ErrorPage errorPage = this.findErrorPage(statusCode, throwable);
        if (errorPage != null && this.sendErrorPage(errorPage.getLocation(), response)) {
            return;
        }
        String message = Escape.htmlElementContent((String)response.getMessage());
        if (message == null) {
            String exceptionMessage;
            if (throwable != null && (exceptionMessage = throwable.getMessage()) != null && exceptionMessage.length() > 0) {
                try (Scanner scanner = new Scanner(exceptionMessage);){
                    message = Escape.htmlElementContent((String)scanner.nextLine());
                }
            }
            if (message == null) {
                message = "";
            }
        }
        String reason = null;
        String description = null;
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.valves", request.getLocales());
        response.setLocale(smClient.getLocale());
        try {
            reason = smClient.getString("http." + statusCode + ".reason");
            description = smClient.getString("http." + statusCode + ".desc");
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        if (reason == null || description == null) {
            if (message.isEmpty()) {
                return;
            }
            reason = smClient.getString("errorReportValve.unknownReason");
            description = smClient.getString("errorReportValve.noDescription");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html lang=\"");
        sb.append(smClient.getLocale().getLanguage()).append("\">");
        sb.append("<head>");
        sb.append("<title>");
        sb.append(smClient.getString("errorReportValve.statusHeader", new Object[]{String.valueOf(statusCode), reason}));
        sb.append("</title>");
        sb.append("<style type=\"text/css\">");
        sb.append("body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append(smClient.getString("errorReportValve.statusHeader", new Object[]{String.valueOf(statusCode), reason})).append("</h1>");
        if (this.isShowReport()) {
            sb.append("<hr class=\"line\" />");
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.type"));
            sb.append("</b> ");
            if (throwable != null) {
                sb.append(smClient.getString("errorReportValve.exceptionReport"));
            } else {
                sb.append(smClient.getString("errorReportValve.statusReport"));
            }
            sb.append("</p>");
            if (!message.isEmpty()) {
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.message"));
                sb.append("</b> ");
                sb.append(message).append("</p>");
            }
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.description"));
            sb.append("</b> ");
            sb.append(description);
            sb.append("</p>");
            if (throwable != null) {
                String stackTrace = this.getPartialServletStackTrace(throwable);
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.exception"));
                sb.append("</b></p><pre>");
                sb.append(Escape.htmlElementContent((String)stackTrace));
                sb.append("</pre>");
                int loops = 0;
                for (Throwable rootCause = throwable.getCause(); rootCause != null && loops < 10; rootCause = rootCause.getCause(), ++loops) {
                    stackTrace = this.getPartialServletStackTrace(rootCause);
                    sb.append("<p><b>");
                    sb.append(smClient.getString("errorReportValve.rootCause"));
                    sb.append("</b></p><pre>");
                    sb.append(Escape.htmlElementContent((String)stackTrace));
                    sb.append("</pre>");
                }
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.note"));
                sb.append("</b> ");
                sb.append(smClient.getString("errorReportValve.rootCauseInLogs"));
                sb.append("</p>");
            }
            sb.append("<hr class=\"line\" />");
        }
        if (this.isShowServerInfo()) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body></html>");
        try {
            block28: {
                try {
                    response.setContentType("text/html");
                    response.setCharacterEncoding("utf-8");
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    if (!this.container.getLogger().isDebugEnabled()) break block28;
                    this.container.getLogger().debug((Object)"Failure to set the content-type of response", t);
                }
            }
            PrintWriter writer = response.getReporter();
            if (writer != null) {
                ((Writer)writer).write(sb.toString());
                response.finishResponse();
            }
        }
        catch (IOException | IllegalStateException exception) {
            // empty catch block
        }
    }

    protected String getPartialServletStackTrace(Throwable t) {
        int i;
        StringBuilder trace = new StringBuilder();
        trace.append(t.toString()).append(System.lineSeparator());
        StackTraceElement[] elements = t.getStackTrace();
        int pos = elements.length;
        for (i = elements.length - 1; i >= 0; --i) {
            if (!elements[i].getClassName().startsWith("org.apache.catalina.core.ApplicationFilterChain") || !elements[i].getMethodName().equals("internalDoFilter")) continue;
            pos = i;
            break;
        }
        for (i = 0; i < pos; ++i) {
            if (elements[i].getClassName().startsWith("org.apache.catalina.core.")) continue;
            trace.append('\t').append(elements[i].toString()).append(System.lineSeparator());
        }
        return trace.toString();
    }

    private boolean sendErrorPage(String location, Response response) {
        File file = new File(location);
        if (!file.isAbsolute()) {
            file = new File(this.getContainer().getCatalinaBase(), location);
        }
        if (!file.isFile() || !file.canRead()) {
            this.getContainer().getLogger().warn((Object)sm.getString("errorReportValve.errorPageNotFound", new Object[]{location}));
            return false;
        }
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        try (ServletOutputStream os = response.getOutputStream();
             FileInputStream is = new FileInputStream(file);){
            IOTools.flow(is, (OutputStream)os);
        }
        catch (IOException e) {
            this.getContainer().getLogger().warn((Object)sm.getString("errorReportValve.errorPageIOException", new Object[]{location}), (Throwable)e);
            return false;
        }
        return true;
    }

    public void setShowReport(boolean showReport) {
        this.showReport = showReport;
    }

    public boolean isShowReport() {
        return this.showReport;
    }

    public void setShowServerInfo(boolean showServerInfo) {
        this.showServerInfo = showServerInfo;
    }

    public boolean isShowServerInfo() {
        return this.showServerInfo;
    }

    public boolean setProperty(String name, String value) {
        if (name.startsWith("errorCode.")) {
            int code = Integer.parseInt(name.substring(10));
            ErrorPage ep = new ErrorPage();
            ep.setErrorCode(code);
            ep.setLocation(value);
            this.errorPageSupport.add(ep);
            return true;
        }
        if (name.startsWith("exceptionType.")) {
            String className = name.substring(14);
            ErrorPage ep = new ErrorPage();
            ep.setExceptionType(className);
            ep.setLocation(value);
            this.errorPageSupport.add(ep);
            return true;
        }
        return false;
    }

    public String getProperty(String name) {
        String className;
        int code;
        ErrorPage ep;
        Object result = name.startsWith("errorCode.") ? ((ep = this.errorPageSupport.find(code = Integer.parseInt(name.substring(10)))) == null ? null : ep.getLocation()) : (name.startsWith("exceptionType.") ? ((ep = this.errorPageSupport.find(className = name.substring(14))) == null ? null : ep.getLocation()) : null);
        return result;
    }
}

