/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package org.apache.felix.bundlerepository;

import java.util.Dictionary;
import org.apache.felix.bundlerepository.FilterImpl;
import org.apache.felix.bundlerepository.MapToDictionary;
import org.osgi.framework.Filter;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Requirement;

public class RequirementImpl
implements Requirement {
    private String m_name = null;
    private boolean m_extend = false;
    private boolean m_multiple = false;
    private boolean m_optional = false;
    private Filter m_filter = null;
    private String m_comment = null;
    private MapToDictionary m_dict = new MapToDictionary(null);

    public synchronized String getName() {
        return this.m_name;
    }

    public synchronized void setName(String name) {
        this.m_name = name;
    }

    public synchronized String getFilter() {
        return this.m_filter.toString();
    }

    public synchronized void setFilter(String filter) {
        this.m_filter = new FilterImpl(filter);
    }

    public synchronized boolean isSatisfied(Capability capability) {
        this.m_dict.setSourceMap(capability.getProperties());
        return this.m_filter.match((Dictionary)this.m_dict);
    }

    public synchronized boolean isExtend() {
        return this.m_extend;
    }

    public synchronized void setExtend(String s) {
        this.m_extend = Boolean.valueOf(s);
    }

    public synchronized boolean isMultiple() {
        return this.m_multiple;
    }

    public synchronized void setMultiple(String s) {
        this.m_multiple = Boolean.valueOf(s);
    }

    public synchronized boolean isOptional() {
        return this.m_optional;
    }

    public synchronized void setOptional(String s) {
        this.m_optional = Boolean.valueOf(s);
    }

    public synchronized String getComment() {
        return this.m_comment;
    }

    public synchronized void addText(String s) {
        this.m_comment = s;
    }

    public synchronized boolean equals(Object o) {
        if (o instanceof Requirement) {
            Requirement r = (Requirement)o;
            return this.m_name.equals(r.getName()) && this.m_optional == r.isOptional() && this.m_multiple == r.isMultiple() && this.m_filter.toString().equals(r.getFilter()) && (this.m_comment == r.getComment() || this.m_comment != null && this.m_comment.equals(r.getComment()));
        }
        return false;
    }

    public synchronized int hashCode() {
        return this.m_filter.toString().hashCode();
    }
}

