/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

import java.util.Map;
import org.apache.batik.transcoder.DefaultErrorHandler;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscodingHints;

public class TranscoderSupport {
    static final ErrorHandler defaultErrorHandler = new DefaultErrorHandler();
    protected TranscodingHints hints = new TranscodingHints();
    protected ErrorHandler handler = defaultErrorHandler;

    public TranscodingHints getTranscodingHints() {
        return new TranscodingHints((Map)this.hints);
    }

    public void addTranscodingHint(TranscodingHints.Key key, Object value) {
        this.hints.put(key, value);
    }

    public void removeTranscodingHint(TranscodingHints.Key key) {
        this.hints.remove(key);
    }

    public void setTranscodingHints(Map hints) {
        this.hints.putAll(hints);
    }

    public void setTranscodingHints(TranscodingHints hints) {
        this.hints = hints;
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.handler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return this.handler;
    }
}

