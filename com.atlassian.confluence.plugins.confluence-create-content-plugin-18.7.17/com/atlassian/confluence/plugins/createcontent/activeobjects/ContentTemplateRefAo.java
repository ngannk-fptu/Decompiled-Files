/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.OneToMany
 *  net.java.ao.Preload
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import java.io.Serializable;
import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.Table;

@Indexes(value={@Index(name="ref_uuid_idx", methodNames={"getUuid"})})
@Preload
@Table(value="C_TEMPLATE_REF")
public interface ContentTemplateRefAo
extends Serializable,
PluginBackedBlueprintAo {
    public long getTemplateId();

    public void setTemplateId(long var1);

    @Accessor(value="CB_PARENT")
    public ContentBlueprintAo getContentBlueprintParent();

    @Mutator(value="CB_PARENT")
    public void setContentBlueprintParent(ContentBlueprintAo var1);

    @Accessor(value="CB_INDEX_PARENT")
    public ContentBlueprintAo getContentBlueprintIndexParent();

    @Mutator(value="CB_INDEX_PARENT")
    public void setContentBlueprintIndexParent(ContentBlueprintAo var1);

    public ContentTemplateRefAo getParent();

    public void setParent(ContentTemplateRefAo var1);

    @OneToMany(reverse="getParent")
    public ContentTemplateRefAo[] getChildTemplateRefs();
}

