/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.h2;

import java.sql.SQLException;

public class H2QueryFailedException
extends RuntimeException {
    public H2QueryFailedException(String s, SQLException e) {
        super(s, e);
    }
}

