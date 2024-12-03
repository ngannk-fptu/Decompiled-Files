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

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import java.io.Serializable;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class NumberEntityEJB
implements EntityBean {
    private EntityContext context;

    public abstract void setId(Long var1);

    public abstract Long getId();

    public abstract void setNumber(long var1);

    public abstract long getNumber();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(int type, Serializable value) {
        if (value == null) {
            this.setNumber(0L);
            return;
        }
        try {
            switch (type) {
                case 1: {
                    this.setNumber((Boolean)value != false ? 1L : 0L);
                    return;
                }
                case 2: 
                case 3: {
                    this.setNumber(((Number)value).longValue());
                    return;
                }
            }
            throw new PropertyImplementationException("Cannot store this type of property.");
        }
        catch (ClassCastException ce) {
            throw new IllegalPropertyException("Cannot cast value to appropriate type for persistence.");
        }
    }

    public Serializable getValue(int type) {
        long value = this.getNumber();
        switch (type) {
            case 1: {
                return value == 0L ? Boolean.FALSE : Boolean.TRUE;
            }
            case 2: {
                return new Integer((int)value);
            }
            case 3: {
                return new Long(value);
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
        return new int[]{1, 2, 3};
    }
}

