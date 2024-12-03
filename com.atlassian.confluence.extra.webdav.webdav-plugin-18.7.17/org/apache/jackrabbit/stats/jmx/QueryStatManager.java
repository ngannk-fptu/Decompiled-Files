/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats.jmx;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.jackrabbit.api.jmx.QueryStatManagerMBean;
import org.apache.jackrabbit.api.stats.QueryStat;
import org.apache.jackrabbit.api.stats.QueryStatDto;

public class QueryStatManager
implements QueryStatManagerMBean {
    private final QueryStat queryStat;

    public QueryStatManager(QueryStat queryStat) {
        this.queryStat = queryStat;
    }

    public boolean isEnabled() {
        return this.queryStat.isEnabled();
    }

    public void enable() {
        this.queryStat.setEnabled(true);
    }

    public void disable() {
        this.queryStat.setEnabled(false);
    }

    public void reset() {
        this.queryStat.reset();
    }

    @Override
    public int getSlowQueriesQueueSize() {
        return this.queryStat.getSlowQueriesQueueSize();
    }

    @Override
    public void setSlowQueriesQueueSize(int size) {
        this.queryStat.setSlowQueriesQueueSize(size);
    }

    @Override
    public void clearSlowQueriesQueue() {
        this.queryStat.clearSlowQueriesQueue();
    }

    @Override
    public int getPopularQueriesQueueSize() {
        return this.queryStat.getPopularQueriesQueueSize();
    }

    @Override
    public void setPopularQueriesQueueSize(int size) {
        this.queryStat.setPopularQueriesQueueSize(size);
    }

    @Override
    public void clearPopularQueriesQueue() {
        this.queryStat.clearPopularQueriesQueue();
    }

    @Override
    public TabularData getSlowQueries() {
        return this.asTabularData(this.queryStat.getSlowQueries());
    }

    @Override
    public TabularData getPopularQueries() {
        return this.asTabularData(this.queryStat.getPopularQueries());
    }

    private TabularData asTabularData(QueryStatDto[] data) {
        TabularDataSupport tds = null;
        try {
            CompositeType ct = QueryStatCompositeTypeFactory.getCompositeType();
            TabularType tt = new TabularType(QueryStatDto.class.getName(), "Query History", ct, QueryStatCompositeTypeFactory.index);
            tds = new TabularDataSupport(tt);
            for (QueryStatDto q : data) {
                tds.put(new CompositeDataSupport(ct, QueryStatCompositeTypeFactory.names, QueryStatCompositeTypeFactory.getValues(q)));
            }
            return tds;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class QueryStatCompositeTypeFactory {
        private static final String[] index = new String[]{"position"};
        private static final String[] names = new String[]{"position", "duration", "occurrenceCount", "language", "statement", "creationTime"};
        private static final String[] descriptions = new String[]{"position", "duration", "occurrenceCount", "language", "statement", "creationTime"};
        private static final OpenType[] types = new OpenType[]{SimpleType.LONG, SimpleType.LONG, SimpleType.INTEGER, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};

        private QueryStatCompositeTypeFactory() {
        }

        public static CompositeType getCompositeType() throws OpenDataException {
            return new CompositeType(QueryStat.class.getName(), QueryStat.class.getName(), names, descriptions, types);
        }

        public static Object[] getValues(QueryStatDto q) {
            return new Object[]{q.getPosition(), q.getDuration(), q.getOccurrenceCount(), q.getLanguage(), q.getStatement(), q.getCreationTime()};
        }
    }
}

