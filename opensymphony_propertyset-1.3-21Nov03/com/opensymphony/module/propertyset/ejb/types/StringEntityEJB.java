/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.PropertyImplementationException;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class StringEntityEJB
implements EntityBean {
    private EntityContext context;

    public abstract void setId(Long var1);

    public abstract Long getId();

    public abstract void setString(String var1);

    public abstract String getString();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(int type, String value) {
        if (value == null) {
            this.setString("");
            return;
        }
        if (type != 5) {
            throw new PropertyImplementationException("Cannot store this type of property.");
        }
        this.setString(value);
    }

    public String getValue(int type) {
        if (type == 5) {
            return this.getString();
        }
        throw new PropertyImplementationException("Cannot retrieve this type of property.");
    }

    public void ejbActivate() {
    }

    public Long ejbCreate(int type, long id) throws CreateException {
        this.setId(new Long(id));
        this.setValue(type, null);
        return null;
    }

    public void ejbLoad() {
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate(int type, long id) throws CreateException {
    }

    public void ejbRemove() throws RemoveException {
    }

    public void ejbStore() {
    }

    public void unsetEntityContext() {
        this.context = null;
    }

    protected int[] allowedTypes() {
        return new int[]{5};
    }
}

