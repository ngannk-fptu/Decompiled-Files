/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.persister.entity.EntityPersister;

interface PostActionEventListener {
    @Deprecated
    public boolean requiresPostCommitHanding(EntityPersister var1);

    default public boolean requiresPostCommitHandling(EntityPersister persister) {
        return this.requiresPostCommitHanding(persister);
    }
}

