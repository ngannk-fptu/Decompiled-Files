/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.google.errorprone.annotations.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class WikiToXhtmlConversionData {
    @XmlElement
    private final String wiki;
    @XmlElement
    private final long entityId;
    @XmlElement
    private final String spaceKey;
    @XmlElement
    private final boolean suppressFirstParagraph;
    @XmlElement
    private final String contextType;

    public WikiToXhtmlConversionData() {
        this.wiki = null;
        this.entityId = 0L;
        this.spaceKey = null;
        this.suppressFirstParagraph = false;
        this.contextType = null;
    }

    public WikiToXhtmlConversionData(String wiki, long entityId, String spaceKey, boolean suppressFirstParagraph, String contextType) {
        this.wiki = wiki;
        this.entityId = entityId;
        this.spaceKey = spaceKey;
        this.suppressFirstParagraph = suppressFirstParagraph;
        this.contextType = contextType;
    }

    public String getWiki() {
        return this.wiki;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean shouldSuppressFirstParagraph() {
        return this.suppressFirstParagraph;
    }

    public String getContextType() {
        return this.contextType;
    }
}

