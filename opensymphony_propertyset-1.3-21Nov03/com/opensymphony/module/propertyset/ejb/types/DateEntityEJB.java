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
import java.sql.Timestamp;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class DateEntityEJB
implements EntityBean {
    private EntityContext context;

    public abstract void setDate(Timestamp var1);

    public abstract Timestamp getDate();

    public abstract void setId(Long var1);

    public abstract Long getId();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(int type, Timestamp value) {
        if (value == null) {
            this.setDate(new Timestamp(0L));
            return;
        }
        if (type == 7) {
            this.setDate(value);
            return;
        }
        throw new PropertyImplementationException("Cannot store this type of property.");
    }

    public Timestamp getValue(int type) {
        switch (type) {
            case 7: {
                return this.getDate();
            }
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
        return new int[]{7};
    }
}

