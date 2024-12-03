/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.extended.EhcacheQueryBuilder;
import org.terracotta.context.query.Query;

public enum StandardPassThroughStatistic {
    CACHE_SIZE(EhcacheQueryBuilder.cache(), Integer.TYPE, null, "size", "cache"),
    LOCAL_HEAP_SIZE(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Integer.TYPE, 0, "size", "local-heap"),
    LOCAL_HEAP_SIZE_BYTES(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Long.TYPE, 0L, "size-in-bytes", "local-heap"),
    LOCAL_OFFHEAP_SIZE(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Long.TYPE, 0L, "size", "local-offheap"),
    LOCAL_OFFHEAP_SIZE_BYTES(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Long.TYPE, 0L, "size-in-bytes", "local-offheap"),
    LOCAL_DISK_SIZE(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Integer.TYPE, 0, "size", "local-disk"),
    LOCAL_DISK_SIZE_BYTES(EhcacheQueryBuilder.cache().children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants()), Long.TYPE, 0L, "size-in-bytes", "local-disk"),
    WRITER_QUEUE_LENGTH(EhcacheQueryBuilder.cache().descendants(), Long.TYPE, 0L, "queue-length", "write-behind"),
    REMOTE_SIZE(EhcacheQueryBuilder.cache().descendants(), Long.TYPE, 0L, "size", "remote"),
    LAST_REJOIN_TIMESTAMP(EhcacheQueryBuilder.cache().descendants(), Long.TYPE, 0L, "lastRejoinTime", "cache");

    private static final int THIRTY = 30;
    private final Class<? extends Number> type;
    private final Query context;
    private final Number absentValue;
    private final String name;
    private final Set<String> tags;

    private <T extends Number> StandardPassThroughStatistic(Query context, Class<T> type, T absentValue, String name, String ... tags) {
        this.context = context;
        this.type = type;
        this.absentValue = absentValue;
        this.name = name;
        this.tags = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(tags)));
    }

    final Query context() {
        return this.context;
    }

    public final Class<? extends Number> type() {
        return this.type;
    }

    public final Number absentValue() {
        return this.absentValue;
    }

    public final String statisticName() {
        return this.name;
    }

    public final Set<String> tags() {
        return this.tags;
    }
}

