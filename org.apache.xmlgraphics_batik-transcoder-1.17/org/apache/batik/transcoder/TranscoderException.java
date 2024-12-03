/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

public class TranscoderException
extends Exception {
    protected Exception ex;

    public TranscoderException(String s) {
        this(s, null);
    }

    public TranscoderException(Exception ex) {
        this(null, ex);
    }

    public TranscoderException(String s, Exception ex) {
        super(s, ex);
        this.ex = ex;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (this.ex != null) {
            msg = msg + "\nEnclosed Exception:\n";
            msg = msg + this.ex.getMessage();
        }
        return msg;
    }

    public Exception getException() {
        return this.ex;
    }
}

