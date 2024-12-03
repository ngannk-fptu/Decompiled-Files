/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.interceptor;

import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

public class CachePutOperation
extends CacheOperation {
    @Nullable
    private final String unless;

    public CachePutOperation(Builder b) {
        super(b);
        this.unless = b.unless;
    }

    @Nullable
    public String getUnless() {
        return this.unless;
    }

    public static class Builder
    extends CacheOperation.Builder {
        @Nullable
        private String unless;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append('\'');
            return sb;
        }

        @Override
        public CachePutOperation build() {
            return new CachePutOperation(this);
        }
    }
}

