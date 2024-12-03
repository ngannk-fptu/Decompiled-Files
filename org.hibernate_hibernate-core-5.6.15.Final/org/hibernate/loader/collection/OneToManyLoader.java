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
import org.hibernate.loader.collection.CollectionLoader;
import org.hibernate.loader.collection.OneToManyJoinWalker;
import org.hibernate.persister.collection.QueryableCollection;
import org.jboss.logging.Logger;

public class OneToManyLoader
extends CollectionLoader {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)OneToManyLoader.class.getName());

    public OneToManyLoader(QueryableCollection oneToManyPersister, SessionFactoryImplementor session, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(oneToManyPersister, 1, session, loadQueryInfluencers);
    }

    public OneToManyLoader(QueryableCollection oneToManyPersister, int batchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        this(oneToManyPersister, batchSize, null, factory, loadQueryInfluencers);
    }

    public OneToManyLoader(QueryableCollection oneToManyPersister, int batchSize, String subquery, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(oneToManyPersister, factory, loadQueryInfluencers);
        OneToManyJoinWalker walker = new OneToManyJoinWalker(oneToManyPersister, batchSize, subquery, factory, loadQueryInfluencers);
        this.initFromWalker(walker);
        this.postInstantiate();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for one-to-many %s: %s", oneToManyPersister.getRole(), this.getSQLString());
        }
    }
}

