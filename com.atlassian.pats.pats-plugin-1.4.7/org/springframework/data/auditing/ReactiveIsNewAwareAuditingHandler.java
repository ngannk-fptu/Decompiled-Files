/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.auditing;

import org.springframework.data.auditing.ReactiveAuditingHandler;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class ReactiveIsNewAwareAuditingHandler
extends ReactiveAuditingHandler {
    private final PersistentEntities entities;

    public ReactiveIsNewAwareAuditingHandler(PersistentEntities entities) {
        super(entities);
        this.entities = entities;
    }

    public Mono<Object> markAudited(Object object) {
        Assert.notNull((Object)object, (String)"Source object must not be null!");
        if (!this.isAuditable(object)) {
            return Mono.just((Object)object);
        }
        PersistentEntity<?, PersistentProperty<?>> entity = this.entities.getRequiredPersistentEntity(object.getClass());
        return entity.isNew(object) ? this.markCreated(object) : this.markModified(object);
    }
}

