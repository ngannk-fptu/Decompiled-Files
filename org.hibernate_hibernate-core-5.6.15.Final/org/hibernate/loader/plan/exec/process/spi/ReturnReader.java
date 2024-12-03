/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;

public interface ReturnReader {
    public Object read(ResultSet var1, ResultSetProcessingContext var2) throws SQLException;
}

