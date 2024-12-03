/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.InputStream;
import org.bouncycastle.asn1.IndefiniteLengthInputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
abstract class LimitedInputStream
extends InputStream {
    protected final InputStream _in;
    private int _limit;

    LimitedInputStream(InputStream in, int limit) {
        this._in = in;
        this._limit = limit;
    }

    int getLimit() {
        return this._limit;
    }

    protected void setParentEofDetect(boolean on) {
        if (this._in instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream)this._in).setEofOn00(on);
        }
    }
}

