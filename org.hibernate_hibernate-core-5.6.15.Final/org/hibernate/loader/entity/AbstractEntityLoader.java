/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

public abstract class AbstractEntityLoader
extends OuterJoinLoader
implements UniqueEntityLoader {
    protected final OuterJoinLoadable persister;
    protected final Type uniqueKeyType;
    protected final String entityName;

    public AbstractEntityLoader(OuterJoinLoadable persister, Type uniqueKeyType, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
        super(factory, loadQueryInfluencers);
        this.uniqueKeyType = uniqueKeyType;
        this.entityName = persister.getEntityName();
        this.persister = persister;
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
        return this.load(id, optionalObject, session, LockOptions.NONE, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, Boolean readOnly) {
        return this.load(id, optionalObject, session, LockOptions.NONE, readOnly);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(id, optionalObject, session, lockOptions, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        return this.load(session, id, optionalObject, id, lockOptions, readOnly);
    }

    @Override
    public Object load(Object id, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(session, id, null, null, lockOptions, null);
    }

    protected Object load(SharedSessionContractImplementor session, Object id, Object optionalObject, Serializable optionalId, LockOptions lockOptions, Boolean readOnly) {
        List list = this.loadEntity(session, id, this.uniqueKeyType, optionalObject, this.entityName, optionalId, this.persister, lockOptions, readOnly);
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() == 0) {
            return null;
        }
        if (this.getCollectionOwners() != null) {
            return list.get(0);
        }
        throw new HibernateException("More than one row with the given identifier was found: " + id + ", for class: " + this.persister.getEntityName());
    }

    @Override
    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SharedSessionContractImplementor session) {
        return row[row.length - 1];
    }

    @Override
    protected boolean isSingleRowLoader() {
        return true;
    }
}

