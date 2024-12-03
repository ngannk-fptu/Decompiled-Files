/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.ParseProperties;
import org.apache.tools.ant.property.PropertyExpander;

public class ResolvePropertyMap
implements GetProperty {
    private final Set<String> seen = new HashSet<String>();
    private final ParseProperties parseProperties;
    private final GetProperty master;
    private Map<String, Object> map;
    private String prefix;
    private boolean prefixValues = false;
    private boolean expandingLHS = true;

    public ResolvePropertyMap(Project project, GetProperty master, Collection<PropertyExpander> expanders) {
        this.master = master;
        this.parseProperties = new ParseProperties(project, expanders, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getProperty(String name) {
        if (this.seen.contains(name)) {
            throw new BuildException("Property %s was circularly defined.", name);
        }
        try {
            Object masterValue;
            String fullKey = name;
            if (this.prefix != null && (this.expandingLHS || this.prefixValues)) {
                fullKey = this.prefix + name;
            }
            if ((masterValue = this.master.getProperty(fullKey)) != null) {
                Object object = masterValue;
                return object;
            }
            this.seen.add(name);
            String recursiveCallKey = name;
            if (this.prefix != null && !this.expandingLHS && !this.prefixValues) {
                recursiveCallKey = this.prefix + name;
            }
            this.expandingLHS = false;
            Object object = this.parseProperties.parseProperties((String)this.map.get(recursiveCallKey));
            return object;
        }
        finally {
            this.seen.remove(name);
        }
    }

    @Deprecated
    public void resolveAllProperties(Map<String, Object> map) {
        this.resolveAllProperties(map, null, false);
    }

    @Deprecated
    public void resolveAllProperties(Map<String, Object> map, String prefix) {
        this.resolveAllProperties(map, null, false);
    }

    public void resolveAllProperties(Map<String, Object> map, String prefix, boolean prefixValues) {
        this.map = map;
        this.prefix = prefix;
        this.prefixValues = prefixValues;
        for (String key : map.keySet()) {
            this.expandingLHS = true;
            Object result = this.getProperty(key);
            String value = result == null ? "" : result.toString();
            map.put(key, value);
        }
    }
}

