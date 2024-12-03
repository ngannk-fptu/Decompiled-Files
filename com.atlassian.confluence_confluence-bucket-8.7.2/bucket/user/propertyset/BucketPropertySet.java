/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.module.propertyset.AbstractPropertySet
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO
 *  com.opensymphony.module.propertyset.hibernate.PropertySetItem
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package bucket.user.propertyset;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BucketPropertySet
extends AbstractPropertySet
implements Serializable {
    protected static final Log log = LogFactory.getLog((String)BucketPropertySet.class.getName());
    private transient HibernatePropertySetDAO propertySetDAO;
    private Long entityId;
    private String entityName;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        return this.getPropertySetDAO().getKeys(this.entityName, this.entityId, prefix, type);
    }

    public int getType(String key) throws PropertyException {
        return this.getByKey(key).getType();
    }

    public boolean exists(String key) throws PropertyException {
        try {
            return this.getByKey(key) != null;
        }
        catch (PropertyException e) {
            return false;
        }
    }

    public void init(Map config, Map args) {
        super.init(config, args);
        this.entityId = (Long)args.get("entityId");
        this.entityName = (String)args.get("entityName");
    }

    public void remove(String key) throws PropertyException {
        this.getPropertySetDAO().remove(this.entityName, this.entityId, key);
    }

    public void remove() throws PropertyException {
        Collection allKeys = this.getKeys();
        for (String key : allKeys) {
            this.getPropertySetDAO().remove(this.entityName, this.entityId, key);
        }
    }

    public boolean supportsType(int type) {
        switch (type) {
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return false;
            }
        }
        return true;
    }

    private boolean isStringType(int type) {
        return type == 5 || type == 6;
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        boolean update = true;
        BucketPropertySetItem item = this.getByKey(key);
        if (item == null) {
            update = false;
            item = new BucketPropertySetItem(this.entityName, this.entityId, key);
        } else if (!this.isStringType(item.getType()) && item.getType() != type || this.isStringType(item.getType()) && !this.isStringType(type)) {
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
            case 5: {
                item.setStringVal((String)value);
                break;
            }
            case 6: {
                item.setTextVal((String)value);
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
        this.getPropertySetDAO().setImpl((PropertySetItem)item, update);
    }

    protected Object get(int type, String key) throws PropertyException {
        BucketPropertySetItem item = this.getByKey(key);
        if (item == null) {
            return null;
        }
        if (item.getType() != type) {
            throw new PropertyException("key '" + key + "' does not have matching type of " + type);
        }
        switch (type) {
            case 1: {
                return item.getBooleanVal();
            }
            case 4: {
                return item.getDoubleVal();
            }
            case 5: {
                return item.getStringVal();
            }
            case 6: {
                return item.getTextVal();
            }
            case 3: {
                return item.getLongVal();
            }
            case 2: {
                return item.getIntVal();
            }
            case 7: {
                return item.getDateVal();
            }
        }
        throw new PropertyException("type " + type + " not supported");
    }

    public void setText(String key, String value) {
        if (value != null && value.length() <= 255) {
            this.setString(key, value);
        } else {
            super.setText(key, value);
        }
    }

    public String getText(String key) {
        BucketPropertySetItem item = this.getByKey(key);
        if (item == null) {
            return null;
        }
        if (item.getType() == 5) {
            return item.getStringVal();
        }
        if (item.getType() == 6) {
            return item.getTextVal();
        }
        throw new PropertyException("key '" + key + "' does not have matching type of " + item.getType());
    }

    public BucketPropertySetItem getByKey(String key) throws PropertyException {
        return (BucketPropertySetItem)this.getPropertySetDAO().findByKey(this.entityName, this.entityId, key);
    }

    private HibernatePropertySetDAO getPropertySetDAO() {
        if (this.propertySetDAO == null) {
            try {
                this.propertySetDAO = (HibernatePropertySetDAO)ContainerManager.getInstance().getContainerContext().getComponent((Object)"propertySetDao");
            }
            catch (ComponentNotFoundException e) {
                log.error((Object)("Failed to find HibernatePropertySetDAO: " + e.getMessage()), (Throwable)e);
                throw new IllegalStateException("Failed to find HibernatePropertySetDAO: " + e.getMessage());
            }
        }
        return this.propertySetDAO;
    }
}

