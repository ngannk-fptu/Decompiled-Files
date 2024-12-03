/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.dom4j.Element
 *  org.osgi.framework.Version
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.plugin.ModuleCompleteKey;
import org.dom4j.Element;
import org.osgi.framework.Version;

public abstract class VersionedResourceNode {
    protected final Version version;
    protected final String base;
    protected final String name;
    protected final String type;
    protected final VersionedResourceContext context;
    protected final VersionedResourceNode parent;

    protected VersionedResourceNode(Element descriptor, VersionedResourceContext context) {
        this(descriptor, context, null);
    }

    protected VersionedResourceNode(Element descriptor, VersionedResourceContext context, VersionedResourceNode parent) {
        this.version = Version.parseVersion((String)descriptor.attributeValue("version"));
        this.base = descriptor.attributeValue("base");
        this.name = descriptor.attributeValue("name");
        this.type = descriptor.attributeValue("type");
        this.context = context;
        this.parent = parent;
    }

    protected static Iterable<Element> children(Element element, String name) {
        return element.elements(name);
    }

    protected VersionedResourceNode root() {
        return this.parent != null ? this.parent.root() : this;
    }

    public Version version() {
        return Version.emptyVersion.equals((Object)this.version) && this.parent != null ? this.parent.version() : this.version;
    }

    public String base() {
        return this.base != null ? this.base : (this.parent != null ? this.parent.base() : "");
    }

    public String name() {
        return this.name != null && this.parent != null ? this.parent.name() + "-" + this.name : (this.parent != null ? this.parent.name() : "");
    }

    public String type() {
        return this.type != null ? this.type : (this.parent != null ? this.parent.type() : "soy");
    }

    public ModuleCompleteKey key() {
        return new ModuleCompleteKey(this.context.getPlugin().getKey(), this.name() + "-" + this.version());
    }
}

