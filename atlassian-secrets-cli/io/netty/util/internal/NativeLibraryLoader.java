/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.NativeLibraryUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;

public final class NativeLibraryLoader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
    private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
    private static final File WORKDIR;
    private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
    private static final boolean TRY_TO_PATCH_SHADED_ID;
    private static final boolean DETECT_NATIVE_LIBRARY_DUPLICATES;
    private static final byte[] UNIQUE_ID_BYTES;

    public static void loadFirstAvailable(ClassLoader loader, String ... names) {
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        for (String name : names) {
            try {
                NativeLibraryLoader.load(name, loader);
                logger.debug("Loaded library with name '{}'", (Object)name);
                return;
            }
            catch (Throwable t) {
                suppressed.add(t);
            }
        }
        IllegalArgumentException iae = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(names));
        ThrowableUtil.addSuppressedAndClear(iae, suppressed);
        throw iae;
    }

    private static String calculateMangledPackagePrefix() {
        String expected;
        String maybeShaded = NativeLibraryLoader.class.getName();
        if (!maybeShaded.endsWith(expected = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.'))) {
            throw new UnsatisfiedLinkError(String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", expected, maybeShaded));
        }
        return maybeShaded.substring(0, maybeShaded.length() - expected.length()).replace("_", "_1").replace('.', '_');
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void load(String originalName, ClassLoader loader) {
        String mangledPackagePrefix = NativeLibraryLoader.calculateMangledPackagePrefix();
        String name = mangledPackagePrefix + originalName;
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        try {
            NativeLibraryLoader.loadLibrary(loader, name, false);
            return;
        }
        catch (Throwable ex) {
            suppressed.add(ex);
            String libname = System.mapLibraryName(name);
            String path = NATIVE_RESOURCE_HOME + libname;
            InputStream in = null;
            FileOutputStream out = null;
            File tmpFile = null;
            URL url = NativeLibraryLoader.getResource(path, loader);
            try {
                int length;
                if (url == null) {
                    if (!PlatformDependent.isOsx()) {
                        FileNotFoundException fnf = new FileNotFoundException(path);
                        ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                        throw fnf;
                    }
                    String fileName = path.endsWith(".jnilib") ? "META-INF/native/lib" + name + ".dynlib" : "META-INF/native/lib" + name + ".jnilib";
                    url = NativeLibraryLoader.getResource(fileName, loader);
                    if (url == null) {
                        FileNotFoundException fnf = new FileNotFoundException(fileName);
                        ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                        throw fnf;
                    }
                }
                int index = libname.lastIndexOf(46);
                String prefix = libname.substring(0, index);
                String suffix = libname.substring(index);
                tmpFile = PlatformDependent.createTempFile(prefix, suffix, WORKDIR);
                in = url.openStream();
                out = new FileOutputStream(tmpFile);
                byte[] buffer = new byte[8192];
                while ((length = in.read(buffer)) > 0) {
                    ((OutputStream)out).write(buffer, 0, length);
                }
                out.flush();
                if (NativeLibraryLoader.shouldShadedLibraryIdBePatched(mangledPackagePrefix)) {
                    NativeLibraryLoader.tryPatchShadedLibraryIdAndSign(tmpFile, originalName);
                }
                NativeLibraryLoader.closeQuietly(out);
                out = null;
                NativeLibraryLoader.loadLibrary(loader, tmpFile.getPath(), true);
            }
            catch (UnsatisfiedLinkError e) {
                try {
                    try {
                        if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead() && !NoexecVolumeDetector.canExecuteExecutable(tmpFile)) {
                            logger.info("{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D{}=[path] to set native working directory separately.", (Object)tmpFile.getPath(), (Object)"io.netty.native.workdir");
                        }
                    }
                    catch (Throwable t) {
                        suppressed.add(t);
                        logger.debug("Error checking if {} is on a file store mounted with noexec", (Object)tmpFile, (Object)t);
                    }
                    ThrowableUtil.addSuppressedAndClear(e, suppressed);
                    throw e;
                    catch (Exception e2) {
                        UnsatisfiedLinkError ule = new UnsatisfiedLinkError("could not load a native library: " + name);
                        ule.initCause(e2);
                        ThrowableUtil.addSuppressedAndClear(ule, suppressed);
                        throw ule;
                    }
                }
                catch (Throwable throwable) {
                    NativeLibraryLoader.closeQuietly(in);
                    NativeLibraryLoader.closeQuietly(out);
                    if (tmpFile == null) throw throwable;
                    if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                        if (tmpFile.delete()) throw throwable;
                    }
                    tmpFile.deleteOnExit();
                    throw throwable;
                }
            }
            NativeLibraryLoader.closeQuietly(in);
            NativeLibraryLoader.closeQuietly(out);
            if (tmpFile == null) return;
            if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                if (tmpFile.delete()) return;
            }
            tmpFile.deleteOnExit();
            return;
        }
    }

    private static URL getResource(String path, ClassLoader loader) {
        Enumeration<URL> urls;
        try {
            urls = loader == null ? ClassLoader.getSystemResources(path) : loader.getResources(path);
        }
        catch (IOException iox) {
            throw new RuntimeException("An error occurred while getting the resources for " + path, iox);
        }
        ArrayList<URL> urlsList = Collections.list(urls);
        int size = urlsList.size();
        switch (size) {
            case 0: {
                return null;
            }
            case 1: {
                return (URL)urlsList.get(0);
            }
        }
        if (DETECT_NATIVE_LIBRARY_DUPLICATES) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                URL url = (URL)urlsList.get(0);
                byte[] digest = NativeLibraryLoader.digest(md, url);
                boolean allSame = true;
                if (digest != null) {
                    for (int i = 1; i < size; ++i) {
                        byte[] digest2 = NativeLibraryLoader.digest(md, (URL)urlsList.get(i));
                        if (digest2 != null && Arrays.equals(digest, digest2)) continue;
                        allSame = false;
                        break;
                    }
                } else {
                    allSame = false;
                }
                if (allSame) {
                    return url;
                }
            }
            catch (NoSuchAlgorithmException e) {
                logger.debug("Don't support SHA-256, can't check if resources have same content.", e);
            }
            throw new IllegalStateException("Multiple resources found for '" + path + "' with different content: " + urlsList);
        }
        logger.warn("Multiple resources found for '" + path + "' with different content: " + urlsList + ". Please fix your dependency graph.");
        return (URL)urlsList.get(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] digest(MessageDigest digest, URL url) {
        InputStream in = null;
        try {
            int i;
            in = url.openStream();
            byte[] bytes = new byte[8192];
            while ((i = in.read(bytes)) != -1) {
                digest.update(bytes, 0, i);
            }
            byte[] byArray = digest.digest();
            return byArray;
        }
        catch (IOException e) {
            logger.debug("Can't read resource.", e);
            byte[] byArray = null;
            return byArray;
        }
        finally {
            NativeLibraryLoader.closeQuietly(in);
        }
    }

    static void tryPatchShadedLibraryIdAndSign(File libraryFile, String originalName) {
        if (!new File("/Library/Developer/CommandLineTools").exists()) {
            logger.debug("Can't patch shaded library id as CommandLineTools are not installed. Consider installing CommandLineTools with 'xcode-select --install'");
            return;
        }
        String newId = new String(NativeLibraryLoader.generateUniqueId(originalName.length()), CharsetUtil.UTF_8);
        if (!NativeLibraryLoader.tryExec("install_name_tool -id " + newId + " " + libraryFile.getAbsolutePath())) {
            return;
        }
        NativeLibraryLoader.tryExec("codesign -s - " + libraryFile.getAbsolutePath());
    }

    private static boolean tryExec(String cmd) {
        try {
            int exitValue = Runtime.getRuntime().exec(cmd).waitFor();
            if (exitValue != 0) {
                logger.debug("Execution of '{}' failed: {}", (Object)cmd, (Object)exitValue);
                return false;
            }
            logger.debug("Execution of '{}' succeed: {}", (Object)cmd, (Object)exitValue);
            return true;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            logger.info("Execution of '{}' failed.", (Object)cmd, (Object)e);
        }
        catch (SecurityException e) {
            logger.error("Execution of '{}' failed.", (Object)cmd, (Object)e);
        }
        return false;
    }

    private static boolean shouldShadedLibraryIdBePatched(String packagePrefix) {
        return TRY_TO_PATCH_SHADED_ID && PlatformDependent.isOsx() && !packagePrefix.isEmpty();
    }

    private static byte[] generateUniqueId(int length) {
        byte[] idBytes = new byte[length];
        for (int i = 0; i < idBytes.length; ++i) {
            idBytes[i] = UNIQUE_ID_BYTES[PlatformDependent.threadLocalRandom().nextInt(UNIQUE_ID_BYTES.length)];
        }
        return idBytes;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void loadLibrary(ClassLoader loader, String name, boolean absolute) {
        Throwable suppressed = null;
        try {
            try {
                Class<?> newHelper = NativeLibraryLoader.tryToLoadClass(loader, NativeLibraryUtil.class);
                NativeLibraryLoader.loadLibraryByHelper(newHelper, name, absolute);
                logger.debug("Successfully loaded the library {}", (Object)name);
                return;
            }
            catch (UnsatisfiedLinkError e) {
                try {
                    block8: {
                        suppressed = e;
                        break block8;
                        catch (Exception e2) {
                            suppressed = e2;
                        }
                    }
                    NativeLibraryUtil.loadLibrary(name, absolute);
                    logger.debug("Successfully loaded the library {}", (Object)name);
                    return;
                }
                catch (UnsatisfiedLinkError ule) {
                    if (suppressed == null) throw ule;
                    ThrowableUtil.addSuppressed((Throwable)ule, suppressed);
                    throw ule;
                }
            }
        }
        catch (NoSuchMethodError nsme) {
            if (suppressed != null) {
                ThrowableUtil.addSuppressed((Throwable)nsme, suppressed);
            }
            NativeLibraryLoader.rethrowWithMoreDetailsIfPossible(name, nsme);
            return;
        }
    }

    @SuppressJava6Requirement(reason="Guarded by version check")
    private static void rethrowWithMoreDetailsIfPossible(String name, NoSuchMethodError error) {
        if (PlatformDependent.javaVersion() >= 7) {
            throw new LinkageError("Possible multiple incompatible native libraries on the classpath for '" + name + "'?", error);
        }
        throw error;
    }

    private static void loadLibraryByHelper(final Class<?> helper, final String name, final boolean absolute) throws UnsatisfiedLinkError {
        Object ret = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Method method = helper.getMethod("loadLibrary", String.class, Boolean.TYPE);
                    method.setAccessible(true);
                    return method.invoke(null, name, absolute);
                }
                catch (Exception e) {
                    return e;
                }
            }
        });
        if (ret instanceof Throwable) {
            Throwable t = (Throwable)ret;
            assert (!(t instanceof UnsatisfiedLinkError)) : t + " should be a wrapper throwable";
            Throwable cause = t.getCause();
            if (cause instanceof UnsatisfiedLinkError) {
                throw (UnsatisfiedLinkError)cause;
            }
            UnsatisfiedLinkError ule = new UnsatisfiedLinkError(t.getMessage());
            ule.initCause(t);
            throw ule;
        }
    }

    private static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper) throws ClassNotFoundException {
        try {
            return Class.forName(helper.getName(), false, loader);
        }
        catch (ClassNotFoundException e1) {
            if (loader == null) {
                throw e1;
            }
            try {
                final byte[] classBinary = NativeLibraryLoader.classToByteArray(helper);
                return (Class)AccessController.doPrivileged(new PrivilegedAction<Class<?>>(){

                    @Override
                    public Class<?> run() {
                        try {
                            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                            defineClass.setAccessible(true);
                            return (Class)defineClass.invoke((Object)loader, helper.getName(), classBinary, 0, classBinary.length);
                        }
                        catch (Exception e) {
                            throw new IllegalStateException("Define class failed!", e);
                        }
                    }
                });
            }
            catch (ClassNotFoundException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
            catch (RuntimeException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
            catch (Error e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
        }
    }

    private static byte[] classToByteArray(Class<?> clazz) throws ClassNotFoundException {
        URL classUrl;
        String fileName = clazz.getName();
        int lastDot = fileName.lastIndexOf(46);
        if (lastDot > 0) {
            fileName = fileName.substring(lastDot + 1);
        }
        if ((classUrl = clazz.getResource(fileName + ".class")) == null) {
            throw new ClassNotFoundException(clazz.getName());
        }
        byte[] buf = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        InputStream in = null;
        try {
            int r22;
            in = classUrl.openStream();
            while ((r22 = in.read(buf)) != -1) {
                out.write(buf, 0, r22);
            }
            byte[] r22 = out.toByteArray();
            return r22;
        }
        catch (IOException ex) {
            throw new ClassNotFoundException(clazz.getName(), ex);
        }
        finally {
            NativeLibraryLoader.closeQuietly(in);
            NativeLibraryLoader.closeQuietly(out);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private NativeLibraryLoader() {
    }

    static {
        UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(CharsetUtil.US_ASCII);
        String workdir = SystemPropertyUtil.get("io.netty.native.workdir");
        if (workdir != null) {
            File f = new File(workdir);
            f.mkdirs();
            try {
                f = f.getAbsoluteFile();
            }
            catch (Exception exception) {
                // empty catch block
            }
            WORKDIR = f;
            logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
        } else {
            WORKDIR = PlatformDependent.tmpdir();
            logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
        }
        DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
        logger.debug("-Dio.netty.native.deleteLibAfterLoading: {}", (Object)DELETE_NATIVE_LIB_AFTER_LOADING);
        TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean("io.netty.native.tryPatchShadedId", true);
        logger.debug("-Dio.netty.native.tryPatchShadedId: {}", (Object)TRY_TO_PATCH_SHADED_ID);
        DETECT_NATIVE_LIBRARY_DUPLICATES = SystemPropertyUtil.getBoolean("io.netty.native.detectNativeLibraryDuplicates", true);
        logger.debug("-Dio.netty.native.detectNativeLibraryDuplicates: {}", (Object)DETECT_NATIVE_LIBRARY_DUPLICATES);
    }

    private static final class NoexecVolumeDetector {
        @SuppressJava6Requirement(reason="Usage guarded by java version check")
        private static boolean canExecuteExecutable(File file) throws IOException {
            EnumSet<PosixFilePermission> executePermissions;
            if (PlatformDependent.javaVersion() < 7) {
                return true;
            }
            if (file.canExecute()) {
                return true;
            }
            Set<PosixFilePermission> existingFilePermissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            if (existingFilePermissions.containsAll(executePermissions = EnumSet.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE))) {
                return false;
            }
            EnumSet<PosixFilePermission> newPermissions = EnumSet.copyOf(existingFilePermissions);
            newPermissions.addAll(executePermissions);
            Files.setPosixFilePermissions(file.toPath(), newPermissions);
            return file.canExecute();
        }

        private NoexecVolumeDetector() {
        }
    }
}

