/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.interceptor.Interceptor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InterceptorMapping
implements Serializable {
    private String name;
    private Interceptor interceptor;
    private final Map<String, String> params;

    public InterceptorMapping(String name, Interceptor interceptor) {
        this(name, interceptor, new HashMap<String, String>());
    }

    public InterceptorMapping(String name, Interceptor interceptor, Map<String, String> params) {
        this.name = name;
        this.interceptor = interceptor;
        this.params = params;
    }

    public String getName() {
        return this.name;
    }

    public Interceptor getInterceptor() {
        return this.interceptor;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InterceptorMapping that = (InterceptorMapping)o;
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        return result;
    }

    public String toString() {
        return "InterceptorMapping: [" + this.name + "] => [" + this.interceptor.getClass().getName() + "] with params [" + this.params + "]";
    }
}

