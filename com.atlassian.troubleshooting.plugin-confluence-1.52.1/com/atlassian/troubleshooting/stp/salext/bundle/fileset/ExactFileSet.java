/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.salext.bundle.fileset;

import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSet;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSetUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class ExactFileSet
implements FileSet {
    private final Set<File> files;

    private ExactFileSet(Collection<File> files) {
        this.files = new HashSet<File>(files);
    }

    public static ExactFileSet ofFiles(File ... files) {
        return new ExactFileSet(Arrays.asList(files));
    }

    public static ExactFileSet ofFiles(Collection<File> files) {
        return new ExactFileSet(files);
    }

    public static ExactFileSet ofPaths(String ... paths) {
        return ExactFileSet.ofFiles(FileSetUtil.filesWithPaths(Arrays.asList(paths)));
    }

    public static ExactFileSet ofPaths(Collection<String> paths) {
        return ExactFileSet.ofFiles(FileSetUtil.filesWithPaths(paths));
    }

    @Override
    @Nonnull
    public Set<File> getFiles() {
        return Collections.unmodifiableSet(this.files);
    }
}

