/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.DecimalEntityEJB;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class PropertyDecimalCMP
extends DecimalEntityEJB
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

    public abstract double getDecimal();

    public abstract void setDecimal(double var1);

    public abstract Long getId();

    public abstract void setId(Long var1);
}

