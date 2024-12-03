/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.springframework.dao.UncategorizedDataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.hibernate5;

import org.hibernate.HibernateException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

public class HibernateSystemException
extends UncategorizedDataAccessException {
    public HibernateSystemException(@Nullable HibernateException cause) {
        super(cause != null ? cause.getMessage() : null, (Throwable)cause);
    }
}

