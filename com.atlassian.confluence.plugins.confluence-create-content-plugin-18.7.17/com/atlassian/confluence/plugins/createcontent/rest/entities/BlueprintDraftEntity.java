/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlueprintDraftEntity {
    @XmlElement
    private long draftId;
    @XmlElement
    private String title;
    @XmlElement
    private String spaceKey;
    @XmlElement
    private String url;

    public BlueprintDraftEntity() {
    }

    @Deprecated
    public BlueprintDraftEntity(Draft draft, String baseUrl) {
        this((ContentEntityObject)draft, baseUrl);
    }

    public BlueprintDraftEntity(ContentEntityObject contentDraft, String baseUrl) {
        this.draftId = contentDraft.getId();
        this.spaceKey = DraftsTransitionHelper.getSpaceKey((ContentEntityObject)contentDraft);
        this.title = contentDraft.getTitle();
        this.url = baseUrl + String.format("/plugins/createcontent/draft-createpage.action?draftId=%s", this.draftId);
    }

    public long getDraftId() {
        return this.draftId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }
}

