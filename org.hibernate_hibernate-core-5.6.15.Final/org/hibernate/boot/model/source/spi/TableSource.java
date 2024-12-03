/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.TableSpecificationSource;

public interface TableSource
extends TableSpecificationSource {
    public String getExplicitTableName();

    public String getRowId();

    public String getCheckConstraint();
}

