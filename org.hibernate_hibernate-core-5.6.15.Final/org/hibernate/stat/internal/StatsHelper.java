/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.entity.EntityPersister;

public class StatsHelper {
    public static final StatsHelper INSTANCE = new StatsHelper();

    public NavigableRole getRootEntityRole(EntityPersister entityDescriptor) {
        String rootEntityName = entityDescriptor.getRootEntityName();
        if (entityDescriptor.getEntityName().equals(rootEntityName)) {
            return entityDescriptor.getNavigableRole();
        }
        EntityPersister rootEntityDescriptor = entityDescriptor.getFactory().getMetamodel().entityPersister(rootEntityName);
        return rootEntityDescriptor.getNavigableRole();
    }

    private StatsHelper() {
    }
}

