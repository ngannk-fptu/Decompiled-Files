/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import java.util.Optional;

class QueryParam {
    final String queryKey;
    final Optional<String> queryValue;

    public QueryParam(String queryKey, Optional<String> queryValue) {
        this.queryKey = queryKey;
        this.queryValue = queryValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueryParam that = (QueryParam)o;
        if (!this.queryKey.equals(that.queryKey)) {
            return false;
        }
        return this.queryValue.equals(that.queryValue);
    }

    public int hashCode() {
        int result = this.queryKey.hashCode();
        result = 31 * result + this.queryValue.hashCode();
        return result;
    }
}

