/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

public enum TypeNullability {
    NULLABLE,
    NON_NULLABLE,
    UNKNOWN;


    public static TypeNullability interpret(short code) {
        switch (code) {
            case 1: {
                return NULLABLE;
            }
            case 0: {
                return NON_NULLABLE;
            }
            case 2: {
                return UNKNOWN;
            }
        }
        throw new IllegalArgumentException("Unknown type nullability code [" + code + "] enountered");
    }
}

