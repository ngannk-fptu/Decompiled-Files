/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.entity;

import java.sql.ResultSet;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.entity.EntityJoinWalker;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class CollectionElementLoader
extends OuterJoinLoader {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)CollectionElementLoader.class.getName());
    private final OuterJoinLoadable persister;
    private final Type keyType;
    private final Type indexType;
    private final String entityName;

    public CollectionElementLoader(QueryableCollection collectionPersister, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(factory, loadQueryInfluencers);
        this.keyType = collectionPersister.getKeyType();
        this.indexType = collectionPersister.getIndexType();
        this.persister = (OuterJoinLoadable)collectionPersister.getElementPersister();
        this.entityName = this.persister.getEntityName();
        EntityJoinWalker walker = new EntityJoinWalker(this.persister, ArrayHelper.join(collectionPersister.getKeyColumnNames(), collectionPersister.toColumns("index")), 1, LockMode.NONE, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.postInstantiate();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for entity %s: %s", this.entityName, this.getSQLString());
        }
    }

    public Object loadElement(SharedSessionContractImplementor session, Object key, Object index) throws HibernateException {
        List list = this.loadEntity(session, key, index, this.keyType, this.indexType, this.persister);
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() == 0) {
            return null;
        }
        if (this.getCollectionOwners() != null) {
            return list.get(0);
        }
        throw new HibernateException("More than one row was found");
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

