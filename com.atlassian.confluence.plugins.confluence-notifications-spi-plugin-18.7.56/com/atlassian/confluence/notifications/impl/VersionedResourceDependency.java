/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.confluence.notifications.impl.VersionedResourceNode;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import org.dom4j.Element;

public class VersionedResourceDependency
extends VersionedResourceNode {
    protected final ModuleCompleteKey key;

    protected VersionedResourceDependency(Element dependencyDescriptor, VersionedResourceContext context, VersionedResourceNode parent) {
        super(dependencyDescriptor, context, parent);
        Preconditions.checkNotNull((Object)this.name);
        StringBuilder moduleCompleteKeyBuilder = new StringBuilder();
        String from = dependencyDescriptor.attributeValue("from");
        if (from != null) {
            ModuleCompleteKey fromKey = null;
            try {
                fromKey = new ModuleCompleteKey(from);
                moduleCompleteKeyBuilder.append(fromKey.getPluginKey());
            }
            catch (IllegalArgumentException e) {
                moduleCompleteKeyBuilder.append(context.getPlugin().getKey());
            }
            moduleCompleteKeyBuilder.append(":");
            if (fromKey == null) {
                moduleCompleteKeyBuilder.append(from);
            } else {
                moduleCompleteKeyBuilder.append(fromKey.getModuleKey());
            }
            moduleCompleteKeyBuilder.append("-");
            moduleCompleteKeyBuilder.append(this.name);
        } else {
            moduleCompleteKeyBuilder.append(context.getPlugin().getKey());
            moduleCompleteKeyBuilder.append(":");
            moduleCompleteKeyBuilder.append(this.name());
        }
        moduleCompleteKeyBuilder.append("-");
        moduleCompleteKeyBuilder.append(this.version());
        this.key = new ModuleCompleteKey(moduleCompleteKeyBuilder.toString());
    }

    @Override
    public String name() {
        return this.root().name() + "-" + this.name;
    }

    @Override
    public ModuleCompleteKey key() {
        return this.key;
    }
}

