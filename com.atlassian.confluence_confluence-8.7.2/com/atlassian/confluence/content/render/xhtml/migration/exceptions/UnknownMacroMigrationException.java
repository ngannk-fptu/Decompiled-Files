/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

public class UnknownMacroMigrationException
extends RuntimeException {
    private final String macroName;

    public UnknownMacroMigrationException(String macroName) {
        super("The macro '" + macroName + "' is unknown.");
        this.macroName = macroName;
    }

    public String getMacroName() {
        return this.macroName;
    }
}

