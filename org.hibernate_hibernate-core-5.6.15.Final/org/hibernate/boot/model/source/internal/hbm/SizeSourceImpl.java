/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.spi.SizeSource;

public class SizeSourceImpl
implements SizeSource {
    private final Integer length;
    private final Integer scale;
    private final Integer precision;

    public SizeSourceImpl(Integer length, Integer scale, Integer precision) {
        this.length = length;
        this.scale = scale;
        this.precision = precision;
    }

    @Override
    public Integer getLength() {
        return this.length;
    }

    @Override
    public Integer getPrecision() {
        return this.precision;
    }

    @Override
    public Integer getScale() {
        return this.scale;
    }
}

