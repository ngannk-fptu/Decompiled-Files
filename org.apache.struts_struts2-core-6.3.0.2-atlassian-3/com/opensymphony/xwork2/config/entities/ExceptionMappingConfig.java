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

public class ExceptionMappingConfig
extends Located
implements Serializable {
    protected String name;
    protected String exceptionClassName;
    protected String result;
    protected Map<String, String> params;

    protected ExceptionMappingConfig(String name, String exceptionClassName, String result) {
        this.name = name;
        this.exceptionClassName = exceptionClassName;
        this.result = result;
        this.params = new LinkedHashMap<String, String>();
    }

    protected ExceptionMappingConfig(ExceptionMappingConfig target) {
        this.name = target.name;
        this.exceptionClassName = target.exceptionClassName;
        this.result = target.result;
        this.params = new LinkedHashMap<String, String>(target.params);
        this.location = target.location;
    }

    public String getName() {
        return this.name;
    }

    public String getExceptionClassName() {
        return this.exceptionClassName;
    }

    public String getResult() {
        return this.result;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExceptionMappingConfig)) {
            return false;
        }
        ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig)o;
        if (this.name != null ? !this.name.equals(exceptionMappingConfig.name) : exceptionMappingConfig.name != null) {
            return false;
        }
        if (this.exceptionClassName != null ? !this.exceptionClassName.equals(exceptionMappingConfig.exceptionClassName) : exceptionMappingConfig.exceptionClassName != null) {
            return false;
        }
        if (this.result != null ? !this.result.equals(exceptionMappingConfig.result) : exceptionMappingConfig.result != null) {
            return false;
        }
        return !(this.params != null ? !this.params.equals(exceptionMappingConfig.params) : exceptionMappingConfig.params != null);
    }

    public int hashCode() {
        int hashCode = this.name != null ? this.name.hashCode() : 0;
        hashCode = 29 * hashCode + (this.exceptionClassName != null ? this.exceptionClassName.hashCode() : 0);
        hashCode = 29 * hashCode + (this.result != null ? this.result.hashCode() : 0);
        hashCode = 29 * hashCode + (this.params != null ? this.params.hashCode() : 0);
        return hashCode;
    }

    public String toString() {
        return "ExceptionMappingConfig: [" + this.name + "] handle [" + this.exceptionClassName + "] to result [" + this.result + "] with params " + this.params;
    }

    public static class Builder {
        protected ExceptionMappingConfig target;

        public Builder(ExceptionMappingConfig toClone) {
            this.target = new ExceptionMappingConfig(toClone);
        }

        public Builder(String name, String exceptionClassName, String result) {
            this.target = new ExceptionMappingConfig(name, exceptionClassName, result);
        }

        public Builder name(String name) {
            this.target.name = name;
            return this;
        }

        public Builder exceptionClassName(String name) {
            this.target.exceptionClassName = name;
            return this;
        }

        public Builder result(String result) {
            this.target.result = result;
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

        public ExceptionMappingConfig build() {
            this.embalmTarget();
            ExceptionMappingConfig result = this.target;
            this.target = new ExceptionMappingConfig(this.target);
            return result;
        }

        protected void embalmTarget() {
            this.target.params = Collections.unmodifiableMap(this.target.params);
        }
    }
}

