/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.ModuleCompleteKeyDeserializer;
import com.atlassian.confluence.plugins.createcontent.rest.ModuleCompleteKeySerializer;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
public class SpaceBlueprint
extends PluginBackedBlueprint {
    private UUID homePageId;
    private DialogWizard dialogWizard;
    private List<ModuleCompleteKey> promotedBps;
    private String category;

    private SpaceBlueprint() {
    }

    public SpaceBlueprint(UUID id, String moduleCompleteKey, String i18nNameKey, boolean pluginClone, List<ModuleCompleteKey> promotedBps, @Nullable DialogWizard dialogWizard, String category) {
        super(id, moduleCompleteKey, i18nNameKey, pluginClone);
        this.promotedBps = promotedBps;
        this.dialogWizard = dialogWizard;
        this.category = category;
    }

    @Nullable
    @XmlElement
    public UUID getHomePageId() {
        return this.homePageId;
    }

    @Nullable
    @XmlElement
    public DialogWizard getDialogWizard() {
        return this.dialogWizard;
    }

    @XmlElement
    @JsonSerialize(contentUsing=ModuleCompleteKeySerializer.class)
    @JsonDeserialize(contentUsing=ModuleCompleteKeyDeserializer.class)
    public List<ModuleCompleteKey> getPromotedBps() {
        return this.promotedBps;
    }

    @Nullable
    @XmlElement
    public String getCategory() {
        return this.category;
    }

    public void setHomePageId(UUID homePageId) {
        this.homePageId = homePageId;
    }

    public void setPromotedBps(List<ModuleCompleteKey> promotedBps) {
        this.promotedBps = promotedBps;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

