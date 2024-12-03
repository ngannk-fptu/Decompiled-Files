/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import java.sql.SQLException;

public interface ViolatedConstraintNameExtracter {
    public String extractConstraintName(SQLException var1);
}

