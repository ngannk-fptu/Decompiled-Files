/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.Template
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.MethodInvocationException
 */
package org.apache.velocity.tools.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class VelocityLayoutServlet
extends VelocityViewServlet {
    private static final long serialVersionUID = -4521817395157483487L;
    public static final String PROPERTY_ERROR_TEMPLATE = "tools.view.servlet.error.template";
    public static final String PROPERTY_LAYOUT_DIR = "tools.view.servlet.layout.directory";
    public static final String PROPERTY_DEFAULT_LAYOUT = "tools.view.servlet.layout.default.template";
    public static final String DEFAULT_ERROR_TEMPLATE = "Error.vm";
    public static final String DEFAULT_LAYOUT_DIR = "layout/";
    public static final String DEFAULT_DEFAULT_LAYOUT = "Default.vm";
    public static final String KEY_SCREEN_CONTENT = "screen_content";
    public static final String KEY_LAYOUT = "layout";
    public static final String KEY_ERROR_CAUSE = "error_cause";
    public static final String KEY_ERROR_STACKTRACE = "stack_trace";
    public static final String KEY_ERROR_INVOCATION_EXCEPTION = "invocation_exception";
    protected String errorTemplate;
    protected String layoutDir;
    protected String defaultLayout;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.errorTemplate = this.getVelocityProperty(PROPERTY_ERROR_TEMPLATE, DEFAULT_ERROR_TEMPLATE);
        this.layoutDir = this.getVelocityProperty(PROPERTY_LAYOUT_DIR, DEFAULT_LAYOUT_DIR);
        this.defaultLayout = this.getVelocityProperty(PROPERTY_DEFAULT_LAYOUT, DEFAULT_DEFAULT_LAYOUT);
        if (!this.layoutDir.endsWith("/")) {
            this.layoutDir = this.layoutDir + '/';
        }
        this.getLog().info((Object)("VelocityLayoutServlet: Error screen is '" + this.errorTemplate + "'"));
        this.getLog().info((Object)("VelocityLayoutServlet: Layout directory is '" + this.layoutDir + "'"));
        this.getLog().info((Object)("VelocityLayoutServlet: Default layout template is '" + this.defaultLayout + "'"));
        this.defaultLayout = this.layoutDir + this.defaultLayout;
    }

    @Override
    protected void fillContext(Context ctx, HttpServletRequest request) {
        String layout = this.findLayout(request);
        if (layout != null) {
            ctx.put(KEY_LAYOUT, (Object)layout);
        }
    }

    protected String findLayout(HttpServletRequest request) {
        String layout = request.getParameter(KEY_LAYOUT);
        if (layout == null) {
            layout = (String)request.getAttribute(KEY_LAYOUT);
        }
        return layout;
    }

    @Override
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws IOException {
        block2: {
            StringWriter sw = new StringWriter();
            template.merge(context, (Writer)sw);
            context.put(KEY_SCREEN_CONTENT, (Object)sw.toString());
            Object obj = context.get(KEY_LAYOUT);
            String layout = obj == null ? null : obj.toString();
            layout = layout == null ? this.defaultLayout : this.layoutDir + layout;
            try {
                template = this.getTemplate(layout);
            }
            catch (Exception e) {
                this.getLog().error((Object)("Can't load layout \"" + layout + "\""), (Throwable)e);
                if (layout.equals(this.defaultLayout)) break block2;
                template = this.getTemplate(this.defaultLayout);
            }
        }
        super.mergeTemplate(template, context, response);
    }

    @Override
    protected void error(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        try {
            Context ctx = this.createContext(request, response);
            Throwable cause = e;
            if (cause instanceof MethodInvocationException) {
                ctx.put(KEY_ERROR_INVOCATION_EXCEPTION, (Object)e);
                cause = ((MethodInvocationException)e).getWrappedThrowable();
            }
            ctx.put(KEY_ERROR_CAUSE, (Object)cause);
            StringWriter sw = new StringWriter();
            cause.printStackTrace(new PrintWriter(sw));
            ctx.put(KEY_ERROR_STACKTRACE, (Object)sw.toString());
            Template et = this.getTemplate(this.errorTemplate);
            this.mergeTemplate(et, ctx, response);
        }
        catch (Exception e2) {
            this.getLog().error((Object)"Error during error template rendering", (Throwable)e2);
            super.error(request, response, e);
        }
    }
}

