/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.hibernate.extras;

import org.hibernate.HibernateException;
import org.hibernate.Session;

@Deprecated
public interface ExportHibernateHandle {
    public Object get(Session var1) throws HibernateException;

    public Class getClazz();
}

