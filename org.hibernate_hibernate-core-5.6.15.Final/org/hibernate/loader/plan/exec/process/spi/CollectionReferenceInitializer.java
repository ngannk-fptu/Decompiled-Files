/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.spi.CollectionReference;

public interface CollectionReferenceInitializer {
    public void finishUpRow(ResultSet var1, ResultSetProcessingContextImpl var2);

    public CollectionReference getCollectionReference();

    public void endLoading(ResultSetProcessingContextImpl var1);
}

