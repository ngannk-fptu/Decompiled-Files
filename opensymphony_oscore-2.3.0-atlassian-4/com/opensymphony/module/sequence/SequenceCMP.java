/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.sequence;

import com.opensymphony.module.sequence.SequenceEJB;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class SequenceCMP
extends SequenceEJB
implements EntityBean {
    @Override
    public abstract void setActualCount(long var1);

    @Override
    public abstract long getActualCount();

    @Override
    public abstract void setName(String var1);

    @Override
    public abstract String getName();

    @Override
    public void setEntityContext(EntityContext ctx) {
        super.setEntityContext(ctx);
    }

    @Override
    public void ejbActivate() {
        super.ejbActivate();
    }

    @Override
    public void ejbLoad() {
        super.ejbLoad();
    }

    @Override
    public void ejbPassivate() {
        super.ejbPassivate();
    }

    @Override
    public void ejbRemove() throws RemoveException {
        super.ejbRemove();
    }

    @Override
    public void ejbStore() {
        super.ejbStore();
    }

    @Override
    public void unsetEntityContext() {
        super.unsetEntityContext();
    }
}

