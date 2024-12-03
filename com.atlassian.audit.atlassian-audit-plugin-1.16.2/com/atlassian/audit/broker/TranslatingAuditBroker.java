/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEntity$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.service.TranslationService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class TranslatingAuditBroker
implements InternalAuditBroker {
    private final TranslationService translationService;
    private final InternalAuditBroker delegatedBroker;

    public TranslatingAuditBroker(InternalAuditBroker delegatedBroker, TranslationService translationService) {
        this.delegatedBroker = delegatedBroker;
        this.translationService = translationService;
    }

    @Override
    public void audit(@Nonnull AuditEntity entity) {
        this.delegatedBroker.audit(this.translate(entity));
    }

    private AuditEntity translate(AuditEntity entity) {
        return new AuditEntity.Builder(entity).type(this.translate(entity.getAuditType())).changedValues(this.translate(entity.getChangedValues())).extraAttributes(this.translate(entity.getExtraAttributes())).build();
    }

    private List<ChangedValue> translate(List<ChangedValue> changedValues) {
        return changedValues.stream().map(c -> ChangedValue.fromI18nKeys((String)c.getI18nKey()).withKeyTranslation(this.translate(c.getI18nKey())).from(c.getFrom()).to(c.getTo()).build()).collect(Collectors.toList());
    }

    private Collection<AuditAttribute> translate(Collection<AuditAttribute> extraAttributes) {
        return extraAttributes.stream().map(a -> AuditAttribute.fromI18nKeys((String)a.getNameI18nKey(), (String)a.getValue()).withNameTranslation(this.translate(a.getNameI18nKey())).build()).collect(Collectors.toList());
    }

    private AuditType translate(AuditType auditType) {
        return AuditType.builder((AuditType)auditType).withCategoryTranslation(this.translate(auditType.getCategoryI18nKey())).withActionTranslation(this.translate(auditType.getActionI18nKey())).build();
    }

    private String translate(String key) {
        return this.translationService.getSiteLocaleText(key);
    }
}

