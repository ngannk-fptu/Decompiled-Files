/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEntity$Builder
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.api.model.audit.AffectedObject
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.audit.AuditRecord$Builder
 *  com.atlassian.confluence.api.model.audit.ChangedValue
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.internal.audit;

import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.api.model.audit.AffectedObject;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.ChangedValue;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.impl.audit.AffectedObjectEntity;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import com.atlassian.confluence.impl.audit.AuditRecordEntityFactory;
import com.atlassian.confluence.impl.audit.ChangedValueEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class AuditFormatConverter {
    public static final String USER_TYPE = "user";
    public static final String UNKNOWN_AUDIT_METHOD = "Unknown";
    private final AuditRecordEntityFactory entityFactory;
    private final ConfluenceUserResolver confluenceUserResolver;

    public AuditFormatConverter(AuditRecordEntityFactory entityFactory, ConfluenceUserResolver confluenceUserResolver) {
        this.entityFactory = entityFactory;
        this.confluenceUserResolver = confluenceUserResolver;
    }

    public AuditEvent toAuditEvent(AuditRecord record, CoverageArea area, CoverageLevel level) {
        return this.toAuditEvent(this.entityFactory.fromAuditRecord(record), area, level);
    }

    public AuditEvent toAuditEvent(AuditRecordEntity dbObject, CoverageArea area, CoverageLevel level) {
        AuditEvent.Builder builder = AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)area, (CoverageLevel)level, (String)dbObject.getCategory(), (String)dbObject.getSummary()).withCategoryTranslation(dbObject.getCategory()).withActionTranslation(dbObject.getSummary()).build()).changedValues(dbObject.getChangedValues().stream().map(AuditFormatConverter::toNewChangedValue).collect(Collectors.toList()));
        return builder.affectedObjects(AuditFormatConverter.createAffectedObjectsList(dbObject)).build();
    }

    public AuditEntity toAuditEntity(AuditRecordEntity dbObject, CoverageArea defaultArea, CoverageLevel defaultLevel) {
        AuditEntity.Builder builder = AuditEntity.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)defaultArea, (CoverageLevel)defaultLevel, (String)dbObject.getCategory(), (String)dbObject.getSummary()).withCategoryTranslation(dbObject.getCategory()).withActionTranslation(dbObject.getSummary()).build()).timestamp(dbObject.getCreationDate()).author(AuditAuthor.builder().type(USER_TYPE).id(Optional.ofNullable(dbObject.getAuthorKey()).map(UserKey::getStringValue).orElse("")).name(dbObject.getAuthorFullName()).build()).changedValues(dbObject.getChangedValues().stream().map(AuditFormatConverter::toNewChangedValue).collect(Collectors.toList())).source(dbObject.getRemoteAddress()).method(UNKNOWN_AUDIT_METHOD);
        return builder.affectedObjects(AuditFormatConverter.createAffectedObjectsList(dbObject)).build();
    }

    private static List<AuditResource> createAffectedObjectsList(AuditRecordEntity dbObject) {
        if (StringUtils.isNotBlank((CharSequence)dbObject.getObjectName()) && StringUtils.isNotBlank((CharSequence)dbObject.getObjectType())) {
            Stream<AuditResource> firstAffectedObject = Stream.of(AuditResource.builder((String)dbObject.getObjectName(), (String)dbObject.getObjectType()).build());
            Stream<AuditResource> associatedObjects = dbObject.getAssociatedObjects().stream().map(AuditFormatConverter::toNewAuditResource);
            return Stream.concat(firstAffectedObject, associatedObjects).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public AuditRecordEntity toDatabaseObject(AuditEntity entity) {
        return this.entityFactory.fromAuditRecord(this.toAuditRecord(entity));
    }

    public AuditRecord toAuditRecord(AuditEntity entity) {
        ImmutableList affectedObjects = ImmutableList.copyOf((Collection)entity.getAffectedObjects());
        String authorUserKey = entity.getAuthor().getId();
        String username = Optional.ofNullable(this.confluenceUserResolver.getUserByKey(new UserKey(authorUserKey))).map(ConfluenceUser::getLowerName).orElse(null);
        AuditRecord.Builder builder = AuditRecord.builder().author(new User(null, username, entity.getAuthor().getName(), authorUserKey)).remoteAddress(entity.getSource()).createdDate(new DateTime(entity.getTimestamp().toEpochMilli())).description("").summary(entity.getAuditType().getAction()).category(entity.getAuditType().getCategory()).isSysAdmin(false).changedValues(entity.getChangedValues().stream().map(AuditFormatConverter::toLegacyChangedValue).collect(Collectors.toList()));
        return this.addAffectedObjectIfPresentAndBuild(builder, (List<AuditResource>)affectedObjects);
    }

    private AuditRecord addAffectedObjectIfPresentAndBuild(AuditRecord.Builder builder, List<AuditResource> affectedObjects) {
        if (affectedObjects != null && !affectedObjects.isEmpty()) {
            builder.affectedObject(AuditFormatConverter.toLegacyAffectedObject(affectedObjects.get(0))).associatedObjects(affectedObjects.stream().skip(1L).map(AuditFormatConverter::toLegacyAffectedObject).collect(Collectors.toSet()));
        }
        return builder.build();
    }

    private static AffectedObject toLegacyAffectedObject(AuditResource resource) {
        return AffectedObject.builder().objectType(resource.getType()).name(resource.getName()).build();
    }

    private static AuditResource toNewAuditResource(AffectedObjectEntity dbObject) {
        return AuditResource.builder((String)Optional.ofNullable(dbObject.getName()).orElse(""), (String)dbObject.getType()).build();
    }

    public static com.atlassian.audit.entity.ChangedValue toNewChangedValue(ChangedValueEntity dbObject) {
        return com.atlassian.audit.entity.ChangedValue.fromI18nKeys((String)dbObject.getName()).withKeyTranslation(dbObject.getName()).from(dbObject.getOldValue()).to(dbObject.getNewValue()).build();
    }

    private static ChangedValue toLegacyChangedValue(com.atlassian.audit.entity.ChangedValue entity) {
        return ChangedValue.builder().name(entity.getKey()).oldValue(Optional.ofNullable(entity.getFrom()).orElse("")).newValue(Optional.ofNullable(entity.getTo()).orElse("")).build();
    }
}

