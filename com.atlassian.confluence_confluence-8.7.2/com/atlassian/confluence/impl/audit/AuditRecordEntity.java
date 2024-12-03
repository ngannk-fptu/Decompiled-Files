/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.impl.audit.AffectedObjectEntity;
import com.atlassian.confluence.impl.audit.ChangedValueEntity;
import com.atlassian.sal.api.user.UserKey;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
public class AuditRecordEntity {
    private String authorName;
    private String authorFullName;
    private UserKey authorKey;
    private String remoteAddress;
    private String summary;
    private String description;
    private String category;
    private String objectName;
    private String objectType;
    private boolean sysAdmin;
    private List<ChangedValueEntity> changedValues;
    private Set<AffectedObjectEntity> associatedObjects;
    private String searchString;
    private long id = 0L;
    private Instant creationDate;

    AuditRecordEntity() {
    }

    public static AuditRecordEntity from(AuditRecordEntity other) {
        AuditRecordEntity copied = new AuditRecordEntity();
        copied.setAuthorFullName(other.getAuthorFullName());
        copied.setAuthorName(other.getAuthorName());
        copied.setAuthorKey(other.getAuthorKey());
        copied.setRemoteAddress(other.getRemoteAddress());
        copied.setCreationDate(other.getCreationDate());
        copied.setSummary(other.getSummary());
        copied.setDescription(other.getDescription());
        copied.setCategory(other.getCategory());
        copied.setObjectName(other.getObjectName());
        copied.setObjectType(other.getObjectType());
        copied.setSysAdmin(other.isSysAdmin());
        copied.setChangedValues(other.getChangedValues().stream().map(changedValue -> ChangedValueEntity.from(changedValue, copied)).collect(Collectors.toList()));
        copied.setAssociatedObjects(other.getAssociatedObjects().stream().map(associatedObject -> AffectedObjectEntity.from(associatedObject, copied)).collect(Collectors.toSet()));
        copied.setId(other.getId());
        copied.setSearchString(other.getSearchString());
        return copied;
    }

    public long getId() {
        return this.id;
    }

    void setId(long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public String getSummary() {
        return this.summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    public String getObjectName() {
        return this.objectName;
    }

    void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return this.objectType;
    }

    void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public List<ChangedValueEntity> getChangedValues() {
        return this.changedValues;
    }

    void setChangedValues(List<ChangedValueEntity> changedValues) {
        this.changedValues = changedValues;
    }

    public Set<AffectedObjectEntity> getAssociatedObjects() {
        return this.associatedObjects;
    }

    void setAssociatedObjects(Set<AffectedObjectEntity> associatedObjects) {
        this.associatedObjects = associatedObjects;
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public boolean isSysAdmin() {
        return this.sysAdmin;
    }

    void setSysAdmin(boolean isSysAdmin) {
        this.sysAdmin = isSysAdmin;
    }

    public String getSearchString() {
        return this.searchString;
    }

    void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getAuthorFullName() {
        return this.authorFullName;
    }

    void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public UserKey getAuthorKey() {
        return this.authorKey;
    }

    void setAuthorKey(UserKey authorKey) {
        this.authorKey = authorKey;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}

