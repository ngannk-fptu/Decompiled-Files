/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.exc.WstxParsingException;
import javax.xml.stream.Location;

public class WstxEOFException
extends WstxParsingException {
    public WstxEOFException(String msg, Location loc) {
        super(msg, loc);
    }
}

