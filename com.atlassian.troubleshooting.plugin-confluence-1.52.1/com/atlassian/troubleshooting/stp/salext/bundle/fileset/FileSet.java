/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.salext.bundle.fileset;

import java.io.File;
import java.util.Set;
import javax.annotation.Nonnull;

public interface FileSet {
    @Nonnull
    public Set<File> getFiles();
}

