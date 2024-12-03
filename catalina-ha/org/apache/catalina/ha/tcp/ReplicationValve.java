/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.Context
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Session
 *  org.apache.catalina.connector.Request
 *  org.apache.catalina.connector.Response
 *  org.apache.catalina.core.StandardContext
 *  org.apache.catalina.valves.ValveBase
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletException;
import org.apache.catalina.Cluster;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.ClusterSession;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class ReplicationValve
extends ValveBase
implements ClusterValve {
    private static final Log log = LogFactory.getLog(ReplicationValve.class);
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.ha.tcp");
    private CatalinaCluster cluster = null;
    protected Pattern filter = null;
    protected final ThreadLocal<ArrayList<DeltaSession>> crossContextSessions = new ThreadLocal();
    protected boolean doProcessingStats = false;
    protected LongAdder totalRequestTime = new LongAdder();
    protected LongAdder totalSendTime = new LongAdder();
    protected LongAdder nrOfRequests = new LongAdder();
    protected AtomicLong lastSendTime = new AtomicLong();
    protected LongAdder nrOfFilterRequests = new LongAdder();
    protected LongAdder nrOfSendRequests = new LongAdder();
    protected LongAdder nrOfCrossContextSendRequests = new LongAdder();
    protected boolean primaryIndicator = false;
    protected String primaryIndicatorName = "org.apache.catalina.ha.tcp.isPrimarySession";

    public ReplicationValve() {
        super(true);
    }

    @Override
    public CatalinaCluster getCluster() {
        return this.cluster;
    }

    @Override
    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }

    public String getFilter() {
        if (this.filter == null) {
            return null;
        }
        return this.filter.toString();
    }

    public void setFilter(String filter) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("ReplicationValve.filter.loading", new Object[]{filter}));
        }
        if (filter == null || filter.length() == 0) {
            this.filter = null;
        } else {
            try {
                this.filter = Pattern.compile(filter);
            }
            catch (PatternSyntaxException pse) {
                log.error((Object)sm.getString("ReplicationValve.filter.failure", new Object[]{filter}), (Throwable)pse);
            }
        }
    }

    public boolean isPrimaryIndicator() {
        return this.primaryIndicator;
    }

    public void setPrimaryIndicator(boolean primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }

    public String getPrimaryIndicatorName() {
        return this.primaryIndicatorName;
    }

    public void setPrimaryIndicatorName(String primaryIndicatorName) {
        this.primaryIndicatorName = primaryIndicatorName;
    }

    public boolean doStatistics() {
        return this.doProcessingStats;
    }

    public void setStatistics(boolean doProcessingStats) {
        this.doProcessingStats = doProcessingStats;
    }

    public long getLastSendTime() {
        return this.lastSendTime.longValue();
    }

    public long getNrOfRequests() {
        return this.nrOfRequests.longValue();
    }

    public long getNrOfFilterRequests() {
        return this.nrOfFilterRequests.longValue();
    }

    public long getNrOfCrossContextSendRequests() {
        return this.nrOfCrossContextSendRequests.longValue();
    }

    public long getNrOfSendRequests() {
        return this.nrOfSendRequests.longValue();
    }

    public long getTotalRequestTime() {
        return this.totalRequestTime.longValue();
    }

    public long getTotalSendTime() {
        return this.totalSendTime.longValue();
    }

    public void registerReplicationSession(DeltaSession session) {
        List sessions = this.crossContextSessions.get();
        if (sessions != null && !sessions.contains(session)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("ReplicationValve.crossContext.registerSession", new Object[]{session.getIdInternal(), session.getManager().getContext().getName()}));
            }
            sessions.add(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Context context;
        long totalstart = 0L;
        if (this.doStatistics()) {
            totalstart = System.currentTimeMillis();
        }
        if (this.primaryIndicator) {
            this.createPrimaryIndicator(request);
        }
        boolean isCrossContext = (context = request.getContext()) != null && context instanceof StandardContext && context.getCrossContext();
        try {
            if (isCrossContext) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("ReplicationValve.crossContext.add"));
                }
                this.crossContextSessions.set(new ArrayList());
            }
            this.getNext().invoke(request, response);
            if (context != null && this.cluster != null && context.getManager() instanceof ClusterManager) {
                ClusterManager clusterManager = (ClusterManager)context.getManager();
                if (this.cluster.getManager(clusterManager.getName()) == null) {
                    return;
                }
                if (this.cluster.hasMembers()) {
                    this.sendReplicationMessage(request, totalstart, isCrossContext, clusterManager);
                } else {
                    this.resetReplicationRequest(request, isCrossContext);
                }
            }
        }
        finally {
            if (isCrossContext) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("ReplicationValve.crossContext.remove"));
                }
                this.crossContextSessions.remove();
            }
        }
    }

    public void resetStatistics() {
        this.totalRequestTime.reset();
        this.totalSendTime.reset();
        this.lastSendTime.set(0L);
        this.nrOfFilterRequests.reset();
        this.nrOfRequests.reset();
        this.nrOfSendRequests.reset();
        this.nrOfCrossContextSendRequests.reset();
    }

    protected synchronized void startInternal() throws LifecycleException {
        if (this.cluster == null) {
            Cluster containerCluster = this.getContainer().getCluster();
            if (containerCluster instanceof CatalinaCluster) {
                this.setCluster((CatalinaCluster)containerCluster);
            } else if (log.isWarnEnabled()) {
                log.warn((Object)sm.getString("ReplicationValve.nocluster"));
            }
        }
        super.startInternal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendReplicationMessage(Request request, long totalstart, boolean isCrossContext, ClusterManager clusterManager) {
        long start = 0L;
        if (this.doStatistics()) {
            start = System.currentTimeMillis();
        }
        try {
            if (!(clusterManager instanceof DeltaManager)) {
                this.sendInvalidSessions(clusterManager);
            }
            this.sendSessionReplicationMessage(request, clusterManager);
            if (isCrossContext) {
                this.sendCrossContextSession();
            }
        }
        catch (Exception x) {
            log.error((Object)sm.getString("ReplicationValve.send.failure"), (Throwable)x);
        }
        finally {
            if (this.doStatistics()) {
                this.updateStats(totalstart, start);
            }
        }
    }

    protected void sendCrossContextSession() {
        List sessions = this.crossContextSessions.get();
        if (sessions != null && sessions.size() > 0) {
            for (DeltaSession session : sessions) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("ReplicationValve.crossContext.sendDelta", new Object[]{session.getManager().getContext().getName()}));
                }
                this.sendMessage(session, (ClusterManager)session.getManager());
                if (!this.doStatistics()) continue;
                this.nrOfCrossContextSendRequests.increment();
            }
        }
    }

    protected void resetReplicationRequest(Request request, boolean isCrossContext) {
        List sessions;
        Session contextSession = request.getSessionInternal(false);
        if (contextSession instanceof DeltaSession) {
            this.resetDeltaRequest(contextSession);
            ((DeltaSession)contextSession).setPrimarySession(true);
        }
        if (isCrossContext && (sessions = (List)this.crossContextSessions.get()) != null && sessions.size() > 0) {
            for (Session session : sessions) {
                this.resetDeltaRequest(session);
                if (!(session instanceof DeltaSession)) continue;
                ((DeltaSession)contextSession).setPrimarySession(true);
            }
        }
    }

    protected void resetDeltaRequest(Session session) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("ReplicationValve.resetDeltaRequest", new Object[]{session.getManager().getContext().getName()}));
        }
        ((DeltaSession)session).resetDeltaRequest();
    }

    protected void sendSessionReplicationMessage(Request request, ClusterManager manager) {
        Session session = request.getSessionInternal(false);
        if (session != null) {
            String uri = request.getDecodedRequestURI();
            if (!this.isRequestWithoutSessionChange(uri)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("ReplicationValve.invoke.uri", new Object[]{uri}));
                }
                this.sendMessage(session, manager);
            } else if (this.doStatistics()) {
                this.nrOfFilterRequests.increment();
            }
        }
    }

    protected void sendMessage(Session session, ClusterManager manager) {
        String id = session.getIdInternal();
        if (id != null) {
            this.send(manager, id);
        }
    }

    protected void send(ClusterManager manager, String sessionId) {
        ClusterMessage msg = manager.requestCompleted(sessionId);
        if (msg != null && this.cluster != null) {
            this.cluster.send(msg);
            if (this.doStatistics()) {
                this.nrOfSendRequests.increment();
            }
        }
    }

    protected void sendInvalidSessions(ClusterManager manager) {
        String[] invalidIds = manager.getInvalidatedSessions();
        if (invalidIds.length > 0) {
            for (String invalidId : invalidIds) {
                try {
                    this.send(manager, invalidId);
                }
                catch (Exception x) {
                    log.error((Object)sm.getString("ReplicationValve.send.invalid.failure", new Object[]{invalidId}), (Throwable)x);
                }
            }
        }
    }

    protected boolean isRequestWithoutSessionChange(String uri) {
        Pattern f = this.filter;
        return f != null && f.matcher(uri).matches();
    }

    protected void updateStats(long requestTime, long clusterTime) {
        long currentTime = System.currentTimeMillis();
        this.lastSendTime.set(currentTime);
        this.totalSendTime.add(currentTime - clusterTime);
        this.totalRequestTime.add(currentTime - requestTime);
        this.nrOfRequests.increment();
        if (log.isInfoEnabled() && this.nrOfRequests.longValue() % 100L == 0L) {
            log.info((Object)sm.getString("ReplicationValve.stats", new Object[]{this.totalRequestTime.longValue() / this.nrOfRequests.longValue(), this.totalSendTime.longValue() / this.nrOfRequests.longValue(), this.nrOfRequests.longValue(), this.nrOfSendRequests.longValue(), this.nrOfCrossContextSendRequests.longValue(), this.nrOfFilterRequests.longValue(), this.totalRequestTime.longValue(), this.totalSendTime.longValue()}));
        }
    }

    protected void createPrimaryIndicator(Request request) throws IOException {
        String id = request.getRequestedSessionId();
        if (id != null && id.length() > 0) {
            Manager manager = request.getContext().getManager();
            Session session = manager.findSession(id);
            if (session instanceof ClusterSession) {
                ClusterSession cses = (ClusterSession)session;
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("ReplicationValve.session.indicator", new Object[]{request.getContext().getName(), id, this.primaryIndicatorName, cses.isPrimarySession()}));
                }
                request.setAttribute(this.primaryIndicatorName, (Object)(cses.isPrimarySession() ? Boolean.TRUE : Boolean.FALSE));
            } else if (log.isDebugEnabled()) {
                if (session != null) {
                    log.debug((Object)sm.getString("ReplicationValve.session.found", new Object[]{request.getContext().getName(), id}));
                } else {
                    log.debug((Object)sm.getString("ReplicationValve.session.invalid", new Object[]{request.getContext().getName(), id}));
                }
            }
        }
    }
}

