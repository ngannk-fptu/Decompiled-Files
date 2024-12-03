/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception;

import java.sql.SQLException;
import org.hibernate.exception.LockAcquisitionException;

public class LockTimeoutException
extends LockAcquisitionException {
    public LockTimeoutException(String string, SQLException root) {
        super(string, root);
    }

    public LockTimeoutException(String string, SQLException root, String sql) {
        super(string, root, sql);
    }
}

