/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.QueryException
 *  org.springframework.dao.InvalidDataAccessResourceUsageException
 */
package org.springframework.orm.hibernate5;

import org.hibernate.QueryException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

public class HibernateQueryException
extends InvalidDataAccessResourceUsageException {
    public HibernateQueryException(QueryException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }

    public String getQueryString() {
        return ((QueryException)this.getCause()).getQueryString();
    }
}

