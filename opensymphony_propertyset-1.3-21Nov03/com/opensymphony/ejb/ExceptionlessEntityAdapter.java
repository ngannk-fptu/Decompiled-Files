/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.ejb;

import com.opensymphony.ejb.AbstractEntityAdapter;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class ExceptionlessEntityAdapter
extends AbstractEntityAdapter
implements EntityBean {
    public void setEntityContext(EntityContext context) {
        this.setContext(context);
    }

    public void ejbActivate() {
    }

    public void ejbLoad() {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() throws RemoveException {
    }

    public void ejbStore() {
    }

    public void unsetEntityContext() {
        this.context = null;
    }

    protected EntityContext getEntityContext() {
        return this.context;
    }
}

