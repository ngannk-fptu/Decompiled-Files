/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sequence.SequenceGenerator
 *  com.opensymphony.module.sequence.SequenceGeneratorHome
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 *  javax.rmi.PortableRemoteObject
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDataHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyDataLocalHome;
import com.opensymphony.module.propertyset.ejb.types.PropertyDateHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyDateLocalHome;
import com.opensymphony.module.propertyset.ejb.types.PropertyDecimalHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyDecimalLocalHome;
import com.opensymphony.module.propertyset.ejb.types.PropertyNumberHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyNumberLocalHome;
import com.opensymphony.module.propertyset.ejb.types.PropertyStringHomeFactory;
import com.opensymphony.module.propertyset.ejb.types.PropertyStringLocalHome;
import com.opensymphony.module.sequence.SequenceGenerator;
import com.opensymphony.module.sequence.SequenceGeneratorHome;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PropertyEntryEJB
implements EntityBean {
    private static final Log logger = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyEntryEJB == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyEntryEJB = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyEntryEJB")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyEntryEJB));
    private EntityContext context;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyEntryEJB;
    static /* synthetic */ Class class$com$opensymphony$module$sequence$SequenceGeneratorHome;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyNumberLocalHome;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyDateLocalHome;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyDecimalLocalHome;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyStringLocalHome;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$types$PropertyDataLocalHome;

    public abstract void setEntityId(long var1);

    public abstract long getEntityId();

    public abstract void setEntityName(String var1);

    public abstract String getEntityName();

    public abstract void setId(Long var1);

    public abstract Long getId();

    public abstract void setKey(String var1);

    public abstract String getKey();

    public abstract void setType(int var1);

    public abstract int getType();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(Serializable value) {
        int type = this.getType();
        Long id = this.getId();
        try {
            if (type == 1 || type == 2 || type == 3) {
                PropertyNumberLocalHome home = PropertyNumberHomeFactory.getLocalHome();
                home.findByPrimaryKey(id).setValue(type, value);
            } else if (type == 7) {
                PropertyDateLocalHome home = PropertyDateHomeFactory.getLocalHome();
                home.findByPrimaryKey(id).setValue(type, (Timestamp)value);
            } else if (type == 4) {
                PropertyDecimalLocalHome home = PropertyDecimalHomeFactory.getLocalHome();
                home.findByPrimaryKey(id).setValue(type, (Double)value);
            } else if (type == 5) {
                PropertyStringLocalHome home = PropertyStringHomeFactory.getLocalHome();
                home.findByPrimaryKey(id).setValue(type, (String)((Object)value));
            } else {
                PropertyDataLocalHome home = PropertyDataHomeFactory.getLocalHome();
                home.findByPrimaryKey(id).setValue(type, value);
            }
        }
        catch (Exception e) {
            logger.error((Object)"Error setting value in PropertySet", (Throwable)e);
        }
    }

    public Serializable getValue() {
        int type = this.getType();
        Long id = this.getId();
        try {
            if (type == 1 || type == 2 || type == 3) {
                PropertyNumberLocalHome home = PropertyNumberHomeFactory.getLocalHome();
                return home.findByPrimaryKey(id).getValue(type);
            }
            if (type == 7) {
                PropertyDateLocalHome home = PropertyDateHomeFactory.getLocalHome();
                return home.findByPrimaryKey(id).getValue(type);
            }
            if (type == 4) {
                PropertyDecimalLocalHome home = PropertyDecimalHomeFactory.getLocalHome();
                return home.findByPrimaryKey(id).getValue(type);
            }
            if (type == 5) {
                PropertyStringLocalHome home = PropertyStringHomeFactory.getLocalHome();
                return home.findByPrimaryKey(id).getValue(type);
            }
            PropertyDataLocalHome home = PropertyDataHomeFactory.getLocalHome();
            return home.findByPrimaryKey(id).getValue(type);
        }
        catch (Exception e) {
            logger.warn((Object)"Error getting value from PropertySet", (Throwable)e);
            return null;
        }
    }

    public void ejbActivate() {
    }

    public Long ejbCreate(String entityName, long entityId, int type, String key) throws CreateException {
        Long id = null;
        try {
            InitialContext ctx = new InitialContext();
            SequenceGeneratorHome genHome = (SequenceGeneratorHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/SequenceGenerator"), (Class)(class$com$opensymphony$module$sequence$SequenceGeneratorHome == null ? (class$com$opensymphony$module$sequence$SequenceGeneratorHome = PropertyEntryEJB.class$("com.opensymphony.module.sequence.SequenceGeneratorHome")) : class$com$opensymphony$module$sequence$SequenceGeneratorHome));
            SequenceGenerator gen = genHome.create();
            id = new Long(gen.getCount("os.PropertyEntry"));
            this.setId(id);
            this.setEntityName(entityName);
            this.setEntityId(entityId);
            this.setType(type);
            this.setKey(key);
            if (type == 1 || type == 2 || type == 3) {
                PropertyNumberLocalHome home = PropertyNumberHomeFactory.getLocalHome();
                home.create(type, id);
            } else if (type == 7) {
                PropertyDateLocalHome home = PropertyDateHomeFactory.getLocalHome();
                home.create(type, id);
            } else if (type == 4) {
                PropertyDecimalLocalHome home = PropertyDecimalHomeFactory.getLocalHome();
                home.create(type, id);
            } else if (type == 5) {
                PropertyStringLocalHome home = PropertyStringHomeFactory.getLocalHome();
                home.create(type, id);
            } else {
                PropertyDataLocalHome home = PropertyDataHomeFactory.getLocalHome();
                home.create(type, id);
            }
        }
        catch (Exception e) {
            logger.error((Object)"Error creating new PropertyEntry", (Throwable)e);
            throw new CreateException(e.toString());
        }
        return id;
    }

    public void ejbLoad() {
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate(String entityName, long entityId, int type, String key) throws CreateException {
    }

    public void ejbRemove() throws RemoveException {
        int type = this.getType();
        Long id = this.getId();
        try {
            InitialContext ctx = new InitialContext();
            if (type == 1 || type == 2 || type == 3) {
                PropertyNumberLocalHome home = (PropertyNumberLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/PropertyNumber"), (Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyNumberLocalHome == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyNumberLocalHome = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyNumberLocalHome")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyNumberLocalHome));
                home.findByPrimaryKey(id).remove();
            } else if (type == 7) {
                PropertyDateLocalHome home = (PropertyDateLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/PropertyDate"), (Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyDateLocalHome == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyDateLocalHome = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyDateLocalHome")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyDateLocalHome));
                home.findByPrimaryKey(id).remove();
            } else if (type == 4) {
                PropertyDecimalLocalHome home = (PropertyDecimalLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/PropertyDecimal"), (Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyDecimalLocalHome == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyDecimalLocalHome = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyDecimalLocalHome")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyDecimalLocalHome));
                home.findByPrimaryKey(id).remove();
            } else if (type == 5) {
                PropertyStringLocalHome home = (PropertyStringLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/PropertyString"), (Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyStringLocalHome == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyStringLocalHome = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyStringLocalHome")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyStringLocalHome));
                home.findByPrimaryKey(id).remove();
            } else {
                PropertyDataLocalHome home = (PropertyDataLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/PropertyData"), (Class)(class$com$opensymphony$module$propertyset$ejb$types$PropertyDataLocalHome == null ? (class$com$opensymphony$module$propertyset$ejb$types$PropertyDataLocalHome = PropertyEntryEJB.class$("com.opensymphony.module.propertyset.ejb.types.PropertyDataLocalHome")) : class$com$opensymphony$module$propertyset$ejb$types$PropertyDataLocalHome));
                home.findByPrimaryKey(id).remove();
            }
        }
        catch (Exception e) {
            logger.error((Object)"Error removing PropertySet", (Throwable)e);
        }
    }

    public void ejbStore() {
    }

    public void unsetEntityContext() {
        this.context = null;
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

