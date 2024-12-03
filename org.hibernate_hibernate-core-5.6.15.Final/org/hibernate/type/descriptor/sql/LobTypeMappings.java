/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.util.Locale;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;

public final class LobTypeMappings {
    @Deprecated
    public static final LobTypeMappings INSTANCE = new LobTypeMappings();

    private LobTypeMappings() {
    }

    @Deprecated
    public boolean hasCorrespondingLobCode(int jdbcTypeCode) {
        return LobTypeMappings.isMappedToKnownLobCode(jdbcTypeCode);
    }

    @Deprecated
    public int getCorrespondingLobCode(int jdbcTypeCode) {
        return LobTypeMappings.getLobCodeTypeMapping(jdbcTypeCode);
    }

    public static boolean isMappedToKnownLobCode(int jdbcTypeCode) {
        return jdbcTypeCode == 2004 || jdbcTypeCode == -2 || jdbcTypeCode == -3 || jdbcTypeCode == -4 || jdbcTypeCode == 2005 || jdbcTypeCode == 1 || jdbcTypeCode == 12 || jdbcTypeCode == -1 || jdbcTypeCode == 2011 || jdbcTypeCode == -15 || jdbcTypeCode == -9 || jdbcTypeCode == -16;
    }

    public static int getLobCodeTypeMapping(int jdbcTypeCode) {
        switch (jdbcTypeCode) {
            case -4: 
            case -3: 
            case -2: 
            case 2004: {
                return 2004;
            }
            case -1: 
            case 1: 
            case 12: 
            case 2005: {
                return 2005;
            }
            case -16: 
            case -15: 
            case -9: 
            case 2011: {
                return 2011;
            }
        }
        throw new IllegalArgumentException(String.format(Locale.ROOT, "JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent", jdbcTypeCode, JdbcTypeNameMapper.getTypeName(jdbcTypeCode)));
    }
}

