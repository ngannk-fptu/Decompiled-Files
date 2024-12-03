/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

public class SequenceInformationImpl
implements SequenceInformation {
    private final QualifiedSequenceName sequenceName;
    private final Long startValue;
    private final Long minValue;
    private final Long maxValue;
    private final Long incrementValue;

    public SequenceInformationImpl(QualifiedSequenceName sequenceName, Long startValue, Long minValue, Long maxValue, Long incrementValue) {
        this.sequenceName = sequenceName;
        this.startValue = startValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.incrementValue = incrementValue;
    }

    @Override
    public QualifiedSequenceName getSequenceName() {
        return this.sequenceName;
    }

    @Override
    public Long getStartValue() {
        return this.startValue;
    }

    @Override
    public Long getMinValue() {
        return this.minValue;
    }

    @Override
    public Long getMaxValue() {
        return this.maxValue;
    }

    @Override
    public Long getIncrementValue() {
        return this.incrementValue;
    }
}

