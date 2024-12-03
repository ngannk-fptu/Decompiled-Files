/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.config.entities.AllowedMethods;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorListHolder;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ActionConfig
extends Located
implements Serializable {
    public static final String DEFAULT_METHOD = "execute";
    public static final String WILDCARD = "*";
    public static final String DEFAULT_METHOD_REGEX = "([A-Za-z0-9_$]*)";
    protected List<InterceptorMapping> interceptors;
    protected Map<String, String> params;
    protected Map<String, ResultConfig> results;
    protected List<ExceptionMappingConfig> exceptionMappings;
    protected String className;
    protected String methodName;
    protected String packageName;
    protected String name;
    protected boolean strictMethodInvocation = true;
    protected AllowedMethods allowedMethods;

    protected ActionConfig(String packageName, String name, String className) {
        this.packageName = packageName;
        this.name = name;
        this.className = className;
        this.params = new LinkedHashMap<String, String>();
        this.results = new LinkedHashMap<String, ResultConfig>();
        this.interceptors = new ArrayList<InterceptorMapping>();
        this.exceptionMappings = new ArrayList<ExceptionMappingConfig>();
    }

    protected ActionConfig(ActionConfig orig) {
        this.name = orig.name;
        this.className = orig.className;
        this.methodName = orig.methodName;
        this.packageName = orig.packageName;
        this.params = new LinkedHashMap<String, String>(orig.params);
        this.interceptors = new ArrayList<InterceptorMapping>(orig.interceptors);
        this.results = new LinkedHashMap<String, ResultConfig>(orig.results);
        this.exceptionMappings = new ArrayList<ExceptionMappingConfig>(orig.exceptionMappings);
        this.allowedMethods = orig.allowedMethods;
        this.location = orig.location;
    }

    public String getName() {
        return this.name;
    }

    public String getClassName() {
        return this.className;
    }

    public List<ExceptionMappingConfig> getExceptionMappings() {
        return this.exceptionMappings;
    }

    public List<InterceptorMapping> getInterceptors() {
        return this.interceptors;
    }

    public Set<String> getAllowedMethods() {
        return this.allowedMethods.list();
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public Map<String, ResultConfig> getResults() {
        return this.results;
    }

    public boolean isAllowedMethod(String method) {
        return method.equals(this.methodName != null ? this.methodName : DEFAULT_METHOD) || this.allowedMethods.isAllowed(method);
    }

    public boolean isStrictMethodInvocation() {
        return this.strictMethodInvocation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionConfig)) {
            return false;
        }
        ActionConfig actionConfig = (ActionConfig)o;
        if (this.className != null ? !this.className.equals(actionConfig.className) : actionConfig.className != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(actionConfig.name) : actionConfig.name != null) {
            return false;
        }
        if (this.interceptors != null ? !this.interceptors.equals(actionConfig.interceptors) : actionConfig.interceptors != null) {
            return false;
        }
        if (this.methodName != null ? !this.methodName.equals(actionConfig.methodName) : actionConfig.methodName != null) {
            return false;
        }
        if (this.params != null ? !this.params.equals(actionConfig.params) : actionConfig.params != null) {
            return false;
        }
        if (this.results != null ? !this.results.equals(actionConfig.results) : actionConfig.results != null) {
            return false;
        }
        return !(this.allowedMethods != null ? !this.allowedMethods.equals(actionConfig.allowedMethods) : actionConfig.allowedMethods != null);
    }

    public int hashCode() {
        int result = this.interceptors != null ? this.interceptors.hashCode() : 0;
        result = 31 * result + (this.params != null ? this.params.hashCode() : 0);
        result = 31 * result + (this.results != null ? this.results.hashCode() : 0);
        result = 31 * result + (this.exceptionMappings != null ? this.exceptionMappings.hashCode() : 0);
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.methodName != null ? this.methodName.hashCode() : 0);
        result = 31 * result + (this.packageName != null ? this.packageName.hashCode() : 0);
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.allowedMethods != null ? this.allowedMethods.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ActionConfig ");
        sb.append(this.name).append(" (");
        sb.append(this.className);
        if (this.methodName != null) {
            sb.append(".").append(this.methodName).append("()");
        }
        sb.append(")");
        sb.append(" - ").append(this.location);
        sb.append(" - ").append(this.allowedMethods);
        sb.append("}");
        return sb.toString();
    }

    public static class Builder
    implements InterceptorListHolder {
        protected ActionConfig target;
        protected Set<String> allowedMethods;
        private String methodRegex;

        public Builder(ActionConfig toClone) {
            this.target = new ActionConfig(toClone);
            this.allowedMethods = toClone.getAllowedMethods();
        }

        public Builder(String packageName, String name, String className) {
            this.target = new ActionConfig(packageName, name, className);
            this.allowedMethods = new HashSet<String>();
        }

        public Builder packageName(String name) {
            this.target.packageName = name;
            return this;
        }

        public Builder name(String name) {
            this.target.name = name;
            return this;
        }

        public Builder className(String name) {
            this.target.className = name;
            return this;
        }

        public Builder defaultClassName(String name) {
            if (StringUtils.isEmpty((CharSequence)this.target.className)) {
                this.target.className = name;
            }
            return this;
        }

        public Builder methodName(String method) {
            this.target.methodName = method;
            this.addAllowedMethod(method);
            return this;
        }

        public Builder addExceptionMapping(ExceptionMappingConfig exceptionMapping) {
            this.target.exceptionMappings.add(exceptionMapping);
            return this;
        }

        public Builder addExceptionMappings(Collection<? extends ExceptionMappingConfig> mappings) {
            this.target.exceptionMappings.addAll(mappings);
            return this;
        }

        public Builder exceptionMappings(Collection<? extends ExceptionMappingConfig> mappings) {
            this.target.exceptionMappings.clear();
            this.target.exceptionMappings.addAll(mappings);
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

        public Builder interceptors(List<InterceptorMapping> interceptors) {
            this.target.interceptors.clear();
            this.target.interceptors.addAll(interceptors);
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

        public Builder addResultConfig(ResultConfig resultConfig) {
            this.target.results.put(resultConfig.getName(), resultConfig);
            return this;
        }

        public Builder addResultConfigs(Collection<ResultConfig> configs) {
            for (ResultConfig rc : configs) {
                this.target.results.put(rc.getName(), rc);
            }
            return this;
        }

        public Builder addResultConfigs(Map<String, ResultConfig> configs) {
            this.target.results.putAll(configs);
            return this;
        }

        public Builder addAllowedMethod(String methodName) {
            if (methodName != null) {
                this.allowedMethods.add(methodName);
            }
            return this;
        }

        public Builder addAllowedMethod(Collection<String> methods) {
            this.allowedMethods.addAll(methods);
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public Builder setStrictMethodInvocation(boolean strictMethodInvocation) {
            this.target.strictMethodInvocation = strictMethodInvocation;
            return this;
        }

        public Builder setDefaultMethodRegex(String methodRegex) {
            this.methodRegex = methodRegex;
            return this;
        }

        public ActionConfig build() {
            this.target.params = Collections.unmodifiableMap(this.target.params);
            this.target.results = Collections.unmodifiableMap(this.target.results);
            this.target.interceptors = Collections.unmodifiableList(this.target.interceptors);
            this.target.exceptionMappings = Collections.unmodifiableList(this.target.exceptionMappings);
            this.target.allowedMethods = AllowedMethods.build(this.target.strictMethodInvocation, this.allowedMethods, this.methodRegex != null ? this.methodRegex : ActionConfig.DEFAULT_METHOD_REGEX);
            ActionConfig result = this.target;
            this.target = new ActionConfig(this.target);
            return result;
        }
    }
}

