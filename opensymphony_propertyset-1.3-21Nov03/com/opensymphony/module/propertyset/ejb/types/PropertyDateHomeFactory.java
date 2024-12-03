/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDateLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyDateHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyDate";
    public static final String JNDI_NAME = "PropertyDate";
    private static PropertyDateLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyDateLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyDateLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

