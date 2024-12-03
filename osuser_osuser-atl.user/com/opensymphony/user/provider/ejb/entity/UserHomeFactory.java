/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.user.provider.ejb.entity.UserLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class UserHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/User";
    public static final String JNDI_NAME = "ejb/osuser/User";
    private static UserLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static UserLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (UserLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

