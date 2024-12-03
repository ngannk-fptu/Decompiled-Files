/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import java.io.Serializable;
import javax.annotation.Nullable;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.StringLength;

@Preload
public interface SpaceBlueprintAo
extends Entity,
Serializable,
PluginBackedBlueprintAo {
    public void setHomePage(ContentTemplateRefAo var1);

    public String getCategory();

    public void setCategory(String var1);

    @Nullable
    public ContentTemplateRefAo getHomePage();

    @StringLength(value=-1)
    public void setPromotedBps(String var1);

    public String getPromotedBps();
}

