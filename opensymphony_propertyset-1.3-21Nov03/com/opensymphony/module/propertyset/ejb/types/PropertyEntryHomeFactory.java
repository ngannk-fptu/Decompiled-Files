/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyEntryLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyEntryHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyEntry";
    public static final String JNDI_NAME = "PropertyEntry";
    private static PropertyEntryLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyEntryLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyEntryLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

