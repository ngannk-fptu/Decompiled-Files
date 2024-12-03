/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.QueryParam;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CoordinateImpl
implements Coordinate {
    private final Map<String, String> params = new LinkedHashMap<String, String>();

    CoordinateImpl(List<QueryParam> c) {
        for (QueryParam keyval : c) {
            if (!keyval.queryValue.isPresent()) continue;
            this.params.put(keyval.queryKey, keyval.queryValue.get());
        }
    }

    @Override
    public void copyTo(UrlBuilder urlBuilder, String key) {
        if (this.params.containsKey(key)) {
            urlBuilder.addToQueryString(key, this.params.get(key));
        }
    }

    @Override
    public String get(String key) {
        return this.params.get(key);
    }

    @Override
    public Iterable<String> getKeys() {
        return this.params.keySet();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CoordinateImpl that = (CoordinateImpl)o;
        return this.params.equals(that.params);
    }

    public int hashCode() {
        return this.params.hashCode();
    }

    public String toString() {
        return "Coordinate{params=" + this.params + '}';
    }
}

