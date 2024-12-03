/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;

public class CollectionLoader
extends OuterJoinLoader
implements CollectionInitializer {
    private final QueryableCollection collectionPersister;

    public CollectionLoader(QueryableCollection collectionPersister, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
        super(factory, loadQueryInfluencers);
        this.collectionPersister = collectionPersister;
    }

    protected QueryableCollection collectionPersister() {
        return this.collectionPersister;
    }

    @Override
    protected boolean isSubselectLoadingEnabled() {
        return this.hasSubselectLoadableCollections();
    }

    @Override
    public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        this.loadCollection(session, id, this.getKeyType());
    }

    protected Type getKeyType() {
        return this.collectionPersister.getKeyType();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + '(' + this.collectionPersister.getRole() + ')';
    }
}

