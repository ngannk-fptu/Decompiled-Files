/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.hibernate.loader.plan.spi.CollectionReturn;

public class CollectionReturnReader
implements ReturnReader {
    private final CollectionReturn collectionReturn;

    public CollectionReturnReader(CollectionReturn collectionReturn) {
        this.collectionReturn = collectionReturn;
    }

    @Override
    public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
        return null;
    }
}

