/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import java.util.Arrays;

public final class AssertionViolatedException
extends RuntimeException {
    private static final long serialVersionUID = -129822266349567409L;
    private String detailMessage;

    public static void main(String[] args) {
        AssertionViolatedException ave = new AssertionViolatedException(Arrays.toString(args));
        ave.extendMessage("\nFOUND:\n\t", "\nExiting!!\n");
        throw ave;
    }

    public AssertionViolatedException() {
    }

    public AssertionViolatedException(String message) {
        message = "INTERNAL ERROR: " + message;
        super(message);
        this.detailMessage = message;
    }

    public AssertionViolatedException(String message, Throwable initCause) {
        message = "INTERNAL ERROR: " + message;
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

