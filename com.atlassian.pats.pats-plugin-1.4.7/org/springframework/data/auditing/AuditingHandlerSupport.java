/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.auditing;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.data.auditing.AuditableBeanWrapper;
import org.springframework.data.auditing.AuditableBeanWrapperFactory;
import org.springframework.data.auditing.Auditor;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.auditing.MappingAuditableBeanWrapperFactory;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AuditingHandlerSupport {
    private static final Log logger = LogFactory.getLog(AuditingHandlerSupport.class);
    private final AuditableBeanWrapperFactory factory;
    private DateTimeProvider dateTimeProvider = CurrentDateTimeProvider.INSTANCE;
    private boolean dateTimeForNow = true;
    private boolean modifyOnCreation = true;

    public AuditingHandlerSupport(PersistentEntities entities) {
        Assert.notNull((Object)entities, (String)"PersistentEntities must not be null!");
        this.factory = new MappingAuditableBeanWrapperFactory(entities);
    }

    public void setDateTimeForNow(boolean dateTimeForNow) {
        this.dateTimeForNow = dateTimeForNow;
    }

    public void setModifyOnCreation(boolean modifyOnCreation) {
        this.modifyOnCreation = modifyOnCreation;
    }

    public void setDateTimeProvider(@Nullable DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider == null ? CurrentDateTimeProvider.INSTANCE : dateTimeProvider;
    }

    protected final boolean isAuditable(Object source) {
        Assert.notNull((Object)source, (String)"Source entity must not be null!");
        return this.factory.getBeanWrapperFor(source).isPresent();
    }

    <T> T markCreated(Auditor auditor, T source) {
        Assert.notNull(source, (String)"Source entity must not be null!");
        return this.touch(auditor, source, true);
    }

    <T> T markModified(Auditor auditor, T source) {
        Assert.notNull(source, (String)"Source entity must not be null!");
        return this.touch(auditor, source, false);
    }

    private <T> T touch(Auditor auditor, T target, boolean isNew) {
        Optional<AuditableBeanWrapper<AuditableBeanWrapper>> wrapper = this.factory.getBeanWrapperFor(target);
        return (T)wrapper.map(it -> {
            Optional<TemporalAccessor> now;
            this.touchAuditor(auditor, (AuditableBeanWrapper<?>)it, isNew);
            Optional<TemporalAccessor> optional = now = this.dateTimeForNow ? this.touchDate((AuditableBeanWrapper<?>)it, isNew) : Optional.empty();
            if (logger.isDebugEnabled()) {
                String defaultedNow = now.map(Object::toString).orElse("not set");
                String defaultedAuditor = auditor.isPresent() ? auditor.toString() : "unknown";
                logger.debug((Object)LogMessage.format((String)"Touched %s - Last modification at %s by %s", (Object)target, (Object)defaultedNow, (Object)defaultedAuditor));
            }
            return it.getBean();
        }).orElse(target);
    }

    private void touchAuditor(Auditor auditor, AuditableBeanWrapper<?> wrapper, boolean isNew) {
        if (!auditor.isPresent()) {
            return;
        }
        Assert.notNull(wrapper, (String)"AuditableBeanWrapper must not be null!");
        if (isNew) {
            wrapper.setCreatedBy(auditor.getValue());
        }
        if (!isNew || this.modifyOnCreation) {
            wrapper.setLastModifiedBy(auditor.getValue());
        }
    }

    private Optional<TemporalAccessor> touchDate(AuditableBeanWrapper<?> wrapper, boolean isNew) {
        Assert.notNull(wrapper, (String)"AuditableBeanWrapper must not be null!");
        Optional<TemporalAccessor> now = this.dateTimeProvider.getNow();
        Assert.notNull(now, () -> String.format("Now must not be null! Returned by: %s!", this.dateTimeProvider.getClass()));
        now.filter(__ -> isNew).ifPresent(wrapper::setCreatedDate);
        now.filter(__ -> !isNew || this.modifyOnCreation).ifPresent(wrapper::setLastModifiedDate);
        return now;
    }
}

