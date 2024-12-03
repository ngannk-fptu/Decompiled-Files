/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Session
 *  org.apache.catalina.connector.Request
 *  org.apache.catalina.connector.Response
 *  org.apache.catalina.session.ManagerBase
 *  org.apache.catalina.session.PersistentManager
 *  org.apache.catalina.valves.ValveBase
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Cluster;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class JvmRouteBinderValve
extends ValveBase
implements ClusterValve {
    public static final Log log = LogFactory.getLog(JvmRouteBinderValve.class);
    protected CatalinaCluster cluster;
    protected static final StringManager sm = StringManager.getManager(JvmRouteBinderValve.class);
    protected boolean enabled = true;
    protected long numberOfSessions = 0L;
    protected String sessionIdAttribute = "org.apache.catalina.ha.session.JvmRouteOriginalSessionID";

    public JvmRouteBinderValve() {
        super(true);
    }

    public String getSessionIdAttribute() {
        return this.sessionIdAttribute;
    }

    public void setSessionIdAttribute(String sessionIdAttribute) {
        this.sessionIdAttribute = sessionIdAttribute;
    }

    public long getNumberOfSessions() {
        return this.numberOfSessions;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        Manager manager;
        if (this.getEnabled() && request.getContext() != null && request.getContext().getDistributable() && !request.isAsyncDispatching() && (manager = request.getContext().getManager()) != null && (manager instanceof ClusterManager && this.getCluster() != null && this.getCluster().getManager(((ClusterManager)manager).getName()) != null || manager instanceof PersistentManager)) {
            this.handlePossibleTurnover(request);
        }
        this.getNext().invoke(request, response);
    }

    protected void handlePossibleTurnover(Request request) {
        String sessionID = request.getRequestedSessionId();
        if (sessionID != null) {
            long t1 = System.currentTimeMillis();
            String jvmRoute = this.getLocalJvmRoute(request);
            if (jvmRoute == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jvmRoute.missingJvmRouteAttribute"));
                }
                return;
            }
            this.handleJvmRoute(request, sessionID, jvmRoute);
            if (log.isDebugEnabled()) {
                long t2 = System.currentTimeMillis();
                long time = t2 - t1;
                log.debug((Object)sm.getString("jvmRoute.turnoverInfo", new Object[]{time}));
            }
        }
    }

    protected String getLocalJvmRoute(Request request) {
        Manager manager = this.getManager(request);
        if (manager instanceof ManagerBase) {
            return ((ManagerBase)manager).getJvmRoute();
        }
        return null;
    }

    protected Manager getManager(Request request) {
        Manager manager = request.getContext().getManager();
        if (log.isDebugEnabled()) {
            if (manager != null) {
                log.debug((Object)sm.getString("jvmRoute.foundManager", new Object[]{manager, request.getContext().getName()}));
            } else {
                log.debug((Object)sm.getString("jvmRoute.notFoundManager", new Object[]{request.getContext().getName()}));
            }
        }
        return manager;
    }

    @Override
    public CatalinaCluster getCluster() {
        return this.cluster;
    }

    @Override
    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }

    protected void handleJvmRoute(Request request, String sessionId, String localJvmRoute) {
        String requestJvmRoute = null;
        int index = sessionId.indexOf(46);
        if (index > 0) {
            requestJvmRoute = sessionId.substring(index + 1);
        }
        if (requestJvmRoute != null && !requestJvmRoute.equals(localJvmRoute)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jvmRoute.failover", new Object[]{requestJvmRoute, localJvmRoute, sessionId}));
            }
            Session catalinaSession = null;
            try {
                catalinaSession = this.getManager(request).findSession(sessionId);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            String id = sessionId.substring(0, index);
            String newSessionID = id + "." + localJvmRoute;
            if (catalinaSession != null) {
                this.changeSessionID(request, sessionId, newSessionID, catalinaSession);
                ++this.numberOfSessions;
            } else {
                try {
                    catalinaSession = this.getManager(request).findSession(newSessionID);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (catalinaSession != null) {
                    this.changeRequestSessionID(request, sessionId, newSessionID);
                } else if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jvmRoute.cannotFindSession", new Object[]{sessionId}));
                }
            }
        }
    }

    protected void changeSessionID(Request request, String sessionId, String newSessionID, Session catalinaSession) {
        this.fireLifecycleEvent("Before session migration", catalinaSession);
        this.getManager(request).changeSessionId(catalinaSession, newSessionID);
        this.changeRequestSessionID(request, sessionId, newSessionID);
        this.changeSessionAuthenticationNote(sessionId, newSessionID, catalinaSession);
        this.fireLifecycleEvent("After session migration", catalinaSession);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("jvmRoute.changeSession", new Object[]{sessionId, newSessionID}));
        }
    }

    protected void changeRequestSessionID(Request request, String sessionId, String newSessionID) {
        request.changeSessionId(newSessionID);
        if (this.sessionIdAttribute != null && !this.sessionIdAttribute.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jvmRoute.set.originalsessionid", new Object[]{this.sessionIdAttribute, sessionId}));
            }
            request.setAttribute(this.sessionIdAttribute, (Object)sessionId);
        }
    }

    protected void changeSessionAuthenticationNote(String sessionId, String newSessionID, Session catalinaSession) {
        if (sessionId.equals(catalinaSession.getNote("org.apache.catalina.authenticator.SESSION_ID"))) {
            catalinaSession.setNote("org.apache.catalina.authenticator.SESSION_ID", (Object)newSessionID);
        }
    }

    protected synchronized void startInternal() throws LifecycleException {
        Cluster containerCluster;
        if (this.cluster == null && (containerCluster = this.getContainer().getCluster()) instanceof CatalinaCluster) {
            this.setCluster((CatalinaCluster)containerCluster);
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("jvmRoute.valve.started"));
            if (this.cluster == null) {
                log.info((Object)sm.getString("jvmRoute.noCluster"));
            }
        }
        super.startInternal();
    }

    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.cluster = null;
        this.numberOfSessions = 0L;
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("jvmRoute.valve.stopped"));
        }
    }
}

