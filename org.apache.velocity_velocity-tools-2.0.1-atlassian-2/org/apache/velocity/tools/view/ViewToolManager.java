/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.velocity.app.VelocityEngine
 */
package org.apache.velocity.tools.view;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.ViewToolContext;

public class ViewToolManager
extends ToolManager {
    public static final String CREATE_SESSION_PROPERTY = "createSession";
    public static final String PUBLISH_TOOLBOXES_PROPERTY = "publishToolboxes";
    public static final String DEFAULT_TOOLBOX_KEY = Toolbox.KEY;
    protected ServletContext servletContext;
    private boolean createSession = true;
    private boolean publishToolboxes = true;
    private boolean appToolsPublished = false;
    private String toolboxKey = DEFAULT_TOOLBOX_KEY;

    public ViewToolManager(ServletContext app) {
        this(app, true, true);
    }

    public ViewToolManager(ServletContext app, boolean includeDefaults) {
        this(app, true, includeDefaults);
    }

    public ViewToolManager(ServletContext app, boolean autoConfig, boolean includeDefaults) {
        super(autoConfig, includeDefaults);
        if (app == null) {
            throw new NullPointerException("ServletContext is required");
        }
        this.servletContext = app;
    }

    @Override
    public void autoConfigure(boolean includeDefaults) {
        super.autoConfigure(includeDefaults);
        FactoryConfiguration injected = ServletUtils.getConfiguration(this.servletContext);
        if (injected != null) {
            this.configure(injected);
        }
    }

    public void setPublishToolboxes(boolean publish) {
        if (publish != this.publishToolboxes) {
            this.debug("Publish toolboxes setting was changed to %s", publish);
            this.publishToolboxes = publish;
        }
    }

    public boolean getPublishToolboxes() {
        return this.publishToolboxes;
    }

    public void setToolboxKey(String key) {
        if (key == null) {
            throw new NullPointerException("toolboxKey cannot be null");
        }
        if (!key.equals(this.toolboxKey)) {
            this.toolboxKey = key;
            this.unpublishApplicationTools();
            this.debug("Toolbox key was changed to %s", key);
        }
    }

    public String getToolboxKey() {
        return this.toolboxKey;
    }

    public void setCreateSession(boolean create) {
        if (create != this.createSession) {
            this.debug("Create session setting was changed to %s", create);
            this.createSession = create;
        }
    }

    public boolean getCreateSession() {
        return this.createSession;
    }

    protected void updateGlobalProperties() {
        Boolean publish;
        Boolean create = (Boolean)this.factory.getGlobalProperty(CREATE_SESSION_PROPERTY);
        if (create != null) {
            this.setCreateSession(create);
        }
        if ((publish = (Boolean)this.factory.getGlobalProperty(PUBLISH_TOOLBOXES_PROPERTY)) != null) {
            this.setPublishToolboxes(publish);
        }
    }

    protected void publishApplicationTools() {
        this.servletContext.setAttribute(this.toolboxKey, (Object)this.getApplicationToolbox());
        this.appToolsPublished = true;
    }

    protected void unpublishApplicationTools() {
        if (this.appToolsPublished) {
            this.servletContext.removeAttribute(this.toolboxKey);
            this.appToolsPublished = false;
        }
    }

    @Override
    public void configure(FactoryConfiguration config) {
        super.configure(config);
        this.unpublishApplicationTools();
        this.updateGlobalProperties();
    }

    @Override
    protected FactoryConfiguration findConfig(String path) {
        return ServletUtils.getConfiguration(path, this.servletContext, false);
    }

    @Override
    protected void addToolboxes(ToolContext context) {
        super.addToolboxes(context);
        if (this.hasSessionTools()) {
            context.addToolbox(this.getSessionToolbox());
        }
    }

    @Override
    public ToolContext createContext(Map<String, Object> toolProps) {
        ToolContext context = super.createContext(toolProps);
        context.putToolProperty("servletContext", this.servletContext);
        this.debug("Non-ViewToolContext was requested from ViewToolManager.", new Object[0]);
        return context;
    }

    public ViewToolContext createContext(HttpServletRequest request, HttpServletResponse response) {
        ViewToolContext context = new ViewToolContext(this.getVelocityEngine(), request, response, this.servletContext);
        this.prepareContext(context, request);
        return context;
    }

    public void prepareContext(ViewToolContext context, HttpServletRequest request) {
        context.setToolboxKey(this.toolboxKey);
        if (this.publishToolboxes) {
            this.publishToolboxes(request);
            VelocityEngine engine = this.getVelocityEngine();
            if (engine != null) {
                context.putVelocityEngine(engine);
            }
            context.setUserCanOverwriteTools(this.getUserCanOverwriteTools());
        } else {
            this.prepareContext(context);
        }
    }

    protected boolean hasSessionTools() {
        return this.hasTools("session");
    }

    protected Toolbox getSessionToolbox() {
        return this.createToolbox("session");
    }

    public void publishToolboxes(ServletRequest request) {
        this.publishToolbox(request);
    }

    private void publishToolbox(ServletRequest request) {
        if (this.hasRequestTools() && request.getAttribute(this.toolboxKey) == null) {
            request.setAttribute(this.toolboxKey, (Object)this.getRequestToolbox());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void publishToolboxes(HttpServletRequest request) {
        HttpSession session;
        this.publishToolbox((ServletRequest)request);
        if (this.hasSessionTools() && (session = request.getSession(this.createSession)) != null) {
            Object object = ServletUtils.getMutex(session, "session.mutex", this);
            synchronized (object) {
                if (session.getAttribute(this.toolboxKey) == null) {
                    session.setAttribute(this.toolboxKey, (Object)this.getSessionToolbox());
                }
            }
        }
        if (!this.appToolsPublished && this.hasApplicationTools()) {
            this.publishApplicationTools();
        }
    }
}

