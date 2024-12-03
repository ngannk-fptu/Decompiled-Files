/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.ojb.broker.PBFactoryException
 *  org.apache.ojb.broker.PersistenceBroker
 *  org.apache.ojb.broker.PersistenceBrokerFactory
 *  org.apache.ojb.broker.query.Criteria
 *  org.apache.ojb.broker.query.Query
 *  org.apache.ojb.broker.query.QueryByCriteria
 *  org.apache.ojb.broker.query.QueryFactory
 *  org.apache.ojb.broker.query.ReportQueryByCriteria
 */
package com.opensymphony.module.propertyset.ojb;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.ojb.OJBPropertySetItem;
import com.opensymphony.util.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;

public class OJBPropertySet
extends AbstractPropertySet {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$ojb$OJBPropertySet == null ? (class$com$opensymphony$module$propertyset$ojb$OJBPropertySet = OJBPropertySet.class$("com.opensymphony.module.propertyset.ojb.OJBPropertySet")) : class$com$opensymphony$module$propertyset$ojb$OJBPropertySet));
    String globalKey = null;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ojb$OJBPropertySet;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        PersistenceBroker broker = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            broker = this.getBroker();
            if (prefix == null) {
                prefix = "";
            }
            Criteria criteria = new Criteria();
            criteria.addEqualTo("globalKey", (Object)this.globalKey);
            criteria.addLike("itemKey", (Object)prefix);
            if (type != 0) {
                criteria.addEqualTo("itemType", (Object)new Integer(type));
            }
            ReportQueryByCriteria q = QueryFactory.newReportQuery((Class)(class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem == null ? (class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem = OJBPropertySet.class$("com.opensymphony.module.propertyset.ojb.OJBPropertySetItem")) : class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem), (Criteria)criteria);
            q.setColumns(new String[]{"itemKey"});
            Iterator iter = broker.getReportQueryIteratorByQuery((Query)q);
            Object[] obj = null;
            while (iter.hasNext()) {
                obj = (Object[])iter.next();
                String itemKey = (String)obj[0];
                list.add(itemKey);
            }
        }
        catch (Exception e) {
            log.error((Object)"An exception occured", (Throwable)e);
            throw new PropertyException(e.getMessage());
        }
        finally {
            if (broker != null) {
                broker.close();
            }
        }
        return list;
    }

    public int getType(String key) throws PropertyException {
        PersistenceBroker broker = null;
        int type = 0;
        try {
            broker = this.getBroker();
            Criteria critere = new Criteria();
            critere.addEqualTo("globalKey", (Object)this.globalKey);
            critere.addLike("itemKey", (Object)key);
            ReportQueryByCriteria q = QueryFactory.newReportQuery((Class)(class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem == null ? (class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem = OJBPropertySet.class$("com.opensymphony.module.propertyset.ojb.OJBPropertySetItem")) : class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem), (Criteria)critere);
            q.setColumns(new String[]{"itemType"});
            Iterator iter = broker.getReportQueryIteratorByQuery((Query)q);
            Object[] obj = null;
            if (iter.hasNext()) {
                obj = (Object[])iter.next();
                type = (Integer)obj[0];
            }
        }
        catch (Exception e) {
            log.error((Object)"An exception occured", (Throwable)e);
            throw new PropertyException(e.getMessage());
        }
        finally {
            if (broker != null) {
                broker.close();
            }
        }
        return type;
    }

    public boolean exists(String key) throws PropertyException {
        return this.getType(key) != 0;
    }

    public void init(Map config, Map args) {
        this.globalKey = (String)args.get("globalKey");
    }

    public void remove(String key) throws PropertyException {
        PersistenceBroker broker = null;
        try {
            broker = this.getBroker();
            Criteria critere = new Criteria();
            critere.addEqualTo("globalKey", (Object)this.globalKey);
            critere.addEqualTo("itemKey", (Object)key);
            QueryByCriteria requete = new QueryByCriteria(class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem == null ? (class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem = OJBPropertySet.class$("com.opensymphony.module.propertyset.ojb.OJBPropertySetItem")) : class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem, critere);
            broker.delete((Object)requete);
        }
        catch (Exception e) {
            log.error((Object)"An exception occured", (Throwable)e);
            throw new PropertyException(e.getMessage());
        }
        finally {
            if (broker != null) {
                broker.close();
            }
        }
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        PersistenceBroker broker = null;
        if (value == null) {
            throw new PropertyException("OJBPropertySet does not allow for null values to be stored");
        }
        try {
            broker = this.getBroker();
            OJBPropertySetItem newProperty = new OJBPropertySetItem();
            newProperty.setItemType(type);
            newProperty.setGlobalKey(this.globalKey);
            newProperty.setItemKey(key);
            switch (type) {
                case 1: {
                    Boolean boolVal = (Boolean)value;
                    newProperty.setLongValue(boolVal != false ? 1L : 0L);
                    break;
                }
                case 7: {
                    newProperty.setDateValue((Date)value);
                    break;
                }
                case 4: {
                    Double dblValue = (Double)value;
                    newProperty.setDoubleValue(dblValue);
                    break;
                }
                case 3: {
                    Long lngValue = (Long)value;
                    newProperty.setLongValue(lngValue);
                    break;
                }
                case 2: {
                    Integer intValue = (Integer)value;
                    newProperty.setLongValue(intValue.longValue());
                    break;
                }
                case 5: {
                    newProperty.setStringValue((String)value);
                    break;
                }
                case 10: {
                    Data data = (Data)value;
                    newProperty.setByteValue(data.getBytes());
                    break;
                }
                default: {
                    throw new PropertyException("This type : " + type + ", isn't supported!");
                }
            }
            broker.store((Object)newProperty);
        }
        catch (Exception e) {
            log.error((Object)"An exception occured", (Throwable)e);
            throw new PropertyException(e.getMessage());
        }
        finally {
            if (broker != null) {
                broker.close();
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Object get(int type, String key) throws PropertyException {
        PersistenceBroker broker = null;
        Object value = null;
        try {
            broker = this.getBroker();
            Criteria critere = new Criteria();
            critere.addEqualTo("globalKey", (Object)this.globalKey);
            critere.addLike("itemKey", (Object)key);
            QueryByCriteria requete = new QueryByCriteria(class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem == null ? (class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem = OJBPropertySet.class$("com.opensymphony.module.propertyset.ojb.OJBPropertySetItem")) : class$com$opensymphony$module$propertyset$ojb$OJBPropertySetItem, critere);
            OJBPropertySetItem item = (OJBPropertySetItem)broker.getObjectByQuery((Query)requete);
            switch (type) {
                case 1: {
                    if (item.getLongValue() == 1L) {
                        value = new Boolean(true);
                        return value;
                    } else {
                        value = new Boolean(false);
                        return value;
                    }
                }
                case 7: {
                    value = item.getDateValue();
                    return value;
                }
                case 4: {
                    value = new Double(item.getDoubleValue());
                    return value;
                }
                case 3: {
                    value = new Long(item.getLongValue());
                    return value;
                }
                case 2: {
                    value = new Integer((int)item.longValue);
                    return value;
                }
                case 5: {
                    value = item.getStringValue();
                    return value;
                }
                case 10: {
                    value = item.getByteValue();
                    return value;
                }
                default: {
                    throw new PropertyException("Type " + type + " is not supported");
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("Could not get value for key " + key + " of type " + type), (Throwable)e);
            throw new PropertyException(e.getMessage());
        }
        finally {
            if (broker != null) {
                broker.close();
            }
        }
    }

    private PersistenceBroker getBroker() throws PBFactoryException {
        PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
        return broker;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

