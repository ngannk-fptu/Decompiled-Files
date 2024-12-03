/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.metadata.ClassMetadata
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.atlassian.confluence.importexport.xmlimport.persister.AbstractObjectPersister;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class HibernateContentPermissionPersister
extends AbstractObjectPersister
implements ObjectPersister {
    public static final Logger log = LoggerFactory.getLogger(HibernateContentPermissionPersister.class);

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject importedObject) throws Exception {
        this.importedObject = importedObject;
        Class<ContentPermission> classToPersist = ContentPermission.class;
        this.entityPersister = context.getPersister(classToPersist);
        ContentPermission objectToPersist = new ContentPermission();
        for (ImportedProperty importedProperty : importedObject.getProperties()) {
            this.updateProperty(context, objectToPersist, importedProperty);
        }
        if (this.unsatisfiedObjectDependencies.isEmpty()) {
            TransientHibernateHandle unfixedHandle = this.getCurrentObjectHandle();
            context.saveObject(unfixedHandle.getId(), classToPersist, objectToPersist);
            return Collections.singletonList(unfixedHandle);
        }
        context.addUnsatisfiedObjectDependencies(this.unsatisfiedObjectDependencies, importedObject);
        return Collections.emptyList();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void updateProperty(ImportProcessorContext context, ContentPermission objectToPersist, ImportedProperty importedProperty) throws Exception {
        if (importedProperty instanceof PrimitiveId) {
            return;
        }
        if (importedProperty instanceof PrimitiveProperty) {
            Object propertyValue = this.getPrimitivePropertyValue(this.entityPersister, (PrimitiveProperty)importedProperty);
            if (propertyValue == null) return;
            ((ClassMetadata)this.entityPersister).setPropertyValue((Object)objectToPersist, importedProperty.getName(), propertyValue);
            return;
        } else {
            if (!(importedProperty instanceof ReferenceProperty)) throw new IllegalArgumentException("Unhandled property type " + importedProperty.getClass() + ": " + importedProperty);
            String idString = ((ReferenceProperty)importedProperty).getId().getValue();
            if ("userSubject".equals(importedProperty.getName())) {
                ConfluenceUserImpl userSubject = this.getReferencePropertyValue(ConfluenceUserImpl.class, idString, context);
                objectToPersist.setSubject(userSubject);
                return;
            } else if ("owningSet".equals(importedProperty.getName())) {
                ContentPermissionSet owningSet = this.getReferencePropertyValue(ContentPermissionSet.class, idString, context);
                objectToPersist.setOwningSet(owningSet);
                return;
            } else if ("creator".equals(importedProperty.getName())) {
                ConfluenceUserImpl creator = this.getReferencePropertyValue(ConfluenceUserImpl.class, idString, context);
                objectToPersist.setCreator(creator);
                return;
            } else {
                if (!"lastModifier".equals(importedProperty.getName())) throw new IllegalArgumentException("Unhandled property type " + importedProperty.getClass() + ": " + importedProperty);
                ConfluenceUserImpl lastModifier = this.getReferencePropertyValue(ConfluenceUserImpl.class, idString, context);
                objectToPersist.setLastModifier(lastModifier);
            }
        }
    }

    private TransientHibernateHandle getCurrentObjectHandle() throws HibernateException {
        return this.persisterOperations.readId(ContentPermission.class, this.importedObject.getIdPropertyStr(), this.entityPersister);
    }
}

