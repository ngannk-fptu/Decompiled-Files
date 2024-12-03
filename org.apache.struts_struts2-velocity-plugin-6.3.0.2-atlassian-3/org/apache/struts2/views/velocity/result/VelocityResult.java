/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionContext
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.inject.Inject
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.PageContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.result.StrutsResultSupport
 *  org.apache.struts2.views.JspSupportServlet
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.struts2.views.velocity.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

public class VelocityResult
extends StrutsResultSupport {
    private static final long serialVersionUID = 7268830767762559424L;
    private static final Logger LOG = LogManager.getLogger(VelocityResult.class);
    private String defaultEncoding;
    private VelocityManager velocityManager;
    private JspFactory jspFactory = JspFactory.getDefaultFactory();

    public VelocityResult() {
    }

    public VelocityResult(String location) {
        super(location);
    }

    @Inject(value="struts.i18n.encoding")
    public void setDefaultEncoding(String val) {
        this.defaultEncoding = val;
    }

    @Inject
    public void setVelocityManager(VelocityManager mgr) {
        this.velocityManager = mgr;
    }

    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ServletContext servletContext = ServletActionContext.getServletContext();
        JspSupportServlet servlet = JspSupportServlet.jspSupportServlet;
        this.velocityManager.init(servletContext);
        boolean usedJspFactory = false;
        PageContext pageContext = ActionContext.getContext().getPageContext();
        if (pageContext == null && servlet != null) {
            pageContext = this.jspFactory.getPageContext((Servlet)servlet, (ServletRequest)request, (ServletResponse)response, null, true, 8192, true);
            ActionContext.getContext().withPageContext(pageContext);
            usedJspFactory = true;
        }
        try {
            String encoding = this.getEncoding(finalLocation);
            String contentType = this.getContentType(finalLocation);
            if (encoding != null) {
                contentType = contentType + ";charset=" + encoding;
            }
            Template t = this.getTemplate(stack, this.velocityManager.getVelocityEngine(), invocation, finalLocation, encoding);
            Context context = this.createContext(this.velocityManager, stack, request, response, finalLocation);
            OutputStreamWriter writer = new OutputStreamWriter((OutputStream)response.getOutputStream(), encoding);
            response.setContentType(contentType);
            t.merge(context, (Writer)writer);
            ((Writer)writer).flush();
        }
        catch (Exception e) {
            LOG.error("Unable to render velocity template: '{}'", (Object)finalLocation, (Object)e);
            throw e;
        }
        finally {
            if (usedJspFactory) {
                this.jspFactory.releasePageContext(pageContext);
            }
        }
    }

    protected String getContentType(String templateLocation) {
        return "text/html";
    }

    protected String getEncoding(String templateLocation) {
        String encoding = this.defaultEncoding;
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    protected Template getTemplate(ValueStack stack, VelocityEngine velocity, ActionInvocation invocation, String location, String encoding) throws Exception {
        if (!location.startsWith("/")) {
            location = invocation.getProxy().getNamespace() + "/" + location;
        }
        Template template = velocity.getTemplate(location, encoding);
        return template;
    }

    protected Context createContext(VelocityManager velocityManager, ValueStack stack, HttpServletRequest request, HttpServletResponse response, String location) {
        return velocityManager.createContext(stack, request, response);
    }
}

