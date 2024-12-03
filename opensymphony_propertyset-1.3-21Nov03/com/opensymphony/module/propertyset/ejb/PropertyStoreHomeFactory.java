/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.rmi.PortableRemoteObject
 */
package com.opensymphony.module.propertyset.ejb;

import com.opensymphony.module.propertyset.ejb.PropertyStoreHome;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class PropertyStoreHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyStore";
    public static final String JNDI_NAME = "PropertyStore";
    private static PropertyStoreHome cachedRemoteHome = null;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyStoreHome getHome() throws NamingException {
        if (cachedRemoteHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                Object objRef = initialContext.lookup(COMP_NAME);
                cachedRemoteHome = (PropertyStoreHome)PortableRemoteObject.narrow((Object)objRef, (Class)(class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome == null ? (class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome = PropertyStoreHomeFactory.class$("com.opensymphony.module.propertyset.ejb.PropertyStoreHome")) : class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome));
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
    public static PropertyStoreHome getHome(Hashtable environment) throws NamingException {
        InitialContext initialContext = new InitialContext(environment);
        try {
            Object objRef = initialContext.lookup(COMP_NAME);
            PropertyStoreHome propertyStoreHome = (PropertyStoreHome)PortableRemoteObject.narrow((Object)objRef, (Class)(class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome == null ? (class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome = PropertyStoreHomeFactory.class$("com.opensymphony.module.propertyset.ejb.PropertyStoreHome")) : class$com$opensymphony$module$propertyset$ejb$PropertyStoreHome));
            return propertyStoreHome;
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

