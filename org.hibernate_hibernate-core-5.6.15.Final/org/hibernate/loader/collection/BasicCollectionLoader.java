/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.collection;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.collection.BasicCollectionJoinWalker;
import org.hibernate.loader.collection.CollectionLoader;
import org.hibernate.persister.collection.QueryableCollection;
import org.jboss.logging.Logger;

public class BasicCollectionLoader
extends CollectionLoader {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)BasicCollectionLoader.class.getName());

    public BasicCollectionLoader(QueryableCollection collectionPersister, SessionFactoryImplementor session, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(collectionPersister, 1, session, loadQueryInfluencers);
    }

    public BasicCollectionLoader(QueryableCollection collectionPersister, int batchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(collectionPersister, batchSize, null, factory, loadQueryInfluencers);
    }

    protected BasicCollectionLoader(QueryableCollection collectionPersister, int batchSize, String subquery, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(collectionPersister, factory, loadQueryInfluencers);
        BasicCollectionJoinWalker walker = new BasicCollectionJoinWalker(collectionPersister, batchSize, subquery, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.postInstantiate();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for collection %s: %s", collectionPersister.getRole(), this.getSQLString());
        }
    }
}

