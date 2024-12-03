/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Draft
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.tinymceplugin.rest.entities.PagePermissionData;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class DraftData {
    @XmlElement
    private String title;
    @XmlElement
    private String content;
    @XmlElement
    private String type;
    @XmlElement
    private String syncRev;
    @XmlElement
    private String spaceKey;
    @XmlElement
    private int pageVersion;
    @XmlElement
    private Long pageId;
    @XmlElement
    private Long draftId;
    @XmlElement
    private Long parentPageId;
    @XmlElement
    private PagePermissionData permissions;
    @XmlElement
    private String date;

    public static DraftData create(Draft draft) {
        return new DraftData(draft.getTitle(), null, draft.getDraftType(), draft.getDraftSpaceKey(), draft.getPageVersion(), draft.getPageIdAsLong(), draft.getId(), draft.getProperties().getLongProperty("legacy.draft.parent.id", 0L), null);
    }

    public DraftData() {
    }

    public DraftData(String title, String content, String type, String spaceKey, int pageVersion, Long pageId, Long draftId, Long parentPageId, PagePermissionData permissions) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.spaceKey = spaceKey;
        this.pageVersion = pageVersion;
        this.pageId = pageId;
        this.draftId = draftId;
        this.parentPageId = parentPageId;
        this.permissions = permissions;
    }

    public DraftData(Long pageId, String type) {
        this(null, null, type, null, 0, pageId, null, 0L, null);
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getType() {
        return this.type;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public int getPageVersion() {
        return this.pageVersion;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public Long getDraftId() {
        return this.draftId;
    }

    public Long getParentPageId() {
        return this.parentPageId;
    }

    public PagePermissionData getPermissions() {
        return this.permissions;
    }

    public void setPermissions(PagePermissionData permissions) {
        this.permissions = permissions;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSyncRev() {
        return this.syncRev;
    }
}

