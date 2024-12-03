/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  net.java.ao.RawEntity
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.impl.AbstractAoManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.java.ao.RawEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="contentTemplateRefManager")
public class DefaultContentTemplateRefManager
extends AbstractAoManager<ContentTemplateRef, ContentTemplateRefAo>
implements ContentTemplateRefManager {
    @Autowired
    public DefaultContentTemplateRefManager(@ComponentImport ActiveObjects activeObjects) {
        super(activeObjects, ContentTemplateRefAo.class);
    }

    @Override
    @Nonnull
    protected ContentTemplateRefAo internalCreateAo(@Nonnull ContentTemplateRef original) {
        ContentTemplateRefAo ao = (ContentTemplateRefAo)this.helperAoManager.createWithUuid();
        this.copyPropertiesIntoAo(ao, original, true);
        ao.save();
        return ao;
    }

    @Override
    @Nonnull
    protected ContentTemplateRefAo internalUpdateAo(@Nonnull ContentTemplateRef object) {
        ContentTemplateRefAo ao = (ContentTemplateRefAo)this.internalGetAoById(object.getId());
        if (ao == null) {
            String error = String.format("Content Template with UUID %s not found", object.getId());
            throw new IllegalStateException(error);
        }
        this.copyPropertiesIntoAo(ao, object, false);
        ao.save();
        return ao;
    }

    private void copyPropertiesIntoAo(@Nonnull ContentTemplateRefAo ao, @Nonnull ContentTemplateRef original, boolean isCreate) {
        if (isCreate) {
            ao.setPluginModuleKey(original.getModuleCompleteKey());
        }
        long templateId = original.getTemplateId();
        ao.setTemplateId(templateId);
        ao.setPluginClone(original.isPluginClone());
        ao.setI18nNameKey(original.getI18nNameKey());
        this.setParent(ao, original.getChildren());
    }

    @Override
    protected void internalDeleteAo(@Nonnull ContentTemplateRefAo ao) {
        ContentTemplateRefAo[] children = ao.getChildTemplateRefs();
        if (children != null) {
            for (ContentTemplateRefAo childAo : children) {
                this.internalDeleteAo(childAo);
            }
            this.activeObjects.delete((RawEntity[])children);
        }
    }

    @Override
    @Nonnull
    protected ContentTemplateRef build(@Nonnull ContentTemplateRefAo ao) {
        ContentTemplateRefAo[] childTemplateRefAos;
        boolean isIndex = true;
        ContentBlueprintAo parentAo = ao.getContentBlueprintIndexParent();
        if (parentAo == null) {
            parentAo = ao.getContentBlueprintParent();
            isIndex = false;
        }
        ContentBlueprint parent = parentAo != null ? this.build(parentAo) : null;
        ContentTemplateRef ref = new ContentTemplateRef(UUID.fromString(ao.getUuid()), ao.getTemplateId(), ao.getPluginModuleKey(), ao.getI18nNameKey(), ao.isPluginClone(), parent);
        if (parent != null) {
            if (isIndex) {
                parent.setIndexPageTemplateRef(ref);
            } else {
                parent.setContentTemplateRefs((List<ContentTemplateRef>)ImmutableList.of((Object)ref));
            }
        }
        for (ContentTemplateRefAo childTemplateRefAo : childTemplateRefAos = ao.getChildTemplateRefs()) {
            ref.addChildTemplateRef(this.build(childTemplateRefAo));
        }
        return ref;
    }

    @Override
    private ContentBlueprint build(ContentBlueprintAo ao) {
        ContentBlueprint result = new ContentBlueprint();
        result.setCreateResult(ao.getCreateResult());
        result.setModuleCompleteKey(ao.getPluginModuleKey());
        result.setI18nNameKey(ao.getI18nNameKey());
        result.setIndexKey(ao.getIndexKey());
        result.setSpaceKey(ao.getSpaceKey());
        return result;
    }

    private void setParent(ContentTemplateRefAo parent, List<ContentTemplateRef> children) {
        for (ContentTemplateRef child : children) {
            ContentTemplateRefAo childAo = (ContentTemplateRefAo)this.internalGetAoById(child.getId());
            if (childAo == null) {
                throw new RuntimeException("Template with UUID '" + child.getId() + "' unrecognized");
            }
            childAo.setParent(parent);
            childAo.save();
            this.setParent(childAo, child.getChildren());
        }
    }
}

