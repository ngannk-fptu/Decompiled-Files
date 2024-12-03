/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.AffectedObject
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.api.model.audit.AffectedObject;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import java.util.Objects;

@Deprecated
public class AffectedObjectEntity {
    private String name;
    private String type;
    private AuditRecordEntity parentRecord;
    private long id;

    AffectedObjectEntity() {
    }

    public AffectedObjectEntity(String name, String type, AuditRecordEntity parentRecord) {
        this.name = name;
        this.type = type;
        this.parentRecord = parentRecord;
        this.id = 0L;
    }

    public static AffectedObjectEntity from(AffectedObjectEntity other, AuditRecordEntity parentRecord) {
        return new AffectedObjectEntity(other.getName(), other.getType(), parentRecord);
    }

    public static AffectedObjectEntity fromAffectedObject(AffectedObject affectedObject, AuditRecordEntity parentRecord) {
        return new AffectedObjectEntity(affectedObject.getName(), affectedObject.getObjectType(), parentRecord);
    }

    public AffectedObject toAffectedObject() {
        return AffectedObject.builder().name(this.getOrEmpty(this.name)).objectType(this.getOrEmpty(this.type)).build();
    }

    public String getType() {
        return this.type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
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

    public boolean equals(Object o) {
        if (!(o instanceof AffectedObjectEntity)) {
            return false;
        }
        AffectedObjectEntity other = (AffectedObjectEntity)o;
        return Objects.equals(other.name, this.name) && Objects.equals(other.type, this.type);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    private String getOrEmpty(String s) {
        return s == null ? "" : s;
    }
}

