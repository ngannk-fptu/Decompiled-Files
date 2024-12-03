/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResource;
import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.confluence.notifications.impl.VersionedResourceDependency;
import com.atlassian.confluence.notifications.impl.VersionedResourceNode;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.LinkedHashMap;
import java.util.Map;
import org.dom4j.Element;

public class VersionedResourceCompilation
extends VersionedResourceNode {
    protected final Iterable<VersionedResourceDependency> dependencies;
    protected final Iterable<VersionedResource> resources;

    protected VersionedResourceCompilation(Element compilationDescriptor, VersionedResourceContext context, VersionedResourceNode parent) {
        this(compilationDescriptor, context, parent, null);
    }

    protected VersionedResourceCompilation(Element compilationDescriptor, VersionedResourceContext context, VersionedResourceNode parent, VersionedResourceCompilation extendedCompilation) {
        super(compilationDescriptor, context, parent);
        LinkedHashMap<ModuleCompleteKey, VersionedResourceDependency> dependencies = new LinkedHashMap<ModuleCompleteKey, VersionedResourceDependency>();
        for (Element element : VersionedResourceCompilation.children(compilationDescriptor, "dependency")) {
            VersionedResourceDependency dependency = new VersionedResourceDependency(element, context, this);
            Preconditions.checkArgument((dependencies.put(dependency.key(), dependency) == null ? 1 : 0) != 0, (String)"Key [%s] for dependency descriptor [%s] under compilation descriptor [%s] is not unique.", (Object)dependency.key().getCompleteKey(), (Object)element, (Object)compilationDescriptor);
        }
        if (extendedCompilation == null) {
            this.dependencies = dependencies.values();
        } else {
            LinkedHashMap<ModuleCompleteKey, VersionedResourceDependency> extendedDependencies = new LinkedHashMap<ModuleCompleteKey, VersionedResourceDependency>();
            for (VersionedResourceDependency extendedDependency : extendedCompilation.dependencies) {
                extendedDependencies.put(extendedDependency.key(), extendedDependency);
            }
            extendedDependencies.putAll(dependencies);
            this.dependencies = extendedDependencies.values();
        }
        LinkedHashMap<String, VersionedResource> resources = new LinkedHashMap<String, VersionedResource>();
        for (Element resourceDescriptor : VersionedResourceCompilation.children(compilationDescriptor, "resource")) {
            VersionedResource resource = new VersionedResource(resourceDescriptor, context, this);
            Preconditions.checkArgument((resources.put(resource.name, resource) == null ? 1 : 0) != 0, (String)"Name for [%s] resource descriptor [%s] under compilation descriptor [%s] is not unique.", (Object)resource.name, (Object)resourceDescriptor, (Object)compilationDescriptor);
        }
        if (extendedCompilation == null) {
            this.resources = resources.values();
        } else {
            LinkedHashMap<String, VersionedResource> linkedHashMap = new LinkedHashMap<String, VersionedResource>();
            for (VersionedResource extendedResource : extendedCompilation.resources) {
                linkedHashMap.put(extendedResource.name, extendedResource);
            }
            linkedHashMap.putAll(resources);
            this.resources = linkedHashMap.values();
        }
        Preconditions.checkArgument((!Iterables.isEmpty(this.dependencies) || !Iterables.isEmpty(this.resources) ? 1 : 0) != 0, (String)"Compilation descriptor [%s] does not contain any dependency or resource elements.", (Object)compilationDescriptor);
    }

    @Override
    public String name() {
        return this.parent.name();
    }

    private void checkedPut(Map<ModuleCompleteKey, VersionedResourceNode> resources, VersionedResourceNode resource) {
        VersionedResourceNode otherResource = resources.get(resource.key());
        Preconditions.checkNotNull((Object)otherResource, (String)"", (Object)resource.key(), (Object)this.version());
        resources.put(resource.key(), resource);
    }
}

