/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.filter.CacheFilter;
import org.radeox.filter.Filter;
import org.radeox.filter.context.FilterContext;

public class FilterPipe {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$FilterPipe == null ? (class$org$radeox$filter$FilterPipe = FilterPipe.class$("org.radeox.filter.FilterPipe")) : class$org$radeox$filter$FilterPipe));
    public static final String FIRST_IN_PIPE = "all";
    public static final String LAST_IN_PIPE = "none";
    public static final String[] EMPTY_BEFORE = new String[0];
    public static final String[] NO_REPLACES = new String[0];
    public static final String[] FIRST_BEFORE = new String[]{"all"};
    private InitialRenderContext initialContext;
    private List filterList = new ArrayList();
    private static Object[] noArguments = new Object[0];
    static /* synthetic */ Class class$org$radeox$filter$FilterPipe;

    public FilterPipe() {
        this(new BaseInitialRenderContext());
    }

    public FilterPipe(InitialRenderContext context) {
        this.initialContext = context;
    }

    public void init() {
        Iterator iterator = new ArrayList(this.filterList).iterator();
        while (iterator.hasNext()) {
            Filter filter = (Filter)iterator.next();
            String[] replaces = filter.replaces();
            for (int i = 0; i < replaces.length; ++i) {
                String replace = replaces[i];
                this.removeFilter(replace);
            }
        }
    }

    public void removeFilter(String filterClass) {
        Iterator iterator = this.filterList.iterator();
        while (iterator.hasNext()) {
            Filter filter = (Filter)iterator.next();
            if (!filter.getClass().getName().equals(filterClass)) continue;
            iterator.remove();
        }
    }

    public void addFilter(Filter filter) {
        filter.setInitialContext(this.initialContext);
        int minIndex = Integer.MAX_VALUE;
        String[] before = filter.before();
        for (int i = 0; i < before.length; ++i) {
            String s = before[i];
            int index = FilterPipe.index(this.filterList, s);
            if (index >= minIndex) continue;
            minIndex = index;
        }
        if (minIndex == Integer.MAX_VALUE) {
            minIndex = -1;
        }
        if (FilterPipe.contains(filter.before(), FIRST_IN_PIPE)) {
            this.filterList.add(0, filter);
        } else if (minIndex != -1) {
            this.filterList.add(minIndex, filter);
        } else {
            this.filterList.add(filter);
        }
    }

    public int index(String filterName) {
        return FilterPipe.index(this.filterList, filterName);
    }

    public static int index(List list, String filterName) {
        for (int i = 0; i < list.size(); ++i) {
            if (!filterName.equals(list.get(i).getClass().getName())) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object value) {
        return Arrays.binarySearch(array, value) != -1;
    }

    public String filter(String input, FilterContext context) {
        String output = input;
        Iterator filterIterator = this.filterList.iterator();
        RenderContext renderContext = context.getRenderContext();
        while (filterIterator.hasNext()) {
            Filter f = (Filter)filterIterator.next();
            try {
                if (f instanceof CacheFilter) {
                    renderContext.setCacheable(true);
                } else {
                    renderContext.setCacheable(false);
                }
                String tmp = f.filter(output, context);
                if (output.equals(tmp)) {
                    renderContext.setCacheable(true);
                }
                if (null == tmp) {
                    log.warn((Object)("FilterPipe.filter: error while filtering: " + f));
                } else {
                    output = tmp;
                }
                renderContext.commitCache();
            }
            catch (Exception e) {
                log.warn((Object)("Filtering exception: " + f), (Throwable)e);
            }
        }
        return output;
    }

    public Filter getFilter(int index) {
        return (Filter)this.filterList.get(index);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

