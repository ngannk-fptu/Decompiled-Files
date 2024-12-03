/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.PostInsertIdentifierGenerator;

public abstract class AbstractPostInsertGenerator
implements PostInsertIdentifierGenerator,
BulkInsertionCapableIdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        return IdentifierGeneratorHelper.POST_INSERT_INDICATOR;
    }

    @Override
    public boolean supportsBulkInsertionIdentifierGeneration() {
        return true;
    }

    @Override
    public String determineBulkInsertionIdentifierGenerationSelectFragment(SqlStringGenerationContext context) {
        return null;
    }
}

