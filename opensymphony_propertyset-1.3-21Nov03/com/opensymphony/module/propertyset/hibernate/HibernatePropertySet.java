/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;
import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import com.opensymphony.util.ClassLoaderUtil;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernatePropertySet
extends AbstractPropertySet {
    protected static Log log = LogFactory.getLog((String)(class$com$opensymphony$module$propertyset$hibernate$HibernatePropertySet == null ? (class$com$opensymphony$module$propertyset$hibernate$HibernatePropertySet = HibernatePropertySet.class$("com.opensymphony.module.propertyset.hibernate.HibernatePropertySet")) : class$com$opensymphony$module$propertyset$hibernate$HibernatePropertySet).getName());
    private HibernateConfigurationProvider configProvider;
    private Long entityId;
    private String entityName;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$hibernate$HibernatePropertySet;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        return this.configProvider.getPropertySetDAO().getKeys(this.entityName, this.entityId, prefix, type);
    }

    public int getType(String key) throws PropertyException {
        return this.findByKey(key).getType();
    }

    public boolean exists(String key) throws PropertyException {
        try {
            this.findByKey(key);
            return true;
        }
        catch (PropertyException e) {
            return false;
        }
    }

    public void init(Map config, Map args) {
        super.init(config, args);
        this.entityId = (Long)args.get("entityId");
        this.entityName = (String)args.get("entityName");
        this.configProvider = (HibernateConfigurationProvider)args.get("configurationProvider");
        if (this.configProvider == null) {
            String configProviderClass = (String)config.get("configuration.provider.class");
            if (configProviderClass != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Setting up property set provider of class: " + configProviderClass));
                }
                try {
                    this.configProvider = (HibernateConfigurationProvider)ClassLoaderUtil.loadClass((String)configProviderClass, this.getClass()).newInstance();
                }
                catch (Exception e) {
                    log.error((Object)("Unable to load configuration provider class: " + configProviderClass), (Throwable)e);
                    return;
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Setting up property set with DefaultHibernateConfigurationProvider");
                }
                this.configProvider = new DefaultHibernateConfigurationProvider();
            }
            this.configProvider.setupConfiguration(config);
        } else if (log.isDebugEnabled()) {
            log.debug((Object)"Setting up property set with hibernate provider passed in args.");
        }
    }

    public void remove(String key) throws PropertyException {
        this.configProvider.getPropertySetDAO().remove(this.entityName, this.entityId, key);
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        PropertySetItem item = null;
        boolean update = true;
        item = this.configProvider.getPropertySetDAO().findByKey(this.entityName, this.entityId, key);
        if (item == null) {
            update = false;
            item = new PropertySetItem(this.entityName, this.entityId, key);
        } else if (item.getType() != type) {
            throw new PropertyException("Existing key '" + key + "' does not have matching type of " + type);
        }
        switch (type) {
            case 1: {
                item.setBooleanVal((Boolean)value);
                break;
            }
            case 4: {
                item.setDoubleVal((Double)value);
                break;
            }
            case 5: 
            case 6: {
                item.setStringVal((String)value);
                break;
            }
            case 3: {
                item.setLongVal((Long)value);
                break;
            }
            case 2: {
                item.setIntVal((Integer)value);
                break;
            }
            case 7: {
                item.setDateVal((Date)value);
                break;
            }
            default: {
                throw new PropertyException("type " + type + " not supported");
            }
        }
        item.setType(type);
        this.configProvider.getPropertySetDAO().setImpl(item, update);
    }

    protected Object get(int type, String key) throws PropertyException {
        PropertySetItem item = this.findByKey(key);
        if (item == null) {
            return null;
        }
        if (item.getType() != type) {
            throw new PropertyException("key '" + key + "' does not have matching type of " + type);
        }
        switch (type) {
            case 1: {
                return new Boolean(item.getBooleanVal());
            }
            case 4: {
                return new Double(item.getDoubleVal());
            }
            case 5: 
            case 6: {
                return item.getStringVal();
            }
            case 3: {
                return new Long(item.getLongVal());
            }
            case 2: {
                return new Integer(item.getIntVal());
            }
            case 7: {
                return item.getDateVal();
            }
        }
        throw new PropertyException("type " + type + " not supported");
    }

    private PropertySetItem findByKey(String key) throws PropertyException {
        return this.configProvider.getPropertySetDAO().findByKey(this.entityName, this.entityId, key);
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

