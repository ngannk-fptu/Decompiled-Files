/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Hibernate {
    private static final Logger log = LoggerFactory.getLogger(Hibernate.class);
    private static final boolean HIBERNATE_PRESENT = Hibernate.isHibernatePresent();

    public static Class<?> getClass(Object obj) {
        if (HIBERNATE_PRESENT) {
            return org.hibernate.Hibernate.getClass((Object)obj);
        }
        return obj.getClass();
    }

    private static boolean isHibernatePresent() {
        try {
            Class.forName("org.hibernate.Hibernate");
            log.debug("Hibernate is present");
            return true;
        }
        catch (ClassNotFoundException ex) {
            log.debug("Hibernate is not present");
            return false;
        }
    }
}

