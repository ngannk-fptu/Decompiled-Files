/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data;

public class SoyDataException
extends RuntimeException {
    private String dataPath;

    public SoyDataException(String message) {
        this(null, message);
    }

    public SoyDataException(String dataPath, String message) {
        super(message);
        this.dataPath = dataPath;
    }

    public SoyDataException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public SoyDataException(String dataPath, String message, Throwable cause) {
        super(message, cause);
        this.dataPath = dataPath;
    }

    public void prependKeyToDataPath(String key) {
        this.dataPath = this.dataPath == null ? key : key + (this.dataPath.charAt(0) == '[' ? "" : ".") + this.dataPath;
    }

    public void prependIndexToDataPath(int index) {
        this.prependKeyToDataPath("[" + index + "]");
    }

    @Override
    public String getMessage() {
        return this.dataPath == null ? super.getMessage() : "At data path '" + this.dataPath + "': " + super.getMessage();
    }
}

