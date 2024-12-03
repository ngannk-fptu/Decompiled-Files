/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.net.URISyntaxException;
import java.util.BitSet;
import org.apache.hc.core5.util.Tokenizer;

final class URISupport {
    static final BitSet HOST_SEPARATORS = new BitSet(256);
    static final BitSet IPV6_HOST_TERMINATORS = new BitSet(256);
    static final BitSet PORT_SEPARATORS = new BitSet(256);
    static final BitSet TERMINATORS = new BitSet(256);

    URISupport() {
    }

    static URISyntaxException createException(CharSequence input, Tokenizer.Cursor cursor, String reason) {
        return new URISyntaxException(input.subSequence(cursor.getLowerBound(), cursor.getUpperBound()).toString(), reason, cursor.getPos());
    }

    static {
        TERMINATORS.set(47);
        TERMINATORS.set(35);
        TERMINATORS.set(63);
        HOST_SEPARATORS.or(TERMINATORS);
        HOST_SEPARATORS.set(64);
        IPV6_HOST_TERMINATORS.set(93);
        PORT_SEPARATORS.or(TERMINATORS);
        PORT_SEPARATORS.set(58);
    }
}

