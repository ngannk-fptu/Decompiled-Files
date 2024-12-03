/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Function
 *  com.sun.jna.Native
 *  com.sun.jna.NativeLibrary
 *  com.sun.jna.Pointer
 */
package com.sun.jna.platform.unix;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class LibCUtil {
    private static final NativeLibrary LIBC = NativeLibrary.getInstance((String)"c");
    private static Function mmap = null;
    private static boolean mmap64 = false;
    private static Function ftruncate = null;
    private static boolean ftruncate64 = false;

    private LibCUtil() {
    }

    public static Pointer mmap(Pointer addr, long length, int prot, int flags, int fd, long offset) {
        Object[] params = new Object[6];
        params[0] = addr;
        if (Native.SIZE_T_SIZE == 4) {
            LibCUtil.require32Bit(length, "length");
            params[1] = (int)length;
        } else {
            params[1] = length;
        }
        params[2] = prot;
        params[3] = flags;
        params[4] = fd;
        if (mmap64 || Native.LONG_SIZE > 4) {
            params[5] = offset;
        } else {
            LibCUtil.require32Bit(offset, "offset");
            params[5] = (int)offset;
        }
        return mmap.invokePointer(params);
    }

    public static int ftruncate(int fd, long length) {
        Object[] params = new Object[2];
        params[0] = fd;
        if (ftruncate64 || Native.LONG_SIZE > 4) {
            params[1] = length;
        } else {
            LibCUtil.require32Bit(length, "length");
            params[1] = (int)length;
        }
        return ftruncate.invokeInt(params);
    }

    public static void require32Bit(long val, String value) {
        if (val > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(value + " exceeds 32bit");
        }
    }

    static {
        try {
            mmap = LIBC.getFunction("mmap64", 64);
            mmap64 = true;
        }
        catch (UnsatisfiedLinkError ex) {
            mmap = LIBC.getFunction("mmap", 64);
        }
        try {
            ftruncate = LIBC.getFunction("ftruncate64", 64);
            ftruncate64 = true;
        }
        catch (UnsatisfiedLinkError ex) {
            ftruncate = LIBC.getFunction("ftruncate", 64);
        }
    }
}

