/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Filter$Result
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.filter.AbstractFilter
 *  org.apache.logging.log4j.core.filter.CompositeFilter
 */
package org.apache.log4j.bridge;

import org.apache.log4j.bridge.FilterWrapper;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.filter.CompositeFilter;

public class FilterAdapter
extends AbstractFilter {
    private final org.apache.log4j.spi.Filter filter;

    public static Filter adapt(org.apache.log4j.spi.Filter filter) {
        if (filter instanceof Filter) {
            return (Filter)filter;
        }
        if (filter instanceof FilterWrapper && filter.getNext() == null) {
            return ((FilterWrapper)filter).getFilter();
        }
        if (filter != null) {
            return new FilterAdapter(filter);
        }
        return null;
    }

    public static org.apache.log4j.spi.Filter addFilter(org.apache.log4j.spi.Filter first, org.apache.log4j.spi.Filter second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        CompositeFilter composite = first instanceof FilterWrapper && ((FilterWrapper)first).getFilter() instanceof CompositeFilter ? (CompositeFilter)((FilterWrapper)first).getFilter() : CompositeFilter.createFilters((Filter[])new Filter[]{FilterAdapter.adapt(first)});
        return FilterWrapper.adapt((Filter)composite.addFilter(FilterAdapter.adapt(second)));
    }

    private FilterAdapter(org.apache.log4j.spi.Filter filter) {
        this.filter = filter;
    }

    public Filter.Result filter(LogEvent event) {
        LogEventAdapter loggingEvent = new LogEventAdapter(event);
        for (org.apache.log4j.spi.Filter next = this.filter; next != null; next = next.getNext()) {
            switch (next.decide(loggingEvent)) {
                case 1: {
                    return Filter.Result.ACCEPT;
                }
                case -1: {
                    return Filter.Result.DENY;
                }
            }
        }
        return Filter.Result.NEUTRAL;
    }

    public org.apache.log4j.spi.Filter getFilter() {
        return this.filter;
    }

    public void start() {
        this.filter.activateOptions();
    }
}

