/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface DerivedValueSource
extends RelationalValueSource {
    public String getExpression();
}

