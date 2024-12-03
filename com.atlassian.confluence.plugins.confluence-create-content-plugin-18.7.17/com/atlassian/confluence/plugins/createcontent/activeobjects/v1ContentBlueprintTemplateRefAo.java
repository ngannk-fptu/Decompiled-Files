/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table(value="CBT_REF")
public interface v1ContentBlueprintTemplateRefAo
extends Entity {
    public ContentBlueprintAo getContentBlueprintAo();

    public void setContentBlueprintAo(ContentBlueprintAo var1);

    public ContentTemplateRefAo getContentTemplateRefAo();

    public void setContentTemplateRefAo(ContentTemplateRefAo var1);
}

