/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.exc.WstxException;
import java.io.IOException;

public class WstxIOException
extends WstxException {
    private static final long serialVersionUID = 1L;

    public WstxIOException(IOException ie) {
        super(ie);
    }

    public WstxIOException(String msg) {
        super(msg);
    }
}

