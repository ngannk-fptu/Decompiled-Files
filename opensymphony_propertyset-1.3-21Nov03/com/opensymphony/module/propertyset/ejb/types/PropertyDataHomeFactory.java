/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDataLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyDataHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyData";
    public static final String JNDI_NAME = "PropertyData";
    private static PropertyDataLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyDataLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyDataLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

