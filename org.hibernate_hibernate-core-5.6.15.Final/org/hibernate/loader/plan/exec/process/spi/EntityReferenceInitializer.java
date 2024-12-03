/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.spi.EntityReference;

public interface EntityReferenceInitializer {
    public EntityReference getEntityReference();

    public void hydrateIdentifier(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;

    public void resolveEntityKey(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;

    public void hydrateEntityState(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;

    public void finishUpRow(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;
}

