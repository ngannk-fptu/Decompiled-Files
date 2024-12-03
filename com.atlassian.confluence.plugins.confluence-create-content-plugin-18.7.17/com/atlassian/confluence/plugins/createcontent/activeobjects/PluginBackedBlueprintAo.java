/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.plugins.createcontent.activeobjects;

import com.atlassian.confluence.plugins.createcontent.activeobjects.UuidBackedAo;
import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.schema.Table;

@Table(value="PluginBackedBp")
public interface PluginBackedBlueprintAo
extends UuidBackedAo {
    public void setPluginModuleKey(String var1);

    public String getPluginModuleKey();

    @Mutator(value="NAME")
    public void setI18nNameKey(String var1);

    @Accessor(value="NAME")
    public String getI18nNameKey();

    public void setPluginClone(boolean var1);

    public boolean isPluginClone();
}

