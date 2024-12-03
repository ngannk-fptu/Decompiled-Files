/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.DerivedValueSource;

public interface RelationalValueSource {
    public String getContainingTableName();

    public Nature getNature();

    public static enum Nature {
        COLUMN(ColumnSource.class),
        DERIVED(DerivedValueSource.class);

        private final Class<? extends RelationalValueSource> specificContractClass;

        private Nature(Class<? extends RelationalValueSource> specificContractClass) {
            this.specificContractClass = specificContractClass;
        }

        public Class<? extends RelationalValueSource> getSpecificContractClass() {
            return this.specificContractClass;
        }
    }
}

