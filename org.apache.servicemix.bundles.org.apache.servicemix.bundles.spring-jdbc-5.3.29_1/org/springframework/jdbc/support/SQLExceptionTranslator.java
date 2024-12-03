/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SQLExceptionTranslator {
    @Nullable
    public DataAccessException translate(String var1, @Nullable String var2, SQLException var3);
}

