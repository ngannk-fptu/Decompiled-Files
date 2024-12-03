/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.ehcache.sizeof.filters.SizeOfFilter;

public final class Configuration {
    private final int maxDepth;
    private final boolean abort;
    private final boolean silent;
    private final SizeOfFilter[] filters;

    public Configuration(int maxDepth, boolean abort, boolean silent, SizeOfFilter ... filters) {
        this.maxDepth = maxDepth;
        this.abort = abort;
        this.silent = silent;
        this.filters = filters;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public boolean isAbort() {
        return this.abort;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public SizeOfFilter[] getFilters() {
        return this.filters;
    }

    public static final class Builder {
        private int maxDepth;
        private boolean silent;
        private boolean abort;
        private final List<SizeOfFilter> filters = new ArrayList<SizeOfFilter>();

        public Builder() {
        }

        public Builder(Configuration cfg) {
            this.maxDepth(cfg.maxDepth);
            this.silent(cfg.silent);
            this.abort(cfg.abort);
            Collections.addAll(this.filters, cfg.filters);
        }

        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder silent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public Builder abort(boolean abort) {
            this.abort = abort;
            return this;
        }

        public Builder addFilter(SizeOfFilter filter) {
            if (!this.filters.contains(filter)) {
                this.filters.add(filter);
            }
            return this;
        }

        public Builder addFilters(SizeOfFilter ... filters) {
            for (SizeOfFilter filter : filters) {
                this.addFilter(filter);
            }
            return this;
        }

        public Builder removeFilter(SizeOfFilter filter) {
            this.filters.remove(filter);
            return this;
        }

        public Builder removeFilters(SizeOfFilter ... filters) {
            this.filters.removeAll(Arrays.asList(filters));
            return this;
        }

        public Builder clearlFilters() {
            this.filters.clear();
            return this;
        }

        public Configuration build() {
            return new Configuration(this.maxDepth, this.abort, this.silent, this.filters.toArray(new SizeOfFilter[this.filters.size()]));
        }
    }
}

