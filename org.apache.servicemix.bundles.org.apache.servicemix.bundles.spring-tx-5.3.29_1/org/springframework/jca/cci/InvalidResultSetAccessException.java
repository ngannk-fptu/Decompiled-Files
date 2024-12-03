/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jca.cci;

import java.sql.SQLException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

@Deprecated
public class InvalidResultSetAccessException
extends InvalidDataAccessResourceUsageException {
    public InvalidResultSetAccessException(String msg, SQLException ex) {
        super(ex.getMessage(), ex);
    }
}

