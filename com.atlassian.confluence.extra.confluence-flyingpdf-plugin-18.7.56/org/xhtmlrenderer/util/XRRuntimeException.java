/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import org.xhtmlrenderer.util.XRLog;

public class XRRuntimeException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public XRRuntimeException(String msg) {
        super(msg);
        this.log(msg);
    }

    public XRRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
        this.log(msg, cause);
    }

    private void log(String msg) {
        XRLog.exception("Unhandled exception. " + msg);
    }

    private void log(String msg, Throwable cause) {
        XRLog.exception("Unhandled exception. " + msg, cause);
    }
}

