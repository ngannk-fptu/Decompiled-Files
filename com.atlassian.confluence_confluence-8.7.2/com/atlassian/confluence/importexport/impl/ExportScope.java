/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

public enum ExportScope {
    PAGE,
    SPACE,
    ALL,
    SITE;


    public String getString() {
        return this.name().toLowerCase();
    }

    public static ExportScope getScopeFromPropertyValue(String string) throws IllegalExportScopeException {
        if (string == null) {
            throw new IllegalExportScopeException();
        }
        for (ExportScope exportScope : ExportScope.values()) {
            if (!exportScope.name().equalsIgnoreCase(string)) continue;
            return exportScope;
        }
        throw new IllegalExportScopeException(string);
    }

    public static class IllegalExportScopeException
    extends Exception {
        public IllegalExportScopeException(String providedString) {
            super("Inappropriate string value provided for exportScope: '" + providedString + "'. Expected page, space or all");
        }

        public IllegalExportScopeException() {
            super("No export scope was provided. Expected page, space or all.");
        }
    }
}

