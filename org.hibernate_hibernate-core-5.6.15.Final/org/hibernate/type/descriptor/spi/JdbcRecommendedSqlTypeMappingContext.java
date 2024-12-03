/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EnumType
 */
package org.hibernate.type.descriptor.spi;

import javax.persistence.EnumType;
import org.hibernate.type.spi.TypeConfiguration;

public interface JdbcRecommendedSqlTypeMappingContext {
    default public boolean isNationalized() {
        return false;
    }

    default public boolean isLob() {
        return false;
    }

    default public EnumType getEnumeratedType() {
        return EnumType.ORDINAL;
    }

    default public int getPreferredSqlTypeCodeForBoolean() {
        return 16;
    }

    public TypeConfiguration getTypeConfiguration();
}

