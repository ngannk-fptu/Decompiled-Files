/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class AgeFileFilter
extends AbstractFileFilter
implements Serializable {
    private static final long serialVersionUID = -2132740084016138541L;
    private final boolean acceptOlder;
    private final Instant cutoffInstant;

    public AgeFileFilter(Date cutoffDate) {
        this(cutoffDate, true);
    }

    public AgeFileFilter(Date cutoffDate, boolean acceptOlder) {
        this(cutoffDate.toInstant(), acceptOlder);
    }

    public AgeFileFilter(File cutoffReference) {
        this(cutoffReference, true);
    }

    public AgeFileFilter(File cutoffReference, boolean acceptOlder) {
        this(FileUtils.lastModifiedUnchecked(cutoffReference), acceptOlder);
    }

    public AgeFileFilter(Instant cutoffInstant) {
        this(cutoffInstant, true);
    }

    public AgeFileFilter(Instant cutoffInstant, boolean acceptOlder) {
        this.acceptOlder = acceptOlder;
        this.cutoffInstant = cutoffInstant;
    }

    public AgeFileFilter(long cutoffMillis) {
        this(Instant.ofEpochMilli(cutoffMillis), true);
    }

    public AgeFileFilter(long cutoffMillis, boolean acceptOlder) {
        this(Instant.ofEpochMilli(cutoffMillis), acceptOlder);
    }

    @Override
    public boolean accept(File file) {
        return this.acceptOlder != FileUtils.isFileNewer(file, this.cutoffInstant);
    }

    @Override
    public FileVisitResult accept(Path file, BasicFileAttributes attributes) {
        return this.get(() -> this.toFileVisitResult(this.acceptOlder != PathUtils.isNewer(file, this.cutoffInstant, new LinkOption[0])));
    }

    @Override
    public String toString() {
        String condition = this.acceptOlder ? "<=" : ">";
        return super.toString() + "(" + condition + this.cutoffInstant + ")";
    }
}

