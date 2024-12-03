/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.Binding
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.http.HTTPBinding
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.EndpointAdapter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;

public class WSServletDelegate {
    public final List<EndpointAdapter> adapters;
    private final Map<String, EndpointAdapter> fixedUrlPatternEndpoints = new HashMap<String, EndpointAdapter>();
    private final List<EndpointAdapter> pathUrlPatternEndpoints = new ArrayList<EndpointAdapter>();
    private static final Logger logger = Logger.getLogger(WSServletDelegate.class.getName());

    public WSServletDelegate(List<EndpointAdapter> adapters, ServletContext context) {
        this.adapters = adapters;
        for (EndpointAdapter info : adapters) {
            this.registerEndpointUrlPattern(info);
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Initializing Servlet for {0}", this.fixedUrlPatternEndpoints);
        }
    }

    public void destroy() {
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Destroying Servlet for {0}", this.fixedUrlPatternEndpoints);
        }
        for (EndpointAdapter a : this.adapters) {
            try {
                a.dispose();
            }
            catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        try {
            EndpointAdapter target = this.getTarget(request);
            if (target != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Got request for endpoint {0}", target.getUrlPattern());
                }
                target.handle(context, request, response);
            } else {
                this.writeNotFoundErrorPage(response, "Invalid Request");
            }
        }
        catch (WebServiceException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            response.setStatus(500);
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, "caught throwable", e);
            response.setStatus(500);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        this.doGet(request, response, context);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        try {
            EndpointAdapter target = this.getTarget(request);
            if (target != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Got request for endpoint {0}", target.getUrlPattern());
                }
            } else {
                this.writeNotFoundErrorPage(response, "Invalid request");
                return;
            }
            Binding binding = target.getEndpoint().getBinding();
            if (binding instanceof HTTPBinding) {
                target.handle(context, request, response);
            } else {
                response.setStatus(405);
            }
        }
        catch (WebServiceException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            response.setStatus(500);
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, "caught throwable", e);
            response.setStatus(500);
            response.setStatus(500);
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        this.doPut(request, response, context);
    }

    private void writeNotFoundErrorPage(HttpServletResponse response, String message) throws IOException {
        response.setStatus(404);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>");
        out.println("Web Services");
        out.println("</title></head>");
        out.println("<body>");
        out.println("Not found " + message);
        out.println("</body>");
        out.println("</html>");
    }

    private void registerEndpointUrlPattern(EndpointAdapter a) {
        String urlPattern = a.getUrlPattern();
        if (urlPattern.indexOf("*.") != -1) {
            logger.log(Level.WARNING, "Ignoring implicit url-pattern {0}", urlPattern);
        } else if (urlPattern.endsWith("/*")) {
            this.pathUrlPatternEndpoints.add(a);
        } else if (this.fixedUrlPatternEndpoints.containsKey(urlPattern)) {
            logger.log(Level.WARNING, "Ignoring duplicate url-pattern {0}", urlPattern);
        } else {
            this.fixedUrlPatternEndpoints.put(urlPattern, a);
        }
    }

    protected EndpointAdapter getTarget(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        EndpointAdapter result = this.fixedUrlPatternEndpoints.get(path);
        if (result == null) {
            for (EndpointAdapter candidate : this.pathUrlPatternEndpoints) {
                String noSlashStar = candidate.getValidPath();
                if (!path.equals(noSlashStar) && !path.startsWith(noSlashStar + "/") && !path.startsWith(noSlashStar + "?")) continue;
                result = candidate;
                break;
            }
        }
        return result;
    }
}

