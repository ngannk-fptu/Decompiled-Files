/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.data.auditing;

import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.auditing.AuditingHandlerSupport;
import org.springframework.data.auditing.Auditor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;

public class AuditingHandler
extends AuditingHandlerSupport
implements InitializingBean {
    private static final Log logger = LogFactory.getLog(AuditingHandler.class);
    private Optional<AuditorAware<?>> auditorAware;

    @Deprecated
    public AuditingHandler(MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {
        this(PersistentEntities.of(mappingContext));
    }

    public AuditingHandler(PersistentEntities entities) {
        super(entities);
        Assert.notNull((Object)entities, (String)"PersistentEntities must not be null!");
        this.auditorAware = Optional.empty();
    }

    public void setAuditorAware(AuditorAware<?> auditorAware) {
        Assert.notNull(auditorAware, (String)"AuditorAware must not be null!");
        this.auditorAware = Optional.of(auditorAware);
    }

    public <T> T markCreated(T source) {
        Assert.notNull(source, (String)"Entity must not be null!");
        return this.markCreated(this.getAuditor(), source);
    }

    public <T> T markModified(T source) {
        Assert.notNull(source, (String)"Entity must not be null!");
        return this.markModified(this.getAuditor(), source);
    }

    Auditor<?> getAuditor() {
        return this.auditorAware.map(AuditorAware::getCurrentAuditor).map(Auditor::ofOptional).orElse(Auditor.none());
    }

    public void afterPropertiesSet() {
        if (!this.auditorAware.isPresent()) {
            logger.debug((Object)"No AuditorAware set! Auditing will not be applied!");
        }
    }
}

