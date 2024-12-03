/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.StreamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelledException
extends StreamsException {
    private static final Logger log = LoggerFactory.getLogger(CancelledException.class);

    public CancelledException() {
    }

    public CancelledException(String s) {
        super(s);
    }

    public CancelledException(String s, InterruptedException cause) {
        super(s, (Throwable)cause);
    }

    public CancelledException(InterruptedException cause) {
        super((Throwable)cause);
    }

    public static void throwIfInterrupted() throws CancelledException {
        if (Thread.interrupted()) {
            CancelledException e = new CancelledException();
            if (log.isDebugEnabled()) {
                log.debug("detected thread interrupt", (Throwable)((Object)e));
            }
            throw e;
        }
    }
}

