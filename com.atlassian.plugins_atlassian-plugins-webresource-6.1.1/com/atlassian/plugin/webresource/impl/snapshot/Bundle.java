/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class Bundle {
    private final String key;
    private final String version;
    private final boolean isTransformable;
    private final List<String> dependencies;
    private final Date updatedAt;
    protected Snapshot snapshot;

    public Bundle(Snapshot snapshot, String key, List<String> dependencies, Date updatedAt, String version, boolean isTransformable) {
        this.snapshot = snapshot;
        this.key = key;
        this.dependencies = dependencies;
        this.updatedAt = updatedAt;
        this.version = version;
        this.isTransformable = isTransformable;
    }

    public String getKey() {
        return this.key;
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isTransformable() {
        return this.isTransformable;
    }

    public boolean hasLegacyConditions() {
        return this.snapshot.webResourcesWithLegacyConditions.contains(this);
    }

    public Snapshot getSnapshot() {
        return this.snapshot;
    }

    public boolean hasLegacyTransformers() {
        return this.snapshot.webResourcesWithLegacyTransformers.contains(this);
    }

    public LinkedHashMap<String, Resource> getResources(RequestCache cache) {
        return new LinkedHashMap<String, Resource>();
    }

    public LinkedHashMap<String, Jsonable> getData() {
        return new LinkedHashMap<String, Jsonable>();
    }

    public CachedCondition getCondition() {
        return null;
    }

    public CachedTransformers getTransformers() {
        return null;
    }

    public TransformerParameters getTransformerParameters() {
        return null;
    }

    public Set<String> getLocationResourceTypesFor(String nameType) {
        return new HashSet<String>();
    }

    @Deprecated
    public boolean isMinificationEnabled() {
        return !this.snapshot.webResourcesWithDisabledMinification.contains(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.key.equals(((Bundle)o).key);
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "{" + this.key + (this.dependencies.isEmpty() ? "" : ", dependencies: " + StringUtils.join(this.dependencies, (String)",")) + "}";
    }
}

