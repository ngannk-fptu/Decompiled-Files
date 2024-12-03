/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

public abstract class VerifierConstraintViolatedException
extends RuntimeException {
    private static final long serialVersionUID = 2946136970490179465L;
    private String detailMessage;

    VerifierConstraintViolatedException() {
    }

    VerifierConstraintViolatedException(String message) {
        super(message);
        this.detailMessage = message;
    }

    VerifierConstraintViolatedException(String message, Throwable initCause) {
        super(message, initCause);
        this.detailMessage = message;
    }

    public void extendMessage(String pre, String post) {
        if (pre == null) {
            pre = "";
        }
        if (this.detailMessage == null) {
            this.detailMessage = "";
        }
        if (post == null) {
            post = "";
        }
        this.detailMessage = pre + this.detailMessage + post;
    }

    @Override
    public String getMessage() {
        return this.detailMessage;
    }
}

