/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.exc.WstxException;
import java.io.IOException;

public class WstxIOException
extends WstxException {
    public WstxIOException(IOException ie) {
        super(ie);
    }

    public WstxIOException(String msg) {
        super(msg);
    }
}

