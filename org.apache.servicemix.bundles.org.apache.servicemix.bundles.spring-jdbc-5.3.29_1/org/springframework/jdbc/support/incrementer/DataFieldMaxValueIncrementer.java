/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 */
package org.springframework.jdbc.support.incrementer;

import org.springframework.dao.DataAccessException;

public interface DataFieldMaxValueIncrementer {
    public int nextIntValue() throws DataAccessException;

    public long nextLongValue() throws DataAccessException;

    public String nextStringValue() throws DataAccessException;
}

