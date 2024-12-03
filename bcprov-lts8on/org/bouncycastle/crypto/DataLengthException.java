/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.RuntimeCryptoException;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DataLengthException
extends RuntimeCryptoException {
    public DataLengthException() {
    }

    public DataLengthException(String message) {
        super(message);
    }
}

