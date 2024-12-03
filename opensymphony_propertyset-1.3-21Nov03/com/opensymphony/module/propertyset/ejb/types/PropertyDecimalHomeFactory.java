/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDecimalLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyDecimalHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyDecimal";
    public static final String JNDI_NAME = "PropertyDecimal";
    private static PropertyDecimalLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyDecimalLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyDecimalLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

