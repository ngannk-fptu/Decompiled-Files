/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

public enum TypeSearchability {
    NONE,
    FULL,
    CHAR,
    BASIC;


    public static TypeSearchability interpret(short code) {
        switch (code) {
            case 3: {
                return FULL;
            }
            case 0: {
                return NONE;
            }
            case 2: {
                return BASIC;
            }
            case 1: {
                return CHAR;
            }
        }
        throw new IllegalArgumentException("Unknown type searchability code [" + code + "] enountered");
    }
}

