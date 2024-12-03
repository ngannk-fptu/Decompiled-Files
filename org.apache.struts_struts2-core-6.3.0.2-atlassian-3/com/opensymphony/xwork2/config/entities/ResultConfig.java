/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultConfig
extends Located
implements Serializable {
    protected Map<String, String> params;
    protected String className;
    protected String name;

    protected ResultConfig(String name, String className) {
        this.name = name;
        this.className = className;
        this.params = new LinkedHashMap<String, String>();
    }

    protected ResultConfig(ResultConfig orig) {
        this.params = orig.params;
        this.name = orig.name;
        this.className = orig.className;
        this.location = orig.location;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResultConfig)) {
            return false;
        }
        ResultConfig resultConfig = (ResultConfig)o;
        if (this.className != null ? !this.className.equals(resultConfig.className) : resultConfig.className != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(resultConfig.name) : resultConfig.name != null) {
            return false;
        }
        return !(this.params != null ? !this.params.equals(resultConfig.params) : resultConfig.params != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 29 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 29 * result + (this.params != null ? this.params.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ResultConfig: [" + this.name + "] => [" + this.className + "] with params " + this.params;
    }

    public static final class Builder {
        protected ResultConfig target;

        public Builder(String name, String className) {
            this.target = new ResultConfig(name, className);
        }

        public Builder(ResultConfig orig) {
            this.target = new ResultConfig(orig);
        }

        public Builder name(String name) {
            this.target.name = name;
            return this;
        }

        public Builder className(String name) {
            this.target.className = name;
            return this;
        }

        public Builder addParam(String name, String value) {
            this.target.params.put(name, value);
            return this;
        }

        public Builder addParams(Map<String, String> params) {
            this.target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public ResultConfig build() {
            this.embalmTarget();
            ResultConfig result = this.target;
            this.target = new ResultConfig(this.target);
            return result;
        }

        protected void embalmTarget() {
            this.target.params = Collections.unmodifiableMap(this.target.params);
        }
    }
}

