/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.UncategorizedDataAccessException
 */
package org.springframework.orm.jpa;

import org.springframework.dao.UncategorizedDataAccessException;

public class JpaSystemException
extends UncategorizedDataAccessException {
    public JpaSystemException(RuntimeException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }
}

