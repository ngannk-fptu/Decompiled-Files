/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAuditListener {
    private static final Logger log = LoggerFactory.getLogger(AbstractAuditListener.class);
    private final AuditHandlerService auditHandlerService;
    private final AuditService service;
    protected final AuditHelper auditHelper;
    protected final StandardAuditResourceTypes resourceTypes;
    private final AuditingContext auditingContext;

    public AbstractAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        this.auditHandlerService = auditHandlerService;
        this.service = service;
        this.auditHelper = auditHelper;
        this.resourceTypes = resourceTypes;
        this.auditingContext = auditingContext;
    }

    protected ChangedValue newChangedValue(String nameKey, Object oldValue, Object newValue) {
        return ChangedValue.fromI18nKeys((String)AuditHelper.buildChangedValueTextKey(nameKey)).from(Objects.toString(oldValue, null)).to(Objects.toString(newValue, null)).build();
    }

    protected AuditHandlerService getAuditHandlerService() {
        return this.auditHandlerService;
    }

    protected void save(Supplier<AuditEvent> eventSupplier) {
        this.saveIfPresent(() -> Optional.of((AuditEvent)eventSupplier.get()));
    }

    protected void saveIfPresent(Supplier<Optional<AuditEvent>> eventSupplier) {
        String summaryKey = eventSupplier.get().map(AuditEvent::getActionI18nKey).orElse(null);
        if (!this.auditingContext.skipAuditing(summaryKey)) {
            try {
                eventSupplier.get().ifPresent(arg_0 -> ((AuditService)this.service).audit(arg_0));
            }
            catch (Exception e) {
                log.error("Error processing auditing event", (Throwable)e);
            }
        }
    }

    protected String getOnOff(boolean isOn) {
        return isOn ? this.auditHelper.translate("common.words.on.small") : this.auditHelper.translate("common.words.off.small");
    }

    protected String getTranslatedYesNoString(boolean isYes) {
        return isYes ? this.auditHelper.translate("yes.name") : this.auditHelper.translate("no.name");
    }

    @Deprecated
    protected AuditEvent.Builder auditEventBuilder(String actionKey, String categoryKey, CoverageArea area, CoverageLevel level) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)area, (CoverageLevel)level, (String)categoryKey, (String)actionKey).build());
    }

    protected <T> List<ChangedValue> calculateChangedValues(T oldEntity, T newEntity) {
        return this.getAuditHandlerService().handle(Optional.ofNullable(oldEntity), Optional.ofNullable(newEntity));
    }

    protected AuditResource buildResourceWithoutId(String name, String type) {
        return this.buildResource(name, type, null);
    }

    protected AuditResource buildResource(String name, String type, long id) {
        return this.buildResource(name, type, String.valueOf(id));
    }

    protected AuditResource buildResource(String name, String type, @Nullable String id) {
        return AuditResource.builder((String)Optional.ofNullable(name).orElse("Undefined"), (String)type).id(id).build();
    }
}

