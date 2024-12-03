/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBHome;

public interface EJBMetaData {
    public EJBHome getEJBHome();

    public Class getHomeInterfaceClass();

    public Class getRemoteInterfaceClass();

    public Class getPrimaryKeyClass();

    public boolean isSession();

    public boolean isStatelessSession();
}

