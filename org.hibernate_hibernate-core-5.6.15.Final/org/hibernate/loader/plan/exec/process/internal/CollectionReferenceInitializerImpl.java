/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.pretty.MessageHelper;
import org.jboss.logging.Logger;

public class CollectionReferenceInitializerImpl
implements CollectionReferenceInitializer {
    private static final Logger log = CoreLogging.logger(CollectionReferenceInitializerImpl.class);
    private final CollectionReference collectionReference;
    private final CollectionReferenceAliases aliases;

    public CollectionReferenceInitializerImpl(CollectionReference collectionReference, CollectionReferenceAliases aliases) {
        this.collectionReference = collectionReference;
        this.aliases = aliases;
    }

    @Override
    public CollectionReference getCollectionReference() {
        return this.collectionReference;
    }

    @Override
    public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
        try {
            PersistenceContext persistenceContext = context.getSession().getPersistenceContextInternal();
            Serializable collectionRowKey = (Serializable)this.collectionReference.getCollectionPersister().readKey(resultSet, this.aliases.getCollectionColumnAliases().getSuffixedKeyAliases(), context.getSession());
            if (collectionRowKey != null) {
                if (log.isDebugEnabled()) {
                    log.debugf("Found row of collection: %s", (Object)MessageHelper.collectionInfoString(this.collectionReference.getCollectionPersister(), collectionRowKey, context.getSession().getFactory()));
                }
                Object collectionOwner = this.findCollectionOwner(collectionRowKey, resultSet, context);
                PersistentCollection rowCollection = persistenceContext.getLoadContexts().getCollectionLoadContext(resultSet).getLoadingCollection(this.collectionReference.getCollectionPersister(), collectionRowKey);
                if (rowCollection != null) {
                    rowCollection.readFrom(resultSet, this.collectionReference.getCollectionPersister(), this.aliases.getCollectionColumnAliases(), collectionOwner);
                }
            } else {
                Serializable optionalKey = this.findCollectionOwnerKey(context);
                if (optionalKey != null) {
                    if (log.isDebugEnabled()) {
                        log.debugf("Result set contains (possibly empty) collection: %s", (Object)MessageHelper.collectionInfoString(this.collectionReference.getCollectionPersister(), optionalKey, context.getSession().getFactory()));
                    }
                    persistenceContext.getLoadContexts().getCollectionLoadContext(resultSet).getLoadingCollection(this.collectionReference.getCollectionPersister(), optionalKey);
                }
            }
        }
        catch (SQLException sqle) {
            throw context.getSession().getFactory().getSQLExceptionHelper().convert(sqle, "could not read next row of results");
        }
    }

    protected Object findCollectionOwner(Serializable collectionRowKey, ResultSet resultSet, ResultSetProcessingContextImpl context) {
        Object collectionOwner = context.getSession().getPersistenceContextInternal().getCollectionOwner(collectionRowKey, this.collectionReference.getCollectionPersister());
        if (collectionOwner == null) {
            // empty if block
        }
        return collectionOwner;
    }

    protected Serializable findCollectionOwnerKey(ResultSetProcessingContextImpl context) {
        Object owner = context.getOwnerProcessingState((Fetch)((Object)this.collectionReference)).getEntityInstance();
        return this.collectionReference.getCollectionPersister().getCollectionType().getKeyOfOwner(owner, context.getSession());
    }

    @Override
    public void endLoading(ResultSetProcessingContextImpl context) {
        context.getSession().getPersistenceContextInternal().getLoadContexts().getCollectionLoadContext(context.getResultSet()).endLoadingCollections(this.collectionReference.getCollectionPersister());
    }
}

