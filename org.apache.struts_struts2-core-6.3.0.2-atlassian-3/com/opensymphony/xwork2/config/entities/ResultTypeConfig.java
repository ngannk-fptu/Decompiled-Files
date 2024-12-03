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

public class ResultTypeConfig
extends Located
implements Serializable {
    protected String className;
    protected String name;
    protected String defaultResultParam;
    protected Map<String, String> params;

    protected ResultTypeConfig(String name, String className) {
        this.name = name;
        this.className = className;
        this.params = new LinkedHashMap<String, String>();
    }

    protected ResultTypeConfig(ResultTypeConfig orig) {
        this.name = orig.name;
        this.className = orig.className;
        this.defaultResultParam = orig.defaultResultParam;
        this.params = new LinkedHashMap<String, String>(orig.params);
        this.location = orig.location;
    }

    public String getDefaultResultParam() {
        return this.defaultResultParam;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(this.params);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResultTypeConfig that = (ResultTypeConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.params != null ? !this.params.equals(that.params) : that.params != null);
    }

    public int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 29 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 29 * result + (this.params != null ? this.params.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ResultTypeConfig: [" + this.name + "] => [" + this.className + "] with defaultParam [" + this.defaultResultParam + "] with params " + this.params;
    }

    public static final class Builder {
        protected ResultTypeConfig target;

        public Builder(String name, String className) {
            this.target = new ResultTypeConfig(name, className);
        }

        public Builder(ResultTypeConfig orig) {
            this.target = new ResultTypeConfig(orig);
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

        public Builder defaultResultParam(String defaultResultParam) {
            this.target.defaultResultParam = defaultResultParam;
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public ResultTypeConfig build() {
            ResultTypeConfig result = this.target;
            this.target = new ResultTypeConfig(this.target);
            return result;
        }
    }
}

