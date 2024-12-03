/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ao.Reconciliations;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.CsvFriendly;
import java.util.Date;

public class Reconciliation
implements CsvFriendly {
    private long contentId;
    private String eventType;
    private String ancestor;
    private String revision;
    private String trigger;
    private Date inserted;

    public static Reconciliation fromEntity(Reconciliations entity) {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setContentId(entity.getContentId());
        reconciliation.setEventType(entity.getEventType());
        reconciliation.setAncestor(entity.getAncestor());
        reconciliation.setRevision(entity.getRevision());
        reconciliation.setTrigger(entity.getTrigger());
        reconciliation.setInserted(entity.getInserted());
        return reconciliation;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAncestor() {
        return this.ancestor;
    }

    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public String getRevision() {
        return this.revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTrigger() {
        return this.trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
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
        sb.append(this.getContentId()).append(",").append(this.getEventType()).append(",").append(this.getAncestor()).append(",").append(this.getRevision()).append(",").append(this.getTrigger()).append(",").append(this.getInserted()).append("\n");
        return sb.toString();
    }
}

