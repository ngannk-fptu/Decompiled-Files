/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.javascript;

public enum JavaScriptEscapeType {
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA(true, true),
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA(true, false),
    XHEXA_DEFAULT_TO_UHEXA(false, true),
    UHEXA(false, false);

    private final boolean useSECs;
    private final boolean useXHexa;

    private JavaScriptEscapeType(boolean useSECs, boolean useXHexa) {
        this.useSECs = useSECs;
        this.useXHexa = useXHexa;
    }

    boolean getUseSECs() {
        return this.useSECs;
    }

    boolean getUseXHexa() {
        return this.useXHexa;
    }
}

