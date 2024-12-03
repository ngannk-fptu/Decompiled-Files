/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.auditing;

import org.springframework.data.auditing.AuditingHandlerSupport;
import org.springframework.data.auditing.Auditor;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class ReactiveAuditingHandler
extends AuditingHandlerSupport {
    private ReactiveAuditorAware<?> auditorAware = Mono::empty;

    public ReactiveAuditingHandler(PersistentEntities entities) {
        super(entities);
    }

    public void setAuditorAware(ReactiveAuditorAware<?> auditorAware) {
        Assert.notNull(auditorAware, (String)"AuditorAware must not be null!");
        this.auditorAware = auditorAware;
    }

    public <T> Mono<T> markCreated(T source) {
        Assert.notNull(source, (String)"Entity must not be null!");
        return this.getAuditor().map(auditor -> this.markCreated((Auditor)auditor, source));
    }

    public <T> Mono<T> markModified(T source) {
        Assert.notNull(source, (String)"Entity must not be null!");
        return this.getAuditor().map(auditor -> this.markModified((Auditor)auditor, source));
    }

    private Mono<? extends Auditor<?>> getAuditor() {
        return this.auditorAware.getCurrentAuditor().map(Auditor::of).defaultIfEmpty(Auditor.none());
    }
}

