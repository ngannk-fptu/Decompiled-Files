/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.ofbiz.core.entity.GenericDelegator
 *  org.ofbiz.core.entity.GenericEntityException
 *  org.ofbiz.core.entity.GenericValue
 *  org.ofbiz.core.util.UtilMisc
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.ofbiz.DataPropertyHandler;
import com.opensymphony.module.propertyset.ofbiz.DatePropertyHandler;
import com.opensymphony.module.propertyset.ofbiz.DecimalPropertyHandler;
import com.opensymphony.module.propertyset.ofbiz.NumberPropertyHandler;
import com.opensymphony.module.propertyset.ofbiz.PropertyHandler;
import com.opensymphony.module.propertyset.ofbiz.StringPropertyHandler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;

public class OFBizPropertySet
extends AbstractPropertySet
implements Serializable {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$ofbiz$OFBizPropertySet == null ? (class$com$opensymphony$module$propertyset$ofbiz$OFBizPropertySet = OFBizPropertySet.class$("com.opensymphony.module.propertyset.ofbiz.OFBizPropertySet")) : class$com$opensymphony$module$propertyset$ofbiz$OFBizPropertySet));
    static Map entityTypeMap;
    Long entityId;
    String delegatorName;
    String entityName;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ofbiz$OFBizPropertySet;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        ArrayList<String> results = new ArrayList<String>();
        try {
            if (prefix == null) {
                prefix = "";
            }
            List c = this.getDelegator().findByAnd("OSPropertyEntry", UtilMisc.toMap((String)"entityId", (Object)this.entityId, (String)"entityName", (Object)this.entityName));
            Iterator iterator = c.iterator();
            while (iterator.hasNext()) {
                GenericValue value = (GenericValue)iterator.next();
                String propertyKey = value.getString("propertyKey");
                if (propertyKey == null || !propertyKey.startsWith(prefix)) continue;
                results.add(propertyKey);
            }
            Collections.sort(results);
        }
        catch (GenericEntityException e) {
            throw new PropertyImplementationException(e);
        }
        return results;
    }

    public int getType(String key) throws PropertyException {
        GenericValue v = this.findPropertyEntry(key);
        if (v == null) {
            throw new PropertyImplementationException("Property not found");
        }
        return v.getInteger("type");
    }

    public boolean exists(String key) throws PropertyException {
        return this.findPropertyEntry(key) != null;
    }

    public void init(Map config, Map args) {
        this.delegatorName = (String)config.get("delegator.name");
        if (this.delegatorName == null) {
            this.delegatorName = "default";
        }
        this.entityId = (Long)args.get("entityId");
        this.entityName = (String)args.get("entityName");
    }

    public void remove(String key) throws PropertyException {
        try {
            int type = this.getType(key);
            GenericValue v = this.findPropertyEntry(key);
            if (v != null) {
                Integer t = v.getInteger("type");
                Long id = v.getLong("id");
                if (type != t) {
                    throw new InvalidPropertyTypeException();
                }
                TypeMap tm = (TypeMap)entityTypeMap.get(t);
                this.getDelegator().removeByAnd(tm.getEntity(), UtilMisc.toMap((String)"id", (Object)id));
            }
            this.getDelegator().removeByAnd("OSPropertyEntry", this.makePropertyEntryFields(key));
        }
        catch (GenericEntityException e) {
            log.error((Object)"Error removing value from PropertySet", (Throwable)e);
            throw new PropertyImplementationException(e);
        }
    }

    protected void setImpl(int type, String key, Object obj) throws PropertyException {
        try {
            Long id;
            GenericValue propertyEntry = this.findPropertyEntry(key);
            if (propertyEntry == null) {
                id = this.getDelegator().getNextSeqId("OSPropertyEntry");
                propertyEntry = this.getDelegator().makeValue("OSPropertyEntry", UtilMisc.toMap((String)"entityId", (Object)this.entityId, (String)"id", (Object)id, (String)"entityName", (Object)this.entityName, (String)"type", (Object)new Integer(type), (String)"propertyKey", (Object)key));
            } else {
                id = propertyEntry.getLong("id");
            }
            TypeMap tm = (TypeMap)entityTypeMap.get(new Integer(type));
            GenericValue propertyTypeEntry = this.getDelegator().makeValue(tm.getEntity(), UtilMisc.toMap((String)"id", (Object)id, (String)"value", (Object)this.processSet(type, obj)));
            ArrayList<GenericValue> entities = new ArrayList<GenericValue>();
            entities.add(propertyEntry);
            entities.add(propertyTypeEntry);
            this.getDelegator().storeAll(entities);
        }
        catch (GenericEntityException e) {
            log.error((Object)"Error setting value in PropertySet", (Throwable)e);
            throw new PropertyImplementationException(e);
        }
    }

    protected Object get(int type, String key) throws PropertyException {
        try {
            GenericValue v = this.findPropertyEntry(key);
            if (v != null) {
                Integer t = v.getInteger("type");
                Long id = v.getLong("id");
                if (type != t) {
                    throw new InvalidPropertyTypeException();
                }
                TypeMap tm = (TypeMap)entityTypeMap.get(t);
                GenericValue property = this.getDelegator().findByPrimaryKey(tm.getEntity(), UtilMisc.toMap((String)"id", (Object)id));
                if (property == null) {
                    return null;
                }
                return this.processGet(type, property.get("value"));
            }
        }
        catch (GenericEntityException e) {
            throw new PropertyImplementationException(e);
        }
        return null;
    }

    private GenericDelegator getDelegator() {
        return GenericDelegator.getGenericDelegator((String)this.delegatorName);
    }

    private GenericValue findPropertyEntry(String key) throws PropertyException {
        try {
            List c = this.getDelegator().findByAnd("OSPropertyEntry", this.makePropertyEntryFields(key));
            if (c == null || c.size() == 0) {
                return null;
            }
            return (GenericValue)c.iterator().next();
        }
        catch (GenericEntityException e) {
            throw new PropertyImplementationException(e);
        }
    }

    private Map makePropertyEntryFields(String key) {
        return UtilMisc.toMap((String)"propertyKey", (Object)key, (String)"entityName", (Object)this.entityName, (String)"entityId", (Object)this.entityId);
    }

    private Object processGet(int type, Object input) throws PropertyException {
        if (input == null) {
            return null;
        }
        TypeMap typeMap = (TypeMap)entityTypeMap.get(new Integer(type));
        PropertyHandler handler = typeMap.getHandler();
        return handler.processGet(type, input);
    }

    private Object processSet(int type, Object input) throws PropertyException {
        if (input == null) {
            return null;
        }
        TypeMap typeMap = (TypeMap)entityTypeMap.get(new Integer(type));
        PropertyHandler handler = typeMap.getHandler();
        return handler.processSet(type, input);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        StringPropertyHandler stringHandler = new StringPropertyHandler();
        StringPropertyHandler textHandler = new StringPropertyHandler();
        DatePropertyHandler dateHandler = new DatePropertyHandler();
        DataPropertyHandler dataHandler = new DataPropertyHandler();
        NumberPropertyHandler numberHandler = new NumberPropertyHandler();
        DecimalPropertyHandler decimalHandler = new DecimalPropertyHandler();
        entityTypeMap = new HashMap();
        entityTypeMap.put(new Integer(1), new TypeMap("OSPropertyNumber", numberHandler));
        entityTypeMap.put(new Integer(2), new TypeMap("OSPropertyNumber", numberHandler));
        entityTypeMap.put(new Integer(3), new TypeMap("OSPropertyNumber", numberHandler));
        entityTypeMap.put(new Integer(4), new TypeMap("OSPropertyDecimal", decimalHandler));
        entityTypeMap.put(new Integer(5), new TypeMap("OSPropertyString", stringHandler));
        entityTypeMap.put(new Integer(6), new TypeMap("OSPropertyText", textHandler));
        entityTypeMap.put(new Integer(7), new TypeMap("OSPropertyDate", dateHandler));
        entityTypeMap.put(new Integer(8), new TypeMap("OSPropertyData", dataHandler));
        entityTypeMap.put(new Integer(9), new TypeMap("OSPropertyData", dataHandler));
        entityTypeMap.put(new Integer(10), new TypeMap("OSPropertyData", dataHandler));
        entityTypeMap.put(new Integer(11), new TypeMap("OSPropertyData", dataHandler));
    }

    static class TypeMap {
        PropertyHandler handler;
        String entity;

        public TypeMap(String entity, PropertyHandler handler) {
            this.entity = entity;
            this.handler = handler;
        }

        public String getEntity() {
            return this.entity;
        }

        public PropertyHandler getHandler() {
            return this.handler;
        }
    }
}

