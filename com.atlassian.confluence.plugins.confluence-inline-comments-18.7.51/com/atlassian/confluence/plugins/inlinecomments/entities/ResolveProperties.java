/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.json.json.JsonObject
 *  com.atlassian.confluence.json.jsonator.GsonJsonator
 *  com.atlassian.confluence.json.jsonator.Gsonable
 *  javax.xml.bind.annotation.XmlElement
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.GsonJsonator;
import com.atlassian.confluence.json.jsonator.Gsonable;
import com.atlassian.confluence.plugins.inlinecomments.utils.ResolveCommentConverter;
import javax.xml.bind.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ResolveProperties
implements Gsonable {
    public static final String STATUS_PROP = "status";
    public static final String LAST_MODIFIER_PROP = "status-lastmodifier";
    public static final String LAST_MODIFIDATE_PROP = "status-lastmoddate";
    public static final String RESOLVED_STATUS = "resolved";
    public static final String REOPEN_STATUS = "reopened";
    public static final String DANGLING_STATUS = "dangling";
    public static final String RESOLVED_PROP = "resolved";
    public static final String RESOLVED_TIME_PROP = "resolved-time";
    public static final String RESOLVED_USER = "resolved-user";
    public static final String RESOLVED_BY_DANGLING = "resolved-by-dangling";
    @XmlElement
    private boolean resolved;
    @XmlElement
    private long resolvedTime;
    @XmlElement
    private String resolvedFriendlyDate;
    @XmlElement
    private String resolvedUser;
    @XmlElement
    private boolean resolvedByDangling;

    public boolean getResolved() {
        return this.resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @JsonIgnore
    public void setResolved(String status) {
        this.resolved = ResolveCommentConverter.isResolved(status);
    }

    public long getResolvedTime() {
        return this.resolvedTime;
    }

    public void setResolvedTime(long resolvedTime) {
        this.resolvedTime = resolvedTime;
    }

    public String getResolvedFriendlyDate() {
        return this.resolvedFriendlyDate;
    }

    public void setResolvedFriendlyDate(String resolvedFriendlyDate) {
        this.resolvedFriendlyDate = resolvedFriendlyDate;
    }

    public String getResolvedUser() {
        return this.resolvedUser;
    }

    public void setResolvedUser(String resolvedUser) {
        this.resolvedUser = resolvedUser;
    }

    @JsonIgnore
    public void setResolvedByDangling(String status) {
        this.resolvedByDangling = ResolveCommentConverter.isResolvedByDangling(status);
    }

    public void setResolvedByDangling(boolean isResolvedDangling) {
        this.resolvedByDangling = isResolvedDangling;
    }

    public String getJsonObjectSerialize() {
        JsonObject resolveJson = new JsonObject();
        GsonJsonator gsonJsonator = new GsonJsonator();
        resolveJson.setProperty("resolveProperties", gsonJsonator.convert((Gsonable)this));
        return resolveJson.serialize();
    }

    public boolean isResolvedByDangling() {
        return this.resolvedByDangling;
    }
}

