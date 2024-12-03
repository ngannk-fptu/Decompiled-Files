/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.OneToMany
 *  net.java.ao.OneToOne
 *  net.java.ao.Preload
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import java.io.Serializable;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.Preload;

@Preload
public interface ContentBlueprintAo
extends Entity,
Serializable,
PluginBackedBlueprintAo {
    @OneToMany(reverse="getContentBlueprintParent")
    public ContentTemplateRefAo[] getContentTemplates();

    @OneToOne(reverse="getContentBlueprintIndexParent")
    public ContentTemplateRefAo getIndexTemplateRef();

    public String getIndexKey();

    public void setIndexKey(String var1);

    public String getCreateResult();

    public void setCreateResult(String var1);

    public String getIndexTitleI18nKey();

    public void setIndexTitleI18nKey(String var1);

    public String getHowToUseTemplate();

    public void setHowToUseTemplate(String var1);

    public String getSpaceKey();

    public void setSpaceKey(String var1);

    public boolean isIndexDisabled();

    public void setIndexDisabled(boolean var1);
}

