/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractQueryReport;

public class SlowQueryReport
extends AbstractQueryReport {
    private static final Log log = LogFactory.getLog(SlowQueryReport.class);
    protected static final ConcurrentHashMap<String, ConcurrentHashMap<String, QueryStats>> perPoolStats = new ConcurrentHashMap();
    protected volatile ConcurrentHashMap<String, QueryStats> queries = null;
    protected int maxQueries = 1000;
    protected boolean logSlow = true;
    protected boolean logFailed = false;
    protected final Comparator<QueryStats> queryStatsComparator = new QueryStatsComparator();

    public static ConcurrentHashMap<String, QueryStats> getPoolStats(String poolname) {
        return perPoolStats.get(poolname);
    }

    public void setMaxQueries(int maxQueries) {
        this.maxQueries = maxQueries;
    }

    @Override
    protected String reportFailedQuery(String query, Object[] args, String name, long start, Throwable t) {
        String sql = super.reportFailedQuery(query, args, name, start, t);
        if (this.maxQueries > 0) {
            long now = System.currentTimeMillis();
            long delta = now - start;
            QueryStats qs = this.getQueryStats(sql);
            if (qs != null) {
                qs.failure(delta, now);
            }
            if (this.isLogFailed() && log.isWarnEnabled()) {
                log.warn((Object)("Failed Query Report SQL=" + sql + "; time=" + delta + " ms;"));
            }
        }
        return sql;
    }

    @Override
    protected String reportQuery(String query, Object[] args, String name, long start, long delta) {
        QueryStats qs;
        String sql = super.reportQuery(query, args, name, start, delta);
        if (this.maxQueries > 0 && (qs = this.getQueryStats(sql)) != null) {
            qs.add(delta, start);
        }
        return sql;
    }

    @Override
    protected String reportSlowQuery(String query, Object[] args, String name, long start, long delta) {
        QueryStats qs;
        String sql = super.reportSlowQuery(query, args, name, start, delta);
        if (this.maxQueries > 0 && (qs = this.getQueryStats(sql)) != null) {
            qs.add(delta, start);
            if (this.isLogSlow() && log.isWarnEnabled()) {
                log.warn((Object)("Slow Query Report SQL=" + sql + "; time=" + delta + " ms;"));
            }
        }
        return sql;
    }

    @Override
    public void closeInvoked() {
    }

    @Override
    public void prepareStatement(String sql, long time) {
        QueryStats qs;
        if (this.maxQueries > 0 && (qs = this.getQueryStats(sql)) != null) {
            qs.prepare(time);
        }
    }

    @Override
    public void prepareCall(String sql, long time) {
        QueryStats qs;
        if (this.maxQueries > 0 && (qs = this.getQueryStats(sql)) != null) {
            qs.prepare(time);
        }
    }

    @Override
    public void poolStarted(ConnectionPool pool) {
        super.poolStarted(pool);
        this.queries = perPoolStats.get(pool.getName());
        if (this.queries == null) {
            this.queries = new ConcurrentHashMap();
            if (perPoolStats.putIfAbsent(pool.getName(), this.queries) != null) {
                this.queries = perPoolStats.get(pool.getName());
            }
        }
    }

    @Override
    public void poolClosed(ConnectionPool pool) {
        perPoolStats.remove(pool.getName());
        super.poolClosed(pool);
    }

    protected QueryStats getQueryStats(String sql) {
        ConcurrentHashMap<String, QueryStats> queries;
        if (sql == null) {
            sql = "";
        }
        if ((queries = this.queries) == null) {
            if (log.isWarnEnabled()) {
                log.warn((Object)"Connection has already been closed or abandoned");
            }
            return null;
        }
        QueryStats qs = queries.get(sql);
        if (qs == null) {
            qs = new QueryStats(sql);
            if (queries.putIfAbsent(sql, qs) != null) {
                qs = queries.get(sql);
            } else if (queries.size() > this.maxQueries) {
                this.removeOldest(queries);
            }
        }
        return qs;
    }

    protected void removeOldest(ConcurrentHashMap<String, QueryStats> queries) {
        ArrayList<QueryStats> list = new ArrayList<QueryStats>(queries.values());
        Collections.sort(list, this.queryStatsComparator);
        int removeIndex = 0;
        while (queries.size() > this.maxQueries) {
            String sql = list.get(removeIndex).getQuery();
            queries.remove(sql);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Removing slow query, capacity reached:" + sql));
            }
            ++removeIndex;
        }
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        super.reset(parent, con);
        this.queries = parent != null ? perPoolStats.get(parent.getName()) : null;
    }

    public boolean isLogSlow() {
        return this.logSlow;
    }

    public void setLogSlow(boolean logSlow) {
        this.logSlow = logSlow;
    }

    public boolean isLogFailed() {
        return this.logFailed;
    }

    public void setLogFailed(boolean logFailed) {
        this.logFailed = logFailed;
    }

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        String threshold = "threshold";
        String maxqueries = "maxQueries";
        String logslow = "logSlow";
        String logfailed = "logFailed";
        PoolProperties.InterceptorProperty p1 = properties.get("threshold");
        PoolProperties.InterceptorProperty p2 = properties.get("maxQueries");
        PoolProperties.InterceptorProperty p3 = properties.get("logSlow");
        PoolProperties.InterceptorProperty p4 = properties.get("logFailed");
        if (p1 != null) {
            this.setThreshold(Long.parseLong(p1.getValue()));
        }
        if (p2 != null) {
            this.setMaxQueries(Integer.parseInt(p2.getValue()));
        }
        if (p3 != null) {
            this.setLogSlow(Boolean.parseBoolean(p3.getValue()));
        }
        if (p4 != null) {
            this.setLogFailed(Boolean.parseBoolean(p4.getValue()));
        }
    }

    public static class QueryStatsComparator
    implements Comparator<QueryStats> {
        @Override
        public int compare(QueryStats stats1, QueryStats stats2) {
            return Long.compare(QueryStatsComparator.handleZero(stats1.lastInvocation), QueryStatsComparator.handleZero(stats2.lastInvocation));
        }

        private static long handleZero(long value) {
            return value == 0L ? Long.MAX_VALUE : value;
        }
    }

    public static class QueryStats {
        static final String[] FIELD_NAMES = new String[]{"query", "nrOfInvocations", "maxInvocationTime", "maxInvocationDate", "minInvocationTime", "minInvocationDate", "totalInvocationTime", "failures", "prepareCount", "prepareTime", "lastInvocation"};
        static final String[] FIELD_DESCRIPTIONS = new String[]{"The SQL query", "The number of query invocations, a call to executeXXX", "The longest time for this query in milliseconds", "The time and date for when the longest query took place", "The shortest time for this query in milliseconds", "The time and date for when the shortest query took place", "The total amount of milliseconds spent executing this query", "The number of failures for this query", "The number of times this query was prepared (prepareStatement/prepareCall)", "The total number of milliseconds spent preparing this query", "The date and time of the last invocation"};
        static final OpenType<?>[] FIELD_TYPES = new OpenType[]{SimpleType.STRING, SimpleType.INTEGER, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.INTEGER, SimpleType.LONG, SimpleType.LONG};
        private final String query;
        private volatile int nrOfInvocations;
        private volatile long maxInvocationTime = Long.MIN_VALUE;
        private volatile long maxInvocationDate;
        private volatile long minInvocationTime = Long.MAX_VALUE;
        private volatile long minInvocationDate;
        private volatile long totalInvocationTime;
        private volatile long failures;
        private volatile int prepareCount;
        private volatile long prepareTime;
        private volatile long lastInvocation = 0L;

        public static String[] getFieldNames() {
            return FIELD_NAMES;
        }

        public static String[] getFieldDescriptions() {
            return FIELD_DESCRIPTIONS;
        }

        public static OpenType<?>[] getFieldTypes() {
            return FIELD_TYPES;
        }

        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            StringBuilder buf = new StringBuilder("QueryStats[query:");
            buf.append(this.query);
            buf.append(", nrOfInvocations:");
            buf.append(this.nrOfInvocations);
            buf.append(", maxInvocationTime:");
            buf.append(this.maxInvocationTime);
            buf.append(", maxInvocationDate:");
            buf.append(sdf.format(new Date(this.maxInvocationDate)));
            buf.append(", minInvocationTime:");
            buf.append(this.minInvocationTime);
            buf.append(", minInvocationDate:");
            buf.append(sdf.format(new Date(this.minInvocationDate)));
            buf.append(", totalInvocationTime:");
            buf.append(this.totalInvocationTime);
            buf.append(", averageInvocationTime:");
            buf.append((float)this.totalInvocationTime / (float)this.nrOfInvocations);
            buf.append(", failures:");
            buf.append(this.failures);
            buf.append(", prepareCount:");
            buf.append(this.prepareCount);
            buf.append(", prepareTime:");
            buf.append(this.prepareTime);
            buf.append(']');
            return buf.toString();
        }

        public CompositeDataSupport getCompositeData(CompositeType type) throws OpenDataException {
            Object[] values = new Object[]{this.query, this.nrOfInvocations, this.maxInvocationTime, this.maxInvocationDate, this.minInvocationTime, this.minInvocationDate, this.totalInvocationTime, this.failures, this.prepareCount, this.prepareTime, this.lastInvocation};
            return new CompositeDataSupport(type, FIELD_NAMES, values);
        }

        public QueryStats(String query) {
            this.query = query;
        }

        public synchronized void prepare(long invocationTime) {
            ++this.prepareCount;
            this.prepareTime += invocationTime;
        }

        public synchronized void add(long invocationTime, long now) {
            this.maxInvocationTime = Math.max(invocationTime, this.maxInvocationTime);
            if (this.maxInvocationTime == invocationTime) {
                this.maxInvocationDate = now;
            }
            this.minInvocationTime = Math.min(invocationTime, this.minInvocationTime);
            if (this.minInvocationTime == invocationTime) {
                this.minInvocationDate = now;
            }
            ++this.nrOfInvocations;
            this.totalInvocationTime += invocationTime;
            this.lastInvocation = now;
        }

        public synchronized void failure(long invocationTime, long now) {
            this.add(invocationTime, now);
            ++this.failures;
        }

        public String getQuery() {
            return this.query;
        }

        public int getNrOfInvocations() {
            return this.nrOfInvocations;
        }

        public long getMaxInvocationTime() {
            return this.maxInvocationTime;
        }

        public long getMaxInvocationDate() {
            return this.maxInvocationDate;
        }

        public long getMinInvocationTime() {
            return this.minInvocationTime;
        }

        public long getMinInvocationDate() {
            return this.minInvocationDate;
        }

        public long getTotalInvocationTime() {
            return this.totalInvocationTime;
        }

        public int hashCode() {
            return this.query.hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof QueryStats) {
                QueryStats qs = (QueryStats)other;
                return qs.query.equals(this.query);
            }
            return false;
        }

        public boolean isOlderThan(QueryStats other) {
            return this.lastInvocation < other.lastInvocation;
        }
    }
}

