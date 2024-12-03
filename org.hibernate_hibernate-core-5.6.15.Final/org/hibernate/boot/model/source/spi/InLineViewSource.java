/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.TableSpecificationSource;

public interface InLineViewSource
extends TableSpecificationSource {
    public String getSelectStatement();

    public String getLogicalName();
}

