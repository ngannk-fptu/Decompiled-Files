/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io;

import java.io.File;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;

@Deprecated
public class FileCleaner {
    private static final FileCleaningTracker INSTANCE = new FileCleaningTracker();

    @Deprecated
    public static synchronized void exitWhenFinished() {
        INSTANCE.exitWhenFinished();
    }

    public static FileCleaningTracker getInstance() {
        return INSTANCE;
    }

    @Deprecated
    public static int getTrackCount() {
        return INSTANCE.getTrackCount();
    }

    @Deprecated
    public static void track(File file, Object marker) {
        INSTANCE.track(file, marker);
    }

    @Deprecated
    public static void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
        INSTANCE.track(file, marker, deleteStrategy);
    }

    @Deprecated
    public static void track(String path, Object marker) {
        INSTANCE.track(path, marker);
    }

    @Deprecated
    public static void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
        INSTANCE.track(path, marker, deleteStrategy);
    }
}

