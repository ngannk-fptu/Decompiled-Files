/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.google.common.collect.Maps
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.AffectedObjectsSerializer;
import com.atlassian.audit.ao.dao.AttributesSerializer;
import com.atlassian.audit.ao.dao.ChangedValuesSerializer;
import com.atlassian.audit.ao.dao.SearchTokenizer;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AuditEntityMapper {
    private final ChangedValuesSerializer changedValueSerializer;
    private final AttributesSerializer attributesSerializer;
    private final AffectedObjectsSerializer affectedObjectsSerializer;

    public AuditEntityMapper(ChangedValuesSerializer changedValueSerializer, AttributesSerializer attributesSerializer, AffectedObjectsSerializer affectedObjectsSerializer) {
        this.changedValueSerializer = Objects.requireNonNull(changedValueSerializer);
        this.attributesSerializer = Objects.requireNonNull(attributesSerializer);
        this.affectedObjectsSerializer = Objects.requireNonNull(affectedObjectsSerializer);
    }

    public Map<String, Object> map(AuditEntity auditEntity) {
        HashMap values = Maps.newHashMap();
        AuditType auditType = auditEntity.getAuditType();
        values.put("LEVEL", auditType.getLevel().name());
        values.put("AREA", auditType.getArea().name());
        values.put("ACTION_T_KEY", auditType.getActionI18nKey());
        values.put("ACTION", auditType.getAction());
        values.put("CATEGORY_T_KEY", auditType.getCategoryI18nKey());
        values.put("CATEGORY", auditType.getCategory());
        values.put("ENTITY_TIMESTAMP", auditEntity.getTimestamp().toEpochMilli());
        values.put("METHOD", auditEntity.getMethod());
        values.put("SYSTEM_INFO", auditEntity.getSystem());
        values.put("NODE", auditEntity.getNode());
        values.put("SOURCE", auditEntity.getSource());
        values.put("CHANGE_VALUES", this.changedValueSerializer.serialize(auditEntity.getChangedValues()));
        values.put("ATTRIBUTES", this.attributesSerializer.serialize(auditEntity.getExtraAttributes()));
        List affectedObjects = auditEntity.getAffectedObjects();
        values.put("RESOURCES", this.affectedObjectsSerializer.serialize(affectedObjects));
        int k = 0;
        for (AuditResource x : affectedObjects) {
            switch (k) {
                case 0: {
                    values.put("PRIMARY_RESOURCE_ID", x.getId());
                    values.put("PRIMARY_RESOURCE_TYPE", x.getType());
                    break;
                }
                case 1: {
                    values.put("SECONDARY_RESOURCE_ID", x.getId());
                    values.put("SECONDARY_RESOURCE_TYPE", x.getType());
                    break;
                }
                case 2: {
                    values.put("RESOURCE_ID_3", x.getId());
                    values.put("RESOURCE_TYPE_3", x.getType());
                    break;
                }
                case 3: {
                    values.put("RESOURCE_ID_4", x.getId());
                    values.put("RESOURCE_TYPE_4", x.getType());
                    break;
                }
                case 4: {
                    values.put("RESOURCE_ID_5", x.getId());
                    values.put("RESOURCE_TYPE_5", x.getType());
                    break;
                }
            }
            ++k;
        }
        AuditAuthor author = auditEntity.getAuthor();
        values.put("USER_ID", author.getId());
        values.put("USER_NAME", author.getName());
        values.put("USER_TYPE", author.getType());
        String searchString = this.createSearchString(auditType, author, affectedObjects, auditEntity.getSource());
        values.put("SEARCH_STRING", searchString);
        return values;
    }

    private String createSearchString(AuditType auditType, AuditAuthor author, Iterable<AuditResource> affectedObjects, String source) {
        SearchTokenizer searchTokenizer = new SearchTokenizer();
        affectedObjects.forEach(obj -> searchTokenizer.put(obj.getName()));
        return searchTokenizer.put(auditType.getActionI18nKey()).put(auditType.getCategoryI18nKey()).put(auditType.getAction()).put(auditType.getCategory()).put(author.getName()).put(source).getTokenizedString();
    }
}

