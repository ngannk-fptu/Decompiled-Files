/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@FunctionalInterface
public interface ParameterMapper {
    public Map<String, ?> createMap(Connection var1) throws SQLException;
}

