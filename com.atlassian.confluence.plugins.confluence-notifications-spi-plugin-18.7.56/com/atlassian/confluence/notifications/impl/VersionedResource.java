/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.confluence.notifications.impl.VersionedResourceDependency;
import com.atlassian.confluence.notifications.impl.VersionedResourceNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.dom4j.Element;

public class VersionedResource
extends VersionedResourceNode {
    protected final String location;
    protected final Iterable<VersionedResourceDependency> dependencies;

    protected VersionedResource(Element resourceDescriptor, VersionedResourceContext context, VersionedResourceNode parent) {
        super(resourceDescriptor, context, parent);
        Preconditions.checkNotNull((Object)this.name, (String)"Expected a 'name' attribute on element [%s].", (Object)resourceDescriptor);
        this.location = resourceDescriptor.attributeValue("location");
        ImmutableList.Builder dependenciesBuilder = ImmutableList.builder();
        for (Element dependencyDescriptor : VersionedResource.children(resourceDescriptor, "dependency")) {
            dependenciesBuilder.add((Object)new VersionedResourceDependency(dependencyDescriptor, context, this));
        }
        this.dependencies = dependenciesBuilder.build();
    }
}

