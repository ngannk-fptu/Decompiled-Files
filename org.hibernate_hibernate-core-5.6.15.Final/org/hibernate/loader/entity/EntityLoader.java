/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.entity.AbstractEntityLoader;
import org.hibernate.loader.entity.EntityJoinWalker;
import org.hibernate.loader.entity.NaturalIdEntityJoinWalker;
import org.hibernate.loader.entity.NaturalIdType;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;

public class EntityLoader
extends AbstractEntityLoader {
    private final boolean batchLoader;
    private final int[][] compositeKeyManyToOneTargetIndices;

    public EntityLoader(OuterJoinLoadable persister, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(persister, 1, lockMode, factory, loadQueryInfluencers);
    }

    public EntityLoader(OuterJoinLoadable persister, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(persister, 1, lockOptions, factory, loadQueryInfluencers);
    }

    public EntityLoader(OuterJoinLoadable persister, int batchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(persister, persister.getIdentifierColumnNames(), persister.getIdentifierType(), batchSize, lockMode, factory, loadQueryInfluencers);
    }

    public EntityLoader(OuterJoinLoadable persister, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(persister, persister.getIdentifierColumnNames(), persister.getIdentifierType(), batchSize, lockOptions, factory, loadQueryInfluencers);
    }

    public EntityLoader(OuterJoinLoadable persister, String[] uniqueKey, Type uniqueKeyType, int batchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(persister, uniqueKeyType, factory, loadQueryInfluencers);
        EntityJoinWalker walker = new EntityJoinWalker(persister, uniqueKey, batchSize, lockMode, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.compositeKeyManyToOneTargetIndices = walker.getCompositeKeyManyToOneTargetIndices();
        this.postInstantiate();
        boolean bl = this.batchLoader = batchSize > 1;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for entity %s [%s]: %s", this.entityName, (Object)lockMode, this.getSQLString());
        }
    }

    public EntityLoader(OuterJoinLoadable persister, String[] uniqueKey, Type uniqueKeyType, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(persister, uniqueKeyType, factory, loadQueryInfluencers);
        EntityJoinWalker walker = new EntityJoinWalker(persister, uniqueKey, batchSize, lockOptions, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.compositeKeyManyToOneTargetIndices = walker.getCompositeKeyManyToOneTargetIndices();
        this.postInstantiate();
        boolean bl = this.batchLoader = batchSize > 1;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for entity %s [%s:%s]: %s", new Object[]{this.entityName, lockOptions.getLockMode(), lockOptions.getTimeOut(), this.getSQLString()});
        }
    }

    public EntityLoader(OuterJoinLoadable persister, boolean[] valueNullness, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(persister, new NaturalIdType(persister, valueNullness), factory, loadQueryInfluencers);
        NaturalIdEntityJoinWalker walker = new NaturalIdEntityJoinWalker(persister, valueNullness, batchSize, lockOptions, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.compositeKeyManyToOneTargetIndices = walker.getCompositeKeyManyToOneTargetIndices();
        this.postInstantiate();
        boolean bl = this.batchLoader = batchSize > 1;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for entity %s [%s:%s]: %s", new Object[]{this.entityName, lockOptions.getLockMode(), lockOptions.getTimeOut(), this.getSQLString()});
        }
    }

    @Deprecated
    public Object loadByUniqueKey(SharedSessionContractImplementor session, Object key) {
        return this.loadByUniqueKey(session, key, null);
    }

    @Deprecated
    public Object loadByUniqueKey(SharedSessionContractImplementor session, Object key, Boolean readOnly) {
        return this.load(session, key, null, null, LockOptions.NONE, readOnly);
    }

    @Override
    protected boolean isSingleRowLoader() {
        return !this.batchLoader;
    }

    @Override
    public int[][] getCompositeKeyManyToOneTargetIndices() {
        return this.compositeKeyManyToOneTargetIndices;
    }
}

