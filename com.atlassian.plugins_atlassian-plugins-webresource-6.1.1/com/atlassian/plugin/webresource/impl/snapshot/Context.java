/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class Context
extends Bundle {
    @Deprecated
    public Context(Snapshot snapshot, String key, List<String> dependencies, List<String> moduleDependencies, Date updatedAt, String version, boolean isTransformable) {
        this(snapshot, key, dependencies, updatedAt, version, isTransformable);
    }

    public Context(Snapshot snapshot, String key, List<String> dependencies, Date updatedAt, String version, boolean isTransformable) {
        super(snapshot, key, dependencies, updatedAt, version, isTransformable);
    }

    @Override
    public LinkedHashMap<String, Resource> getResources(RequestCache cache) {
        LinkedHashMap<String, Resource> resources = new LinkedHashMap<String, Resource>();
        return resources;
    }
}

