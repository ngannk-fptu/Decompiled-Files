/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializationConfig$Feature
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.audit.rest.v1.utils;

import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.rest.model.AuditAttributeJson;
import com.atlassian.audit.rest.model.AuditAuthorJson;
import com.atlassian.audit.rest.model.AuditEntityJson;
import com.atlassian.audit.rest.model.AuditResourceJson;
import com.atlassian.audit.rest.model.AuditTypeJson;
import com.atlassian.audit.rest.model.ChangedValueJson;
import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class AuditEntitySerializer {
    private static ObjectMapper mapper = new ObjectMapper();

    private AuditEntitySerializer() {
    }

    @Nonnull
    public static String serialize(@Nonnull AuditEntity auditEntity) {
        Objects.requireNonNull(auditEntity, "auditEntity");
        mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        try {
            return mapper.writeValueAsString((Object)auditEntity);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public static ChangedValueJson toJson(ChangedValue changedValue) {
        return new ChangedValueJson(changedValue.getKey(), changedValue.getI18nKey(), changedValue.getFrom(), changedValue.getTo());
    }

    public static AuditEntityJson toJson(AuditEntity entity, TimeZone timeZone) {
        return new AuditEntityJson(entity.getTimestamp().atZone(timeZone.toZoneId()), AuditEntitySerializer.toJson(entity.getAuthor()), AuditEntitySerializer.toJson(entity.getAuditType()), entity.getAffectedObjects().stream().map(o -> AuditEntitySerializer.toJson(o)).collect(Collectors.toList()), entity.getChangedValues().stream().map(AuditEntitySerializer::toJson).collect(Collectors.toList()), entity.getSource(), entity.getSystem(), entity.getNode(), entity.getMethod(), entity.getExtraAttributes().stream().map(AuditEntitySerializer::toJson).sorted(Comparator.comparing(AuditAttributeJson::getName)).collect(Collectors.toList()));
    }

    public static AuditAttributeJson toJson(AuditAttribute auditAttribute) {
        return new AuditAttributeJson(auditAttribute.getNameI18nKey(), auditAttribute.getName(), auditAttribute.getValue());
    }

    public static AuditAuthorJson toJson(AuditAuthor author) {
        return new AuditAuthorJson(author.getName() != null ? author.getName() : AuditAuthor.UNKNOWN_AUTHOR.getName(), author.getType(), author.getId(), author.getUri(), "");
    }

    public static AuditTypeJson toJson(AuditType auditType) {
        return new AuditTypeJson(auditType.getCategoryI18nKey(), auditType.getCategory(), auditType.getActionI18nKey(), auditType.getAction());
    }

    public static AuditResourceJson toJson(AuditResource auditResource) {
        return new AuditResourceJson(auditResource.getName(), auditResource.getType(), auditResource.getUri(), auditResource.getId());
    }
}

