/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.boot.model.relational.QualifiedSequenceName;

public interface SequenceInformation {
    public QualifiedSequenceName getSequenceName();

    @Deprecated
    default public int getIncrementSize() {
        Long incrementSize = this.getIncrementValue();
        return incrementSize != null ? incrementSize.intValue() : -1;
    }

    public Long getStartValue();

    public Long getMinValue();

    public Long getMaxValue();

    public Long getIncrementValue();
}

