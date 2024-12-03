/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport;
import org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReportJmxMBean;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;

public class SlowQueryReportJmx
extends SlowQueryReport
implements NotificationEmitter,
SlowQueryReportJmxMBean {
    public static final String SLOW_QUERY_NOTIFICATION = "SLOW QUERY";
    public static final String FAILED_QUERY_NOTIFICATION = "FAILED QUERY";
    public static final String objectNameAttribute = "objectName";
    protected static volatile CompositeType SLOW_QUERY_TYPE;
    private static final Log log;
    protected static final ConcurrentHashMap<String, SlowQueryReportJmxMBean> mbeans;
    protected volatile NotificationBroadcasterSupport notifier = new NotificationBroadcasterSupport();
    protected String poolName = null;
    protected static final AtomicLong notifySequence;
    protected boolean notifyPool = true;
    protected ConnectionPool pool = null;

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        this.notifier.addNotificationListener(listener, filter, handback);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return this.notifier.getNotificationInfo();
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.notifier.removeNotificationListener(listener);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {
        this.notifier.removeNotificationListener(listener, filter, handback);
    }

    protected static CompositeType getCompositeType() {
        if (SLOW_QUERY_TYPE == null) {
            try {
                SLOW_QUERY_TYPE = new CompositeType(SlowQueryReportJmx.class.getName(), "Composite data type for query statistics", SlowQueryReport.QueryStats.getFieldNames(), SlowQueryReport.QueryStats.getFieldDescriptions(), SlowQueryReport.QueryStats.getFieldTypes());
            }
            catch (OpenDataException x) {
                log.warn((Object)"Unable to initialize composite data type for JMX stats and notifications.", (Throwable)x);
            }
        }
        return SLOW_QUERY_TYPE;
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        super.reset(parent, con);
        if (parent != null) {
            this.poolName = parent.getName();
            this.pool = parent;
            this.registerJmx();
        }
    }

    @Override
    public void poolClosed(ConnectionPool pool) {
        this.poolName = pool.getName();
        this.deregisterJmx();
        super.poolClosed(pool);
    }

    @Override
    public void poolStarted(ConnectionPool pool) {
        this.pool = pool;
        super.poolStarted(pool);
        this.poolName = pool.getName();
    }

    @Override
    protected String reportFailedQuery(String query, Object[] args, String name, long start, Throwable t) {
        query = super.reportFailedQuery(query, args, name, start, t);
        if (this.isLogFailed()) {
            this.notifyJmx(query, FAILED_QUERY_NOTIFICATION);
        }
        return query;
    }

    protected void notifyJmx(String query, String type) {
        block6: {
            try {
                long sequence = notifySequence.incrementAndGet();
                if (this.isNotifyPool()) {
                    if (this.pool != null && this.pool.getJmxPool() != null) {
                        this.pool.getJmxPool().notify(type, query);
                    }
                } else if (this.notifier != null) {
                    Notification notification = new Notification(type, this, sequence, System.currentTimeMillis(), query);
                    this.notifier.sendNotification(notification);
                }
            }
            catch (RuntimeOperationsException e) {
                if (!log.isDebugEnabled()) break block6;
                log.debug((Object)"Unable to send failed query notification.", (Throwable)e);
            }
        }
    }

    @Override
    protected String reportSlowQuery(String query, Object[] args, String name, long start, long delta) {
        query = super.reportSlowQuery(query, args, name, start, delta);
        if (this.isLogSlow()) {
            this.notifyJmx(query, SLOW_QUERY_NOTIFICATION);
        }
        return query;
    }

    public String[] getPoolNames() {
        Set keys = perPoolStats.keySet();
        return keys.toArray(new String[0]);
    }

    public String getPoolName() {
        return this.poolName;
    }

    public boolean isNotifyPool() {
        return this.notifyPool;
    }

    public void setNotifyPool(boolean notifyPool) {
        this.notifyPool = notifyPool;
    }

    public void resetStats() {
        ConcurrentHashMap queries = (ConcurrentHashMap)perPoolStats.get(this.poolName);
        if (queries != null) {
            Iterator it = ((ConcurrentHashMap.KeySetView)queries.keySet()).iterator();
            while (it.hasNext()) {
                it.remove();
            }
        }
    }

    @Override
    public CompositeData[] getSlowQueriesCD() throws OpenDataException {
        Set stats;
        CompositeDataSupport[] result = null;
        ConcurrentHashMap queries = (ConcurrentHashMap)perPoolStats.get(this.poolName);
        if (queries != null && (stats = queries.entrySet()) != null) {
            result = new CompositeDataSupport[stats.size()];
            Iterator it = stats.iterator();
            int pos = 0;
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                SlowQueryReport.QueryStats qs = (SlowQueryReport.QueryStats)entry.getValue();
                result[pos++] = qs.getCompositeData(SlowQueryReportJmx.getCompositeType());
            }
        }
        return result;
    }

    protected void deregisterJmx() {
        try {
            if (mbeans.remove(this.poolName) != null) {
                ObjectName oname = this.getObjectName(this.getClass(), this.poolName);
                JmxUtil.unregisterJmx(oname);
            }
        }
        catch (MalformedObjectNameException | RuntimeOperationsException e) {
            log.warn((Object)"Jmx deregistration failed.", (Throwable)e);
        }
    }

    public ObjectName getObjectName(Class<?> clazz, String poolName) throws MalformedObjectNameException {
        Map<String, PoolProperties.InterceptorProperty> properties = this.getProperties();
        ObjectName oname = properties != null && properties.containsKey(objectNameAttribute) ? new ObjectName(properties.get(objectNameAttribute).getValue()) : new ObjectName("tomcat.jdbc:type=" + clazz.getName() + ",name=" + poolName);
        return oname;
    }

    protected void registerJmx() {
        try {
            if (!this.isNotifyPool()) {
                if (SlowQueryReportJmx.getCompositeType() != null) {
                    ObjectName oname = this.getObjectName(this.getClass(), this.poolName);
                    if (mbeans.putIfAbsent(this.poolName, this) == null) {
                        JmxUtil.registerJmx(oname, null, this);
                    }
                } else {
                    log.warn((Object)(SlowQueryReport.class.getName() + "- No JMX support, composite type was not found."));
                }
            }
        }
        catch (MalformedObjectNameException | RuntimeOperationsException e) {
            log.error((Object)"Jmx registration failed, no JMX data will be exposed for the query stats.", (Throwable)e);
        }
    }

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        String threshold = "notifyPool";
        PoolProperties.InterceptorProperty p1 = properties.get("notifyPool");
        if (p1 != null) {
            this.setNotifyPool(Boolean.parseBoolean(p1.getValue()));
        }
    }

    static {
        log = LogFactory.getLog(SlowQueryReportJmx.class);
        mbeans = new ConcurrentHashMap();
        notifySequence = new AtomicLong(0L);
    }
}

