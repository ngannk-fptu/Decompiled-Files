/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.xhtml;

public enum MacroMigrationPoint {
    VIEW("view");

    private final String point;

    private MacroMigrationPoint(String point) {
        this.point = point;
    }

    public static MacroMigrationPoint of(String point) {
        for (MacroMigrationPoint builtInPoint : MacroMigrationPoint.values()) {
            if (!builtInPoint.point.equals(point)) continue;
            return builtInPoint;
        }
        throw new IllegalArgumentException("Unknown macro migration point: " + point);
    }
}

