/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import org.apache.hc.core5.util.Tokenizer;

public class ParserCursor
extends Tokenizer.Cursor {
    public ParserCursor(int lowerBound, int upperBound) {
        super(lowerBound, upperBound);
    }
}

