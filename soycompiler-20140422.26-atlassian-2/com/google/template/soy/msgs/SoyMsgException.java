/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.msgs;

public class SoyMsgException
extends RuntimeException {
    private String fileOrResourceName = null;

    public SoyMsgException(String message) {
        super(message);
    }

    public SoyMsgException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoyMsgException(Throwable cause) {
        super(cause);
    }

    public void setFileOrResourceName(String fileOrResourceName) {
        this.fileOrResourceName = fileOrResourceName;
    }

    public String getFileOrResourceName() {
        return this.fileOrResourceName;
    }

    @Override
    public String getMessage() {
        if (this.fileOrResourceName != null) {
            return "While processing \"" + this.fileOrResourceName + "\": " + super.getMessage();
        }
        return super.getMessage();
    }
}

