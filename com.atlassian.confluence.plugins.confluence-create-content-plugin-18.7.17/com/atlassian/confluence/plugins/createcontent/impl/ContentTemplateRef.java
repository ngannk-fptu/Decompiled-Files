/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class ContentTemplateRef
extends PluginBackedBlueprint {
    private long templateId;
    private List<ContentTemplateRef> children = new ArrayList<ContentTemplateRef>();
    private ContentBlueprint parent;

    private ContentTemplateRef() {
    }

    public ContentTemplateRef(@Nullable UUID id, long templateId, @Nonnull String moduleCompleteKey, @Nonnull String i18nNameKey, boolean pluginClone, @Nullable ContentBlueprint parent) {
        super(id, moduleCompleteKey, i18nNameKey, pluginClone);
        this.templateId = templateId;
        this.parent = parent;
    }

    @XmlElement
    public long getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    @Nullable
    @XmlTransient
    public ContentBlueprint getParent() {
        return this.parent;
    }

    public void addChildTemplateRef(@Nonnull ContentTemplateRef child) {
        this.children.add(child);
    }

    @XmlElement
    @Nonnull
    public List<ContentTemplateRef> getChildren() {
        return this.children;
    }

    @Nonnull
    public String toString() {
        return this.getTemplateId() + ":" + this.getModuleCompleteKey();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentTemplateRef that = (ContentTemplateRef)o;
        return this.getId().equals(that.getId());
    }

    public int hashCode() {
        return this.getId().hashCode();
    }
}

