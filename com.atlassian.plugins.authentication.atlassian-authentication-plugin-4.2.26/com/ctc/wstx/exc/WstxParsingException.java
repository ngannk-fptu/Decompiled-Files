/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.exc.WstxException;
import javax.xml.stream.Location;

public class WstxParsingException
extends WstxException {
    public WstxParsingException(String msg, Location loc) {
        super(msg, loc);
    }

    public WstxParsingException(String msg) {
        super(msg);
    }
}

