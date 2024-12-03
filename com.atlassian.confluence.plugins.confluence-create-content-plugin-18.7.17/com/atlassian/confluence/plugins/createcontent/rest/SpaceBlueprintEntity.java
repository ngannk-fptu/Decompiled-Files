/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceBlueprintEntity {
    @XmlElement
    private SpaceBlueprint spaceBlueprint;
    @XmlAttribute
    private UUID homePageId;
    @XmlAttribute
    private Long homePageTemplateId;

    private SpaceBlueprintEntity() {
    }

    public SpaceBlueprintEntity(@Nonnull SpaceBlueprint spaceBlueprint, @Nonnull UUID homePageId) {
        this.spaceBlueprint = spaceBlueprint;
        this.homePageId = homePageId;
    }

    public SpaceBlueprintEntity(@Nonnull SpaceBlueprint spaceBlueprint, long homePageTemplateId) {
        this.spaceBlueprint = spaceBlueprint;
        this.homePageTemplateId = homePageTemplateId;
    }

    @Nonnull
    SpaceBlueprint getSpaceBlueprint() {
        return this.spaceBlueprint;
    }

    @Nullable
    UUID getHomePageId() {
        return this.homePageId;
    }

    @Nullable
    Long getHomePageTemplateId() {
        return this.homePageTemplateId;
    }
}

