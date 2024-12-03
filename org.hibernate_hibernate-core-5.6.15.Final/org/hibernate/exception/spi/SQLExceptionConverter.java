/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import java.io.Serializable;
import java.sql.SQLException;
import org.hibernate.JDBCException;

public interface SQLExceptionConverter
extends Serializable {
    public JDBCException convert(SQLException var1, String var2, String var3);
}

