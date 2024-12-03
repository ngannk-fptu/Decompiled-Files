/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1ParsingException
extends IllegalStateException {
    private Throwable cause;

    public ASN1ParsingException(String message) {
        super(message);
    }

    public ASN1ParsingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

