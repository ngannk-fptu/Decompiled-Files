/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

@Deprecated
public class FilterManager {
    protected static FilterManager manager;
    protected static final int DEFAULT_CACHE_CLEAN_SIZE = 100;
    protected static final long DEFAULT_CACHE_SLEEP_TIME = 600000L;
    protected Map<Integer, FilterItem> cache = new HashMap<Integer, FilterItem>();
    protected int cacheCleanSize = 100;
    protected long cleanSleepTime = 600000L;
    protected FilterCleaner filterCleaner = new FilterCleaner();

    public static synchronized FilterManager getInstance() {
        if (manager == null) {
            manager = new FilterManager();
        }
        return manager;
    }

    protected FilterManager() {
        Thread fcThread = new Thread(this.filterCleaner);
        fcThread.setDaemon(true);
        fcThread.start();
    }

    public void setCacheSize(int cacheCleanSize) {
        this.cacheCleanSize = cacheCleanSize;
    }

    public void setCleanThreadSleepTime(long cleanSleepTime) {
        this.cleanSleepTime = cleanSleepTime;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Filter getFilter(Filter filter) {
        Map<Integer, FilterItem> map = this.cache;
        synchronized (map) {
            FilterItem fi = null;
            fi = this.cache.get(filter.hashCode());
            if (fi != null) {
                fi.timestamp = new Date().getTime();
                return fi.filter;
            }
            this.cache.put(filter.hashCode(), new FilterItem(filter));
            return filter;
        }
    }

    protected class FilterCleaner
    implements Runnable {
        private boolean running = true;
        private TreeSet<Map.Entry<Integer, FilterItem>> sortedFilterItems;

        public FilterCleaner() {
            this.sortedFilterItems = new TreeSet<Map.Entry<Integer, FilterItem>>(new Comparator<Map.Entry<Integer, FilterItem>>(){

                @Override
                public int compare(Map.Entry<Integer, FilterItem> a, Map.Entry<Integer, FilterItem> b) {
                    FilterItem fia = a.getValue();
                    FilterItem fib = b.getValue();
                    if (fia.timestamp == fib.timestamp) {
                        return 0;
                    }
                    if (fia.timestamp < fib.timestamp) {
                        return -1;
                    }
                    return 1;
                }
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            while (this.running) {
                if (FilterManager.this.cache.size() > FilterManager.this.cacheCleanSize) {
                    this.sortedFilterItems.clear();
                    Map<Integer, FilterItem> map = FilterManager.this.cache;
                    synchronized (map) {
                        this.sortedFilterItems.addAll(FilterManager.this.cache.entrySet());
                        Iterator<Map.Entry<Integer, FilterItem>> it = this.sortedFilterItems.iterator();
                        int numToDelete = (int)((double)(FilterManager.this.cache.size() - FilterManager.this.cacheCleanSize) * 1.5);
                        int counter = 0;
                        while (it.hasNext() && counter++ < numToDelete) {
                            Map.Entry<Integer, FilterItem> entry = it.next();
                            FilterManager.this.cache.remove(entry.getKey());
                        }
                    }
                    this.sortedFilterItems.clear();
                }
                try {
                    Thread.sleep(FilterManager.this.cleanSleepTime);
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
        }
    }

    protected class FilterItem {
        public Filter filter;
        public long timestamp;

        public FilterItem(Filter filter) {
            this.filter = filter;
            this.timestamp = new Date().getTime();
        }
    }
}

