/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class EncoderException
extends IllegalStateException {
    private Throwable cause;

    EncoderException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

