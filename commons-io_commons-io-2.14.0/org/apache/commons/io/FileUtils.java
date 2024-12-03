/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.CloseableURLConnection;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.file.StandardDeleteOption;
import org.apache.commons.io.filefilter.FileEqualsFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.function.IOConsumer;
import org.apache.commons.io.function.Uncheck;

public class FileUtils {
    public static final long ONE_KB = 1024L;
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(1024L);
    public static final long ONE_MB = 0x100000L;
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
    public static final long ONE_GB = 0x40000000L;
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    public static final long ONE_TB = 0x10000000000L;
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    public static final long ONE_PB = 0x4000000000000L;
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
    public static final long ONE_EB = 0x1000000000000000L;
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);
    public static final BigInteger ONE_ZB = BigInteger.valueOf(1024L).multiply(BigInteger.valueOf(0x1000000000000000L));
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);
    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    public static String byteCountToDisplaySize(BigInteger size) {
        Objects.requireNonNull(size, "size");
        String displaySize = size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_EB_BI) + " EB" : (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_PB_BI) + " PB" : (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_TB_BI) + " TB" : (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_GB_BI) + " GB" : (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_MB_BI) + " MB" : (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0 ? size.divide(ONE_KB_BI) + " KB" : size + " bytes")))));
        return displaySize;
    }

    public static String byteCountToDisplaySize(long size) {
        return FileUtils.byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    public static String byteCountToDisplaySize(Number size) {
        return FileUtils.byteCountToDisplaySize(size.longValue());
    }

    public static Checksum checksum(File file, Checksum checksum) throws IOException {
        FileUtils.requireExistsChecked(file, "file");
        FileUtils.requireFile(file, "file");
        Objects.requireNonNull(checksum, "checksum");
        try (CheckedInputStream inputStream = new CheckedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]), checksum);){
            IOUtils.consume(inputStream);
        }
        return checksum;
    }

    public static long checksumCRC32(File file) throws IOException {
        return FileUtils.checksum(file, new CRC32()).getValue();
    }

    public static void cleanDirectory(File directory) throws IOException {
        IOConsumer.forAll(FileUtils::forceDelete, FileUtils.listFiles(directory, null));
    }

    private static void cleanDirectoryOnExit(File directory) throws IOException {
        IOConsumer.forAll(FileUtils::forceDeleteOnExit, FileUtils.listFiles(directory, null));
    }

    public static boolean contentEquals(File file1, File file2) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        FileUtils.requireFile(file1, "file1");
        FileUtils.requireFile(file2, "file2");
        if (file1.length() != file2.length()) {
            return false;
        }
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        try (InputStream input1 = Files.newInputStream(file1.toPath(), new OpenOption[0]);){
            boolean bl;
            block18: {
                InputStream input2 = Files.newInputStream(file2.toPath(), new OpenOption[0]);
                try {
                    bl = IOUtils.contentEquals(input1, input2);
                    if (input2 == null) break block18;
                }
                catch (Throwable throwable) {
                    if (input2 != null) {
                        try {
                            input2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                input2.close();
            }
            return bl;
        }
    }

    public static boolean contentEqualsIgnoreEOL(File file1, File file2, String charsetName) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        FileUtils.requireFile(file1, "file1");
        FileUtils.requireFile(file2, "file2");
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        Charset charset = Charsets.toCharset(charsetName);
        try (InputStreamReader input1 = new InputStreamReader(Files.newInputStream(file1.toPath(), new OpenOption[0]), charset);){
            boolean bl;
            try (InputStreamReader input2 = new InputStreamReader(Files.newInputStream(file2.toPath(), new OpenOption[0]), charset);){
                bl = IOUtils.contentEqualsIgnoreEOL(input1, input2);
            }
            return bl;
        }
    }

    public static File[] convertFileCollectionToFileArray(Collection<File> files) {
        return files.toArray(EMPTY_FILE_ARRAY);
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, true);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, filter, true);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, filter, preserveFileDate, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter fileFilter, boolean preserveFileDate, CopyOption ... copyOptions) throws IOException {
        File[] srcFiles;
        FileUtils.requireFileCopy(srcDir, destDir);
        FileUtils.requireDirectory(srcDir, "srcDir");
        FileUtils.requireCanonicalPathsNotEquals(srcDir, destDir);
        ArrayList<String> exclusionList = null;
        String srcDirCanonicalPath = srcDir.getCanonicalPath();
        String destDirCanonicalPath = destDir.getCanonicalPath();
        if (destDirCanonicalPath.startsWith(srcDirCanonicalPath) && (srcFiles = FileUtils.listFiles(srcDir, fileFilter)).length > 0) {
            exclusionList = new ArrayList<String>(srcFiles.length);
            for (File srcFile : srcFiles) {
                exclusionList.add(new File(destDir, srcFile.getName()).getCanonicalPath());
            }
        }
        FileUtils.doCopyDirectory(srcDir, destDir, fileFilter, exclusionList, preserveFileDate, copyOptions);
    }

    public static void copyDirectoryToDirectory(File sourceDir, File destinationDir) throws IOException {
        FileUtils.requireDirectoryIfExists(sourceDir, "sourceDir");
        FileUtils.requireDirectoryIfExists(destinationDir, "destinationDir");
        FileUtils.copyDirectory(sourceDir, new File(destinationDir, sourceDir.getName()), true);
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileUtils.copyFile(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        FileUtils.copyFile(srcFile, destFile, preserveFileDate, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate, CopyOption ... copyOptions) throws IOException {
        FileUtils.requireFileCopy(srcFile, destFile);
        FileUtils.requireFile(srcFile, "srcFile");
        FileUtils.requireCanonicalPathsNotEquals(srcFile, destFile);
        FileUtils.createParentDirectories(destFile);
        FileUtils.requireFileIfExists(destFile, "destFile");
        if (destFile.exists()) {
            FileUtils.requireCanWrite(destFile, "destFile");
        }
        Files.copy(srcFile.toPath(), destFile.toPath(), copyOptions);
        if (preserveFileDate && !FileUtils.setTimes(srcFile, destFile)) {
            throw new IOException("Cannot set the file time.");
        }
    }

    public static void copyFile(File srcFile, File destFile, CopyOption ... copyOptions) throws IOException {
        FileUtils.copyFile(srcFile, destFile, true, copyOptions);
    }

    public static long copyFile(File input, OutputStream output) throws IOException {
        try (InputStream fis = Files.newInputStream(input.toPath(), new OpenOption[0]);){
            long l = IOUtils.copyLarge(fis, output);
            return l;
        }
    }

    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        FileUtils.copyFileToDirectory(srcFile, destDir, true);
    }

    public static void copyFileToDirectory(File sourceFile, File destinationDir, boolean preserveFileDate) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        FileUtils.requireDirectoryIfExists(destinationDir, "destinationDir");
        FileUtils.copyFile(sourceFile, new File(destinationDir, sourceFile.getName()), preserveFileDate);
    }

    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        try (InputStream inputStream = source;){
            FileUtils.copyToFile(inputStream, destination);
        }
    }

    public static void copyToDirectory(File sourceFile, File destinationDir) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        if (sourceFile.isFile()) {
            FileUtils.copyFileToDirectory(sourceFile, destinationDir);
        } else if (sourceFile.isDirectory()) {
            FileUtils.copyDirectoryToDirectory(sourceFile, destinationDir);
        } else {
            throw new FileNotFoundException("The source " + sourceFile + " does not exist");
        }
    }

    public static void copyToDirectory(Iterable<File> sourceIterable, File destinationDir) throws IOException {
        Objects.requireNonNull(sourceIterable, "sourceIterable");
        for (File src : sourceIterable) {
            FileUtils.copyFileToDirectory(src, destinationDir);
        }
    }

    public static void copyToFile(InputStream inputStream, File file) throws IOException {
        try (OutputStream out = FileUtils.newOutputStream(file, false);){
            IOUtils.copy(inputStream, out);
        }
    }

    public static void copyURLToFile(URL source, File destination) throws IOException {
        Path path = destination.toPath();
        PathUtils.createParentDirectories(path, new FileAttribute[0]);
        PathUtils.copy(source::openStream, path, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyURLToFile(URL source, File destination, int connectionTimeoutMillis, int readTimeoutMillis) throws IOException {
        try (CloseableURLConnection urlConnection = CloseableURLConnection.open(source);){
            urlConnection.setConnectTimeout(connectionTimeoutMillis);
            urlConnection.setReadTimeout(readTimeoutMillis);
            try (InputStream stream = urlConnection.getInputStream();){
                FileUtils.copyInputStreamToFile(stream, destination);
            }
        }
    }

    public static File createParentDirectories(File file) throws IOException {
        return FileUtils.mkdirs(FileUtils.getParentFile(file));
    }

    public static File current() {
        return PathUtils.current().toFile();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String decodeUrl(String url) {
        String decoded = url;
        if (url != null && url.indexOf(37) >= 0) {
            int n = url.length();
            StringBuilder builder = new StringBuilder();
            ByteBuffer byteBuffer = ByteBuffer.allocate(n);
            int i = 0;
            while (i < n) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            byteBuffer.put(octet);
                        } while ((i += 3) < n && url.charAt(i) == '%');
                        continue;
                    }
                    catch (RuntimeException runtimeException) {
                    }
                    finally {
                        if (byteBuffer.position() <= 0) continue;
                        byteBuffer.flip();
                        builder.append(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                        byteBuffer.clear();
                        continue;
                    }
                }
                builder.append(url.charAt(i++));
            }
            decoded = builder.toString();
        }
        return decoded;
    }

    public static File delete(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Files.delete(file.toPath());
        return file;
    }

    public static void deleteDirectory(File directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            return;
        }
        if (!FileUtils.isSymlink(directory)) {
            FileUtils.cleanDirectory(directory);
        }
        FileUtils.delete(directory);
    }

    private static void deleteDirectoryOnExit(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        directory.deleteOnExit();
        if (!FileUtils.isSymlink(directory)) {
            FileUtils.cleanDirectoryOnExit(directory);
        }
    }

    public static boolean deleteQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                FileUtils.cleanDirectory(file);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            return file.delete();
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static boolean directoryContains(File directory, File child) throws IOException {
        FileUtils.requireDirectoryExists(directory, "directory");
        if (child == null || !directory.exists() || !child.exists()) {
            return false;
        }
        return FilenameUtils.directoryContains(directory.getCanonicalPath(), child.getCanonicalPath());
    }

    private static void doCopyDirectory(File srcDir, File destDir, FileFilter fileFilter, List<String> exclusionList, boolean preserveDirDate, CopyOption ... copyOptions) throws IOException {
        File[] srcFiles = FileUtils.listFiles(srcDir, fileFilter);
        FileUtils.requireDirectoryIfExists(destDir, "destDir");
        FileUtils.mkdirs(destDir);
        FileUtils.requireCanWrite(destDir, "destDir");
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList != null && exclusionList.contains(srcFile.getCanonicalPath())) continue;
            if (srcFile.isDirectory()) {
                FileUtils.doCopyDirectory(srcFile, dstFile, fileFilter, exclusionList, preserveDirDate, copyOptions);
                continue;
            }
            FileUtils.copyFile(srcFile, dstFile, preserveDirDate, copyOptions);
        }
        if (preserveDirDate) {
            FileUtils.setTimes(srcDir, destDir);
        }
    }

    public static void forceDelete(File file) throws IOException {
        Counters.PathCounters deleteCounters;
        Objects.requireNonNull(file, "file");
        try {
            deleteCounters = PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY, StandardDeleteOption.OVERRIDE_READ_ONLY);
        }
        catch (IOException e) {
            throw new IOException("Cannot delete file: " + file, e);
        }
        if (deleteCounters.getFileCounter().get() < 1L && deleteCounters.getDirectoryCounter().get() < 1L) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
    }

    public static void forceDeleteOnExit(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            FileUtils.deleteDirectoryOnExit(file);
        } else {
            file.deleteOnExit();
        }
    }

    public static void forceMkdir(File directory) throws IOException {
        FileUtils.mkdirs(directory);
    }

    public static void forceMkdirParent(File file) throws IOException {
        FileUtils.forceMkdir(FileUtils.getParentFile(Objects.requireNonNull(file, "file")));
    }

    public static File getFile(File directory, String ... names) {
        Objects.requireNonNull(directory, "directory");
        Objects.requireNonNull(names, "names");
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    public static File getFile(String ... names) {
        Objects.requireNonNull(names, "names");
        File file = null;
        for (String name : names) {
            file = file == null ? new File(name) : new File(file, name);
        }
        return file;
    }

    private static File getParentFile(File file) {
        return file == null ? null : file.getParentFile();
    }

    public static File getTempDirectory() {
        return new File(FileUtils.getTempDirectoryPath());
    }

    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static File getUserDirectory() {
        return new File(FileUtils.getUserDirectoryPath());
    }

    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }

    public static boolean isDirectory(File file, LinkOption ... options) {
        return file != null && Files.isDirectory(file.toPath(), options);
    }

    public static boolean isEmptyDirectory(File directory) throws IOException {
        return PathUtils.isEmptyDirectory(directory.toPath());
    }

    public static boolean isFileNewer(File file, ChronoLocalDate chronoLocalDate) {
        return FileUtils.isFileNewer(file, chronoLocalDate, LocalTime.MAX);
    }

    public static boolean isFileNewer(File file, ChronoLocalDate chronoLocalDate, LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return FileUtils.isFileNewer(file, chronoLocalDate.atTime(localTime));
    }

    public static boolean isFileNewer(File file, ChronoLocalDate chronoLocalDate, OffsetTime offsetTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(offsetTime, "offsetTime");
        return FileUtils.isFileNewer(file, chronoLocalDate.atTime(offsetTime.toLocalTime()));
    }

    public static boolean isFileNewer(File file, ChronoLocalDateTime<?> chronoLocalDateTime) {
        return FileUtils.isFileNewer(file, chronoLocalDateTime, ZoneId.systemDefault());
    }

    public static boolean isFileNewer(File file, ChronoLocalDateTime<?> chronoLocalDateTime, ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return FileUtils.isFileNewer(file, chronoLocalDateTime.atZone(zoneId));
    }

    public static boolean isFileNewer(File file, ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return Uncheck.get(() -> PathUtils.isNewer(file.toPath(), chronoZonedDateTime, new LinkOption[0]));
    }

    public static boolean isFileNewer(File file, Date date) {
        Objects.requireNonNull(date, "date");
        return FileUtils.isFileNewer(file, date.getTime());
    }

    public static boolean isFileNewer(File file, File reference) {
        FileUtils.requireExists(reference, "reference");
        return Uncheck.get(() -> PathUtils.isNewer(file.toPath(), reference.toPath()));
    }

    public static boolean isFileNewer(File file, FileTime fileTime) throws IOException {
        Objects.requireNonNull(file, "file");
        return PathUtils.isNewer(file.toPath(), fileTime, new LinkOption[0]);
    }

    public static boolean isFileNewer(File file, Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return Uncheck.get(() -> PathUtils.isNewer(file.toPath(), instant, new LinkOption[0]));
    }

    public static boolean isFileNewer(File file, long timeMillis) {
        Objects.requireNonNull(file, "file");
        return Uncheck.get(() -> PathUtils.isNewer(file.toPath(), timeMillis, new LinkOption[0]));
    }

    public static boolean isFileNewer(File file, OffsetDateTime offsetDateTime) {
        Objects.requireNonNull(offsetDateTime, "offsetDateTime");
        return FileUtils.isFileNewer(file, offsetDateTime.toInstant());
    }

    public static boolean isFileOlder(File file, ChronoLocalDate chronoLocalDate) {
        return FileUtils.isFileOlder(file, chronoLocalDate, LocalTime.MAX);
    }

    public static boolean isFileOlder(File file, ChronoLocalDate chronoLocalDate, LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return FileUtils.isFileOlder(file, chronoLocalDate.atTime(localTime));
    }

    public static boolean isFileOlder(File file, ChronoLocalDate chronoLocalDate, OffsetTime offsetTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(offsetTime, "offsetTime");
        return FileUtils.isFileOlder(file, chronoLocalDate.atTime(offsetTime.toLocalTime()));
    }

    public static boolean isFileOlder(File file, ChronoLocalDateTime<?> chronoLocalDateTime) {
        return FileUtils.isFileOlder(file, chronoLocalDateTime, ZoneId.systemDefault());
    }

    public static boolean isFileOlder(File file, ChronoLocalDateTime<?> chronoLocalDateTime, ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return FileUtils.isFileOlder(file, chronoLocalDateTime.atZone(zoneId));
    }

    public static boolean isFileOlder(File file, ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return FileUtils.isFileOlder(file, chronoZonedDateTime.toInstant());
    }

    public static boolean isFileOlder(File file, Date date) {
        Objects.requireNonNull(date, "date");
        return FileUtils.isFileOlder(file, date.getTime());
    }

    public static boolean isFileOlder(File file, File reference) {
        FileUtils.requireExists(reference, "reference");
        return Uncheck.get(() -> PathUtils.isOlder(file.toPath(), reference.toPath()));
    }

    public static boolean isFileOlder(File file, FileTime fileTime) throws IOException {
        Objects.requireNonNull(file, "file");
        return PathUtils.isOlder(file.toPath(), fileTime, new LinkOption[0]);
    }

    public static boolean isFileOlder(File file, Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return Uncheck.get(() -> PathUtils.isOlder(file.toPath(), instant, new LinkOption[0]));
    }

    public static boolean isFileOlder(File file, long timeMillis) {
        Objects.requireNonNull(file, "file");
        return Uncheck.get(() -> PathUtils.isOlder(file.toPath(), timeMillis, new LinkOption[0]));
    }

    public static boolean isFileOlder(File file, OffsetDateTime offsetDateTime) {
        Objects.requireNonNull(offsetDateTime, "offsetDateTime");
        return FileUtils.isFileOlder(file, offsetDateTime.toInstant());
    }

    public static boolean isRegularFile(File file, LinkOption ... options) {
        return file != null && Files.isRegularFile(file.toPath(), options);
    }

    public static boolean isSymlink(File file) {
        return file != null && Files.isSymbolicLink(file.toPath());
    }

    public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.listFiles(directory, fileFilter, dirFilter).iterator();
    }

    public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive) {
        return Uncheck.apply(d -> FileUtils.streamFiles(d, recursive, extensions).iterator(), directory);
    }

    public static Iterator<File> iterateFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }

    public static long lastModified(File file) throws IOException {
        return FileUtils.lastModifiedFileTime(file).toMillis();
    }

    public static FileTime lastModifiedFileTime(File file) throws IOException {
        return Files.getLastModifiedTime(Objects.requireNonNull(file.toPath(), "file"), new LinkOption[0]);
    }

    public static long lastModifiedUnchecked(File file) {
        return Uncheck.apply(FileUtils::lastModified, file);
    }

    public static LineIterator lineIterator(File file) throws IOException {
        return FileUtils.lineIterator(file, null);
    }

    public static LineIterator lineIterator(File file, String charsetName) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath(), new OpenOption[0]);
            return IOUtils.lineIterator(inputStream, charsetName);
        }
        catch (IOException | RuntimeException ex) {
            IOUtils.closeQuietly((Closeable)inputStream, ex::addSuppressed);
            throw ex;
        }
    }

    private static AccumulatorPathVisitor listAccumulate(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter, FileVisitOption ... options) throws IOException {
        boolean isDirFilterSet = dirFilter != null;
        FileEqualsFileFilter rootDirFilter = new FileEqualsFileFilter(directory);
        FileEqualsFileFilter dirPathFilter = isDirFilterSet ? rootDirFilter.or(dirFilter) : rootDirFilter;
        AccumulatorPathVisitor visitor = new AccumulatorPathVisitor(Counters.noopPathCounters(), fileFilter, dirPathFilter, (p, e) -> FileVisitResult.CONTINUE);
        HashSet<FileVisitOption> optionSet = new HashSet<FileVisitOption>();
        Collections.addAll(optionSet, options);
        Files.walkFileTree(directory.toPath(), optionSet, FileUtils.toMaxDepth(isDirFilterSet), visitor);
        return visitor;
    }

    private static File[] listFiles(File directory, FileFilter fileFilter) throws IOException {
        File[] files;
        FileUtils.requireDirectoryExists(directory, "directory");
        File[] fileArray = files = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
        if (files == null) {
            throw new IOException("Unknown I/O error listing contents of directory: " + directory);
        }
        return files;
    }

    public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        AccumulatorPathVisitor visitor = Uncheck.apply(d -> FileUtils.listAccumulate(d, FileFileFilter.INSTANCE.and(fileFilter), dirFilter, FileVisitOption.FOLLOW_LINKS), directory);
        return visitor.getFileList().stream().map(Path::toFile).collect(Collectors.toList());
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
        return Uncheck.apply(d -> FileUtils.toList(FileUtils.streamFiles(d, recursive, extensions)), directory);
    }

    public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        AccumulatorPathVisitor visitor = Uncheck.apply(d -> FileUtils.listAccumulate(d, fileFilter, dirFilter, FileVisitOption.FOLLOW_LINKS), directory);
        List<Path> list = visitor.getFileList();
        list.addAll(visitor.getDirList());
        return list.stream().map(Path::toFile).collect(Collectors.toList());
    }

    private static File mkdirs(File directory) throws IOException {
        if (directory != null && !directory.mkdirs() && !directory.isDirectory()) {
            throw new IOException("Cannot create directory '" + directory + "'.");
        }
        return directory;
    }

    public static void moveDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.validateMoveParameters(srcDir, destDir);
        FileUtils.requireDirectory(srcDir, "srcDir");
        FileUtils.requireAbsent(destDir, "destDir");
        if (!srcDir.renameTo(destDir)) {
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }
            FileUtils.copyDirectory(srcDir, destDir);
            FileUtils.deleteDirectory(srcDir);
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir + "' after copy to '" + destDir + "'");
            }
        }
    }

    public static void moveDirectoryToDirectory(File source, File destDir, boolean createDestDir) throws IOException {
        FileUtils.validateMoveParameters(source, destDir);
        if (!destDir.isDirectory()) {
            if (destDir.exists()) {
                throw new IOException("Destination '" + destDir + "' is not a directory");
            }
            if (!createDestDir) {
                throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist [createDestDir=" + false + "]");
            }
            FileUtils.mkdirs(destDir);
        }
        FileUtils.moveDirectory(source, new File(destDir, source.getName()));
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        FileUtils.moveFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES);
    }

    public static void moveFile(File srcFile, File destFile, CopyOption ... copyOptions) throws IOException {
        FileUtils.validateMoveParameters(srcFile, destFile);
        FileUtils.requireFile(srcFile, "srcFile");
        FileUtils.requireAbsent(destFile, "destFile");
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            FileUtils.copyFile(srcFile, destFile, false, copyOptions);
            if (!srcFile.delete()) {
                FileUtils.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }

    public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
        FileUtils.validateMoveParameters(srcFile, destDir);
        if (!destDir.exists() && createDestDir) {
            FileUtils.mkdirs(destDir);
        }
        FileUtils.requireExistsChecked(destDir, "destDir");
        FileUtils.requireDirectory(destDir, "destDir");
        FileUtils.moveFile(srcFile, new File(destDir, srcFile.getName()));
    }

    public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        FileUtils.validateMoveParameters(src, destDir);
        if (src.isDirectory()) {
            FileUtils.moveDirectoryToDirectory(src, destDir, createDestDir);
        } else {
            FileUtils.moveFileToDirectory(src, destDir, createDestDir);
        }
    }

    public static OutputStream newOutputStream(File file, boolean append) throws IOException {
        return PathUtils.newOutputStream(Objects.requireNonNull(file, "file").toPath(), append);
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return new FileInputStream(file);
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return FileUtils.openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.exists()) {
            FileUtils.requireFile(file, "file");
            FileUtils.requireCanWrite(file, "file");
        } else {
            FileUtils.createParentDirectories(file);
        }
        return new FileOutputStream(file, append);
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return Files.readAllBytes(file.toPath());
    }

    @Deprecated
    public static String readFileToString(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    public static String readFileToString(File file, Charset charsetName) throws IOException {
        return IOUtils.toString(() -> Files.newInputStream(file.toPath(), new OpenOption[0]), Charsets.toCharset(charsetName));
    }

    public static String readFileToString(File file, String charsetName) throws IOException {
        return FileUtils.readFileToString(file, Charsets.toCharset(charsetName));
    }

    @Deprecated
    public static List<String> readLines(File file) throws IOException {
        return FileUtils.readLines(file, Charset.defaultCharset());
    }

    public static List<String> readLines(File file, Charset charset) throws IOException {
        return Files.readAllLines(file.toPath(), charset);
    }

    public static List<String> readLines(File file, String charsetName) throws IOException {
        return FileUtils.readLines(file, Charsets.toCharset(charsetName));
    }

    private static void requireAbsent(File file, String name) throws FileExistsException {
        if (file.exists()) {
            throw new FileExistsException(String.format("File element in parameter '%s' already exists: '%s'", name, file));
        }
    }

    private static void requireCanonicalPathsNotEquals(File file1, File file2) throws IOException {
        String canonicalPath = file1.getCanonicalPath();
        if (canonicalPath.equals(file2.getCanonicalPath())) {
            throw new IllegalArgumentException(String.format("File canonical paths are equal: '%s' (file1='%s', file2='%s')", canonicalPath, file1, file2));
        }
    }

    private static void requireCanWrite(File file, String name) {
        Objects.requireNonNull(file, "file");
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File parameter '" + name + " is not writable: '" + file + "'");
        }
    }

    private static File requireDirectory(File directory, String name) {
        Objects.requireNonNull(directory, name);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a directory: '" + directory + "'");
        }
        return directory;
    }

    private static File requireDirectoryExists(File directory, String name) {
        FileUtils.requireExists(directory, name);
        FileUtils.requireDirectory(directory, name);
        return directory;
    }

    private static File requireDirectoryIfExists(File directory, String name) {
        Objects.requireNonNull(directory, name);
        if (directory.exists()) {
            FileUtils.requireDirectory(directory, name);
        }
        return directory;
    }

    private static File requireExists(File file, String fileParamName) {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }

    private static File requireExistsChecked(File file, String fileParamName) throws FileNotFoundException {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new FileNotFoundException("File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }

    private static File requireFile(File file, String name) {
        Objects.requireNonNull(file, name);
        if (!file.isFile()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a file: " + file);
        }
        return file;
    }

    private static void requireFileCopy(File source, File destination) throws FileNotFoundException {
        FileUtils.requireExistsChecked(source, "source");
        Objects.requireNonNull(destination, "destination");
    }

    private static File requireFileIfExists(File file, String name) {
        Objects.requireNonNull(file, name);
        return file.exists() ? FileUtils.requireFile(file, name) : file;
    }

    private static boolean setTimes(File sourceFile, File targetFile) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        Objects.requireNonNull(targetFile, "targetFile");
        try {
            BasicFileAttributes srcAttr = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class, new LinkOption[0]);
            BasicFileAttributeView destAttrView = Files.getFileAttributeView(targetFile.toPath(), BasicFileAttributeView.class, new LinkOption[0]);
            destAttrView.setTimes(srcAttr.lastModifiedTime(), srcAttr.lastAccessTime(), srcAttr.creationTime());
            return true;
        }
        catch (IOException ignored) {
            return targetFile.setLastModified(sourceFile.lastModified());
        }
    }

    public static long sizeOf(File file) {
        FileUtils.requireExists(file, "file");
        return Uncheck.get(() -> PathUtils.sizeOf(file.toPath()));
    }

    public static BigInteger sizeOfAsBigInteger(File file) {
        FileUtils.requireExists(file, "file");
        return Uncheck.get(() -> PathUtils.sizeOfAsBigInteger(file.toPath()));
    }

    public static long sizeOfDirectory(File directory) {
        FileUtils.requireDirectoryExists(directory, "directory");
        return Uncheck.get(() -> PathUtils.sizeOfDirectory(directory.toPath()));
    }

    public static BigInteger sizeOfDirectoryAsBigInteger(File directory) {
        FileUtils.requireDirectoryExists(directory, "directory");
        return Uncheck.get(() -> PathUtils.sizeOfDirectoryAsBigInteger(directory.toPath()));
    }

    public static Stream<File> streamFiles(File directory, boolean recursive, String ... extensions) throws IOException {
        IOFileFilter filter = extensions == null ? FileFileFilter.INSTANCE : FileFileFilter.INSTANCE.and(new SuffixFileFilter(FileUtils.toSuffixes(extensions)));
        return PathUtils.walk(directory.toPath(), filter, FileUtils.toMaxDepth(recursive), false, FileVisitOption.FOLLOW_LINKS).map(Path::toFile);
    }

    public static File toFile(URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        String filename = url.getFile().replace('/', File.separatorChar);
        return new File(FileUtils.decodeUrl(filename));
    }

    public static File[] toFiles(URL ... urls) {
        if (IOUtils.length(urls) == 0) {
            return EMPTY_FILE_ARRAY;
        }
        File[] files = new File[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            URL url = urls[i];
            if (url == null) continue;
            if (!"file".equalsIgnoreCase(url.getProtocol())) {
                throw new IllegalArgumentException("Can only convert file URL to a File: " + url);
            }
            files[i] = FileUtils.toFile(url);
        }
        return files;
    }

    private static List<File> toList(Stream<File> stream) {
        return stream.collect(Collectors.toList());
    }

    private static int toMaxDepth(boolean recursive) {
        return recursive ? Integer.MAX_VALUE : 1;
    }

    private static String[] toSuffixes(String ... extensions) {
        return (String[])Stream.of(Objects.requireNonNull(extensions, "extensions")).map(e -> "." + e).toArray(String[]::new);
    }

    public static void touch(File file) throws IOException {
        PathUtils.touch(Objects.requireNonNull(file, "file").toPath());
    }

    public static URL[] toURLs(File ... files) throws IOException {
        Objects.requireNonNull(files, "files");
        URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    private static void validateMoveParameters(File source, File destination) throws FileNotFoundException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        if (!source.exists()) {
            throw new FileNotFoundException("Source '" + source + "' does not exist");
        }
    }

    public static boolean waitFor(File file, int seconds) {
        Objects.requireNonNull(file, "file");
        return PathUtils.waitFor(file.toPath(), Duration.ofSeconds(seconds), PathUtils.EMPTY_LINK_OPTION_ARRAY);
    }

    @Deprecated
    public static void write(File file, CharSequence data) throws IOException {
        FileUtils.write(file, data, Charset.defaultCharset(), false);
    }

    @Deprecated
    public static void write(File file, CharSequence data, boolean append) throws IOException {
        FileUtils.write(file, data, Charset.defaultCharset(), append);
    }

    public static void write(File file, CharSequence data, Charset charset) throws IOException {
        FileUtils.write(file, data, charset, false);
    }

    public static void write(File file, CharSequence data, Charset charset, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, Objects.toString(data, null), charset, append);
    }

    public static void write(File file, CharSequence data, String charsetName) throws IOException {
        FileUtils.write(file, data, charsetName, false);
    }

    public static void write(File file, CharSequence data, String charsetName, boolean append) throws IOException {
        FileUtils.write(file, data, Charsets.toCharset(charsetName), append);
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, false);
    }

    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, 0, data.length, append);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, off, len, false);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len, boolean append) throws IOException {
        try (OutputStream out = FileUtils.newOutputStream(file, append);){
            out.write(data, off, len);
        }
    }

    public static void writeLines(File file, Collection<?> lines) throws IOException {
        FileUtils.writeLines(file, null, lines, null, false);
    }

    public static void writeLines(File file, Collection<?> lines, boolean append) throws IOException {
        FileUtils.writeLines(file, null, lines, null, append);
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {
        FileUtils.writeLines(file, null, lines, lineEnding, false);
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        FileUtils.writeLines(file, null, lines, lineEnding, append);
    }

    public static void writeLines(File file, String charsetName, Collection<?> lines) throws IOException {
        FileUtils.writeLines(file, charsetName, lines, null, false);
    }

    public static void writeLines(File file, String charsetName, Collection<?> lines, boolean append) throws IOException {
        FileUtils.writeLines(file, charsetName, lines, null, append);
    }

    public static void writeLines(File file, String charsetName, Collection<?> lines, String lineEnding) throws IOException {
        FileUtils.writeLines(file, charsetName, lines, lineEnding, false);
    }

    public static void writeLines(File file, String charsetName, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(FileUtils.newOutputStream(file, append));){
            IOUtils.writeLines(lines, lineEnding, (OutputStream)out, charsetName);
        }
    }

    @Deprecated
    public static void writeStringToFile(File file, String data) throws IOException {
        FileUtils.writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    @Deprecated
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, data, Charset.defaultCharset(), append);
    }

    public static void writeStringToFile(File file, String data, Charset charset) throws IOException {
        FileUtils.writeStringToFile(file, data, charset, false);
    }

    public static void writeStringToFile(File file, String data, Charset charset, boolean append) throws IOException {
        try (OutputStream out = FileUtils.newOutputStream(file, append);){
            IOUtils.write(data, out, charset);
        }
    }

    public static void writeStringToFile(File file, String data, String charsetName) throws IOException {
        FileUtils.writeStringToFile(file, data, charsetName, false);
    }

    public static void writeStringToFile(File file, String data, String charsetName, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, data, Charsets.toCharset(charsetName), append);
    }

    @Deprecated
    public FileUtils() {
    }
}

