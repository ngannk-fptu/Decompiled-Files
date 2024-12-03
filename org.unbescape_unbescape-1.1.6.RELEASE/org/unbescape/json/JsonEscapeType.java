/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.json;

public enum JsonEscapeType {
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA(true),
    UHEXA(false);

    private final boolean useSECs;

    private JsonEscapeType(boolean useSECs) {
        this.useSECs = useSECs;
    }

    boolean getUseSECs() {
        return this.useSECs;
    }
}

