/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.auditing;

import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;

public class IsNewAwareAuditingHandler
extends AuditingHandler {
    private final PersistentEntities entities;

    @Deprecated
    public IsNewAwareAuditingHandler(MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {
        this(PersistentEntities.of(mappingContext));
    }

    public IsNewAwareAuditingHandler(PersistentEntities entities) {
        super(entities);
        this.entities = entities;
    }

    public Object markAudited(Object object) {
        Assert.notNull((Object)object, (String)"Source object must not be null!");
        if (!this.isAuditable(object)) {
            return object;
        }
        PersistentEntity<?, PersistentProperty<?>> entity = this.entities.getRequiredPersistentEntity(object.getClass());
        return entity.isNew(object) ? this.markCreated(object) : this.markModified(object);
    }
}

