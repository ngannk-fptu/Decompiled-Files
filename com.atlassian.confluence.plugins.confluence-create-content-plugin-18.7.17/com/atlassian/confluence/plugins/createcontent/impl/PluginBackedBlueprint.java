/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.impl.UuidBacked;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;

public abstract class PluginBackedBlueprint
extends UuidBacked {
    private String moduleCompleteKey;
    private String i18nNameKey;
    private boolean pluginClone;

    protected PluginBackedBlueprint() {
    }

    protected PluginBackedBlueprint(UUID id, String moduleCompleteKey, String i18nNameKey, boolean pluginClone) {
        super(id);
        this.moduleCompleteKey = moduleCompleteKey;
        this.i18nNameKey = i18nNameKey;
        this.pluginClone = pluginClone;
    }

    @XmlElement
    public String getModuleCompleteKey() {
        return this.moduleCompleteKey;
    }

    @XmlElement
    public String getI18nNameKey() {
        return this.i18nNameKey;
    }

    @XmlElement
    public boolean isPluginClone() {
        return this.pluginClone;
    }

    public void setModuleCompleteKey(String moduleCompleteKey) {
        this.moduleCompleteKey = moduleCompleteKey;
    }

    public void setI18nNameKey(String i18nNameKey) {
        this.i18nNameKey = i18nNameKey;
    }

    public void setPluginClone(boolean pluginClone) {
        this.pluginClone = pluginClone;
    }
}

