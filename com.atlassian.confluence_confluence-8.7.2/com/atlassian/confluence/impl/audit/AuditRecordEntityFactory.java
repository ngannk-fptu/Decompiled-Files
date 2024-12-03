/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.impl.audit.AffectedObjectEntity;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import com.atlassian.confluence.impl.audit.AuditSearchUtils;
import com.atlassian.confluence.impl.audit.ChangedValueEntity;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.user.UserKey;
import java.time.Instant;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class AuditRecordEntityFactory {
    public static final int SEARCH_STRING_LENGTH_LIMIT = 4000;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public AuditRecordEntityFactory(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public AuditRecordEntity fromAuditRecord(AuditRecord auditRecord) {
        return this.updateSearchString(this.getAuditRecordEntity(auditRecord));
    }

    public AuditRecordEntity fromAuditRecordWithI18n(AuditRecord auditRecord) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        AuditRecordEntity translated = this.getAuditRecordEntity(auditRecord);
        translated.setCategory(i18NBean.getText(translated.getCategory()));
        translated.setSummary(i18NBean.getText(translated.getSummary()));
        translated.setDescription(i18NBean.getText(translated.getDescription()));
        translated.setObjectType(i18NBean.getText(translated.getObjectType()));
        translated.setAssociatedObjects(translated.getAssociatedObjects().stream().map(o -> new AffectedObjectEntity(o.getName(), i18NBean.getText(o.getType()), translated)).collect(Collectors.toSet()));
        translated.setChangedValues(translated.getChangedValues().stream().map(v -> new ChangedValueEntity(i18NBean.getText(v.getName()), v.getOldValue(), v.getNewValue(), translated)).collect(Collectors.toList()));
        return this.updateSearchString(translated);
    }

    private AuditRecordEntity getAuditRecordEntity(AuditRecord auditRecord) {
        UserKey userKey = auditRecord.getAuthor().optionalUserKey().orElse(new UserKey(""));
        String userName = auditRecord.getAuthor().getUsername();
        String userFullName = auditRecord.getAuthor().getDisplayName();
        AuditRecordEntity entity = new AuditRecordEntity();
        entity.setCreationDate(Instant.ofEpochMilli(auditRecord.getCreationDate().getMillis()));
        entity.setAuthorName(userName);
        entity.setAuthorFullName(userFullName);
        entity.setAuthorKey(userKey);
        entity.setRemoteAddress(auditRecord.getRemoteAddress());
        entity.setCategory(auditRecord.getCategory());
        entity.setSummary(auditRecord.getSummary());
        entity.setSysAdmin(auditRecord.isSysAdmin());
        entity.setDescription(auditRecord.getDescription());
        entity.setObjectName(auditRecord.getAffectedObject().getName());
        entity.setObjectType(auditRecord.getAffectedObject().getObjectType());
        entity.setAssociatedObjects(auditRecord.getAssociatedObjects().stream().map(associatedObject -> AffectedObjectEntity.fromAffectedObject(associatedObject, entity)).collect(Collectors.toSet()));
        entity.setChangedValues(auditRecord.getChangedValues().stream().map(changedValue -> ChangedValueEntity.fromChangedValue(changedValue, entity)).collect(Collectors.toList()));
        return entity;
    }

    private AuditRecordEntity updateSearchString(AuditRecordEntity entity) {
        entity.setSearchString(StringUtils.left((String)AuditSearchUtils.computeSearchString(entity), (int)4000));
        return entity;
    }
}

