/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.sequence;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class SequenceEJB
implements EntityBean {
    private EntityContext context;

    public abstract void setActualCount(long var1);

    public abstract long getActualCount();

    public abstract void setName(String var1);

    public abstract String getName();

    public long getCount(int increment) {
        long temp = this.getActualCount();
        this.setActualCount(temp += (long)increment);
        return temp;
    }

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void ejbActivate() {
    }

    public String ejbCreate(String name) throws CreateException {
        this.setName(name);
        return null;
    }

    public void ejbLoad() {
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate(String name) throws CreateException {
    }

    public void ejbRemove() throws RemoveException {
    }

    public void ejbStore() {
    }

    public void unsetEntityContext() {
        this.context = null;
    }
}

