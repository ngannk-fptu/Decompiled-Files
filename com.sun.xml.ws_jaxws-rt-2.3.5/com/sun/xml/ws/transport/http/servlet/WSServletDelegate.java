/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 *  com.sun.istack.localization.Localizer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.http.HTTPBinding
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.localization.Localizable;
import com.sun.istack.localization.Localizer;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.servlet.JAXWSRIServletProbeProvider;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPBinding;

public class WSServletDelegate {
    public final List<ServletAdapter> adapters;
    private final Map<String, ServletAdapter> fixedUrlPatternEndpoints = new HashMap<String, ServletAdapter>();
    private final List<ServletAdapter> pathUrlPatternEndpoints = new ArrayList<ServletAdapter>();
    private final Map<Locale, Localizer> localizerMap = new HashMap<Locale, Localizer>();
    private final JAXWSRIServletProbeProvider probe = new JAXWSRIServletProbeProvider();
    private static final Localizer defaultLocalizer = new Localizer();
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.servlet.http");

    public WSServletDelegate(List<ServletAdapter> adapters, ServletContext context) {
        String publishStatusPageParam;
        this.adapters = adapters;
        for (ServletAdapter info : adapters) {
            this.registerEndpointUrlPattern(info);
        }
        this.localizerMap.put(defaultLocalizer.getLocale(), defaultLocalizer);
        if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.SERVLET_INFO_INITIALIZE());
        }
        if ((publishStatusPageParam = context.getInitParameter("com.sun.xml.ws.server.http.publishStatusPage")) != null) {
            HttpAdapter.setPublishStatus(Boolean.parseBoolean(publishStatusPageParam));
        }
    }

    public void destroy() {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.SERVLET_INFO_DESTROY());
        }
    }

    public void doHead(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException {
        try {
            ServletAdapter target = this.getTarget(request);
            if (target != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(WsservletMessages.SERVLET_TRACE_GOT_REQUEST_FOR_ENDPOINT(target.name));
                }
                target.handle(context, request, response);
            } else {
                response.setStatus(404);
            }
        }
        catch (JAXWSExceptionBase e) {
            logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), (Throwable)((Object)e));
            response.setStatus(500);
        }
        catch (Throwable e) {
            if (e instanceof Localizable) {
                logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), e);
            } else {
                logger.log(Level.SEVERE, "caught throwable", e);
            }
            response.setStatus(500);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException {
        try {
            ServletAdapter target = this.getTarget(request);
            if (target != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(WsservletMessages.SERVLET_TRACE_GOT_REQUEST_FOR_ENDPOINT(target.name));
                }
                final String path = request.getContextPath() + target.getValidPath();
                this.probe.startedEvent(path);
                target.invokeAsync(context, request, response, new HttpAdapter.CompletionCallback(){

                    @Override
                    public void onCompletion() {
                        WSServletDelegate.this.probe.endedEvent(path);
                    }
                });
            } else {
                Localizer localizer = this.getLocalizerFor((ServletRequest)request);
                this.writeNotFoundErrorPage(localizer, response, "Invalid Request");
            }
        }
        catch (JAXWSExceptionBase e) {
            logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), (Throwable)((Object)e));
            response.setStatus(500);
        }
        catch (Throwable e) {
            if (e instanceof Localizable) {
                logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), e);
            } else {
                logger.log(Level.SEVERE, "caught throwable", e);
            }
            response.setStatus(500);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException {
        this.doGet(request, response, context);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException {
        try {
            ServletAdapter target = this.getTarget(request);
            if (target != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(WsservletMessages.SERVLET_TRACE_GOT_REQUEST_FOR_ENDPOINT(target.name));
                }
            } else {
                Localizer localizer = this.getLocalizerFor((ServletRequest)request);
                this.writeNotFoundErrorPage(localizer, response, "Invalid request");
                return;
            }
            WSBinding binding = target.getEndpoint().getBinding();
            if (binding instanceof HTTPBinding) {
                target.handle(context, request, response);
            } else {
                response.setStatus(405);
            }
        }
        catch (JAXWSExceptionBase e) {
            logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), (Throwable)((Object)e));
            response.setStatus(500);
        }
        catch (Throwable e) {
            if (e instanceof Localizable) {
                logger.log(Level.SEVERE, defaultLocalizer.localize((Localizable)e), e);
            } else {
                logger.log(Level.SEVERE, "caught throwable", e);
            }
            response.setStatus(500);
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException {
        this.doPut(request, response, context);
    }

    private void writeNotFoundErrorPage(Localizer localizer, HttpServletResponse response, String message) throws IOException {
        response.setStatus(404);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(message));
        out.println("</body>");
        out.println("</html>");
    }

    private void registerEndpointUrlPattern(ServletAdapter a) {
        String urlPattern = a.urlPattern;
        if (urlPattern.indexOf("*.") != -1) {
            logger.warning(WsservletMessages.SERVLET_WARNING_IGNORING_IMPLICIT_URL_PATTERN(a.name));
        } else if (urlPattern.endsWith("/*")) {
            this.pathUrlPatternEndpoints.add(a);
        } else if (this.fixedUrlPatternEndpoints.containsKey(urlPattern)) {
            logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_URL_PATTERN(a.name));
        } else {
            this.fixedUrlPatternEndpoints.put(urlPattern, a);
        }
    }

    protected ServletAdapter getTarget(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        ServletAdapter result = this.fixedUrlPatternEndpoints.get(path);
        if (result == null) {
            for (ServletAdapter candidate : this.pathUrlPatternEndpoints) {
                String noSlashStar = candidate.getValidPath();
                if (!path.equals(noSlashStar) && !path.startsWith(noSlashStar + "/") && !path.startsWith(noSlashStar + "?")) continue;
                result = candidate;
                break;
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Localizer getLocalizerFor(ServletRequest request) {
        Locale locale = request.getLocale();
        if (locale.equals(defaultLocalizer.getLocale())) {
            return defaultLocalizer;
        }
        Map<Locale, Localizer> map = this.localizerMap;
        synchronized (map) {
            Localizer localizer = this.localizerMap.get(locale);
            if (localizer == null) {
                localizer = new Localizer(locale);
                this.localizerMap.put(locale, localizer);
            }
            return localizer;
        }
    }
}

