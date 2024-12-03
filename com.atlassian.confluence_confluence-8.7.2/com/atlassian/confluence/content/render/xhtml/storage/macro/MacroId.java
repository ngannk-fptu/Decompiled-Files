/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

public class MacroId {
    private final String macroId;

    public static MacroId fromString(String macroId) {
        return new MacroId(macroId);
    }

    private MacroId(String macroId) {
        assert (macroId != null);
        this.macroId = macroId;
    }

    public String getId() {
        return this.macroId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroId b = (MacroId)o;
        return this.macroId.equals(b.macroId);
    }

    public int hashCode() {
        return this.macroId.hashCode();
    }
}

