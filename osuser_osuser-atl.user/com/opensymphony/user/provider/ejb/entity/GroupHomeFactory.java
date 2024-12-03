/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.user.provider.ejb.entity.GroupLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GroupHomeFactory {
    public static final String COMP_NAME = "java:comp/env/ejb/Group";
    public static final String JNDI_NAME = "ejb/osuser/Group";
    private static GroupLocalHome cachedLocalHome = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GroupLocalHome getLocalHome() throws NamingException {
        if (cachedLocalHome == null) {
            InitialContext initialContext = new InitialContext();
            try {
                cachedLocalHome = (GroupLocalHome)initialContext.lookup(COMP_NAME);
            }
            finally {
                initialContext.close();
            }
        }
        return cachedLocalHome;
    }
}

