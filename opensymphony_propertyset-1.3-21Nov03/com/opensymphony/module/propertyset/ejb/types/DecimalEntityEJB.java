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

public abstract class DecimalEntityEJB
implements EntityBean {
    private EntityContext context;

    public abstract void setDecimal(double var1);

    public abstract double getDecimal();

    public abstract void setId(Long var1);

    public abstract Long getId();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(int type, Double value) {
        if (value == null) {
            this.setDecimal(0.0);
            return;
        }
        if (type != 4) {
            throw new PropertyImplementationException("Cannot store this type of property.");
        }
        this.setDecimal(value);
    }

    public Double getValue(int type) {
        if (type == 4) {
            return new Double(this.getDecimal());
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
        return new int[]{4};
    }
}

