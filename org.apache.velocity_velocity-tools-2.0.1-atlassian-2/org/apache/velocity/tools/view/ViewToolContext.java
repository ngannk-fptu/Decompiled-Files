/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.view;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.ViewContext;

public class ViewToolContext
extends ToolContext
implements ViewContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext application;
    private final VelocityEngine velocity;
    private String toolboxKey = DEFAULT_TOOLBOX_KEY;

    public ViewToolContext(VelocityEngine velocity, HttpServletRequest request, HttpServletResponse response, ServletContext application) {
        super(velocity);
        this.velocity = velocity;
        this.request = request;
        this.response = response;
        this.application = application;
        this.putToolProperties();
    }

    protected void setToolboxKey(String key) {
        this.toolboxKey = key;
    }

    protected void putToolProperties() {
        this.putToolProperty("request", this.getRequest());
        if (this.getRequest() != null) {
            this.putToolProperty("locale", this.getRequest().getLocale());
        }
        this.putToolProperty("response", this.getResponse());
        this.putToolProperty("session", this.getSession());
        this.putToolProperty("servletContext", this.getServletContext());
        this.putToolProperty("requestPath", ServletUtils.getPath(this.getRequest()));
    }

    @Override
    protected List<Toolbox> getToolboxes() {
        if (super.getToolboxes().isEmpty()) {
            this.addToolboxesUnderKey(this.toolboxKey);
        }
        return super.getToolboxes();
    }

    protected void addToolboxesUnderKey(String toolboxKey) {
        Toolbox appTools;
        Toolbox sessTools;
        Toolbox reqTools = (Toolbox)this.getRequest().getAttribute(toolboxKey);
        if (reqTools != null) {
            this.addToolbox(reqTools);
        }
        if (this.getSession() != null && (sessTools = (Toolbox)this.getSession().getAttribute(toolboxKey)) != null) {
            this.addToolbox(sessTools);
        }
        if ((appTools = (Toolbox)this.getServletContext().getAttribute(toolboxKey)) != null) {
            this.addToolbox(appTools);
        }
    }

    @Override
    public Object get(String key) {
        Object o;
        boolean overwrite = this.getUserCanOverwriteTools();
        Object object = o = overwrite ? this.getUserVar(key) : this.getToolVar(key);
        if (o == null) {
            o = overwrite ? this.getToolVar(key) : this.getUserVar(key);
        }
        return o;
    }

    protected Object getUserVar(String key) {
        Object o = this.internalGet(key);
        if (o != null) {
            return o;
        }
        return this.getAttribute(key);
    }

    protected Object getToolVar(String key) {
        Object o = this.findTool(key);
        if (o != null) {
            return o;
        }
        return this.getServletApi(key);
    }

    protected Object getServletApi(String key) {
        if (key.equals("request")) {
            return this.request;
        }
        if (key.equals("response")) {
            return this.response;
        }
        if (key.equals("session")) {
            return this.getSession();
        }
        if (key.equals("application")) {
            return this.application;
        }
        return null;
    }

    @Override
    public Object getAttribute(String key) {
        Object o = this.request.getAttribute(key);
        if (o == null) {
            if (this.getSession() != null) {
                try {
                    o = this.getSession().getAttribute(key);
                }
                catch (IllegalStateException ise) {
                    o = null;
                }
            }
            if (o == null) {
                o = this.application.getAttribute(key);
            }
        }
        return o;
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

    public HttpSession getSession() {
        return this.getRequest().getSession(false);
    }

    @Override
    public ServletContext getServletContext() {
        return this.application;
    }

    @Override
    public Context getVelocityContext() {
        return this;
    }

    @Override
    public VelocityEngine getVelocityEngine() {
        return this.velocity;
    }

    public boolean containsKey(String key) {
        return super.containsKey(key) || this.getAttribute(key) != null || key.equals("request") && this.request != null || key.equals("response") && this.response != null || key.equals("session") && this.getSession() != null || key.equals("application") && this.application != null;
    }
}

