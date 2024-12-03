/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyEntryEJB;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class PropertyEntryCMP
extends PropertyEntryEJB
implements EntityBean {
    public void ejbLoad() {
        super.ejbLoad();
    }

    public void ejbStore() {
        super.ejbStore();
    }

    public void ejbActivate() {
        super.ejbActivate();
    }

    public void ejbPassivate() {
        super.ejbPassivate();
    }

    public void setEntityContext(EntityContext ctx) {
        super.setEntityContext(ctx);
    }

    public void unsetEntityContext() {
        super.unsetEntityContext();
    }

    public void ejbRemove() throws RemoveException {
        super.ejbRemove();
    }

    public abstract long getEntityId();

    public abstract void setEntityId(long var1);

    public abstract String getEntityName();

    public abstract void setEntityName(String var1);

    public abstract Long getId();

    public abstract void setId(Long var1);

    public abstract String getKey();

    public abstract void setKey(String var1);

    public abstract int getType();

    public abstract void setType(int var1);
}

