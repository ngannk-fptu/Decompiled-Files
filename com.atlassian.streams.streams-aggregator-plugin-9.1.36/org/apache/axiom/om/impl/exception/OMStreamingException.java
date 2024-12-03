/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.exception;

import org.apache.axiom.om.OMException;

public class OMStreamingException
extends OMException {
    private static final long serialVersionUID = 8108888406034145092L;

    public OMStreamingException() {
    }

    public OMStreamingException(String message) {
        super(message);
    }

    public OMStreamingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OMStreamingException(Throwable cause) {
        super(cause);
    }
}

