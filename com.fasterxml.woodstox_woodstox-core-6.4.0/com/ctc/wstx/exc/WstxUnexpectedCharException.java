/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.exc.WstxParsingException;
import javax.xml.stream.Location;

public class WstxUnexpectedCharException
extends WstxParsingException {
    final char mChar;

    public WstxUnexpectedCharException(String msg, Location loc, char c) {
        super(msg, loc);
        this.mChar = c;
    }

    public char getChar() {
        return this.mChar;
    }
}

