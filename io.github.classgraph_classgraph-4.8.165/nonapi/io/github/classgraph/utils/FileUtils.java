/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

public final class FileUtils {
    private static Method directByteBufferCleanerMethod;
    private static Method cleanerCleanMethod;
    private static Method attachmentMethod;
    private static Object theUnsafe;
    private static AtomicBoolean initialized;
    private static String currDirPath;
    public static final int MAX_BUFFER_SIZE = 0x7FFFFFF7;

    private FileUtils() {
    }

    public static String currDirPath() {
        if (currDirPath == null) {
            Path path = null;
            String currDirPathStr = System.getProperty("user.dir");
            if (currDirPathStr != null) {
                try {
                    path = Paths.get(currDirPathStr, new String[0]);
                }
                catch (InvalidPathException invalidPathException) {
                    // empty catch block
                }
            }
            if (path == null) {
                try {
                    path = Paths.get("", new String[0]);
                }
                catch (InvalidPathException invalidPathException) {
                    // empty catch block
                }
            }
            currDirPath = FastPathResolver.resolve(path == null ? "" : path.toString());
        }
        return currDirPath;
    }

    public static String sanitizeEntryPath(String path, boolean removeInitialSlash, boolean removeFinalSlash) {
        int startIdx;
        if (path.isEmpty()) {
            return "";
        }
        boolean foundSegmentToSanitize = false;
        int pathLen = path.length();
        char[] pathChars = new char[pathLen];
        path.getChars(0, pathLen, pathChars, 0);
        int lastSepIdx = -1;
        char prevC = '\u0000';
        int ii = pathLen + 1;
        for (int i = 0; i < ii; ++i) {
            char c;
            char c2 = c = i == pathLen ? (char)'\u0000' : pathChars[i];
            if (c == '/' || c == '!' || c == '\u0000') {
                int segmentLength = i - (lastSepIdx + 1);
                if (segmentLength == 0 && prevC == c || segmentLength == 1 && pathChars[i - 1] == '.' || segmentLength == 2 && pathChars[i - 2] == '.' && pathChars[i - 1] == '.') {
                    foundSegmentToSanitize = true;
                }
                lastSepIdx = i;
            }
            prevC = c;
        }
        boolean pathHasInitialSlash = pathChars[0] == '/';
        boolean pathHasInitialSlashSlash = pathHasInitialSlash && pathLen > 1 && pathChars[1] == '/';
        StringBuilder pathSanitized = new StringBuilder(pathLen + 16);
        if (foundSegmentToSanitize) {
            ArrayList allSectionSegments = new ArrayList();
            ArrayList<CharSequence> currSectionSegments = new ArrayList<CharSequence>();
            allSectionSegments.add(currSectionSegments);
            int lastSepIdx2 = -1;
            for (int i = 0; i < pathLen + 1; ++i) {
                char c;
                char c3 = c = i == pathLen ? (char)'\u0000' : pathChars[i];
                if (c != '/' && c != '!' && c != '\u0000') continue;
                int segmentStartIdx = lastSepIdx2 + 1;
                int segmentLen = i - segmentStartIdx;
                if (segmentLen != 0 && (segmentLen != 1 || pathChars[segmentStartIdx] != '.')) {
                    if (segmentLen == 2 && pathChars[segmentStartIdx] == '.' && pathChars[segmentStartIdx + 1] == '.') {
                        if (!currSectionSegments.isEmpty()) {
                            currSectionSegments.remove(currSectionSegments.size() - 1);
                        }
                    } else {
                        currSectionSegments.add(path.subSequence(segmentStartIdx, segmentStartIdx + segmentLen));
                    }
                }
                if (c == '!' && !currSectionSegments.isEmpty()) {
                    currSectionSegments = new ArrayList();
                    allSectionSegments.add(currSectionSegments);
                }
                lastSepIdx2 = i;
            }
            for (List list : allSectionSegments) {
                if (list.isEmpty()) continue;
                if (pathSanitized.length() > 0) {
                    pathSanitized.append('!');
                }
                for (CharSequence sectionSegment : list) {
                    pathSanitized.append('/');
                    pathSanitized.append(sectionSegment);
                }
            }
            if (pathSanitized.length() == 0 && pathHasInitialSlash) {
                pathSanitized.append('/');
            }
        } else {
            pathSanitized.append(path);
        }
        if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows && pathHasInitialSlashSlash) {
            pathSanitized.insert(0, '/');
        }
        if (removeInitialSlash || !pathHasInitialSlash) {
            for (startIdx = 0; startIdx < pathSanitized.length() && pathSanitized.charAt(startIdx) == '/'; ++startIdx) {
            }
        }
        if (removeFinalSlash) {
            while (pathSanitized.length() > 0 && pathSanitized.charAt(pathSanitized.length() - 1) == '/') {
                pathSanitized.setLength(pathSanitized.length() - 1);
            }
        }
        return pathSanitized.substring(startIdx);
    }

    public static boolean isClassfile(String path) {
        int len = path.length();
        return len > 6 && path.regionMatches(true, len - 6, ".class", 0, 6);
    }

    public static boolean canRead(File file) {
        try {
            return file.canRead();
        }
        catch (SecurityException e) {
            return false;
        }
    }

    public static boolean canRead(Path path) {
        try {
            if (!Files.isReadable(path)) {
                return false;
            }
        }
        catch (SecurityException e) {
            return false;
        }
        return Files.isRegularFile(path, new LinkOption[0]);
    }

    public static boolean canReadAndIsFile(File file) {
        try {
            if (!file.canRead()) {
                return false;
            }
        }
        catch (SecurityException e) {
            return false;
        }
        return file.isFile();
    }

    public static boolean canReadAndIsFile(Path path) {
        try {
            if (!Files.isReadable(path)) {
                return false;
            }
        }
        catch (SecurityException e) {
            return false;
        }
        return Files.isRegularFile(path, new LinkOption[0]);
    }

    public static void checkCanReadAndIsFile(File file) throws IOException {
        try {
            if (!file.canRead()) {
                throw new FileNotFoundException("File does not exist or cannot be read: " + file);
            }
        }
        catch (SecurityException e) {
            throw new FileNotFoundException("File " + file + " cannot be accessed: " + e);
        }
        if (!file.isFile()) {
            throw new IOException("Not a regular file: " + file);
        }
    }

    public static void checkCanReadAndIsFile(Path path) throws IOException {
        try {
            if (!Files.isReadable(path)) {
                throw new FileNotFoundException("Path does not exist or cannot be read: " + path);
            }
        }
        catch (SecurityException e) {
            throw new FileNotFoundException("Path " + path + " cannot be accessed: " + e);
        }
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            throw new IOException("Not a regular file: " + path);
        }
    }

    public static boolean canReadAndIsDir(File file) {
        try {
            if (!file.canRead()) {
                return false;
            }
        }
        catch (SecurityException e) {
            return false;
        }
        return file.isDirectory();
    }

    public static boolean canReadAndIsDir(Path path) {
        try {
            if (!Files.isReadable(path)) {
                return false;
            }
        }
        catch (SecurityException e) {
            return false;
        }
        return Files.isDirectory(path, new LinkOption[0]);
    }

    public static void checkCanReadAndIsDir(File file) throws IOException {
        try {
            if (!file.canRead()) {
                throw new FileNotFoundException("Directory does not exist or cannot be read: " + file);
            }
        }
        catch (SecurityException e) {
            throw new FileNotFoundException("File " + file + " cannot be accessed: " + e);
        }
        if (!file.isDirectory()) {
            throw new IOException("Not a directory: " + file);
        }
    }

    public static String getParentDirPath(String path, char separator) {
        int lastSlashIdx = path.lastIndexOf(separator);
        if (lastSlashIdx <= 0) {
            return "";
        }
        return path.substring(0, lastSlashIdx);
    }

    public static String getParentDirPath(String path) {
        return FileUtils.getParentDirPath(path, '/');
    }

    private static void lookupCleanMethodPrivileged() {
        if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
            try {
                cleanerCleanMethod = Class.forName("sun.misc.Cleaner").getDeclaredMethod("clean", new Class[0]);
                cleanerCleanMethod.setAccessible(true);
                Class<?> directByteBufferClass = Class.forName("sun.nio.ch.DirectBuffer");
                directByteBufferCleanerMethod = directByteBufferClass.getDeclaredMethod("cleaner", new Class[0]);
                attachmentMethod = directByteBufferClass.getMethod("attachment", new Class[0]);
                attachmentMethod.setAccessible(true);
            }
            catch (SecurityException e) {
                throw new RuntimeException("You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\") and ReflectPermission(\"suppressAccessChecks\")", e);
            }
            catch (LinkageError | ReflectiveOperationException e) {}
        } else {
            try {
                Class<?> unsafeClass;
                try {
                    unsafeClass = Class.forName("sun.misc.Unsafe");
                }
                catch (LinkageError | ReflectiveOperationException e) {
                    throw new RuntimeException("Could not get class sun.misc.Unsafe", e);
                }
                Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                theUnsafe = theUnsafeField.get(null);
                cleanerCleanMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
                cleanerCleanMethod.setAccessible(true);
            }
            catch (SecurityException e) {
                throw new RuntimeException("You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\") and ReflectPermission(\"suppressAccessChecks\")", e);
            }
            catch (LinkageError | ReflectiveOperationException throwable) {
                // empty catch block
            }
        }
    }

    private static boolean closeDirectByteBufferPrivileged(ByteBuffer byteBuffer, LogNode log) {
        if (!byteBuffer.isDirect()) {
            return true;
        }
        try {
            if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
                if (attachmentMethod == null) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, attachmentMethod == null");
                    }
                    return false;
                }
                if (attachmentMethod.invoke((Object)byteBuffer, new Object[0]) != null) {
                    return false;
                }
                if (directByteBufferCleanerMethod == null) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanerMethod == null");
                    }
                    return false;
                }
                try {
                    directByteBufferCleanerMethod.setAccessible(true);
                }
                catch (Exception e) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanerMethod.setAccessible(true) failed");
                    }
                    return false;
                }
                Object cleanerInstance = directByteBufferCleanerMethod.invoke((Object)byteBuffer, new Object[0]);
                if (cleanerInstance == null) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleaner == null");
                    }
                    return false;
                }
                if (cleanerCleanMethod == null) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanMethod == null");
                    }
                    return false;
                }
                try {
                    cleanerCleanMethod.invoke(cleanerInstance, new Object[0]);
                    return true;
                }
                catch (Exception e) {
                    if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanMethod.invoke(cleaner) failed: " + e);
                    }
                    return false;
                }
            }
            if (theUnsafe == null) {
                if (log != null) {
                    log.log("Could not unmap ByteBuffer, theUnsafe == null");
                }
                return false;
            }
            if (cleanerCleanMethod == null) {
                if (log != null) {
                    log.log("Could not unmap ByteBuffer, cleanMethod == null");
                }
                return false;
            }
            try {
                cleanerCleanMethod.invoke(theUnsafe, byteBuffer);
                return true;
            }
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        catch (ReflectiveOperationException | SecurityException e) {
            if (log != null) {
                log.log("Could not unmap ByteBuffer: " + e);
            }
            return false;
        }
    }

    public static boolean closeDirectByteBuffer(final ByteBuffer byteBuffer, ReflectionUtils reflectionUtils, final LogNode log) {
        if (byteBuffer != null && byteBuffer.isDirect()) {
            if (!initialized.get()) {
                try {
                    reflectionUtils.doPrivileged(new Callable<Void>(){

                        @Override
                        public Void call() throws Exception {
                            FileUtils.lookupCleanMethodPrivileged();
                            return null;
                        }
                    });
                }
                catch (Throwable e) {
                    throw new RuntimeException("Cannot get buffer cleaner method", e);
                }
                initialized.set(true);
            }
            try {
                return reflectionUtils.doPrivileged(new Callable<Boolean>(){

                    @Override
                    public Boolean call() throws Exception {
                        return FileUtils.closeDirectByteBufferPrivileged(byteBuffer, log);
                    }
                });
            }
            catch (Throwable t) {
                return false;
            }
        }
        return false;
    }

    static {
        initialized = new AtomicBoolean();
    }
}

