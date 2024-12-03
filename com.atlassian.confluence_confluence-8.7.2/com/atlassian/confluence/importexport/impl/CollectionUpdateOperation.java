/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.hibernate.persister.entity.EntityPersister
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import java.util.Collection;
import org.hibernate.persister.entity.EntityPersister;

@Internal
public class CollectionUpdateOperation
implements Operation {
    final ImportProcessorContext context;
    final TransientHibernateHandle collectionOwnerKey;
    final String collectionName;
    final TransientHibernateHandle memberKey;

    public CollectionUpdateOperation(ImportProcessorContext context, TransientHibernateHandle collectionOwnerKey, String collectionName, TransientHibernateHandle memberKey) {
        this.context = context;
        this.collectionOwnerKey = collectionOwnerKey;
        this.collectionName = collectionName;
        this.memberKey = memberKey;
    }

    @Override
    public void execute() throws Exception {
        Object targetObject = this.context.lookupObjectByUnfixedHandle(this.collectionOwnerKey);
        Object objectToInsert = this.context.lookupObjectByUnfixedHandle(this.memberKey);
        if (targetObject == null || objectToInsert == null) {
            throw new RuntimeException(this.getDescription() + " failed.");
        }
        EntityPersister persister = this.context.getEntityPersister(this.collectionOwnerKey.getClazz());
        Collection collection = (Collection)persister.getPropertyValue(targetObject, this.collectionName);
        collection.add(objectToInsert);
        this.context.getSession().update(objectToInsert);
        this.context.getSession().update(targetObject);
    }

    @Override
    public String getDescription() throws Exception {
        return "Add " + this.memberKey + " to collection " + this.collectionName + " on object " + this.collectionOwnerKey;
    }
}

