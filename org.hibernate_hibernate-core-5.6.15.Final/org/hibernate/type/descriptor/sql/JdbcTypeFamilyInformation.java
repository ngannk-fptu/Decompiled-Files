/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.util.concurrent.ConcurrentHashMap;

public class JdbcTypeFamilyInformation {
    public static final JdbcTypeFamilyInformation INSTANCE = new JdbcTypeFamilyInformation();
    private ConcurrentHashMap<Integer, Family> typeCodeToFamilyMap = new ConcurrentHashMap();

    public Family locateJdbcTypeFamilyByTypeCode(int typeCode) {
        return this.typeCodeToFamilyMap.get(typeCode);
    }

    public static enum Family {
        BINARY(-2, -3, -4),
        NUMERIC(-5, 3, 8, 6, 4, 2, 7, 5, -6),
        CHARACTER(1, -16, -1, -15, -9, 12),
        DATETIME(91, 92, 93),
        CLOB(2005, 2011);

        private final int[] typeCodes;

        private Family(int ... typeCodes) {
            this.typeCodes = typeCodes;
            for (int typeCode : typeCodes) {
                INSTANCE.typeCodeToFamilyMap.put(typeCode, this);
            }
        }

        public int[] getTypeCodes() {
            return this.typeCodes;
        }
    }
}

