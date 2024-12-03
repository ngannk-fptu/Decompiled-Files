/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.BackupParserUtil;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PropertySetItemPersister
implements ObjectPersister {
    private final Logger log = LoggerFactory.getLogger(PropertySetItemPersister.class);
    private static final String PROP_TRUNCATION_COUNT = "bucketpropertyset.truncationcount";

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject object) throws Exception {
        Long fixedId;
        BucketPropertySetItem item = new BucketPropertySetItem(object.getCompositeId().getPropertyValue("entityName"), Long.parseLong(object.getCompositeId().getPropertyValue("entityId")), object.getCompositeId().getPropertyValue("key"));
        if (item.getKey().length() > 200) {
            this.fixBucketPropertySetItemKey(item, context);
        }
        if ((fixedId = this.getFixedEntityIdForPropertySetItem(item.getEntityId(), context)) != null) {
            item.setEntityId(fixedId.longValue());
        }
        for (ImportedProperty importedProperty : object.getProperties()) {
            PrimitiveProperty prop = (PrimitiveProperty)importedProperty;
            if (prop.getValue() == null || prop.getValue().length() <= 0) continue;
            BeanUtils.setProperty((Object)item, (String)prop.getName(), (Object)this.fixPropertyValue(prop.getName(), prop.getValue()));
        }
        context.saveObject(item);
        return Collections.emptyList();
    }

    private Object fixPropertyValue(String name, String value) throws ParseException {
        if (name.equals("booleanVal")) {
            return Boolean.parseBoolean(value);
        }
        if (name.equals("doubleVal")) {
            return Double.parseDouble(value);
        }
        if (name.equals("longVal")) {
            return Long.parseLong(value);
        }
        if (name.equals("intVal")) {
            return Integer.parseInt(value);
        }
        if (name.equals("dateVal")) {
            return BackupParserUtil.parseTimestamp(value);
        }
        return value;
    }

    private void fixBucketPropertySetItemKey(BucketPropertySetItem item, ImportProcessorContext context) {
        StringTokenizer st;
        Integer truncationCount = (Integer)context.getContextVariable(PROP_TRUNCATION_COUNT);
        if (truncationCount == null) {
            truncationCount = 0;
        }
        String key = item.getKey();
        Object newKey = null;
        if (key.startsWith("tasklist.") && (st = new StringTokenizer(key, ".")).countTokens() >= 2) {
            String taskItemName;
            String taskListName = "";
            st.nextToken();
            if (st.countTokens() == 2) {
                taskListName = st.nextToken();
                taskItemName = st.nextToken();
            } else if (st.countTokens() == 1 && key.startsWith("tasklist..")) {
                taskItemName = st.nextToken();
            } else {
                taskListName = st.nextToken();
                String noPrefixString = key.substring(key.indexOf(".") + 1);
                int indexOfPeriod = noPrefixString.indexOf(".");
                taskItemName = noPrefixString.substring(indexOfPeriod + 1);
                this.log.warn("More than 2 tokens found for ENTITY_KEY=" + key + ". Best guess at tasklist name: " + taskListName);
            }
            try {
                Integer.parseInt(taskListName);
                Integer.parseInt(taskItemName);
                this.log.info("Skipping ENTITY_KEY=" + key + " as it has already been hashed.");
                newKey = key;
            }
            catch (NumberFormatException exception) {
                int taskListNameHash = StringUtils.defaultString((String)taskListName).hashCode();
                int taskItemNameHash = StringUtils.defaultString((String)taskItemName).hashCode();
                newKey = taskListNameHash + "." + taskItemNameHash;
            }
        }
        if (newKey == null) {
            this.log.error("truncating property set item key: '" + key + "'");
            Integer n = truncationCount;
            Integer n2 = truncationCount = Integer.valueOf(truncationCount + 1);
            newKey = key.substring(0, 190) + n;
            context.setContextVariable(PROP_TRUNCATION_COUNT, truncationCount);
        }
        item.setKey(newKey);
    }

    private Long getFixedEntityIdForPropertySetItem(Long entityId, ImportProcessorContext context) {
        Class[] entityClasses;
        for (Class entityClass : entityClasses = new Class[]{Page.class, BlogPost.class, CustomContentEntityObject.class}) {
            TransientHibernateHandle handle = TransientHibernateHandle.create(entityClass, entityId);
            Serializable fixedId = context.getIdMappingFor(handle);
            if (fixedId == null) continue;
            return (Long)fixedId;
        }
        return null;
    }
}

