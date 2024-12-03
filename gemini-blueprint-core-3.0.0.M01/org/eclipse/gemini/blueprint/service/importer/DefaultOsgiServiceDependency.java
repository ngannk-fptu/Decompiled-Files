/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.osgi.framework.Filter;
import org.springframework.util.ObjectUtils;

public class DefaultOsgiServiceDependency
implements OsgiServiceDependency {
    private final String beanName;
    private final Filter filter;
    private final boolean mandatoryService;
    private final String toString;
    private final int hashCode;

    public DefaultOsgiServiceDependency(String beanName, Filter filter, boolean mandatoryService) {
        this.beanName = beanName;
        this.filter = filter;
        this.mandatoryService = mandatoryService;
        this.toString = "DependencyService[Name=" + (beanName != null ? beanName : "null") + "][Filter=" + filter + "][Mandatory=" + mandatoryService + "]";
        int result = 17;
        result = 37 * result + DefaultOsgiServiceDependency.class.hashCode();
        result = 37 * result + (filter == null ? 0 : filter.hashCode());
        result = 37 * result + (beanName == null ? 0 : beanName.hashCode());
        this.hashCode = result = 37 * result + (mandatoryService ? 0 : 1);
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public Filter getServiceFilter() {
        return this.filter;
    }

    @Override
    public boolean isMandatory() {
        return this.mandatoryService;
    }

    public String toString() {
        return this.toString;
    }

    public boolean equals(Object obj) {
        if (obj instanceof OsgiServiceDependency) {
            OsgiServiceDependency other = (OsgiServiceDependency)obj;
            return other.isMandatory() == this.mandatoryService && this.filter.equals((Object)other.getServiceFilter()) && ObjectUtils.nullSafeEquals((Object)this.beanName, (Object)other.getBeanName());
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }
}

