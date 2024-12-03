/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.FinderException
 *  javax.ejb.ObjectNotFoundException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.ejb;

import com.opensymphony.module.propertyset.DuplicatePropertyKeyException;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.ejb.types.PropertyEntryHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyEntryLocal;
import com.opensymphony.module.propertyset.ejb.types.PropertyEntryLocalHome;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyStoreEJB
implements SessionBean {
    private static final Log logger = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$ejb$PropertyStoreEJB == null ? (class$com$opensymphony$module$propertyset$ejb$PropertyStoreEJB = PropertyStoreEJB.class$("com.opensymphony.module.propertyset.ejb.PropertyStoreEJB")) : class$com$opensymphony$module$propertyset$ejb$PropertyStoreEJB));
    private PropertyEntryLocalHome entryHome;
    private SessionContext context;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$PropertyStoreEJB;

    public Collection getKeys(String entityName, long entityId, String prefix, int type) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("getKeys(" + entityName + "," + entityId + ")"));
            }
            ArrayList<String> results = new ArrayList<String>();
            Iterator entries = this.entryHome.findByNameAndId(entityName, entityId).iterator();
            while (entries.hasNext()) {
                PropertyEntryLocal entry = (PropertyEntryLocal)entries.next();
                String key = entry.getKey();
                if (prefix != null && !key.startsWith(prefix) || type != 0 && type != entry.getType()) continue;
                results.add(key);
            }
            Collections.sort(results);
            return results;
        }
        catch (FinderException e) {
            logger.error((Object)"Could not find keys.", (Throwable)e);
            throw new PropertyImplementationException(e);
        }
    }

    public void setSessionContext(SessionContext ctx) {
        try {
            this.entryHome = PropertyEntryHomeFactory.getLocalHome();
        }
        catch (NamingException e) {
            logger.fatal((Object)"Could not lookup PropertyEntryHome.", (Throwable)e);
            throw new EJBException((Exception)e);
        }
        this.context = ctx;
    }

    public int getType(String entityName, long entityId, String key) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getType(" + entityName + "," + entityId + ",\"" + key + "\")"));
        }
        try {
            return this.entryHome.findByEntity(entityName, entityId, key).getType();
        }
        catch (ObjectNotFoundException e) {
            return 0;
        }
        catch (FinderException e) {
            logger.error((Object)"Could not find type.", (Throwable)e);
            throw new PropertyImplementationException(e);
        }
    }

    public void ejbActivate() {
    }

    public void ejbCreate() throws CreateException {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
    }

    public boolean exists(String entityName, long entityId, String key) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("exists(" + entityName + "," + entityId + ",\"" + key + "\")"));
        }
        return this.getType(entityName, entityId, key) != 0;
    }

    public Serializable get(String entityName, long entityId, int type, String key) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("get(" + entityName + "," + entityId + "," + type + ",\"" + key + "\")"));
        }
        try {
            PropertyEntryLocal entry = this.entryHome.findByEntity(entityName, entityId, key);
            if (type != entry.getType()) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)"wrong property type");
                }
                throw new InvalidPropertyTypeException();
            }
            return entry.getValue();
        }
        catch (ObjectNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"no property found");
            }
            return null;
        }
        catch (PropertyException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error((Object)"Could not retrieve value.", (Throwable)e);
            throw new PropertyImplementationException(e);
        }
    }

    public void removeEntry(String entityName, long entityId, String key) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("remove(" + entityName + "," + entityId + ",\"" + key + "\")"));
        }
        try {
            this.entryHome.findByEntity(entityName, entityId, key).remove();
        }
        catch (ObjectNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Value did not exist anyway.");
            }
        }
        catch (PropertyException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error((Object)"Could not remove value.", (Throwable)e);
            throw new PropertyImplementationException("Could not remove value.", e);
        }
    }

    /*
     * WARNING - void declaration
     */
    public void set(String entityName, long entityId, int type, String key, Serializable value) {
        void var7_6;
        PropertyEntryLocal entry;
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("set(" + entityName + "," + entityId + "," + type + ",\"" + key + "\", [" + value + "] )"));
        }
        if (value == null) {
            this.removeEntry(entityName, entityId, key);
            return;
        }
        try {
            entry = this.entryHome.findByEntity(entityName, entityId, key);
            if (entry.getType() != type) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)"property is of different type");
                }
                throw new DuplicatePropertyKeyException();
            }
        }
        catch (ObjectNotFoundException e) {
            try {
                entry = this.entryHome.create(entityName, entityId, type, key);
            }
            catch (CreateException ce) {
                logger.error((Object)"Could not create new property.", (Throwable)ce);
                throw new PropertyImplementationException("Could not create new property.", ce);
            }
        }
        catch (PropertyException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error((Object)"Could not set property.", (Throwable)e);
            throw new PropertyImplementationException("Could not set property.", e);
        }
        var7_6.setValue(value);
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

