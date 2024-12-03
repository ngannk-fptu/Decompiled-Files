/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyStringLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyStringHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyString";
    public static final String JNDI_NAME = "PropertyString";
    private static PropertyStringLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyStringLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyStringLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

