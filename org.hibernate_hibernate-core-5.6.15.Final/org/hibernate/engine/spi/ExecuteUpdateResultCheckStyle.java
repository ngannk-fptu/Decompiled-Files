/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

public enum ExecuteUpdateResultCheckStyle {
    NONE("none"),
    COUNT("rowcount"),
    PARAM("param");

    private final String name;

    private ExecuteUpdateResultCheckStyle(String name) {
        this.name = name;
    }

    public String externalName() {
        return this.name;
    }

    public static ExecuteUpdateResultCheckStyle fromExternalName(String name) {
        if (name.equalsIgnoreCase(ExecuteUpdateResultCheckStyle.NONE.name)) {
            return NONE;
        }
        if (name.equalsIgnoreCase(ExecuteUpdateResultCheckStyle.COUNT.name)) {
            return COUNT;
        }
        if (name.equalsIgnoreCase(ExecuteUpdateResultCheckStyle.PARAM.name)) {
            return PARAM;
        }
        return null;
    }

    public static ExecuteUpdateResultCheckStyle determineDefault(String customSql, boolean callable) {
        if (customSql == null) {
            return COUNT;
        }
        return callable ? PARAM : COUNT;
    }
}

