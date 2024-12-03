/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import java.sql.SQLException;
import org.hibernate.JDBCException;

public interface SQLExceptionConversionDelegate {
    public JDBCException convert(SQLException var1, String var2, String var3);
}

