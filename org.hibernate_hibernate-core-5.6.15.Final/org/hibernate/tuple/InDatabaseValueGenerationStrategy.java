/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.tuple.GenerationTiming;

public interface InDatabaseValueGenerationStrategy {
    public GenerationTiming getGenerationTiming();

    public boolean referenceColumnsInSql();

    public String[] getReferencedColumnValues();
}

