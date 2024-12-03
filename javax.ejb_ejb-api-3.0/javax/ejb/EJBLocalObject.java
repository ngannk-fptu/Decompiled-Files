/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.RemoveException;

public interface EJBLocalObject {
    public EJBLocalHome getEJBLocalHome() throws EJBException;

    public Object getPrimaryKey() throws EJBException;

    public void remove() throws RemoveException, EJBException;

    public boolean isIdentical(EJBLocalObject var1) throws EJBException;
}

