/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.ChangedValue
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.api.model.audit.ChangedValue;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;

@Deprecated
public class ChangedValueEntity {
    private String name;
    private String oldValue;
    private String newValue;
    private AuditRecordEntity parentRecord;
    private long id;

    ChangedValueEntity() {
    }

    public ChangedValueEntity(String name, String oldValue, String newValue, AuditRecordEntity parentRecord) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.parentRecord = parentRecord;
        this.id = 0L;
    }

    public static ChangedValueEntity from(ChangedValueEntity other, AuditRecordEntity parentRecord) {
        return new ChangedValueEntity(other.getName(), other.getOldValue(), other.getNewValue(), parentRecord);
    }

    public static ChangedValueEntity fromChangedValue(ChangedValue changedValue, AuditRecordEntity parentRecord) {
        return new ChangedValueEntity(changedValue.getName(), changedValue.getOldValue(), changedValue.getNewValue(), parentRecord);
    }

    public ChangedValue toChangedValue() {
        return ChangedValue.builder().name(this.getOrEmpty(this.name)).oldValue(this.getOrEmpty(this.oldValue)).newValue(this.getOrEmpty(this.newValue)).build();
    }

    public String getOldValue() {
        return this.oldValue;
    }

    void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getNewValue() {
        return this.newValue;
    }

    void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public AuditRecordEntity getParentRecord() {
        return this.parentRecord;
    }

    void setParentRecord(AuditRecordEntity parentRecord) {
        this.parentRecord = parentRecord;
    }

    public long getId() {
        return this.id;
    }

    void setId(long id) {
        this.id = id;
    }

    private String getOrEmpty(String s) {
        return s == null ? "" : s;
    }
}

