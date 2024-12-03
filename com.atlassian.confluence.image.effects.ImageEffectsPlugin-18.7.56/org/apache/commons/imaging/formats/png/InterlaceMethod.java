/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

public enum InterlaceMethod {
    NONE(false),
    ADAM7(true);

    private final boolean progressive;

    private InterlaceMethod(boolean progressive) {
        this.progressive = progressive;
    }

    public boolean isProgressive() {
        return this.progressive;
    }
}

