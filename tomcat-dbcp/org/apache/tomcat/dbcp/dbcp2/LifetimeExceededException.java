/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;

final class LifetimeExceededException
extends SQLException {
    private static final long serialVersionUID = -3783783104516492659L;

    LifetimeExceededException() {
    }

    LifetimeExceededException(String reason) {
        super(reason);
    }
}

