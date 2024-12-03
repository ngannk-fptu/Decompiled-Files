/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.sql.Connection;

public interface Validator {
    public boolean validate(Connection var1, int var2);
}

