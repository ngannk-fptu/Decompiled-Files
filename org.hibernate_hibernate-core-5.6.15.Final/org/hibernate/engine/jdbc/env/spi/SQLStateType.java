/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

public enum SQLStateType {
    XOpen,
    SQL99,
    UNKNOWN;


    public static SQLStateType interpretReportedSQLStateType(int sqlStateType) {
        switch (sqlStateType) {
            case 2: {
                return SQL99;
            }
            case 1: {
                return XOpen;
            }
        }
        return UNKNOWN;
    }
}

