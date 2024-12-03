/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.salext.bundle.fileset;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FileSetUtil {
    private static final Function<String, File> PATH_TO_FILE = new Function<String, File>(){

        public File apply(@Nullable String input) {
            if (input == null) {
                throw new IllegalArgumentException("File path should not be null");
            }
            return new File(input);
        }
    };

    private FileSetUtil() {
    }

    @Nonnull
    public static File fileWithPath(@Nonnull String path) {
        return (File)PATH_TO_FILE.apply((Object)path);
    }

    @Nonnull
    public static Collection<File> filesWithPaths(@Nonnull Collection<String> paths) {
        return Lists.newArrayList((Iterable)Iterables.transform(paths, PATH_TO_FILE));
    }
}

