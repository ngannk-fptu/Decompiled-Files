/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.digester.RuleSet
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.velocity.tools.view.servlet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.ToolInfo;
import org.apache.velocity.tools.view.XMLToolboxManager;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;
import org.apache.velocity.tools.view.servlet.ServletToolboxRuleSet;

@Deprecated
public class ServletToolboxManager
extends XMLToolboxManager {
    public static final String SESSION_TOOLS_KEY = ServletToolboxManager.class.getName() + ":session-tools";
    protected static final Log LOG = LogFactory.getLog(ServletToolboxManager.class);
    private ServletContext servletContext;
    private Map appTools;
    private ArrayList sessionToolInfo;
    private ArrayList requestToolInfo;
    private boolean createSession;
    private static HashMap managersMap = new HashMap();
    private static RuleSet servletRuleSet = new ServletToolboxRuleSet();

    private ServletToolboxManager(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.appTools = new HashMap();
        this.sessionToolInfo = new ArrayList();
        this.requestToolInfo = new ArrayList();
        this.createSession = true;
        LOG.warn((Object)"ServletToolboxManager has been deprecated. Please use org.apache.velocity.tools.ToolboxFactory instead.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized ServletToolboxManager getInstance(ServletContext servletContext, String toolboxFile) {
        String uniqueKey;
        ServletToolboxManager toolboxManager;
        if (!toolboxFile.startsWith("/")) {
            toolboxFile = "/" + toolboxFile;
        }
        if ((toolboxManager = (ServletToolboxManager)managersMap.get(uniqueKey = servletContext.hashCode() + 58 + toolboxFile)) == null) {
            InputStream is = null;
            try {
                is = servletContext.getResourceAsStream(toolboxFile);
                if (is != null) {
                    LOG.info((Object)("Using config file '" + toolboxFile + "'"));
                    toolboxManager = new ServletToolboxManager(servletContext);
                    toolboxManager.load(is);
                    managersMap.put(uniqueKey, toolboxManager);
                    LOG.debug((Object)"Toolbox setup complete.");
                } else {
                    LOG.debug((Object)("No toolbox was found at '" + toolboxFile + "'"));
                }
            }
            catch (Exception e) {
                LOG.error((Object)("Problem loading toolbox '" + toolboxFile + "'"), (Throwable)e);
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception exception) {}
            }
        }
        return toolboxManager;
    }

    public void setCreateSession(boolean b) {
        this.createSession = b;
        LOG.debug((Object)("create-session is set to " + b));
    }

    public void setXhtml(Boolean value) {
        this.servletContext.setAttribute("XHTML", (Object)value);
        LOG.info((Object)("XHTML is set to " + value));
    }

    @Override
    protected RuleSet getRuleSet() {
        return servletRuleSet;
    }

    @Override
    protected boolean validateToolInfo(ToolInfo info) {
        ServletToolInfo sti;
        if (!super.validateToolInfo(info)) {
            return false;
        }
        if (info instanceof ServletToolInfo && (sti = (ServletToolInfo)info).getRequestPath() != null && !"request".equalsIgnoreCase(sti.getScope())) {
            LOG.error((Object)(sti.getKey() + " must be a request-scoped tool to have a request path restriction!"));
            return false;
        }
        return true;
    }

    @Override
    public void addTool(ToolInfo info) {
        if (this.validateToolInfo(info)) {
            if (info instanceof ServletToolInfo) {
                ServletToolInfo sti = (ServletToolInfo)info;
                if ("request".equalsIgnoreCase(sti.getScope())) {
                    this.requestToolInfo.add(sti);
                    return;
                }
                if ("session".equalsIgnoreCase(sti.getScope())) {
                    this.sessionToolInfo.add(sti);
                    return;
                }
                if ("application".equalsIgnoreCase(sti.getScope())) {
                    this.appTools.put(sti.getKey(), sti.getInstance(this.servletContext));
                    return;
                }
                LOG.warn((Object)("Unknown scope '" + sti.getScope() + "' - " + sti.getKey() + " will be request scoped."));
                this.requestToolInfo.add(info);
            } else {
                this.requestToolInfo.add(info);
            }
        }
    }

    @Override
    public void addData(ToolInfo info) {
        if (this.validateToolInfo(info)) {
            this.appTools.put(info.getKey(), info.getInstance(null));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map getToolbox(Object initData) {
        HttpSession session;
        ViewContext ctx = (ViewContext)initData;
        String requestPath = ServletUtils.getPath(ctx.getRequest());
        HashMap toolbox = new HashMap(this.appTools);
        if (!this.sessionToolInfo.isEmpty() && (session = ctx.getRequest().getSession(this.createSession)) != null) {
            Object object = this.getMutex(session);
            synchronized (object) {
                HashMap<String, Object> stmap = (HashMap<String, Object>)session.getAttribute(SESSION_TOOLS_KEY);
                if (stmap == null) {
                    stmap = new HashMap<String, Object>(this.sessionToolInfo.size());
                    for (ServletToolInfo sti : this.sessionToolInfo) {
                        stmap.put(sti.getKey(), sti.getInstance(ctx));
                    }
                    session.setAttribute(SESSION_TOOLS_KEY, stmap);
                }
                toolbox.putAll(stmap);
            }
        }
        for (ToolInfo info : this.requestToolInfo) {
            ServletToolInfo sti;
            if (info instanceof ServletToolInfo && !(sti = (ServletToolInfo)info).allowsRequestPath(requestPath)) continue;
            toolbox.put(info.getKey(), info.getInstance(ctx));
        }
        return toolbox;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getMutex(HttpSession session) {
        Object lock = session.getAttribute("session.mutex");
        if (lock == null) {
            ServletToolboxManager servletToolboxManager = this;
            synchronized (servletToolboxManager) {
                lock = session.getAttribute("session.mutex");
                if (lock == null) {
                    lock = new Boolean(true);
                    session.setAttribute("session.mutex", lock);
                }
            }
        }
        return lock;
    }
}

