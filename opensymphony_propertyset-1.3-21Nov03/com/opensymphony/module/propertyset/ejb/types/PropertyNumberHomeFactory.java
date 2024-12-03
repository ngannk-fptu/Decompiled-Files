/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyNumberLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PropertyNumberHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyNumber";
    public static final String JNDI_NAME = "PropertyNumber";
    private static PropertyNumberLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyNumberLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (PropertyNumberLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

