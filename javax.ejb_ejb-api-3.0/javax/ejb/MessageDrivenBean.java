/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;
import javax.ejb.MessageDrivenContext;

public interface MessageDrivenBean
extends EnterpriseBean {
    public void setMessageDrivenContext(MessageDrivenContext var1) throws EJBException;

    public void ejbRemove() throws EJBException;
}

