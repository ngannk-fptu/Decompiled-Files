/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JVMUtil;
import com.hazelcast.util.OsHelper;
import java.io.File;
import java.io.InputStream;

public final class ICMPHelper {
    private ICMPHelper() {
    }

    private static native boolean isRawSocketPermitted0();

    public static boolean isRawSocketPermitted() {
        return ICMPHelper.isRawSocketPermitted0();
    }

    private static String extractBundledLib() {
        InputStream src = null;
        try {
            src = IOUtil.getFileFromResourcesAsStream(ICMPHelper.getBundledLibraryPath());
            File dest = File.createTempFile("hazelcast-libicmp-helper-", ".so");
            IOUtil.copy(src, dest);
            String string = dest.getAbsolutePath();
            return string;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        finally {
            IOUtil.closeResource(src);
        }
    }

    private static String getBundledLibraryPath() {
        if (!OsHelper.isUnixFamily()) {
            throw new IllegalStateException("ICMP not supported in this platform: " + OsHelper.OS);
        }
        return JVMUtil.is32bitJVM() ? "lib/linux-x86/libicmp_helper.so" : "lib/linux-x86_64/libicmp_helper.so";
    }

    static {
        System.load(ICMPHelper.extractBundledLib());
    }
}

