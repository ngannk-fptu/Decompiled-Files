/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.enhanced;

import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.ExportableProducer;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.Optimizer;

public interface DatabaseStructure
extends ExportableProducer {
    @Deprecated
    default public String getName() {
        return this.getPhysicalName().render();
    }

    public QualifiedName getPhysicalName();

    public int getTimesAccessed();

    public int getInitialValue();

    public int getIncrementSize();

    public AccessCallback buildCallback(SharedSessionContractImplementor var1);

    @Deprecated
    default public void prepare(Optimizer optimizer) {
    }

    default public void configure(Optimizer optimizer) {
        this.prepare(optimizer);
    }

    @Override
    public void registerExportables(Database var1);

    default public void initialize(SqlStringGenerationContext context) {
    }

    public boolean isPhysicalSequence();

    @Deprecated
    default public String[] getAllSqlForTests() {
        return new String[0];
    }
}

