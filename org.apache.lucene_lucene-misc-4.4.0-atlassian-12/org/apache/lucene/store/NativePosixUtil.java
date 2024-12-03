/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class NativePosixUtil {
    public static final int NORMAL = 0;
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 2;
    public static final int WILLNEED = 3;
    public static final int DONTNEED = 4;
    public static final int NOREUSE = 5;

    private static native int posix_fadvise(FileDescriptor var0, long var1, long var3, int var5) throws IOException;

    public static native int posix_madvise(ByteBuffer var0, int var1) throws IOException;

    public static native int madvise(ByteBuffer var0, int var1) throws IOException;

    public static native FileDescriptor open_direct(String var0, boolean var1) throws IOException;

    public static native long pread(FileDescriptor var0, long var1, ByteBuffer var3) throws IOException;

    public static void advise(FileDescriptor fd, long offset, long len, int advise) throws IOException {
        int code = NativePosixUtil.posix_fadvise(fd, offset, len, advise);
        if (code != 0) {
            throw new RuntimeException("posix_fadvise failed code=" + code);
        }
    }

    static {
        System.loadLibrary("NativePosixUtil");
    }
}

