/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.descriptor.web.Injectable;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.NamingResources;

public class ResourceBase
implements Serializable,
Injectable {
    private static final long serialVersionUID = 1L;
    private String description = null;
    private String name = null;
    private String type = null;
    private String lookupName = null;
    private final Map<String, Object> properties = new HashMap<String, Object>();
    private final List<InjectionTarget> injectionTargets = new ArrayList<InjectionTarget>();
    private NamingResources resources = null;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLookupName() {
        return this.lookupName;
    }

    public void setLookupName(String lookupName) {
        if (lookupName == null || lookupName.length() == 0) {
            this.lookupName = null;
            return;
        }
        this.lookupName = lookupName;
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public void removeProperty(String name) {
        this.properties.remove(name);
    }

    public Iterator<String> listProperties() {
        return this.properties.keySet().iterator();
    }

    @Override
    public void addInjectionTarget(String injectionTargetName, String jndiName) {
        InjectionTarget target = new InjectionTarget(injectionTargetName, jndiName);
        this.injectionTargets.add(target);
    }

    @Override
    public List<InjectionTarget> getInjectionTargets() {
        return this.injectionTargets;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.description == null ? 0 : this.description.hashCode());
        result = 31 * result + (this.injectionTargets == null ? 0 : this.injectionTargets.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.properties == null ? 0 : this.properties.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        result = 31 * result + (this.lookupName == null ? 0 : this.lookupName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ResourceBase other = (ResourceBase)obj;
        if (this.description == null ? other.description != null : !this.description.equals(other.description)) {
            return false;
        }
        if (this.injectionTargets == null ? other.injectionTargets != null : !this.injectionTargets.equals(other.injectionTargets)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        if (this.properties == null ? other.properties != null : !this.properties.equals(other.properties)) {
            return false;
        }
        if (this.type == null ? other.type != null : !this.type.equals(other.type)) {
            return false;
        }
        return !(this.lookupName == null ? other.lookupName != null : !this.lookupName.equals(other.lookupName));
    }

    public NamingResources getNamingResources() {
        return this.resources;
    }

    public void setNamingResources(NamingResources resources) {
        this.resources = resources;
    }
}

