/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.AffectedObjectsSerializer;
import com.atlassian.audit.ao.dao.AttributesSerializer;
import com.atlassian.audit.ao.dao.ChangedValuesSerializer;
import com.atlassian.audit.ao.dao.entity.AoAuditEntity;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AoAuditEntityMapper {
    private final ChangedValuesSerializer changedValueSerializer;
    private final AttributesSerializer attributesSerializer;
    private final AffectedObjectsSerializer affectedObjectsSerializer;

    public AoAuditEntityMapper(ChangedValuesSerializer changedValueSerializer, AttributesSerializer attributesSerializer, AffectedObjectsSerializer affectedObjectsSerializer) {
        this.changedValueSerializer = Objects.requireNonNull(changedValueSerializer);
        this.attributesSerializer = Objects.requireNonNull(attributesSerializer);
        this.affectedObjectsSerializer = Objects.requireNonNull(affectedObjectsSerializer);
    }

    public AuditEntity map(AoAuditEntity ao) {
        AuditAuthor author = AuditAuthor.builder().id(Optional.ofNullable(ao.getUserId()).orElse(AuditAuthor.UNKNOWN_AUTHOR.getId())).name(ao.getUsername()).type(ao.getUserType()).build();
        return AuditEntity.builder((AuditType)this.extractType(ao)).id(ao.getId()).source(ao.getSource()).timestamp(Instant.ofEpochMilli(ao.getTimestamp())).affectedObjects(this.affectedObjectsSerializer.deserialize(ao.getResources())).changedValues(this.changedValueSerializer.deserialize(ao.getChangedValues()).stream().sorted(Comparator.comparing(ChangedValue::getKey)).collect(Collectors.toList())).extraAttributes((Collection)this.attributesSerializer.deserialize(ao.getAttributes()).stream().sorted(Comparator.comparing(AuditAttribute::getName)).collect(Collectors.toList())).author(author).method(ao.getMethod()).system(ao.getSystem()).node(ao.getNode()).build();
    }

    private AuditType extractType(AoAuditEntity ao) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.valueOf((String)ao.getArea()), (CoverageLevel)CoverageLevel.valueOf((String)ao.getLevel()), (String)(ao.getCategoryI18nKey() == null ? ao.getCategory() : ao.getCategoryI18nKey()), (String)(ao.getActionI18nKey() == null ? ao.getAction() : ao.getActionI18nKey())).withCategoryTranslation(ao.getCategory()).withActionTranslation(ao.getAction()).build();
    }
}

