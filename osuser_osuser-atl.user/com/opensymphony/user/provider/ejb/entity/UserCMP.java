/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.user.provider.ejb.entity.UserEJB;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class UserCMP
extends UserEJB
implements EntityBean {
    public void ejbLoad() {
    }

    public void ejbStore() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void setEntityContext(EntityContext ctx) {
        super.setEntityContext(ctx);
    }

    public void unsetEntityContext() {
        super.unsetEntityContext();
    }

    public void ejbRemove() throws RemoveException {
    }

    public abstract Long getId();

    public abstract void setId(Long var1);

    public abstract String getName();

    public abstract void setName(String var1);

    public abstract String getPasswordHash();

    public abstract void setPasswordHash(String var1);
}

