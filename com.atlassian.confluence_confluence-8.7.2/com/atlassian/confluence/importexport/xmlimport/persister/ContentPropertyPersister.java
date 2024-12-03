/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.persister.AbstractObjectPersister;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ContentPropertyPersister
extends AbstractObjectPersister {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertyPersister.class);

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject importedObject) throws Exception {
        ContentProperty contentProperty = new ContentProperty();
        EntityPersister persister = context.getPersister(ContentProperty.class);
        Type idType = persister.getIdentifierType();
        Serializable id = (Serializable)this.persisterOperations.literalTypeFromString(idType, importedObject.getIdPropertyStr());
        for (ImportedProperty property : importedObject.getProperties()) {
            if (!(property instanceof PrimitiveProperty) || ((PrimitiveProperty)property).getValue().isEmpty()) continue;
            Object value = this.getPrimitivePropertyValue(persister, (PrimitiveProperty)property);
            if (property instanceof PrimitiveId) {
                contentProperty.setId((Long)value);
                continue;
            }
            persister.getEntityTuplizer().setPropertyValue((Object)contentProperty, property.getName(), value);
        }
        String oldKey = contentProperty.getStringValue();
        TransientHibernateHandle handle = TransientHibernateHandle.create(ConfluenceUserImpl.class, (Serializable)new UserKey(oldKey));
        UserKey newKey = (UserKey)context.getIdMappingFor(handle);
        if (newKey != null) {
            contentProperty.setStringValue(newKey.toString());
        } else {
            log.info("Could not find new status-lastmodifier mapping for user id: " + oldKey);
        }
        context.saveObject(id, contentProperty.getClass(), contentProperty);
        return Collections.emptyList();
    }
}

