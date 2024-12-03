/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import java.nio.file.FileSystemException;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class InsecureRecursiveDeleteException
extends FileSystemException {
    public InsecureRecursiveDeleteException(@CheckForNull String file) {
        super(file, null, "unable to guarantee security of recursive delete");
    }
}

