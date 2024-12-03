/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.ColumnBindingDefaults;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface RelationalValueSourceContainer
extends ColumnBindingDefaults {
    public List<RelationalValueSource> getRelationalValueSources();
}

