/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.Clock;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduleType;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class SecondsBasedEntryTaskScheduler<K, V>
implements EntryTaskScheduler<K, V> {
    public static final double FACTOR = 1000.0;
    private static final long INITIAL_TIME_MILLIS = Clock.currentTimeMillis();
    private static final Comparator<ScheduledEntry> SCHEDULED_ENTRIES_COMPARATOR = new Comparator<ScheduledEntry>(){

        @Override
        public int compare(ScheduledEntry o1, ScheduledEntry o2) {
            if (o1.getScheduleId() > o2.getScheduleId()) {
                return 1;
            }
            if (o1.getScheduleId() < o2.getScheduleId()) {
                return -1;
            }
            return 0;
        }
    };
    private HashMap<K, PerKeyScheduler> keys = new HashMap();
    private HashMap<Integer, ScheduledGroup> groups = new HashMap();
    private final AtomicLong uniqueIdGenerator = new AtomicLong();
    private final Object mutex = new Object();
    private final TaskScheduler taskScheduler;
    private final ScheduledEntryProcessor<K, V> entryProcessor;
    private final ScheduleType scheduleType;

    SecondsBasedEntryTaskScheduler(TaskScheduler taskScheduler, ScheduledEntryProcessor<K, V> entryProcessor, ScheduleType scheduleType) {
        this.taskScheduler = taskScheduler;
        this.entryProcessor = entryProcessor;
        this.scheduleType = scheduleType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean schedule(long delayMillis, K key, V value) {
        int delaySeconds = SecondsBasedEntryTaskScheduler.ceilToSecond(delayMillis);
        final int second = SecondsBasedEntryTaskScheduler.findRelativeSecond(delayMillis);
        long id = this.uniqueIdGenerator.incrementAndGet();
        Object object = this.mutex;
        synchronized (object) {
            ScheduledGroup group;
            ScheduledEntry<K, V> entry = new ScheduledEntry<K, V>(key, value, delayMillis, delaySeconds, id);
            PerKeyScheduler keyScheduler = this.keys.get(key);
            if (keyScheduler == null) {
                switch (this.scheduleType) {
                    case POSTPONE: {
                        keyScheduler = new PerKeyPostponeScheduler(key);
                        break;
                    }
                    case FOR_EACH: {
                        keyScheduler = new PerKeyForEachScheduler(key);
                        break;
                    }
                    default: {
                        throw new RuntimeException("Undefined schedule type.");
                    }
                }
                this.keys.put(key, keyScheduler);
            }
            if ((group = this.groups.get(second)) == null) {
                Runnable groupExecutor = new Runnable(){

                    @Override
                    public void run() {
                        SecondsBasedEntryTaskScheduler.this.executeGroup(second);
                    }
                };
                ScheduledFuture<?> executorFuture = this.taskScheduler.schedule(groupExecutor, (long)delaySeconds, TimeUnit.SECONDS);
                group = new ScheduledGroup(second, executorFuture);
                this.groups.put(second, group);
            }
            return keyScheduler.schedule(entry, group);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeGroup(int second) {
        ArrayList entries;
        Object object = this.mutex;
        synchronized (object) {
            ScheduledGroup group = this.groups.remove(second);
            if (group == null) {
                return;
            }
            entries = new ArrayList(group.listEntries());
            for (ScheduledEntry scheduledEntry : entries) {
                this.keys.get(scheduledEntry.getKey()).executed(scheduledEntry);
            }
        }
        Collections.sort(entries, SCHEDULED_ENTRIES_COMPARATOR);
        this.entryProcessor.process(this, entries);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScheduledEntry<K, V> get(K key) {
        Object object = this.mutex;
        synchronized (object) {
            PerKeyScheduler keyScheduler = this.keys.get(key);
            return keyScheduler != null ? keyScheduler.get() : null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScheduledEntry<K, V> cancel(K key) {
        Object object = this.mutex;
        synchronized (object) {
            PerKeyScheduler keyScheduler = this.keys.get(key);
            return keyScheduler != null ? keyScheduler.cancel() : null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int cancelIfExists(K key, V value) {
        Object object = this.mutex;
        synchronized (object) {
            PerKeyScheduler keyScheduler = this.keys.get(key);
            return keyScheduler != null ? keyScheduler.cancelIfExists(value) : 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancelAll() {
        Object object = this.mutex;
        synchronized (object) {
            for (ScheduledGroup group : this.groups.values()) {
                group.executor.cancel(false);
            }
            this.groups.clear();
            this.keys.clear();
        }
    }

    public String toString() {
        return "EntryTaskScheduler{numberOfEntries=" + this.size() + ", numberOfKeys=" + this.keys.size() + ", numberOfGroups=" + this.groups.size() + '}';
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int size() {
        Object object = this.mutex;
        synchronized (object) {
            int size = 0;
            for (ScheduledGroup group : this.groups.values()) {
                size += group.countEntries();
            }
            return size;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        Object object = this.mutex;
        synchronized (object) {
            return this.groups.isEmpty() && this.keys.isEmpty();
        }
    }

    static int findRelativeSecond(long delayMillis) {
        long now = Clock.currentTimeMillis();
        long d = now + delayMillis - INITIAL_TIME_MILLIS;
        return SecondsBasedEntryTaskScheduler.ceilToSecond(d);
    }

    private static int ceilToSecond(long delayMillis) {
        return (int)Math.ceil((double)delayMillis / 1000.0);
    }

    private static boolean areEqual(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    private final class ScheduledGroup {
        final int second;
        final ScheduledFuture executor;
        final Map<Long, ScheduledEntry<K, V>> idToEntryMap = new HashMap();

        private ScheduledGroup(int second, ScheduledFuture executor) {
            this.second = second;
            this.executor = executor;
        }

        private void addEntry(Long id, ScheduledEntry<K, V> entry) {
            this.idToEntryMap.put(id, entry);
        }

        private ScheduledEntry<K, V> getEntry(Long id) {
            return this.idToEntryMap.get(id);
        }

        private Collection<ScheduledEntry<K, V>> listEntries() {
            return this.idToEntryMap.values();
        }

        private int countEntries() {
            return this.idToEntryMap.size();
        }

        private ScheduledEntry<K, V> removeEntry(Long id) {
            ScheduledEntry entry = this.idToEntryMap.remove(id);
            if (this.idToEntryMap.isEmpty()) {
                this.executor.cancel(false);
                SecondsBasedEntryTaskScheduler.this.groups.remove(this.second);
            }
            return entry;
        }
    }

    private final class PerKeyForEachScheduler
    extends PerKeyScheduler {
        final K key;
        final Map<Long, ScheduledGroup> idToGroupMap;

        PerKeyForEachScheduler(K key) {
            this.idToGroupMap = new HashMap<Long, ScheduledGroup>();
            this.key = key;
        }

        @Override
        boolean schedule(ScheduledEntry<K, V> entry, ScheduledGroup group) {
            Long id = entry.getScheduleId();
            this.idToGroupMap.put(id, group);
            group.addEntry(id, entry);
            return true;
        }

        @Override
        ScheduledEntry<K, V> get() {
            ScheduledEntry entry = null;
            for (Map.Entry<Long, ScheduledGroup> idToGroup : this.idToGroupMap.entrySet()) {
                Long id = idToGroup.getKey();
                ScheduledGroup group = idToGroup.getValue();
                entry = group.getEntry(id);
            }
            assert (entry != null);
            return entry;
        }

        @Override
        ScheduledEntry<K, V> cancel() {
            ScheduledEntry entry = null;
            for (Map.Entry<Long, ScheduledGroup> idToGroup : this.idToGroupMap.entrySet()) {
                Long id = idToGroup.getKey();
                ScheduledGroup group = idToGroup.getValue();
                entry = group.removeEntry(id);
            }
            SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
            assert (entry != null);
            return entry;
        }

        @Override
        int cancelIfExists(V value) {
            int cancelled = 0;
            Iterator<Map.Entry<Long, ScheduledGroup>> iterator = this.idToGroupMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, ScheduledGroup> idToGroup = iterator.next();
                Long id = idToGroup.getKey();
                ScheduledGroup group = idToGroup.getValue();
                ScheduledEntry entry = group.getEntry(id);
                if (!SecondsBasedEntryTaskScheduler.areEqual(entry.getValue(), value)) continue;
                group.removeEntry(id);
                iterator.remove();
                ++cancelled;
            }
            if (this.idToGroupMap.isEmpty()) {
                SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
            }
            return cancelled;
        }

        @Override
        void executed(ScheduledEntry<K, V> entry) {
            this.idToGroupMap.remove(entry.getScheduleId());
            if (this.idToGroupMap.isEmpty()) {
                SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
            }
        }
    }

    private final class PerKeyPostponeScheduler
    extends PerKeyScheduler {
        final K key;
        Long id;
        ScheduledGroup group;

        PerKeyPostponeScheduler(K key) {
            this.key = key;
        }

        @Override
        boolean schedule(ScheduledEntry<K, V> newEntry, ScheduledGroup newGroup) {
            if (newGroup == this.group) {
                return false;
            }
            if (this.group != null) {
                this.group.removeEntry(this.id);
            }
            this.id = newEntry.getScheduleId();
            this.group = newGroup;
            newGroup.addEntry(this.id, newEntry);
            return true;
        }

        @Override
        ScheduledEntry<K, V> get() {
            return this.group.getEntry(this.id);
        }

        @Override
        ScheduledEntry<K, V> cancel() {
            ScheduledEntry entry = this.group.removeEntry(this.id);
            SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
            return entry;
        }

        @Override
        int cancelIfExists(V value) {
            ScheduledEntry entry = this.group.getEntry(this.id);
            if (SecondsBasedEntryTaskScheduler.areEqual(entry.getValue(), value)) {
                this.group.removeEntry(this.id);
                SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
                return 1;
            }
            return 0;
        }

        @Override
        void executed(ScheduledEntry<K, V> entry) {
            assert (entry.getScheduleId() == this.id.longValue());
            SecondsBasedEntryTaskScheduler.this.keys.remove(this.key);
        }
    }

    private abstract class PerKeyScheduler {
        private PerKeyScheduler() {
        }

        abstract boolean schedule(ScheduledEntry<K, V> var1, ScheduledGroup var2);

        abstract ScheduledEntry<K, V> get();

        abstract ScheduledEntry<K, V> cancel();

        abstract int cancelIfExists(V var1);

        abstract void executed(ScheduledEntry<K, V> var1);
    }
}

