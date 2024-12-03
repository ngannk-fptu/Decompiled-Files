/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;
import javax.ejb.RemoveException;

public interface EJBLocalHome {
    public void remove(Object var1) throws RemoveException, EJBException;
}

