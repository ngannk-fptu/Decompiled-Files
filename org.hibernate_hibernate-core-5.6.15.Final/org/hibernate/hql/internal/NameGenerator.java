/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public final class NameGenerator {
    private NameGenerator() {
    }

    public static String[][] generateColumnNames(Type[] types, SessionFactoryImplementor f) throws MappingException {
        String[][] columnNames = new String[types.length][];
        for (int i = 0; i < types.length; ++i) {
            int span = types[i].getColumnSpan(f);
            columnNames[i] = new String[span];
            for (int j = 0; j < span; ++j) {
                columnNames[i][j] = NameGenerator.scalarName(i, j);
            }
        }
        return columnNames;
    }

    public static String scalarName(int x, int y) {
        return NameGenerator.scalarName("col_" + x, y);
    }

    public static String scalarName(String base, int num) {
        return base + '_' + num + '_';
    }

    public static String[] scalarNames(String base, int count) {
        String[] names = new String[count];
        for (int j = 0; j < count; ++j) {
            names[j] = NameGenerator.scalarName(base, j);
        }
        return names;
    }

    public static String[] scalarNames(int uniqueness, int count) {
        return NameGenerator.scalarNames("col_" + uniqueness, count);
    }
}

