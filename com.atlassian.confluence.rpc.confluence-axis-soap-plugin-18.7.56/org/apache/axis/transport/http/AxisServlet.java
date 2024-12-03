/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpUtils
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.security.servlet.ServletSecurityProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisHttpSession;
import org.apache.axis.transport.http.AxisServletBase;
import org.apache.axis.transport.http.FilterPrintWriter;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.ServletEndpointContextImpl;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

public class AxisServlet
extends AxisServletBase {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$AxisServlet == null ? (class$org$apache$axis$transport$http$AxisServlet = AxisServlet.class$("org.apache.axis.transport.http.AxisServlet")) : class$org$apache$axis$transport$http$AxisServlet).getName());
    private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
    private static Log exceptionLog = LogFactory.getLog("org.apache.axis.EXCEPTIONS");
    public static final String INIT_PROPERTY_TRANSPORT_NAME = "transport.name";
    public static final String INIT_PROPERTY_USE_SECURITY = "use-servlet-security";
    public static final String INIT_PROPERTY_ENABLE_LIST = "axis.enableListQuery";
    public static final String INIT_PROPERTY_JWS_CLASS_DIR = "axis.jws.servletClassDir";
    public static final String INIT_PROPERTY_DISABLE_SERVICES_LIST = "axis.disableServiceList";
    public static final String INIT_PROPERTY_SERVICES_PATH = "axis.servicesPath";
    private String transportName;
    private Handler transport;
    private ServletSecurityProvider securityProvider = null;
    private String servicesPath;
    private static boolean isDebug = false;
    private boolean enableList = false;
    private boolean disableServicesList = false;
    private String jwsClassDir = null;
    static /* synthetic */ Class class$org$apache$axis$transport$http$AxisServlet;

    protected String getJWSClassDir() {
        return this.jwsClassDir;
    }

    public void init() throws ServletException {
        super.init();
        ServletContext context = this.getServletConfig().getServletContext();
        isDebug = log.isDebugEnabled();
        if (isDebug) {
            log.debug((Object)"In servlet init");
        }
        this.transportName = this.getOption(context, INIT_PROPERTY_TRANSPORT_NAME, "http");
        if (JavaUtils.isTrueExplicitly(this.getOption(context, INIT_PROPERTY_USE_SECURITY, null))) {
            this.securityProvider = new ServletSecurityProvider();
        }
        this.enableList = JavaUtils.isTrueExplicitly(this.getOption(context, INIT_PROPERTY_ENABLE_LIST, null));
        this.jwsClassDir = this.getOption(context, INIT_PROPERTY_JWS_CLASS_DIR, null);
        this.disableServicesList = JavaUtils.isTrue(this.getOption(context, INIT_PROPERTY_DISABLE_SERVICES_LIST, "false"));
        this.servicesPath = this.getOption(context, INIT_PROPERTY_SERVICES_PATH, "/services/");
        if (this.jwsClassDir != null) {
            if (this.getHomeDir() != null) {
                this.jwsClassDir = this.getHomeDir() + this.jwsClassDir;
            }
        } else {
            this.jwsClassDir = this.getDefaultJWSClassDir();
        }
        this.initQueryStringHandlers();
        try {
            ServiceAdmin.setEngine(this.getEngine(), context.getServerInfo());
        }
        catch (AxisFault af) {
            exceptionLog.info((Object)("Exception setting AxisEngine on ServiceAdmin " + af));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FilterPrintWriter writer;
        block16: {
            block15: {
                if (isDebug) {
                    log.debug((Object)"Enter: doGet()");
                }
                writer = new FilterPrintWriter(response);
                try {
                    try {
                        boolean hasNoPath;
                        boolean isJWSPage;
                        AxisServer engine = this.getEngine();
                        ServletContext servletContext = this.getServletConfig().getServletContext();
                        String pathInfo = request.getPathInfo();
                        String realpath = servletContext.getRealPath(request.getServletPath());
                        if (realpath == null) {
                            realpath = request.getServletPath();
                        }
                        if (isJWSPage = request.getRequestURI().endsWith(".jws")) {
                            pathInfo = request.getServletPath();
                        }
                        if (this.processQuery(request, response, writer)) {
                            Object var15_11 = null;
                            ((PrintWriter)writer).close();
                            if (!isDebug) return;
                            break block15;
                        }
                        boolean bl = hasNoPath = pathInfo == null || pathInfo.equals("");
                        if (!this.disableServicesList) {
                            if (hasNoPath) {
                                this.reportAvailableServices(response, writer, request);
                                break block16;
                            }
                            if (realpath == null) break block16;
                            MessageContext msgContext = this.createMessageContext(engine, request, response);
                            String url = HttpUtils.getRequestURL((HttpServletRequest)request).toString();
                            msgContext.setProperty("transport.url", url);
                            String serviceName = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
                            SOAPService s = engine.getService(serviceName);
                            if (s == null) {
                                if (isJWSPage) {
                                    this.reportCantGetJWSService(request, response, writer);
                                    break block16;
                                } else {
                                    this.reportCantGetAxisService(request, response, writer);
                                }
                                break block16;
                            }
                            this.reportServiceInfo(response, writer, s, serviceName);
                            break block16;
                        }
                        response.setContentType("text/html; charset=utf-8");
                        ((PrintWriter)writer).println("<html><h1>Axis HTTP Servlet</h1>");
                        ((PrintWriter)writer).println(Messages.getMessage("reachedServlet00"));
                        ((PrintWriter)writer).println("<p>" + Messages.getMessage("transportName00", "<b>" + this.transportName + "</b>"));
                        ((PrintWriter)writer).println("</html>");
                        break block16;
                    }
                    catch (AxisFault fault) {
                        this.reportTroubleInGet(fault, response, writer);
                        Object var15_13 = null;
                        ((PrintWriter)writer).close();
                        if (!isDebug) return;
                        log.debug((Object)"Exit: doGet()");
                        return;
                    }
                    catch (Exception e) {
                        this.reportTroubleInGet(e, response, writer);
                        Object var15_14 = null;
                        ((PrintWriter)writer).close();
                        if (!isDebug) return;
                        log.debug((Object)"Exit: doGet()");
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var15_15 = null;
                    ((PrintWriter)writer).close();
                    if (!isDebug) throw throwable;
                    log.debug((Object)"Exit: doGet()");
                    throw throwable;
                }
            }
            log.debug((Object)"Exit: doGet()");
            return;
        }
        Object var15_12 = null;
        ((PrintWriter)writer).close();
        if (!isDebug) return;
        log.debug((Object)"Exit: doGet()");
    }

    private void reportTroubleInGet(Throwable exception, HttpServletResponse response, PrintWriter writer) {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(500);
        writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
        writer.println("<p>" + Messages.getMessage("somethingWrong00") + "</p>");
        if (exception instanceof AxisFault) {
            AxisFault fault = (AxisFault)exception;
            this.processAxisFault(fault);
            this.writeFault(writer, fault);
        } else {
            this.logException(exception);
            writer.println("<pre>Exception - " + exception + "<br>");
            if (this.isDevelopment()) {
                writer.println(JavaUtils.stackToString(exception));
            }
            writer.println("</pre>");
        }
    }

    protected void processAxisFault(AxisFault fault) {
        Element runtimeException = fault.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        if (runtimeException != null) {
            exceptionLog.info((Object)Messages.getMessage("axisFault00"), (Throwable)fault);
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        } else if (exceptionLog.isDebugEnabled()) {
            exceptionLog.debug((Object)Messages.getMessage("axisFault00"), (Throwable)fault);
        }
        if (!this.isDevelopment()) {
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        }
    }

    protected void logException(Throwable e) {
        exceptionLog.info((Object)Messages.getMessage("exception00"), e);
    }

    private void writeFault(PrintWriter writer, AxisFault axisFault) {
        String localizedMessage = XMLUtils.xmlEncodeString(axisFault.getLocalizedMessage());
        writer.println("<pre>Fault - " + localizedMessage + "<br>");
        writer.println(axisFault.dumpToString());
        writer.println("</pre>");
    }

    protected void reportServiceInfo(HttpServletResponse response, PrintWriter writer, SOAPService service, String serviceName) {
        response.setContentType("text/html; charset=utf-8");
        writer.println("<h1>" + service.getName() + "</h1>");
        writer.println("<p>" + Messages.getMessage("axisService00") + "</p>");
        writer.println("<i>" + Messages.getMessage("perhaps00") + "</i>");
    }

    protected void reportNoWSDL(HttpServletResponse res, PrintWriter writer, String moreDetailCode, AxisFault axisFault) {
    }

    protected void reportAvailableServices(HttpServletResponse response, PrintWriter writer, HttpServletRequest request) throws ConfigurationException, AxisFault {
        Iterator i;
        AxisServer engine = this.getEngine();
        response.setContentType("text/html; charset=utf-8");
        writer.println("<h2>And now... Some Services</h2>");
        try {
            i = engine.getConfig().getDeployedServices();
        }
        catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault)configException.getContainedException();
            }
            throw configException;
        }
        String defaultBaseURL = this.getWebappBase(request) + this.servicesPath;
        writer.println("<ul>");
        while (i.hasNext()) {
            ServiceDesc sd = (ServiceDesc)i.next();
            StringBuffer sb = new StringBuffer();
            sb.append("<li>");
            String name = sd.getName();
            sb.append(name);
            sb.append(" <a href=\"");
            String endpointURL = sd.getEndpointURL();
            String baseURL = endpointURL == null ? defaultBaseURL : endpointURL;
            sb.append(baseURL);
            sb.append(name);
            sb.append("?wsdl\"><i>(wsdl)</i></a></li>");
            writer.println(sb.toString());
            ArrayList operations = sd.getOperations();
            if (operations.isEmpty()) continue;
            writer.println("<ul>");
            Iterator it = operations.iterator();
            while (it.hasNext()) {
                OperationDesc desc = (OperationDesc)it.next();
                writer.println("<li>" + desc.getName());
            }
            writer.println("</ul>");
        }
        writer.println("</ul>");
    }

    protected void reportCantGetAxisService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        response.setStatus(404);
        response.setContentType("text/html; charset=utf-8");
        writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
        writer.println("<p>" + Messages.getMessage("noService06") + "</p>");
    }

    protected void reportCantGetJWSService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        String requestPath = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        String realpath = this.getServletConfig().getServletContext().getRealPath(requestPath);
        log.debug((Object)("JWS real path: " + realpath));
        boolean foundJWSFile = new File(realpath).exists() && realpath.endsWith(".jws");
        response.setContentType("text/html; charset=utf-8");
        if (foundJWSFile) {
            response.setStatus(200);
            writer.println(Messages.getMessage("foundJWS00") + "<p>");
            String url = request.getRequestURI();
            String urltext = Messages.getMessage("foundJWS01");
            writer.println("<a href='" + url + "?wsdl'>" + urltext + "</a>");
        } else {
            response.setStatus(404);
            writer.println(Messages.getMessage("noService06"));
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String contentType;
        Message responseMsg;
        MessageContext msgContext;
        String soapAction;
        long t4;
        long t3;
        long t2;
        long t1;
        long t0;
        block32: {
            t0 = 0L;
            t1 = 0L;
            t2 = 0L;
            t3 = 0L;
            t4 = 0L;
            soapAction = null;
            msgContext = null;
            if (isDebug) {
                log.debug((Object)"Enter: doPost()");
            }
            if (tlog.isDebugEnabled()) {
                t0 = System.currentTimeMillis();
            }
            responseMsg = null;
            contentType = null;
            try {
                AxisServer engine = this.getEngine();
                if (engine == null) {
                    ServletException se = new ServletException(Messages.getMessage("noEngine00"));
                    log.debug((Object)"No Engine!", (Throwable)se);
                    throw se;
                }
                res.setBufferSize(8192);
                msgContext = this.createMessageContext(engine, req, res);
                if (this.securityProvider != null) {
                    if (isDebug) {
                        log.debug((Object)("securityProvider:" + this.securityProvider));
                    }
                    msgContext.setProperty("securityProvider", this.securityProvider);
                }
                Message requestMsg = new Message(req.getInputStream(), false, req.getHeader("Content-Type"), req.getHeader("Content-Location"));
                MimeHeaders requestMimeHeaders = requestMsg.getMimeHeaders();
                Enumeration e = req.getHeaderNames();
                while (e.hasMoreElements()) {
                    String headerName = (String)e.nextElement();
                    Enumeration f = req.getHeaders(headerName);
                    while (f.hasMoreElements()) {
                        String headerValue = (String)f.nextElement();
                        requestMimeHeaders.addHeader(headerName, headerValue);
                    }
                }
                if (isDebug) {
                    log.debug((Object)("Request Message:" + requestMsg));
                }
                msgContext.setRequestMessage(requestMsg);
                String url = HttpUtils.getRequestURL((HttpServletRequest)req).toString();
                msgContext.setProperty("transport.url", url);
                try {
                    String requestEncoding = (String)requestMsg.getProperty("javax.xml.soap.character-set-encoding");
                    if (requestEncoding != null) {
                        msgContext.setProperty("javax.xml.soap.character-set-encoding", requestEncoding);
                    }
                }
                catch (SOAPException e1) {
                    // empty catch block
                }
                try {
                    soapAction = this.getSoapAction(req);
                    if (soapAction != null) {
                        msgContext.setUseSOAPAction(true);
                        msgContext.setSOAPActionURI(soapAction);
                    }
                    msgContext.setSession(new AxisHttpSession(req));
                    if (tlog.isDebugEnabled()) {
                        t1 = System.currentTimeMillis();
                    }
                    if (isDebug) {
                        log.debug((Object)"Invoking Axis Engine.");
                    }
                    engine.invoke(msgContext);
                    if (isDebug) {
                        log.debug((Object)"Return from Axis Engine.");
                    }
                    if (tlog.isDebugEnabled()) {
                        t2 = System.currentTimeMillis();
                    }
                    responseMsg = msgContext.getResponseMessage();
                }
                catch (AxisFault fault) {
                    this.processAxisFault(fault);
                    this.configureResponseFromAxisFault(res, fault);
                    responseMsg = msgContext.getResponseMessage();
                    if (responseMsg == null) {
                        responseMsg = new Message(fault);
                        ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
                    }
                }
                catch (Exception e2) {
                    responseMsg = msgContext.getResponseMessage();
                    res.setStatus(500);
                    responseMsg = this.convertExceptionToAxisFault(e2, responseMsg);
                    ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
                }
                catch (Throwable t) {
                    this.logException(t);
                    responseMsg = msgContext.getResponseMessage();
                    res.setStatus(500);
                    responseMsg = new Message(new AxisFault(t.toString(), t));
                    ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
                }
            }
            catch (AxisFault fault) {
                this.processAxisFault(fault);
                this.configureResponseFromAxisFault(res, fault);
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg != null) break block32;
                responseMsg = new Message(fault);
                ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
            }
        }
        if (tlog.isDebugEnabled()) {
            t3 = System.currentTimeMillis();
        }
        if (responseMsg != null) {
            MimeHeaders responseMimeHeaders = responseMsg.getMimeHeaders();
            Iterator i = responseMimeHeaders.getAllHeaders();
            while (i.hasNext()) {
                MimeHeader responseMimeHeader = (MimeHeader)i.next();
                res.addHeader(responseMimeHeader.getName(), responseMimeHeader.getValue());
            }
            String responseEncoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
            if (responseEncoding != null) {
                try {
                    responseMsg.setProperty("javax.xml.soap.character-set-encoding", responseEncoding);
                }
                catch (SOAPException e) {
                    // empty catch block
                }
            }
            contentType = responseMsg.getContentType(msgContext.getSOAPConstants());
            this.sendResponse(contentType, res, responseMsg);
        } else {
            res.setStatus(202);
        }
        if (isDebug) {
            log.debug((Object)"Response sent.");
            log.debug((Object)"Exit: doPost()");
        }
        if (tlog.isDebugEnabled()) {
            t4 = System.currentTimeMillis();
            tlog.debug((Object)("axisServlet.doPost: " + soapAction + " pre=" + (t1 - t0) + " invoke=" + (t2 - t1) + " post=" + (t3 - t2) + " send=" + (t4 - t3) + " " + msgContext.getTargetService() + "." + (msgContext.getOperation() == null ? "" : msgContext.getOperation().getName())));
        }
    }

    private void configureResponseFromAxisFault(HttpServletResponse response, AxisFault fault) {
        int status = this.getHttpServletResponseStatus(fault);
        if (status == 401) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"AXIS\"");
        }
        response.setStatus(status);
    }

    private Message convertExceptionToAxisFault(Exception exception, Message responseMsg) {
        this.logException(exception);
        if (responseMsg == null) {
            AxisFault fault = AxisFault.makeFault(exception);
            this.processAxisFault(fault);
            responseMsg = new Message(fault);
        }
        return responseMsg;
    }

    protected int getHttpServletResponseStatus(AxisFault af) {
        return af.getFaultCode().getLocalPart().startsWith("Server.Unauth") ? 401 : 500;
    }

    private void sendResponse(String contentType, HttpServletResponse res, Message responseMsg) throws AxisFault, IOException {
        if (responseMsg == null) {
            res.setStatus(204);
            if (isDebug) {
                log.debug((Object)"NO AXIS MESSAGE TO RETURN!");
            }
        } else {
            if (isDebug) {
                log.debug((Object)("Returned Content-Type:" + contentType));
            }
            try {
                res.setContentType(contentType);
                responseMsg.writeTo((OutputStream)res.getOutputStream());
            }
            catch (SOAPException e) {
                this.logException(e);
            }
        }
        if (!res.isCommitted()) {
            res.flushBuffer();
        }
    }

    private MessageContext createMessageContext(AxisEngine engine, HttpServletRequest req, HttpServletResponse res) {
        MessageContext msgContext = new MessageContext(engine);
        String requestPath = AxisServlet.getRequestPath(req);
        if (isDebug) {
            log.debug((Object)("MessageContext:" + msgContext));
            log.debug((Object)("HEADER_CONTENT_TYPE:" + req.getHeader("Content-Type")));
            log.debug((Object)("HEADER_CONTENT_LOCATION:" + req.getHeader("Content-Location")));
            log.debug((Object)("Constants.MC_HOME_DIR:" + String.valueOf(this.getHomeDir())));
            log.debug((Object)("Constants.MC_RELATIVE_PATH:" + requestPath));
            log.debug((Object)("HTTPConstants.MC_HTTP_SERVLETLOCATION:" + String.valueOf(this.getWebInfPath())));
            log.debug((Object)("HTTPConstants.MC_HTTP_SERVLETPATHINFO:" + req.getPathInfo()));
            log.debug((Object)("HTTPConstants.HEADER_AUTHORIZATION:" + req.getHeader("Authorization")));
            log.debug((Object)("Constants.MC_REMOTE_ADDR:" + req.getRemoteAddr()));
            log.debug((Object)("configPath:" + String.valueOf(this.getWebInfPath())));
        }
        msgContext.setTransportName(this.transportName);
        msgContext.setProperty("jws.classDir", this.jwsClassDir);
        msgContext.setProperty("home.dir", this.getHomeDir());
        msgContext.setProperty("path", requestPath);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, (Object)this);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION, this.getWebInfPath());
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO, req.getPathInfo());
        msgContext.setProperty("Authorization", req.getHeader("Authorization"));
        msgContext.setProperty("remoteaddr", req.getRemoteAddr());
        ServletEndpointContextImpl sec = new ServletEndpointContextImpl();
        msgContext.setProperty("servletEndpointContext", sec);
        String realpath = this.getServletConfig().getServletContext().getRealPath(requestPath);
        if (realpath != null) {
            msgContext.setProperty("realpath", realpath);
        }
        msgContext.setProperty("configPath", this.getWebInfPath());
        return msgContext;
    }

    private String getSoapAction(HttpServletRequest req) throws AxisFault {
        String soapAction = req.getHeader("SOAPAction");
        if (isDebug) {
            log.debug((Object)("HEADER_SOAP_ACTION:" + soapAction));
        }
        if (soapAction == null) {
            AxisFault af = new AxisFault("Client.NoSOAPAction", Messages.getMessage("noHeader00", "SOAPAction"), null, null);
            exceptionLog.error((Object)Messages.getMessage("genFault00"), (Throwable)af);
            throw af;
        }
        if (soapAction.startsWith("\"") && soapAction.endsWith("\"") && soapAction.length() >= 2) {
            int end = soapAction.length() - 1;
            soapAction = soapAction.substring(1, end);
        }
        if (soapAction.length() == 0) {
            soapAction = req.getContextPath();
        }
        return soapAction;
    }

    protected String getDefaultJWSClassDir() {
        return this.getWebInfPath() == null ? null : this.getWebInfPath() + File.separator + "jwsClasses";
    }

    public void initQueryStringHandlers() {
        try {
            this.transport = this.getEngine().getTransport(this.transportName);
            if (this.transport == null) {
                this.transport = new SimpleTargetedChain();
                this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
                this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
                this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
                return;
            }
            boolean defaultQueryStrings = true;
            String useDefaults = (String)this.transport.getOption("useDefaultQueryStrings");
            if (useDefaults != null && useDefaults.toLowerCase().equals("false")) {
                defaultQueryStrings = false;
            }
            if (defaultQueryStrings) {
                this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
                this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
                this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
            }
        }
        catch (AxisFault e) {
            this.transport = new SimpleTargetedChain();
            this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
            this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
            this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
            return;
        }
    }

    private boolean processQuery(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws AxisFault {
        String path = request.getServletPath();
        String queryString = request.getQueryString();
        AxisServer engine = this.getEngine();
        Iterator i = this.transport.getOptions().keySet().iterator();
        if (queryString == null) {
            return false;
        }
        String servletURI = request.getContextPath() + path;
        String reqURI = request.getRequestURI();
        String serviceName = servletURI.length() + 1 < reqURI.length() ? reqURI.substring(servletURI.length() + 1) : "";
        while (i.hasNext()) {
            String queryHandler = (String)i.next();
            if (!queryHandler.startsWith("qs.")) continue;
            String handlerName = queryHandler.substring(queryHandler.indexOf(".") + 1).toLowerCase();
            int length = 0;
            boolean firstParamFound = false;
            while (!firstParamFound && length < queryString.length()) {
                char ch;
                if ((ch = queryString.charAt(length++)) != '&' && ch != '=') continue;
                firstParamFound = true;
                --length;
            }
            if (length < queryString.length()) {
                queryString = queryString.substring(0, length);
            }
            if (!queryString.toLowerCase().equals(handlerName)) continue;
            if (this.transport.getOption(queryHandler).equals("")) {
                return false;
            }
            try {
                MessageContext msgContext = this.createMessageContext(engine, request, response);
                Class<?> plugin = Class.forName((String)this.transport.getOption(queryHandler));
                Method pluginMethod = plugin.getDeclaredMethod("invoke", msgContext.getClass());
                String url = HttpUtils.getRequestURL((HttpServletRequest)request).toString();
                msgContext.setProperty("transport.url", url);
                msgContext.setProperty("transport.http.plugin.serviceName", serviceName);
                msgContext.setProperty("transport.http.plugin.pluginName", handlerName);
                msgContext.setProperty("transport.http.plugin.isDevelopment", new Boolean(this.isDevelopment()));
                msgContext.setProperty("transport.http.plugin.enableList", new Boolean(this.enableList));
                msgContext.setProperty("transport.http.plugin.engine", engine);
                msgContext.setProperty("transport.http.plugin.writer", writer);
                msgContext.setProperty("transport.http.plugin.log", log);
                msgContext.setProperty("transport.http.plugin.exceptionLog", exceptionLog);
                pluginMethod.invoke(plugin.newInstance(), msgContext);
                writer.close();
                return true;
            }
            catch (InvocationTargetException ie) {
                this.reportTroubleInGet(ie.getTargetException(), response, writer);
                return true;
            }
            catch (Exception e) {
                this.reportTroubleInGet(e, response, writer);
                return true;
            }
        }
        return false;
    }

    private static String getRequestPath(HttpServletRequest request) {
        return request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
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

