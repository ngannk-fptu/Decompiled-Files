/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 *  com.twelvemonkeys.util.Visitor
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.FastByteArrayOutputStream;
import com.twelvemonkeys.io.FileSystem;
import com.twelvemonkeys.io.FilenameMaskFilter;
import com.twelvemonkeys.io.Win32File;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.Visitor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.text.NumberFormat;

public final class FileUtil {
    public static final int BUF_SIZE = 1024;
    private static String TEMP_DIR = null;
    private static final FileSystem FS = FileSystem.get();
    private static ThreadLocal<NumberFormat> sNumberFormat = new ThreadLocal<NumberFormat>(){

        @Override
        protected NumberFormat initialValue() {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(0);
            return numberFormat;
        }
    };

    public static void main(String[] stringArray) throws IOException {
        File file;
        if (stringArray[0].startsWith("file:")) {
            file = FileUtil.toFile(new URL(stringArray[0]));
            System.out.println(file);
        } else {
            file = new File(stringArray[0]);
            System.out.println(file.toURL());
        }
        System.out.println("Free space: " + FileUtil.getFreeSpace(file) + "/" + FileUtil.getTotalSpace(file) + " bytes");
    }

    private FileUtil() {
    }

    public static boolean copy(String string, String string2) throws IOException {
        return FileUtil.copy(new File(string), new File(string2), false);
    }

    public static boolean copy(String string, String string2, boolean bl) throws IOException {
        return FileUtil.copy(new File(string), new File(string2), bl);
    }

    public static boolean copy(File file, File file2) throws IOException {
        return FileUtil.copy(file, file2, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean copy(File file, File file2, boolean bl) throws IOException {
        if (file.isDirectory()) {
            return FileUtil.copyDir(file, file2, bl);
        }
        if (file2.isDirectory()) {
            file2 = new File(file2, file.getName());
        }
        if (!bl && file2.exists()) {
            return false;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file2);
            FileUtil.copy(fileInputStream, fileOutputStream);
        }
        catch (Throwable throwable) {
            FileUtil.close(fileInputStream);
            FileUtil.close(fileOutputStream);
            throw throwable;
        }
        FileUtil.close(fileInputStream);
        FileUtil.close(fileOutputStream);
        return true;
    }

    public static void close(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void close(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    static void close(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static boolean copyDir(File file, File file2, boolean bl) throws IOException {
        File[] fileArray;
        if (file2.exists() && !file2.isDirectory()) {
            throw new IOException("A directory may only be copied to another directory, not to a file");
        }
        file2.mkdirs();
        boolean bl2 = true;
        for (File file3 : fileArray = file.listFiles()) {
            if (FileUtil.copy(file3, new File(file2, file3.getName()), bl)) continue;
            bl2 = false;
        }
        return bl2;
    }

    public static boolean copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int n;
        Validate.notNull((Object)inputStream, (String)"from");
        Validate.notNull((Object)outputStream, (String)"to");
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 2048);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 2048);
        byte[] byArray = new byte[1024];
        while ((n = ((InputStream)bufferedInputStream).read(byArray)) != -1) {
            ((OutputStream)bufferedOutputStream).write(byArray, 0, n);
        }
        ((OutputStream)bufferedOutputStream).flush();
        return true;
    }

    public static String getExtension(String string) {
        return FileUtil.getExtension0(FileUtil.getFilename(string));
    }

    public static String getExtension(File file) {
        return FileUtil.getExtension0(file.getName());
    }

    private static String getExtension0(String string) {
        int n = string.lastIndexOf(46);
        if (n >= 0) {
            return string.substring(n + 1);
        }
        return null;
    }

    public static String getBasename(String string) {
        return FileUtil.getBasename0(FileUtil.getFilename(string));
    }

    public static String getBasename(File file) {
        return FileUtil.getBasename0(file.getName());
    }

    public static String getBasename0(String string) {
        int n = string.lastIndexOf(46);
        if (n >= 0) {
            return string.substring(0, n);
        }
        return string;
    }

    public static String getDirectoryname(String string) {
        return FileUtil.getDirectoryname(string, File.separatorChar);
    }

    public static String getDirectoryname(String string, char c) {
        int n = string.lastIndexOf(c);
        if (n < 0) {
            return "";
        }
        return string.substring(0, n);
    }

    public static String getFilename(String string) {
        return FileUtil.getFilename(string, File.separatorChar);
    }

    public static String getFilename(String string, char c) {
        int n = string.lastIndexOf(c);
        if (n < 0) {
            return string;
        }
        return string.substring(n + 1);
    }

    public static boolean isEmpty(File file) {
        if (file.isDirectory()) {
            return file.list().length == 0;
        }
        return file.length() == 0L;
    }

    public static File getTempDirFile() {
        return new File(FileUtil.getTempDir());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getTempDir() {
        Class<FileUtil> clazz = FileUtil.class;
        synchronized (FileUtil.class) {
            if (TEMP_DIR == null) {
                String string = System.getProperty("java.io.tmpdir");
                if (StringUtil.isEmpty((String)string)) {
                    string = new File("/temp").exists() ? "/temp" : "/tmp";
                }
                TEMP_DIR = string;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return TEMP_DIR;
        }
    }

    public static byte[] read(String string) throws IOException {
        return FileUtil.read(new File(string));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] read(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        byte[] byArray = new byte[(int)file.length()];
        BufferedInputStream bufferedInputStream = null;
        try {
            int n;
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 2048);
            for (int i = 0; (n = ((InputStream)bufferedInputStream).read(byArray, i, ((InputStream)bufferedInputStream).available())) != -1 && i < byArray.length; i += n) {
            }
        }
        catch (Throwable throwable) {
            FileUtil.close(bufferedInputStream);
            throw throwable;
        }
        FileUtil.close(bufferedInputStream);
        return byArray;
    }

    public static byte[] read(InputStream inputStream) throws IOException {
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream(1024);
        FileUtil.copy(inputStream, fastByteArrayOutputStream);
        return ((ByteArrayOutputStream)fastByteArrayOutputStream).toByteArray();
    }

    public static boolean write(OutputStream outputStream, byte[] byArray) throws IOException {
        outputStream.write(byArray);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean write(File file, byte[] byArray) throws IOException {
        boolean bl = false;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bl = FileUtil.write(bufferedOutputStream, byArray);
        }
        catch (Throwable throwable) {
            FileUtil.close(bufferedOutputStream);
            throw throwable;
        }
        FileUtil.close(bufferedOutputStream);
        return bl;
    }

    public static boolean write(String string, byte[] byArray) throws IOException {
        return FileUtil.write(new File(string), byArray);
    }

    public static boolean delete(File file, boolean bl) throws IOException {
        if (bl && file.isDirectory()) {
            return FileUtil.deleteDir(file);
        }
        return file.exists() && file.delete();
    }

    private static boolean deleteDir(File file) throws IOException {
        class DeleteFilesVisitor
        implements Visitor<File> {
            private int failedCount = 0;
            private IOException exception = null;

            DeleteFilesVisitor() {
            }

            public void visit(File file) {
                block3: {
                    try {
                        if (!FileUtil.delete(file, true)) {
                            ++this.failedCount;
                        }
                    }
                    catch (IOException iOException) {
                        ++this.failedCount;
                        if (this.exception != null) break block3;
                        this.exception = iOException;
                    }
                }
            }

            boolean succeeded() throws IOException {
                if (this.exception != null) {
                    throw this.exception;
                }
                return this.failedCount == 0;
            }
        }
        DeleteFilesVisitor deleteFilesVisitor = new DeleteFilesVisitor();
        FileUtil.visitFiles(file, null, deleteFilesVisitor);
        return deleteFilesVisitor.succeeded() && file.delete();
    }

    public static boolean delete(String string, boolean bl) throws IOException {
        return FileUtil.delete(new File(string), bl);
    }

    public static boolean delete(File file) throws IOException {
        return FileUtil.delete(file, false);
    }

    public static boolean delete(String string) throws IOException {
        return FileUtil.delete(new File(string), false);
    }

    public static boolean rename(File file, File file2, boolean bl) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (file.isFile() && file2.isDirectory()) {
            file2 = new File(file2, file.getName());
        }
        return (bl || !file2.exists()) && file.renameTo(file2);
    }

    public static boolean rename(File file, File file2) throws IOException {
        return FileUtil.rename(file, file2, false);
    }

    public static boolean rename(File file, String string, boolean bl) throws IOException {
        return FileUtil.rename(file, new File(string), bl);
    }

    public static boolean rename(File file, String string) throws IOException {
        return FileUtil.rename(file, new File(string), false);
    }

    public static boolean rename(String string, String string2, boolean bl) throws IOException {
        return FileUtil.rename(new File(string), new File(string2), bl);
    }

    public static boolean rename(String string, String string2) throws IOException {
        return FileUtil.rename(new File(string), new File(string2), false);
    }

    public static File[] list(String string) throws FileNotFoundException {
        return FileUtil.list(string, null);
    }

    public static File[] list(String string, String string2) throws FileNotFoundException {
        if (StringUtil.isEmpty((String)string)) {
            return null;
        }
        File file = FileUtil.resolve(string);
        if (!file.isDirectory() || !file.canRead()) {
            throw new FileNotFoundException("\"" + string + "\" is not a directory or is not readable.");
        }
        if (StringUtil.isEmpty((String)string2)) {
            return file.listFiles();
        }
        FilenameMaskFilter filenameMaskFilter = new FilenameMaskFilter(string2);
        return file.listFiles(filenameMaskFilter);
    }

    public static File toFile(URL uRL) {
        if (uRL == null) {
            throw new NullPointerException("URL == null");
        }
        if (!"file".equals(uRL.getProtocol())) {
            throw new IllegalArgumentException("URL scheme is not \"file\"");
        }
        if (uRL.getAuthority() != null) {
            throw new IllegalArgumentException("URL has an authority component");
        }
        if (uRL.getRef() != null) {
            throw new IllegalArgumentException("URI has a fragment component");
        }
        if (uRL.getQuery() != null) {
            throw new IllegalArgumentException("URL has a query component");
        }
        String string = uRL.getPath();
        if (!string.startsWith("/")) {
            throw new IllegalArgumentException("URI is not hierarchical");
        }
        if (string.isEmpty()) {
            throw new IllegalArgumentException("URI path component is empty");
        }
        if (File.separatorChar != '/') {
            string = string.replace('/', File.separatorChar);
        }
        return FileUtil.resolve(string);
    }

    public static File resolve(String string) {
        return Win32File.wrap(new File(string));
    }

    public static File resolve(File file) {
        return Win32File.wrap(file);
    }

    public static File resolve(File file, String string) {
        return Win32File.wrap(new File(file, string));
    }

    public static File[] resolve(File[] fileArray) {
        return Win32File.wrap(fileArray);
    }

    public static long getFreeSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long l = FileUtil.getSpace16("getFreeSpace", file2);
        if (l != null) {
            return l;
        }
        return FS.getFreeSpace(file2);
    }

    public static long getUsableSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long l = FileUtil.getSpace16("getUsableSpace", file2);
        if (l != null) {
            return l;
        }
        return FileUtil.getTotalSpace(file2);
    }

    public static long getTotalSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long l = FileUtil.getSpace16("getTotalSpace", file2);
        if (l != null) {
            return l;
        }
        return FS.getTotalSpace(file2);
    }

    private static Long getSpace16(String string, File file) {
        try {
            Method method = File.class.getMethod(string, new Class[0]);
            return (Long)method.invoke((Object)file, new Object[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            Throwable throwable = invocationTargetException.getTargetException();
            if (throwable instanceof SecurityException) {
                throw (SecurityException)throwable;
            }
            throw new UndeclaredThrowableException(throwable);
        }
        return null;
    }

    public static String toHumanReadableSize(long l) {
        if (l < 1024L) {
            return l + " Bytes";
        }
        if (l < 0x100000L) {
            return FileUtil.getSizeFormat().format((double)l / 1024.0) + " KB";
        }
        if (l < 0x40000000L) {
            return FileUtil.getSizeFormat().format((double)l / 1048576.0) + " MB";
        }
        if (l < 0x10000000000L) {
            return FileUtil.getSizeFormat().format((double)l / 1.073741824E9) + " GB";
        }
        if (l < 0x4000000000000L) {
            return FileUtil.getSizeFormat().format((double)l / 1.099511627776E12) + " TB";
        }
        return FileUtil.getSizeFormat().format((double)l / 1.125899906842624E15) + " PB";
    }

    private static NumberFormat getSizeFormat() {
        return sNumberFormat.get();
    }

    public static void visitFiles(File file, final FileFilter fileFilter, final Visitor<File> visitor) {
        Validate.notNull((Object)file, (String)"directory");
        Validate.notNull(visitor, (String)"visitor");
        file.listFiles(new FileFilter(){

            @Override
            public boolean accept(File file) {
                if (fileFilter == null || fileFilter.accept(file)) {
                    visitor.visit((Object)file);
                }
                return false;
            }
        });
    }
}

