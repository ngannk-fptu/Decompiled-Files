/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorLocator;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageConfig
extends Located
implements Comparable<PackageConfig>,
Serializable,
InterceptorLocator {
    protected Map<String, ActionConfig> actionConfigs;
    protected Map<String, ResultConfig> globalResultConfigs;
    protected Set<String> globalAllowedMethods;
    protected Map<String, Object> interceptorConfigs;
    protected Map<String, ResultTypeConfig> resultTypeConfigs;
    protected List<ExceptionMappingConfig> globalExceptionMappingConfigs;
    protected List<PackageConfig> parents;
    protected String defaultInterceptorRef;
    protected String defaultActionRef;
    protected String defaultResultType;
    protected String defaultClassRef;
    protected String name;
    protected String namespace = "";
    protected boolean isAbstract = false;
    protected boolean needsRefresh;
    protected boolean strictMethodInvocation = true;

    protected PackageConfig(String name) {
        this.name = name;
        this.actionConfigs = new LinkedHashMap<String, ActionConfig>();
        this.globalResultConfigs = new LinkedHashMap<String, ResultConfig>();
        this.globalAllowedMethods = new HashSet<String>();
        this.interceptorConfigs = new LinkedHashMap<String, Object>();
        this.resultTypeConfigs = new LinkedHashMap<String, ResultTypeConfig>();
        this.globalExceptionMappingConfigs = new ArrayList<ExceptionMappingConfig>();
        this.parents = new ArrayList<PackageConfig>();
    }

    protected PackageConfig(PackageConfig orig) {
        this.defaultInterceptorRef = orig.defaultInterceptorRef;
        this.defaultActionRef = orig.defaultActionRef;
        this.defaultResultType = orig.defaultResultType;
        this.defaultClassRef = orig.defaultClassRef;
        this.name = orig.name;
        this.namespace = orig.namespace;
        this.isAbstract = orig.isAbstract;
        this.needsRefresh = orig.needsRefresh;
        this.actionConfigs = new LinkedHashMap<String, ActionConfig>(orig.actionConfigs);
        this.globalResultConfigs = new LinkedHashMap<String, ResultConfig>(orig.globalResultConfigs);
        this.globalAllowedMethods = new LinkedHashSet<String>(orig.globalAllowedMethods);
        this.interceptorConfigs = new LinkedHashMap<String, Object>(orig.interceptorConfigs);
        this.resultTypeConfigs = new LinkedHashMap<String, ResultTypeConfig>(orig.resultTypeConfigs);
        this.globalExceptionMappingConfigs = new ArrayList<ExceptionMappingConfig>(orig.globalExceptionMappingConfigs);
        this.parents = new ArrayList<PackageConfig>(orig.parents);
        this.location = orig.location;
        this.strictMethodInvocation = orig.strictMethodInvocation;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public Map<String, ActionConfig> getActionConfigs() {
        return this.actionConfigs;
    }

    public Map<String, ActionConfig> getAllActionConfigs() {
        LinkedHashMap<String, ActionConfig> retMap = new LinkedHashMap<String, ActionConfig>();
        if (!this.parents.isEmpty()) {
            for (PackageConfig parent : this.parents) {
                retMap.putAll(parent.getAllActionConfigs());
            }
        }
        retMap.putAll(this.getActionConfigs());
        return retMap;
    }

    public Map<String, ResultConfig> getAllGlobalResults() {
        LinkedHashMap<String, ResultConfig> retMap = new LinkedHashMap<String, ResultConfig>();
        if (!this.parents.isEmpty()) {
            for (PackageConfig parentConfig : this.parents) {
                retMap.putAll(parentConfig.getAllGlobalResults());
            }
        }
        retMap.putAll(this.getGlobalResultConfigs());
        return retMap;
    }

    public Map<String, Object> getAllInterceptorConfigs() {
        LinkedHashMap<String, Object> retMap = new LinkedHashMap<String, Object>();
        if (!this.parents.isEmpty()) {
            for (PackageConfig parentContext : this.parents) {
                retMap.putAll(parentContext.getAllInterceptorConfigs());
            }
        }
        retMap.putAll(this.getInterceptorConfigs());
        return retMap;
    }

    public Map<String, ResultTypeConfig> getAllResultTypeConfigs() {
        LinkedHashMap<String, ResultTypeConfig> retMap = new LinkedHashMap<String, ResultTypeConfig>();
        if (!this.parents.isEmpty()) {
            for (PackageConfig parentContext : this.parents) {
                retMap.putAll(parentContext.getAllResultTypeConfigs());
            }
        }
        retMap.putAll(this.getResultTypeConfigs());
        return retMap;
    }

    public List<ExceptionMappingConfig> getAllExceptionMappingConfigs() {
        ArrayList<ExceptionMappingConfig> allExceptionMappings = new ArrayList<ExceptionMappingConfig>();
        if (!this.parents.isEmpty()) {
            for (PackageConfig parentContext : this.parents) {
                allExceptionMappings.addAll(parentContext.getAllExceptionMappingConfigs());
            }
        }
        allExceptionMappings.addAll(this.getGlobalExceptionMappingConfigs());
        return allExceptionMappings;
    }

    public String getDefaultInterceptorRef() {
        return this.defaultInterceptorRef;
    }

    public String getDefaultActionRef() {
        return this.defaultActionRef;
    }

    public String getDefaultClassRef() {
        if (this.defaultClassRef == null && !this.parents.isEmpty()) {
            for (PackageConfig parent : this.parents) {
                String parentDefault = parent.getDefaultClassRef();
                if (parentDefault == null) continue;
                return parentDefault;
            }
        }
        return this.defaultClassRef;
    }

    public String getDefaultResultType() {
        return this.defaultResultType;
    }

    public String getFullDefaultInterceptorRef() {
        if (this.defaultInterceptorRef == null && !this.parents.isEmpty()) {
            for (PackageConfig parent : this.parents) {
                String parentDefault = parent.getFullDefaultInterceptorRef();
                if (parentDefault == null) continue;
                return parentDefault;
            }
        }
        return this.defaultInterceptorRef;
    }

    public String getFullDefaultActionRef() {
        if (this.defaultActionRef == null && !this.parents.isEmpty()) {
            for (PackageConfig parent : this.parents) {
                String parentDefault = parent.getFullDefaultActionRef();
                if (parentDefault == null) continue;
                return parentDefault;
            }
        }
        return this.defaultActionRef;
    }

    public String getFullDefaultResultType() {
        if (this.defaultResultType == null && !this.parents.isEmpty()) {
            for (PackageConfig parent : this.parents) {
                String parentDefault = parent.getFullDefaultResultType();
                if (parentDefault == null) continue;
                return parentDefault;
            }
        }
        return this.defaultResultType;
    }

    public Map<String, ResultConfig> getGlobalResultConfigs() {
        return this.globalResultConfigs;
    }

    public Map<String, Object> getInterceptorConfigs() {
        return this.interceptorConfigs;
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public List<PackageConfig> getParents() {
        return new ArrayList<PackageConfig>(this.parents);
    }

    public Map<String, ResultTypeConfig> getResultTypeConfigs() {
        return this.resultTypeConfigs;
    }

    public boolean isNeedsRefresh() {
        return this.needsRefresh;
    }

    public List<ExceptionMappingConfig> getGlobalExceptionMappingConfigs() {
        return this.globalExceptionMappingConfigs;
    }

    public Set<String> getGlobalAllowedMethods() {
        return Collections.unmodifiableSet(this.globalAllowedMethods);
    }

    public boolean isStrictMethodInvocation() {
        return this.strictMethodInvocation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PackageConfig that = (PackageConfig)o;
        if (this.isAbstract != that.isAbstract) {
            return false;
        }
        if (this.needsRefresh != that.needsRefresh) {
            return false;
        }
        if (this.strictMethodInvocation != that.strictMethodInvocation) {
            return false;
        }
        if (this.actionConfigs != null ? !this.actionConfigs.equals(that.actionConfigs) : that.actionConfigs != null) {
            return false;
        }
        if (this.globalResultConfigs != null ? !this.globalResultConfigs.equals(that.globalResultConfigs) : that.globalResultConfigs != null) {
            return false;
        }
        if (this.globalAllowedMethods != null ? !this.globalAllowedMethods.equals(that.globalAllowedMethods) : that.globalAllowedMethods != null) {
            return false;
        }
        if (this.interceptorConfigs != null ? !this.interceptorConfigs.equals(that.interceptorConfigs) : that.interceptorConfigs != null) {
            return false;
        }
        if (this.resultTypeConfigs != null ? !this.resultTypeConfigs.equals(that.resultTypeConfigs) : that.resultTypeConfigs != null) {
            return false;
        }
        if (this.globalExceptionMappingConfigs != null ? !this.globalExceptionMappingConfigs.equals(that.globalExceptionMappingConfigs) : that.globalExceptionMappingConfigs != null) {
            return false;
        }
        if (this.parents != null ? !this.parents.equals(that.parents) : that.parents != null) {
            return false;
        }
        if (this.defaultInterceptorRef != null ? !this.defaultInterceptorRef.equals(that.defaultInterceptorRef) : that.defaultInterceptorRef != null) {
            return false;
        }
        if (this.defaultActionRef != null ? !this.defaultActionRef.equals(that.defaultActionRef) : that.defaultActionRef != null) {
            return false;
        }
        if (this.defaultResultType != null ? !this.defaultResultType.equals(that.defaultResultType) : that.defaultResultType != null) {
            return false;
        }
        if (this.defaultClassRef != null ? !this.defaultClassRef.equals(that.defaultClassRef) : that.defaultClassRef != null) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        return !(this.namespace == null ? that.namespace != null : !this.namespace.equals(that.namespace));
    }

    public int hashCode() {
        int result = this.actionConfigs != null ? this.actionConfigs.hashCode() : 0;
        result = 31 * result + (this.globalResultConfigs != null ? this.globalResultConfigs.hashCode() : 0);
        result = 31 * result + (this.globalAllowedMethods != null ? this.globalAllowedMethods.hashCode() : 0);
        result = 31 * result + (this.interceptorConfigs != null ? this.interceptorConfigs.hashCode() : 0);
        result = 31 * result + (this.resultTypeConfigs != null ? this.resultTypeConfigs.hashCode() : 0);
        result = 31 * result + (this.globalExceptionMappingConfigs != null ? this.globalExceptionMappingConfigs.hashCode() : 0);
        result = 31 * result + (this.parents != null ? this.parents.hashCode() : 0);
        result = 31 * result + (this.defaultInterceptorRef != null ? this.defaultInterceptorRef.hashCode() : 0);
        result = 31 * result + (this.defaultActionRef != null ? this.defaultActionRef.hashCode() : 0);
        result = 31 * result + (this.defaultResultType != null ? this.defaultResultType.hashCode() : 0);
        result = 31 * result + (this.defaultClassRef != null ? this.defaultClassRef.hashCode() : 0);
        result = 31 * result + this.name.hashCode();
        result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        result = 31 * result + (this.isAbstract ? 1 : 0);
        result = 31 * result + (this.needsRefresh ? 1 : 0);
        result = 31 * result + (this.strictMethodInvocation ? 1 : 0);
        return result;
    }

    public String toString() {
        return "PackageConfig: [" + this.name + "] for namespace [" + this.namespace + "] with parents [" + this.parents + "]";
    }

    @Override
    public int compareTo(PackageConfig other) {
        String full = this.namespace + "!" + this.name;
        String otherFull = other.namespace + "!" + other.name;
        return full.compareTo(otherFull);
    }

    @Override
    public Object getInterceptorConfig(String name) {
        return this.getAllInterceptorConfigs().get(name);
    }

    public static class Builder
    implements InterceptorLocator {
        protected PackageConfig target;

        public Builder(String name) {
            this.target = new PackageConfig(name);
        }

        public Builder(PackageConfig config) {
            this.target = new PackageConfig(config);
        }

        public Builder name(String name) {
            this.target.name = name;
            return this;
        }

        public Builder isAbstract(boolean isAbstract) {
            this.target.isAbstract = isAbstract;
            return this;
        }

        public Builder defaultInterceptorRef(String name) {
            this.target.defaultInterceptorRef = name;
            return this;
        }

        public Builder defaultActionRef(String name) {
            this.target.defaultActionRef = name;
            return this;
        }

        public Builder defaultClassRef(String defaultClassRef) {
            this.target.defaultClassRef = defaultClassRef;
            return this;
        }

        public Builder defaultResultType(String defaultResultType) {
            this.target.defaultResultType = defaultResultType;
            return this;
        }

        public Builder namespace(String namespace) {
            this.target.namespace = namespace == null ? "" : namespace;
            return this;
        }

        public Builder needsRefresh(boolean needsRefresh) {
            this.target.needsRefresh = needsRefresh;
            return this;
        }

        public Builder addActionConfig(String name, ActionConfig action) {
            this.target.actionConfigs.put(name, action);
            return this;
        }

        public Builder addParents(List<PackageConfig> parents) {
            for (PackageConfig config : parents) {
                this.addParent(config);
            }
            return this;
        }

        public Builder addGlobalResultConfig(ResultConfig resultConfig) {
            this.target.globalResultConfigs.put(resultConfig.getName(), resultConfig);
            return this;
        }

        public Builder addGlobalResultConfigs(Map<String, ResultConfig> resultConfigs) {
            this.target.globalResultConfigs.putAll(resultConfigs);
            return this;
        }

        public Set<String> getGlobalAllowedMethods() {
            Set<String> allowedMethods = this.target.globalAllowedMethods;
            allowedMethods.addAll(this.getParentsAllowedMethods(this.target.parents));
            return Collections.unmodifiableSet(allowedMethods);
        }

        public Set<String> getParentsAllowedMethods(List<PackageConfig> parents) {
            HashSet<String> allowedMethods = new HashSet<String>();
            for (PackageConfig parent : parents) {
                allowedMethods.addAll(parent.globalAllowedMethods);
                allowedMethods.addAll(this.getParentsAllowedMethods(parent.getParents()));
            }
            return allowedMethods;
        }

        public Builder addGlobalAllowedMethods(Set<String> allowedMethods) {
            this.target.globalAllowedMethods.addAll(allowedMethods);
            return this;
        }

        public Builder addExceptionMappingConfig(ExceptionMappingConfig exceptionMappingConfig) {
            this.target.globalExceptionMappingConfigs.add(exceptionMappingConfig);
            return this;
        }

        public Builder addGlobalExceptionMappingConfigs(List<ExceptionMappingConfig> exceptionMappingConfigs) {
            this.target.globalExceptionMappingConfigs.addAll(exceptionMappingConfigs);
            return this;
        }

        public Builder addInterceptorConfig(InterceptorConfig config) {
            this.target.interceptorConfigs.put(config.getName(), config);
            return this;
        }

        public Builder addInterceptorStackConfig(InterceptorStackConfig config) {
            this.target.interceptorConfigs.put(config.getName(), config);
            return this;
        }

        public Builder addParent(PackageConfig parent) {
            this.target.parents.add(0, parent);
            return this;
        }

        public Builder addResultTypeConfig(ResultTypeConfig config) {
            this.target.resultTypeConfigs.put(config.getName(), config);
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public boolean isNeedsRefresh() {
            return this.target.needsRefresh;
        }

        public String getDefaultClassRef() {
            return this.target.defaultClassRef;
        }

        public String getName() {
            return this.target.name;
        }

        public String getNamespace() {
            return this.target.namespace;
        }

        public String getFullDefaultResultType() {
            return this.target.getFullDefaultResultType();
        }

        public ResultTypeConfig getResultType(String type) {
            return this.target.getAllResultTypeConfigs().get(type);
        }

        @Override
        public Object getInterceptorConfig(String name) {
            return this.target.getAllInterceptorConfigs().get(name);
        }

        public Builder strictMethodInvocation(boolean strict) {
            this.target.strictMethodInvocation = strict;
            return this;
        }

        public boolean isStrictMethodInvocation() {
            return this.target.strictMethodInvocation;
        }

        public PackageConfig build() {
            this.target.actionConfigs = Collections.unmodifiableMap(this.target.actionConfigs);
            this.target.globalResultConfigs = Collections.unmodifiableMap(this.target.globalResultConfigs);
            this.target.interceptorConfigs = Collections.unmodifiableMap(this.target.interceptorConfigs);
            this.target.resultTypeConfigs = Collections.unmodifiableMap(this.target.resultTypeConfigs);
            this.target.globalExceptionMappingConfigs = Collections.unmodifiableList(this.target.globalExceptionMappingConfigs);
            this.target.parents = Collections.unmodifiableList(this.target.parents);
            PackageConfig result = this.target;
            this.target = new PackageConfig(result);
            return result;
        }

        public String toString() {
            return "[BUILDER] " + this.target.toString();
        }
    }
}

