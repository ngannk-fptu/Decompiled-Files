/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.ArrayUtils
 *  org.hibernate.Hibernate
 *  org.hibernate.persister.entity.EntityPersister
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.persister.ReflectiveObjectPersister;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.persister.entity.EntityPersister;

@Internal
public class ObjectUpdateOperation
implements Operation {
    final ImportProcessorContext context;
    final TransientHibernateHandle objectHandle;
    final String propertyName;
    final TransientHibernateHandle propertyValueHandle;

    public ObjectUpdateOperation(ImportProcessorContext context, TransientHibernateHandle objectHandle, String propertyName, TransientHibernateHandle propertyValueHandle) {
        this.context = context;
        this.objectHandle = objectHandle;
        this.propertyName = propertyName;
        this.propertyValueHandle = propertyValueHandle;
    }

    @Override
    public void execute() throws Exception {
        Object target = this.context.lookupObjectByUnfixedHandle(this.objectHandle);
        target = ReflectiveObjectPersister.unproxyIfRequired(target);
        Object value = this.context.lookupObjectByUnfixedHandle(this.propertyValueHandle);
        if (target == null || value == null) {
            throw new RuntimeException(this.getDescription() + " failed.");
        }
        EntityPersister persister = this.context.getEntityPersister(Hibernate.getClass((Object)target));
        persister.setPropertyValue(target, ArrayUtils.indexOf((Object[])persister.getPropertyNames(), (Object)this.propertyName), value);
    }

    @Override
    public String getDescription() throws Exception {
        return "Set property " + this.propertyName + " on " + this.objectHandle + " to " + this.propertyValueHandle;
    }
}

