/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;

public interface BulkInsertionCapableIdentifierGenerator
extends IdentifierGenerator {
    public boolean supportsBulkInsertionIdentifierGeneration();

    default public String determineBulkInsertionIdentifierGenerationSelectFragment(Dialect dialect) {
        throw new IllegalStateException("determineBulkInsertionIdentifierGenerationSelectFragment(...) was not implemented!");
    }

    default public String determineBulkInsertionIdentifierGenerationSelectFragment(SqlStringGenerationContext context) {
        return this.determineBulkInsertionIdentifierGenerationSelectFragment(context.getDialect());
    }
}

