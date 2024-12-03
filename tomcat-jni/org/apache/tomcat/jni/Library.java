/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.io.File;
import org.apache.tomcat.jni.LibraryNotFoundError;

public final class Library {
    private static final String[] NAMES = new String[]{"tcnative-1", "libtcnative-1", "tcnative-2", "libtcnative-2"};
    private static final String CATALINA_HOME_PROP = "catalina.home";
    private static Library _instance = null;
    public static int TCN_MAJOR_VERSION = 0;
    public static int TCN_MINOR_VERSION = 0;
    public static int TCN_PATCH_VERSION = 0;
    public static int TCN_IS_DEV_VERSION = 0;
    public static int APR_MAJOR_VERSION = 0;
    public static int APR_MINOR_VERSION = 0;
    public static int APR_PATCH_VERSION = 0;
    public static int APR_IS_DEV_VERSION = 0;
    @Deprecated
    public static boolean APR_HAVE_IPV6 = false;
    @Deprecated
    public static boolean APR_HAS_SHARED_MEMORY = false;
    @Deprecated
    public static boolean APR_HAS_THREADS = false;
    @Deprecated
    public static boolean APR_HAS_SENDFILE = false;
    @Deprecated
    public static boolean APR_HAS_MMAP = false;
    @Deprecated
    public static boolean APR_HAS_FORK = false;
    @Deprecated
    public static boolean APR_HAS_RANDOM = false;
    @Deprecated
    public static boolean APR_HAS_OTHER_CHILD = false;
    @Deprecated
    public static boolean APR_HAS_DSO = false;
    @Deprecated
    public static boolean APR_HAS_SO_ACCEPTFILTER = false;
    @Deprecated
    public static boolean APR_HAS_UNICODE_FS = false;
    @Deprecated
    public static boolean APR_HAS_PROC_INVOKED = false;
    @Deprecated
    public static boolean APR_HAS_USER = false;
    @Deprecated
    public static boolean APR_HAS_LARGE_FILES = false;
    @Deprecated
    public static boolean APR_HAS_XTHREAD_FILES = false;
    @Deprecated
    public static boolean APR_HAS_OS_UUID = false;
    @Deprecated
    public static boolean APR_IS_BIGENDIAN = false;
    @Deprecated
    public static boolean APR_FILES_AS_SOCKETS = false;
    @Deprecated
    public static boolean APR_CHARSET_EBCDIC = false;
    @Deprecated
    public static boolean APR_TCP_NODELAY_INHERITED = false;
    @Deprecated
    public static boolean APR_O_NONBLOCK_INHERITED = false;
    @Deprecated
    public static boolean APR_POLLSET_WAKEABLE = false;
    @Deprecated
    public static boolean APR_HAVE_UNIX = false;
    @Deprecated
    public static int APR_SIZEOF_VOIDP;
    @Deprecated
    public static int APR_PATH_MAX;
    @Deprecated
    public static int APRMAXHOSTLEN;
    @Deprecated
    public static int APR_MAX_IOVEC_SIZE;
    @Deprecated
    public static int APR_MAX_SECS_TO_LINGER;
    @Deprecated
    public static int APR_MMAP_THRESHOLD;
    @Deprecated
    public static int APR_MMAP_LIMIT;

    private Library() throws Exception {
        boolean loaded = false;
        StringBuilder err = new StringBuilder();
        File binLib = new File(System.getProperty(CATALINA_HOME_PROP), "bin");
        for (int i = 0; i < NAMES.length; ++i) {
            File library = new File(binLib, System.mapLibraryName(NAMES[i]));
            try {
                System.load(library.getAbsolutePath());
                loaded = true;
            }
            catch (ThreadDeath | VirtualMachineError t) {
                throw t;
            }
            catch (Throwable t) {
                if (library.exists()) {
                    throw t;
                }
                if (i > 0) {
                    err.append(", ");
                }
                err.append(t.getMessage());
            }
            if (loaded) break;
        }
        if (!loaded) {
            String path = System.getProperty("java.library.path");
            String[] paths = path.split(File.pathSeparator);
            String[] stringArray = NAMES;
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                String value = stringArray[i];
                try {
                    System.loadLibrary(value);
                    loaded = true;
                }
                catch (ThreadDeath | VirtualMachineError t) {
                    throw t;
                }
                catch (Throwable t) {
                    String name = System.mapLibraryName(value);
                    for (String s : paths) {
                        File fd = new File(s, name);
                        if (!fd.exists()) continue;
                        throw t;
                    }
                    if (err.length() > 0) {
                        err.append(", ");
                    }
                    err.append(t.getMessage());
                }
                if (loaded) break;
            }
        }
        if (!loaded) {
            StringBuilder names = new StringBuilder();
            for (String name : NAMES) {
                names.append(name);
                names.append(", ");
            }
            throw new LibraryNotFoundError(names.substring(0, names.length() - 2), err.toString());
        }
    }

    private Library(String libraryName) {
        System.loadLibrary(libraryName);
    }

    private static native boolean initialize();

    public static native void terminate();

    private static native boolean has(int var0);

    private static native int version(int var0);

    private static native int size(int var0);

    public static native String versionString();

    public static native String aprVersionString();

    @Deprecated
    public static native long globalPool();

    public static synchronized boolean initialize(String libraryName) throws Exception {
        if (_instance == null) {
            _instance = libraryName == null ? new Library() : new Library(libraryName);
            TCN_MAJOR_VERSION = Library.version(1);
            TCN_MINOR_VERSION = Library.version(2);
            TCN_PATCH_VERSION = Library.version(3);
            TCN_IS_DEV_VERSION = Library.version(4);
            APR_MAJOR_VERSION = Library.version(17);
            APR_MINOR_VERSION = Library.version(18);
            APR_PATCH_VERSION = Library.version(19);
            APR_IS_DEV_VERSION = Library.version(20);
            APR_SIZEOF_VOIDP = Library.size(1);
            APR_PATH_MAX = Library.size(2);
            APRMAXHOSTLEN = Library.size(3);
            APR_MAX_IOVEC_SIZE = Library.size(4);
            APR_MAX_SECS_TO_LINGER = Library.size(5);
            APR_MMAP_THRESHOLD = Library.size(6);
            APR_MMAP_LIMIT = Library.size(7);
            APR_HAVE_IPV6 = Library.has(0);
            APR_HAS_SHARED_MEMORY = Library.has(1);
            APR_HAS_THREADS = Library.has(2);
            APR_HAS_SENDFILE = Library.has(3);
            APR_HAS_MMAP = Library.has(4);
            APR_HAS_FORK = Library.has(5);
            APR_HAS_RANDOM = Library.has(6);
            APR_HAS_OTHER_CHILD = Library.has(7);
            APR_HAS_DSO = Library.has(8);
            APR_HAS_SO_ACCEPTFILTER = Library.has(9);
            APR_HAS_UNICODE_FS = Library.has(10);
            APR_HAS_PROC_INVOKED = Library.has(11);
            APR_HAS_USER = Library.has(12);
            APR_HAS_LARGE_FILES = Library.has(13);
            APR_HAS_XTHREAD_FILES = Library.has(14);
            APR_HAS_OS_UUID = Library.has(15);
            APR_IS_BIGENDIAN = Library.has(16);
            APR_FILES_AS_SOCKETS = Library.has(17);
            APR_CHARSET_EBCDIC = Library.has(18);
            APR_TCP_NODELAY_INHERITED = Library.has(19);
            APR_O_NONBLOCK_INHERITED = Library.has(20);
            APR_POLLSET_WAKEABLE = Library.has(21);
            APR_HAVE_UNIX = Library.has(22);
            if (APR_MAJOR_VERSION < 1) {
                throw new UnsatisfiedLinkError("Unsupported APR Version (" + Library.aprVersionString() + ")");
            }
            if (!APR_HAS_THREADS) {
                throw new UnsatisfiedLinkError("Missing threading support from APR");
            }
        }
        return Library.initialize();
    }

    @Deprecated
    public static void load(String filename) {
        System.load(filename);
    }

    @Deprecated
    public static void loadLibrary(String libname) {
        System.loadLibrary(libname);
    }
}

