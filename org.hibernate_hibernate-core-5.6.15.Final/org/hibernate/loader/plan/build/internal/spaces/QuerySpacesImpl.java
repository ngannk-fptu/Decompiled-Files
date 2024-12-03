/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.build.internal.spaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.build.internal.spaces.CollectionQuerySpaceImpl;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.internal.spaces.CompositeQuerySpaceImpl;
import org.hibernate.loader.plan.build.internal.spaces.EntityQuerySpaceImpl;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.QuerySpaceUidNotRegisteredException;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public class QuerySpacesImpl
implements ExpandingQuerySpaces {
    private static final Logger log = CoreLogging.logger(QuerySpacesImpl.class);
    private final SessionFactoryImplementor sessionFactory;
    private final List<QuerySpace> roots = new ArrayList<QuerySpace>();
    private final Map<String, QuerySpace> querySpaceByUid = new ConcurrentHashMap<String, QuerySpace>();
    private int implicitUidBase;

    public QuerySpacesImpl(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<QuerySpace> getRootQuerySpaces() {
        return this.roots;
    }

    @Override
    public QuerySpace findQuerySpaceByUid(String uid) {
        return this.querySpaceByUid.get(uid);
    }

    @Override
    public QuerySpace getQuerySpaceByUid(String uid) {
        QuerySpace space = this.findQuerySpaceByUid(uid);
        if (space == null) {
            throw new QuerySpaceUidNotRegisteredException(uid);
        }
        return space;
    }

    @Override
    public String generateImplicitUid() {
        return "<gen:" + this.implicitUidBase++ + ">";
    }

    @Override
    public ExpandingEntityQuerySpace makeRootEntityQuerySpace(String uid, EntityPersister entityPersister) {
        ExpandingEntityQuerySpace space = this.makeEntityQuerySpace(uid, entityPersister, true);
        this.roots.add(space);
        return space;
    }

    @Override
    public ExpandingEntityQuerySpace makeEntityQuerySpace(String uid, EntityPersister entityPersister, boolean canJoinsBeRequired) {
        this.checkQuerySpaceDoesNotExist(uid);
        EntityQuerySpaceImpl space = new EntityQuerySpaceImpl(entityPersister, uid, (ExpandingQuerySpaces)this, canJoinsBeRequired && !entityPersister.getEntityMetamodel().hasSubclasses());
        this.registerQuerySpace(space);
        return space;
    }

    @Override
    public ExpandingCollectionQuerySpace makeRootCollectionQuerySpace(String uid, CollectionPersister collectionPersister) {
        ExpandingCollectionQuerySpace space = this.makeCollectionQuerySpace(uid, collectionPersister, true);
        this.roots.add(space);
        return space;
    }

    @Override
    public ExpandingCollectionQuerySpace makeCollectionQuerySpace(String uid, CollectionPersister collectionPersister, boolean canJoinsBeRequired) {
        this.checkQuerySpaceDoesNotExist(uid);
        CollectionQuerySpaceImpl space = new CollectionQuerySpaceImpl(collectionPersister, uid, (ExpandingQuerySpaces)this, canJoinsBeRequired);
        this.registerQuerySpace(space);
        return space;
    }

    @Override
    public ExpandingCompositeQuerySpace makeCompositeQuerySpace(String uid, CompositePropertyMapping compositePropertyMapping, boolean canJoinsBeRequired) {
        this.checkQuerySpaceDoesNotExist(uid);
        CompositeQuerySpaceImpl space = new CompositeQuerySpaceImpl(compositePropertyMapping, uid, (ExpandingQuerySpaces)this, canJoinsBeRequired);
        this.registerQuerySpace(space);
        return space;
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    private void checkQuerySpaceDoesNotExist(String uid) {
        if (this.querySpaceByUid.containsKey(uid)) {
            throw new IllegalStateException("Encountered duplicate QuerySpace uid : " + uid);
        }
    }

    private void registerQuerySpace(QuerySpace querySpace) {
        log.debugf("Adding QuerySpace : uid = %s -> %s]", (Object)querySpace.getUid(), (Object)querySpace);
        QuerySpace previous = this.querySpaceByUid.put(querySpace.getUid(), querySpace);
        if (previous != null) {
            throw new IllegalStateException("Encountered duplicate QuerySpace uid : " + querySpace.getUid());
        }
    }
}

