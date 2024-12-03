/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.Configuration
 *  freemarker.template.Template
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.views.freemarker.FreemarkerManager;

public class DefaultDispatcherErrorHandler
implements DispatcherErrorHandler {
    private static final Logger LOG = LogManager.getLogger(DefaultDispatcherErrorHandler.class);
    private FreemarkerManager freemarkerManager;
    private boolean devMode;
    private Template template;

    @Inject
    public void setFreemarkerManager(FreemarkerManager freemarkerManager) {
        this.freemarkerManager = freemarkerManager;
    }

    @Inject(value="struts.devMode")
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean((String)devMode);
    }

    @Override
    public void init(ServletContext ctx) {
        try {
            Configuration config = this.freemarkerManager.getConfiguration(ctx);
            this.template = config.getTemplate("/org/apache/struts2/dispatcher/error.ftl");
        }
        catch (IOException e) {
            throw new StrutsException(e);
        }
    }

    @Override
    public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        Boolean devModeOverride = PrepareOperations.getDevModeOverride();
        if (devModeOverride != null ? devModeOverride != false : this.devMode) {
            this.handleErrorInDevMode(response, code, e);
        } else {
            this.sendErrorResponse(request, response, code, e);
        }
    }

    protected void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        try {
            if (code == 500) {
                LOG.error("Exception occurred during processing request: {}", (Object)e.getMessage(), (Object)e);
                request.setAttribute("javax.servlet.error.exception", (Object)e);
                request.setAttribute("javax.servlet.jsp.jspException", (Object)e);
            }
            response.sendError(code, e.getMessage());
        }
        catch (IOException e1) {
            LOG.warn("Unable to send error response, code: {};", (Object)code, (Object)e1);
        }
        catch (IllegalStateException ise) {
            LOG.warn("Unable to send error response, code: {}; isCommited: {};", (Object)code, (Object)response.isCommitted(), (Object)ise);
        }
    }

    protected void handleErrorInDevMode(HttpServletResponse response, int code, Exception e) {
        LOG.debug("Exception occurred during processing request: {}", (Object)e.getMessage(), (Object)e);
        try {
            ArrayList<Throwable> chain = new ArrayList<Throwable>();
            Throwable cur = e;
            chain.add(cur);
            while ((cur = cur.getCause()) != null) {
                chain.add(cur);
            }
            StringWriter writer = new StringWriter();
            this.template.process(this.createReportData(e, chain), (Writer)writer);
            response.setContentType("text/html");
            response.getWriter().write(((Object)writer).toString());
            response.getWriter().close();
        }
        catch (Exception exp) {
            try {
                LOG.debug("Cannot show problem report!", (Throwable)exp);
                response.sendError(code, "Unable to show problem report:\n" + exp + "\n\n" + LocationUtils.getLocation(exp));
            }
            catch (IOException ex) {
                LOG.warn("Unable to send error response, code: {};", (Object)code, (Object)ex);
            }
            catch (IllegalStateException ise) {
                LOG.warn("Unable to send error response, code: {}; isCommited: {};", (Object)code, (Object)response.isCommitted(), (Object)ise);
            }
        }
    }

    protected HashMap<String, Object> createReportData(Exception e, List<Throwable> chain) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("exception", e);
        data.put("unknown", Location.UNKNOWN);
        data.put("chain", chain);
        data.put("locator", new Dispatcher.Locator());
        return data;
    }
}

