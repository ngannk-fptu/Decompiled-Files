/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.config.entities.InterceptorListHolder;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InterceptorStackConfig
extends Located
implements Serializable {
    private static final long serialVersionUID = 2897260918170270343L;
    protected List<InterceptorMapping> interceptors;
    protected String name;

    protected InterceptorStackConfig() {
        this.interceptors = new ArrayList<InterceptorMapping>();
    }

    protected InterceptorStackConfig(InterceptorStackConfig orig) {
        this.name = orig.name;
        this.interceptors = new ArrayList<InterceptorMapping>(orig.interceptors);
        this.location = orig.location;
    }

    public Collection<InterceptorMapping> getInterceptors() {
        return this.interceptors;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }
        InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig)o;
        if (this.interceptors != null ? !this.interceptors.equals(interceptorStackConfig.interceptors) : interceptorStackConfig.interceptors != null) {
            return false;
        }
        return !(this.name != null ? !this.name.equals(interceptorStackConfig.name) : interceptorStackConfig.name != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 29 * result + (this.interceptors != null ? this.interceptors.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "InterceptorStackConfig: [" + this.name + "] contains " + this.interceptors;
    }

    public static class Builder
    implements InterceptorListHolder {
        protected InterceptorStackConfig target = new InterceptorStackConfig();

        public Builder(String name) {
            this.target.name = name;
        }

        public Builder name(String name) {
            this.target.name = name;
            return this;
        }

        @Override
        public Builder addInterceptor(InterceptorMapping interceptor) {
            this.target.interceptors.add(interceptor);
            return this;
        }

        @Override
        public Builder addInterceptors(List<InterceptorMapping> interceptors) {
            this.target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public InterceptorStackConfig build() {
            this.embalmTarget();
            InterceptorStackConfig result = this.target;
            this.target = new InterceptorStackConfig(this.target);
            return result;
        }

        protected void embalmTarget() {
            this.target.interceptors = Collections.unmodifiableList(this.target.interceptors);
        }
    }
}

