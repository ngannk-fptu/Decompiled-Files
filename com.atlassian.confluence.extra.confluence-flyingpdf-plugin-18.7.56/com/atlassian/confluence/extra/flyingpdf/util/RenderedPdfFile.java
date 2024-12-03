/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class RenderedPdfFile
implements Serializable {
    private final File file;
    private final int numPages;

    public static RenderedPdfFile withKnownSize(File file, int numPages) {
        return new RenderedPdfFile(file, numPages);
    }

    private RenderedPdfFile(File file, Integer numPages) {
        this.file = file;
        this.numPages = numPages;
    }

    public File getFile() {
        return this.file;
    }

    public int getNumPages() {
        return this.numPages;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RenderedPdfFile that = (RenderedPdfFile)o;
        return Objects.equals(this.file, that.file) && Objects.equals(this.numPages, that.numPages);
    }

    public int hashCode() {
        return Objects.hash(this.file, this.numPages);
    }
}

