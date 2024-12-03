/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ao.SynchronyRequests;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.CsvFriendly;
import java.util.Date;

public class SynchronyRequest
implements CsvFriendly {
    private long contentId;
    private String type;
    private String url;
    private String payload;
    private boolean successful;
    private Date inserted;

    public static SynchronyRequest fromEntity(SynchronyRequests entity) {
        SynchronyRequest synchronyRequest = new SynchronyRequest();
        synchronyRequest.setContentId(entity.getContentId());
        synchronyRequest.setType(entity.getType());
        synchronyRequest.setUrl(entity.getUrl());
        synchronyRequest.setPayload(entity.getPayload());
        synchronyRequest.setSuccessful(entity.isSuccessful());
        synchronyRequest.setInserted(entity.getInserted());
        return synchronyRequest;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Date getInserted() {
        return this.inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    @Override
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getContentId()).append(",").append(this.getType()).append(",").append(this.getUrl()).append(",").append(this.getPayload()).append(",").append(this.isSuccessful()).append(",").append(this.getInserted()).append("\n");
        return sb.toString();
    }
}

