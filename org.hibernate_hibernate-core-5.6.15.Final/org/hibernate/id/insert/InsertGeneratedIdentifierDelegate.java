/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import java.io.Serializable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.insert.Binder;
import org.hibernate.id.insert.IdentifierGeneratingInsert;

public interface InsertGeneratedIdentifierDelegate {
    @Deprecated
    default public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
        throw new IllegalStateException("prepareIdentifierGeneratingInsert(...) was not implemented!");
    }

    default public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert(SqlStringGenerationContext context) {
        return this.prepareIdentifierGeneratingInsert();
    }

    public Serializable performInsert(String var1, SharedSessionContractImplementor var2, Binder var3);
}

