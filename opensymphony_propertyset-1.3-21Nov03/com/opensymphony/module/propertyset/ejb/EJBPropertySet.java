/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.DataUtil
 *  com.opensymphony.util.EJBUtils
 *  javax.ejb.CreateException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.ejb;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.ejb.PropertyStore;
import com.opensymphony.module.propertyset.ejb.PropertyStoreHome;
import com.opensymphony.util.DataUtil;
import com.opensymphony.util.EJBUtils;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import javax.ejb.CreateException;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EJBPropertySet
extends AbstractPropertySet
implements Serializable {
    private static final Log logger = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$ejb$EJBPropertySet == null ? (class$com$opensymphony$module$propertyset$ejb$EJBPropertySet = EJBPropertySet.class$("com.opensymphony.module.propertyset.ejb.EJBPropertySet")) : class$com$opensymphony$module$propertyset$ejb$EJBPropertySet));
    private PropertyStore store;
    private String entityName;
    private long entityId;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$EJBPropertySet;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        try {
            return this.store.getKeys(this.entityName, this.entityId, prefix, type);
        }
        catch (RemoteException re) {
            throw new PropertyImplementationException(re);
        }
    }

    public int getType(String key) throws PropertyException {
        try {
            return this.store.getType(this.entityName, this.entityId, key);
        }
        catch (RemoteException re) {
            throw new PropertyImplementationException(re);
        }
    }

    public boolean exists(String key) throws PropertyException {
        try {
            return this.store.exists(this.entityName, this.entityId, key);
        }
        catch (RemoteException re) {
            throw new PropertyImplementationException(re);
        }
    }

    public void init(Map config, Map args) {
        this.entityId = DataUtil.getLong((Long)((Long)args.get("entityId")));
        this.entityName = (String)args.get("entityName");
        String storeLocation = (String)config.get("storeLocation");
        if (storeLocation == null) {
            storeLocation = "PropertyStore";
        }
        try {
            PropertyStoreHome home = (PropertyStoreHome)EJBUtils.lookup((String)storeLocation, (Class)(class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome == null ? (class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome = EJBPropertySet.class$("com.opensymphony.module.propertyset.ejb.PropertyStoreHome")) : class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome));
            this.store = home.create();
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (CreateException e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) throws PropertyException {
        try {
            this.store.removeEntry(this.entityName, this.entityId, key);
        }
        catch (RemoteException re) {
            throw new PropertyImplementationException(re);
        }
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        try {
            this.store.set(this.entityName, this.entityId, type, key, (Serializable)value);
        }
        catch (RemoteException re) {
            logger.error((Object)"RemoteExecption while setting property", (Throwable)re);
            throw new PropertyImplementationException(re);
        }
    }

    protected Object get(int type, String key) throws PropertyException {
        try {
            return this.store.get(this.entityName, this.entityId, type, key);
        }
        catch (RemoteException re) {
            throw new PropertyImplementationException(re);
        }
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

