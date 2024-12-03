/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatContext;

public class FilenameGenerator {
    private String path;
    private long value;
    private String suffix;

    public FilenameGenerator(String path, String initialValue, String suffix) throws NumberFormatException {
        ApfloatContext ctx;
        if (path == null) {
            ctx = ApfloatContext.getContext();
            path = ctx.getProperty("filePath");
        }
        if (initialValue == null) {
            ctx = ApfloatContext.getContext();
            initialValue = ctx.getProperty("fileInitialValue");
        }
        if (suffix == null) {
            ctx = ApfloatContext.getContext();
            suffix = ctx.getProperty("fileSuffix");
        }
        this.path = path;
        this.value = Long.parseLong(initialValue);
        this.suffix = suffix;
    }

    public synchronized String generateFilename() {
        return this.path + this.value++ + this.suffix;
    }

    public String getPath() {
        return this.path;
    }

    public synchronized String getInitialValue() {
        return String.valueOf(this.value);
    }

    public String getSuffix() {
        return this.suffix;
    }
}

