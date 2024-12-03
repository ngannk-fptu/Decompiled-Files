/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.rmi.PortableRemoteObject
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.user.provider.ejb.UserManagerHome;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class UserManagerHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/UserManager";
    public static final String JNDI_NAME = "ejb/osuser/Manager";
    private static UserManagerHome cachedRemoteHome = null;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$UserManagerHome;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static UserManagerHome getHome() throws NamingException {
        if (cachedRemoteHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                Object objRef = initialContext.lookup(COMP_NAME);
                cachedRemoteHome = (UserManagerHome)PortableRemoteObject.narrow((Object)objRef, (Class)(class$com$opensymphony$user$provider$ejb$UserManagerHome == null ? (class$com$opensymphony$user$provider$ejb$UserManagerHome = UserManagerHomeFactory.class$("com.opensymphony.user.provider.ejb.UserManagerHome")) : class$com$opensymphony$user$provider$ejb$UserManagerHome));
            }
            finally {
                initialContext.close();
            }
        }
        return cachedRemoteHome;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static UserManagerHome getHome(Hashtable environment) throws NamingException {
        InitialContext initialContext = new InitialContext(environment);
        try {
            Object objRef = initialContext.lookup(COMP_NAME);
            UserManagerHome userManagerHome = (UserManagerHome)PortableRemoteObject.narrow((Object)objRef, (Class)(class$com$opensymphony$user$provider$ejb$UserManagerHome == null ? (class$com$opensymphony$user$provider$ejb$UserManagerHome = UserManagerHomeFactory.class$("com.opensymphony.user.provider.ejb.UserManagerHome")) : class$com$opensymphony$user$provider$ejb$UserManagerHome));
            return userManagerHome;
        }
        finally {
            initialContext.close();
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

