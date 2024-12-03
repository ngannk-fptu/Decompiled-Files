/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.opensymphony.user.provider.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.hibernate.HibernateBaseProvider;
import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class HibernateProfileProvider
extends HibernateBaseProvider
implements ProfileProvider {
    private String entityName;

    public PropertySet getPropertySet(String name) {
        HashMap<String, Object> args = new HashMap<String, Object>();
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        if (user != null) {
            args.put("entityId", new Long(user.getId()));
            args.put("entityName", this.entityName + "_user");
        } else {
            HibernateGroup group = this.getGroupDAO().findGroupByGroupname(name);
            args.put("entityId", new Long(group.getId()));
            args.put("entityName", this.entityName + "_group");
        }
        args.put("configurationProvider", this.configProvider);
        PropertySet propertySet = PropertySetManager.getInstance((String)"hibernate", args);
        return propertySet;
    }

    public boolean create(String name) {
        return false;
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        HibernateGroup group;
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        result = user != null ? true : (group = this.getGroupDAO().findGroupByGroupname(name)) != null;
        return result;
    }

    public boolean init(Properties properties) {
        boolean result = super.init(properties);
        this.entityName = properties.getProperty("propertySetEntity", "OSUser");
        return result;
    }

    public List list() {
        return null;
    }

    public boolean remove(String name) {
        return false;
    }
}

