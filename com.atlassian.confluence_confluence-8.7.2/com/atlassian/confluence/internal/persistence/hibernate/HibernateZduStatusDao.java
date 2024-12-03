/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.ZduStatusEntity;
import com.atlassian.confluence.internal.persistence.ZduStatusDao;
import java.util.Optional;

public class HibernateZduStatusDao
extends HibernateObjectDao<ZduStatusEntity>
implements ZduStatusDao {
    @Override
    public Class<ZduStatusEntity> getPersistentClass() {
        return ZduStatusEntity.class;
    }

    @Override
    public void deleteStatus() {
        this.getStatus().ifPresent(entity -> this.getHibernateTemplate().delete(entity));
    }

    @Override
    public Optional<ZduStatusEntity> getStatus() {
        return this.findAll().stream().findFirst();
    }

    @Override
    public void setStatus(ZduStatusEntity state) {
        ZduStatusEntity stateToSave = this.getStatus().orElse(state);
        stateToSave.setState(state.getState());
        stateToSave.setOriginalClusterVersion(state.getOriginalClusterVersion());
        this.saveEntity(stateToSave);
    }
}

