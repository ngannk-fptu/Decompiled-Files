/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.plugins.createcontent.rest.ModuleCompleteKeyDeserializer;
import com.atlassian.confluence.plugins.createcontent.rest.ModuleCompleteKeySerializer;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
public class BlueprintPageEntity {
    @XmlElement
    String spaceKey;
    @XmlElement
    Long parentPageId;
    @XmlElement
    @JsonSerialize(contentUsing=ModuleCompleteKeySerializer.class)
    @JsonDeserialize(contentUsing=ModuleCompleteKeyDeserializer.class)
    ModuleCompleteKey moduleCompleteKey;
    @XmlElement
    Map<String, Object> context;

    private BlueprintPageEntity() {
    }

    public BlueprintPageEntity(String spaceKey, Long parentPageId, ModuleCompleteKey moduleCompleteKey, Map<String, Object> context) {
        this.spaceKey = spaceKey;
        this.parentPageId = parentPageId;
        this.moduleCompleteKey = moduleCompleteKey;
        this.context = context;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public Long getParentPageId() {
        return this.parentPageId;
    }

    public ModuleCompleteKey getModuleCompleteKey() {
        return this.moduleCompleteKey;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}

