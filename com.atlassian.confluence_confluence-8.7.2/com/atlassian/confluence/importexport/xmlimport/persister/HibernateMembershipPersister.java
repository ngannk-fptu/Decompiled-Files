/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 *  org.hibernate.HibernateException
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
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.atlassian.confluence.importexport.xmlimport.persister.AbstractObjectPersister;
import com.atlassian.crowd.embedded.hibernate2.HibernateMembership;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class HibernateMembershipPersister
extends AbstractObjectPersister
implements ObjectPersister {
    public static final Logger log = LoggerFactory.getLogger(HibernateMembershipPersister.class);

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject importedObject) throws Exception {
        this.importedObject = importedObject;
        Class<HibernateMembership> classToPersist = HibernateMembership.class;
        this.entityPersister = context.getPersister(classToPersist);
        HibernateMembership objectToPersist = new HibernateMembership();
        for (ImportedProperty importedProperty : importedObject.getProperties()) {
            if (importedProperty instanceof PrimitiveId) continue;
            String idString = ((ReferenceProperty)importedProperty).getId().getValue();
            if ("parentGroup".equals(importedProperty.getName())) {
                InternalGroup parentGroup = this.getReferencePropertyValue(InternalGroup.class, idString, context);
                objectToPersist.setParentGroup(parentGroup);
                continue;
            }
            if ("userMember".equals(importedProperty.getName())) {
                InternalUser userMember = this.getReferencePropertyValue(InternalUser.class, idString, context);
                objectToPersist.setUserMember(userMember);
                continue;
            }
            if ("groupMember".equals(importedProperty.getName())) {
                InternalGroup groupMember = this.getReferencePropertyValue(InternalGroup.class, idString, context);
                objectToPersist.setGroupMember(groupMember);
                continue;
            }
            throw new IllegalArgumentException("Unhandled property type " + importedProperty.getClass() + ": " + importedProperty);
        }
        if (this.unsatisfiedObjectDependencies.isEmpty()) {
            TransientHibernateHandle unfixedHandle = this.getCurrentObjectHandle();
            context.saveObject(unfixedHandle.getId(), classToPersist, objectToPersist);
            return Collections.singletonList(unfixedHandle);
        }
        context.addUnsatisfiedObjectDependencies(this.unsatisfiedObjectDependencies, importedObject);
        return Collections.emptyList();
    }

    private TransientHibernateHandle getCurrentObjectHandle() throws HibernateException {
        return this.persisterOperations.readId(HibernateMembership.class, this.importedObject.getIdPropertyStr(), this.entityPersister);
    }
}

