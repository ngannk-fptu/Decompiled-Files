/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Query
 *  net.sf.hibernate.Session
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import java.io.Serializable;
import java.util.List;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

public class HibernatePropertySetDAOUtils {
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$hibernate$PropertySetItem;

    public static PropertySetItem getItem(Session session, String entityName, Long entityId, String key) throws HibernateException {
        return (PropertySetItem)session.load(class$com$opensymphony$module$propertyset$hibernate$PropertySetItem == null ? (class$com$opensymphony$module$propertyset$hibernate$PropertySetItem = HibernatePropertySetDAOUtils.class$("com.opensymphony.module.propertyset.hibernate.PropertySetItem")) : class$com$opensymphony$module$propertyset$hibernate$PropertySetItem, (Serializable)new PropertySetItem(entityName, entityId, key));
    }

    public static List getKeysImpl(Session session, String entityName, Long entityId, String prefix, int type) throws HibernateException {
        Query query;
        if (prefix != null && type > 0) {
            query = session.getNamedQuery("all_keys_with_type_like");
            query.setString("like", prefix + '%');
            query.setInteger("type", type);
        } else if (prefix != null) {
            query = session.getNamedQuery("all_keys_like");
            query.setString("like", prefix + '%');
        } else if (type > 0) {
            query = session.getNamedQuery("all_keys_with_type");
            query.setInteger("type", type);
        } else {
            query = session.getNamedQuery("all_keys");
        }
        query.setString("entityName", entityName);
        query.setLong("entityId", entityId.longValue());
        return query.list();
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

