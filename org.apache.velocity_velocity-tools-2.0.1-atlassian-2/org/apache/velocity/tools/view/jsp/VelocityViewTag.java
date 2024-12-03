/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.runtime.resource.loader.StringResourceLoader
 *  org.apache.velocity.runtime.resource.util.StringResourceRepository
 */
package org.apache.velocity.tools.view.jsp;

import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.VelocityView;
import org.apache.velocity.tools.view.ViewToolContext;
import org.apache.velocity.tools.view.jsp.JspToolContext;

public class VelocityViewTag
extends BodyTagSupport {
    public static final String DEFAULT_BODY_CONTENT_KEY = "bodyContent";
    private static final long serialVersionUID = -3329444102562079189L;
    protected transient VelocityView view;
    protected transient ViewToolContext context;
    protected transient StringResourceRepository repository;
    protected String var;
    protected String scope;
    protected String template;
    protected String bodyContentKey = "bodyContent";
    private boolean cache = false;

    protected void reset() {
        super.setId(null);
        this.var = null;
        this.scope = null;
        this.template = null;
        this.bodyContentKey = DEFAULT_BODY_CONTENT_KEY;
        this.cache = false;
    }

    public void setId(String id) {
        if (id == null) {
            throw new NullPointerException("id cannot be null");
        }
        super.setId(id);
        this.cache = true;
    }

    protected String getLogId() {
        String id = super.getId();
        if (id == null) {
            id = ((Object)((Object)this)).getClass().getSimpleName();
        }
        return id;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return this.var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setBodyContentKey(String key) {
        this.bodyContentKey = DEFAULT_BODY_CONTENT_KEY;
    }

    public String getBodyContentKey() {
        return this.bodyContentKey;
    }

    public void setCache(String s) {
        this.cache = "true".equalsIgnoreCase(s);
    }

    public String getCache() {
        return String.valueOf(this.cache);
    }

    public VelocityView getVelocityView() {
        return this.view;
    }

    public void setVelocityView(VelocityView view) {
        this.view = view;
    }

    public ViewToolContext getViewToolContext() {
        return this.context;
    }

    public void setViewToolContext(ViewToolContext context) {
        this.context = context;
    }

    public StringResourceRepository getRepository() {
        if (this.repository == null) {
            this.setRepository(StringResourceLoader.getRepository());
        }
        return this.repository;
    }

    public void setRepository(StringResourceRepository repo) {
        this.repository = repo;
    }

    public int doStartTag() throws JspException {
        this.initializeView();
        return 2;
    }

    public int doEndTag() throws JspException {
        if (this.hasContent()) {
            try {
                String varname = this.getVar();
                if (varname == null) {
                    this.renderContent((Writer)this.pageContext.getOut());
                } else {
                    StringWriter out = new StringWriter();
                    this.renderContent(out);
                    this.pageContext.setAttribute(varname, (Object)out.toString(), VelocityViewTag.toScopeInt(this.getScope()));
                }
            }
            catch (Exception e) {
                throw new JspException("Failed to render " + ((Object)((Object)this)).getClass() + ": " + this.getLogId(), (Throwable)e);
            }
        }
        return 6;
    }

    protected void initializeView() {
        VelocityView view = ServletUtils.getVelocityView(this.pageContext.getServletConfig());
        JspToolContext context = new JspToolContext(view.getVelocityEngine(), this.pageContext);
        view.prepareContext(context, (HttpServletRequest)this.pageContext.getRequest());
        this.setVelocityView(view);
        this.setViewToolContext(context);
    }

    protected boolean hasContent() {
        return this.getBodyContent() != null || this.getTemplate() != null;
    }

    protected void renderContent(Writer out) throws Exception {
        if (this.getTemplate() != null) {
            VelocityView view = this.getVelocityView();
            ViewToolContext context = this.getViewToolContext();
            Template template = view.getTemplate(this.getTemplate());
            if (this.getBodyContent() != null) {
                context.put(this.getBodyContentKey(), this.getRenderedBody());
            }
            template.merge((Context)context, out);
        } else {
            this.renderBody(out);
        }
    }

    protected String getRenderedBody() throws Exception {
        StringWriter out = new StringWriter();
        this.renderBody(out);
        return out.toString();
    }

    protected boolean isCached() {
        return this.getRepository().getStringResource(this.getId()) != null;
    }

    protected void renderBody(Writer out) throws Exception {
        String template;
        String name = this.getId();
        if (this.cache && !this.isCached()) {
            template = this.getBodyContent().getString();
            if (name == null) {
                name = template;
            }
            this.cache(name, template);
        }
        if (!this.cache) {
            this.evalBody(out);
        } else {
            template = this.getVelocityView().getTemplate(name);
            template.merge((Context)this.getViewToolContext(), out);
        }
    }

    protected void evalBody(Writer out) throws Exception {
        VelocityEngine engine = this.getVelocityView().getVelocityEngine();
        engine.evaluate((Context)this.getViewToolContext(), out, this.getLogId(), this.getBodyContent().getReader());
    }

    protected static int toScopeInt(String scope) {
        if (scope == null) {
            return 1;
        }
        if (scope.equalsIgnoreCase("request")) {
            return 2;
        }
        if (scope.equalsIgnoreCase("session")) {
            return 3;
        }
        if (scope.equalsIgnoreCase("application")) {
            return 4;
        }
        if (scope.equalsIgnoreCase("page")) {
            return 1;
        }
        throw new IllegalArgumentException("Unknown scope: " + scope);
    }

    protected void cache(String name, String template) {
        try {
            this.getRepository().putStringResource(name, template);
        }
        catch (Exception cnfe) {
            this.getVelocityView().getLog().error((Object)"Could not cache body in a StringResourceRepository", (Throwable)cnfe);
            this.cache = false;
        }
    }

    public void release() {
        super.release();
        this.reset();
    }
}

