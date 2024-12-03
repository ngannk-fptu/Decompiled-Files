/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;

public interface ColumnsAndFormulasSourceContainer {
    public RelationalValueSourceHelper.ColumnsAndFormulasSource getColumnsAndFormulasSource();
}

