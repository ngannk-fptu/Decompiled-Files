/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import org.apache.tika.exception.TikaException;

public class TikaEmitterException
extends TikaException {
    public TikaEmitterException(String msg) {
        super(msg);
    }

    public TikaEmitterException(String msg, Throwable t) {
        super(msg, t);
    }
}

