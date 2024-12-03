/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.ManyToMany
 *  net.java.ao.Preload
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.v1ContentBlueprintTemplateRefAo;
import net.java.ao.ManyToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Preload
@Table(value="CONTENT_BLUEPRINT_AO")
public interface v1ContentBlueprintAo
extends ContentBlueprintAo {
    public v1ContentBlueprintAo getParent();

    public void setParent(v1ContentBlueprintAo var1);

    @Override
    @ManyToMany(value=v1ContentBlueprintTemplateRefAo.class, reverse="getContentBlueprintAo", through="getContentTemplateRefAo")
    public ContentTemplateRefAo[] getContentTemplates();

    @Override
    public ContentTemplateRefAo getIndexTemplateRef();
}

