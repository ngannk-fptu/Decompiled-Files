/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.spi.AfterLoadAction;

public interface RowReader {
    public Object readRow(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;

    public void finishUp(ResultSetProcessingContextImpl var1, List<AfterLoadAction> var2);
}

