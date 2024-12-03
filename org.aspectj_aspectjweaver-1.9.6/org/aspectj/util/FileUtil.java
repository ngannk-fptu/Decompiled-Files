/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.aspectj.util.LangUtil;

public class FileUtil {
    public static final File DEFAULT_PARENT = new File(".");
    public static final List<String> SOURCE_SUFFIXES = Collections.unmodifiableList(Arrays.asList(".java", ".aj"));
    public static final FileFilter ZIP_FILTER = new FileFilter(){

        @Override
        public boolean accept(File file) {
            return FileUtil.isZipFile(file);
        }

        public String toString() {
            return "ZIP_FILTER";
        }
    };
    static final int[] INT_RA = new int[0];
    public static final FileFilter ALL = new FileFilter(){

        @Override
        public boolean accept(File f) {
            return true;
        }
    };
    public static final FileFilter DIRS_AND_WRITABLE_CLASSES = new FileFilter(){

        @Override
        public boolean accept(File file) {
            return null != file && (file.isDirectory() || file.canWrite() && file.getName().toLowerCase().endsWith(".class"));
        }
    };
    private static final boolean PERMIT_CVS;
    public static final FileFilter aspectjSourceFileFilter;
    static final String FILECHARS = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean isZipFile(File file) {
        try {
            return null != file && new ZipFile(file) != null;
        }
        catch (IOException e) {
            return false;
        }
    }

    public static int zipSuffixLength(File file) {
        return null == file ? 0 : FileUtil.zipSuffixLength(file.getPath());
    }

    public static int zipSuffixLength(String path) {
        String test;
        if (null != path && 4 < path.length() && (".zip".equals(test = path.substring(path.length() - 4).toLowerCase()) || ".jar".equals(test))) {
            return 4;
        }
        return 0;
    }

    public static boolean hasSourceSuffix(File file) {
        return null != file && FileUtil.hasSourceSuffix(file.getPath());
    }

    public static boolean hasSourceSuffix(String path) {
        return null != path && 0 != FileUtil.sourceSuffixLength(path);
    }

    public static int sourceSuffixLength(File file) {
        return null == file ? 0 : FileUtil.sourceSuffixLength(file.getPath());
    }

    public static int sourceSuffixLength(String path) {
        if (LangUtil.isEmpty(path)) {
            return 0;
        }
        for (String suffix : SOURCE_SUFFIXES) {
            if (!path.endsWith(suffix) && !path.toLowerCase().endsWith(suffix)) continue;
            return suffix.length();
        }
        return 0;
    }

    public static boolean canReadDir(File dir) {
        return null != dir && dir.canRead() && dir.isDirectory();
    }

    public static boolean canReadFile(File file) {
        return null != file && file.canRead() && file.isFile();
    }

    public static boolean canWriteDir(File dir) {
        return null != dir && dir.canWrite() && dir.isDirectory();
    }

    public static boolean canWriteFile(File file) {
        return null != file && file.canWrite() && file.isFile();
    }

    public static void throwIaxUnlessCanReadDir(File dir, String label) {
        if (!FileUtil.canReadDir(dir)) {
            throw new IllegalArgumentException(label + " not readable dir: " + dir);
        }
    }

    public static void throwIaxUnlessCanWriteFile(File file, String label) {
        if (!FileUtil.canWriteFile(file)) {
            throw new IllegalArgumentException(label + " not writable file: " + file);
        }
    }

    public static void throwIaxUnlessCanWriteDir(File dir, String label) {
        if (!FileUtil.canWriteDir(dir)) {
            throw new IllegalArgumentException(label + " not writable dir: " + dir);
        }
    }

    public static String[] getPaths(File[] files) {
        if (null == files || 0 == files.length) {
            return new String[0];
        }
        String[] result = new String[files.length];
        for (int i = 0; i < result.length; ++i) {
            if (null == files[i]) continue;
            result[i] = files[i].getPath();
        }
        return result;
    }

    public static String[] getPaths(List<File> files) {
        int size;
        int n = size = null == files ? 0 : files.size();
        if (0 == size) {
            return new String[0];
        }
        String[] result = new String[size];
        for (int i = 0; i < size; ++i) {
            File file = files.get(i);
            if (null == file) continue;
            result[i] = file.getPath();
        }
        return result;
    }

    public static String fileToClassName(File basedir, File classFile) {
        LangUtil.throwIaxIfNull(classFile, "classFile");
        String classFilePath = FileUtil.normalizedPath(classFile);
        if (!classFilePath.endsWith(".class")) {
            String m = classFile + " does not end with .class";
            throw new IllegalArgumentException(m);
        }
        classFilePath = classFilePath.substring(0, classFilePath.length() - 6);
        if (null != basedir) {
            String basePath = FileUtil.normalizedPath(basedir);
            if (!classFilePath.startsWith(basePath)) {
                String m = classFile + " does not start with " + basedir;
                throw new IllegalArgumentException(m);
            }
            classFilePath = classFilePath.substring(basePath.length() + 1);
        } else {
            int loc;
            String[] suffixes = new String[]{"com", "org", "java", "javax"};
            boolean found = false;
            for (int i = 0; !found && i < suffixes.length; ++i) {
                int loc2 = classFilePath.indexOf(suffixes[i] + "/");
                if (0 != loc2 && (-1 == loc2 || '/' != classFilePath.charAt(loc2 - 1))) continue;
                classFilePath = classFilePath.substring(loc2);
                found = true;
            }
            if (!found && -1 != (loc = classFilePath.lastIndexOf("/"))) {
                classFilePath = classFilePath.substring(loc + 1);
            }
        }
        return classFilePath.replace('/', '.');
    }

    public static String normalizedPath(File file, File basedir) {
        String basePath;
        String filePath = FileUtil.normalizedPath(file);
        if (null != basedir && filePath.startsWith(basePath = FileUtil.normalizedPath(basedir)) && (filePath = filePath.substring(basePath.length())).startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return filePath;
    }

    public static String flatten(File[] files, String infix) {
        if (LangUtil.isEmpty(files)) {
            return "";
        }
        return FileUtil.flatten(FileUtil.getPaths(files), infix);
    }

    public static String flatten(String[] paths, String infix) {
        if (null == infix) {
            infix = File.pathSeparator;
        }
        StringBuffer result = new StringBuffer();
        boolean first = true;
        for (int i = 0; i < paths.length; ++i) {
            String path = paths[i];
            if (null == path) continue;
            if (first) {
                first = false;
            } else {
                result.append(infix);
            }
            result.append(path);
        }
        return result.toString();
    }

    public static String normalizedPath(File file) {
        return null == file ? "" : FileUtil.weakNormalize(file.getAbsolutePath());
    }

    public static String weakNormalize(String path) {
        if (null != path) {
            path = path.replace('\\', '/').trim();
        }
        return path;
    }

    public static File getBestFile(String[] paths) {
        if (null == paths) {
            return null;
        }
        File result = null;
        for (int i = 0; null == result && i < paths.length; ++i) {
            String path = paths[i];
            if (null == path) continue;
            if (path.startsWith("sp:")) {
                try {
                    path = System.getProperty(path.substring(3));
                }
                catch (Throwable t) {
                    path = null;
                }
                if (null == path) continue;
            }
            try {
                File f = new File(path);
                if (!f.exists() || !f.canRead()) continue;
                result = FileUtil.getBestFile(f);
                continue;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return result;
    }

    public static File getBestFile(String[] paths, boolean mustBeJar) {
        if (null == paths) {
            return null;
        }
        File result = null;
        for (int i = 0; null == result && i < paths.length; ++i) {
            String path = paths[i];
            if (null == path) continue;
            if (path.startsWith("sp:")) {
                try {
                    path = System.getProperty(path.substring(3));
                }
                catch (Throwable t) {
                    path = null;
                }
                if (null == path) continue;
            }
            try {
                File f = new File(path);
                if (!f.exists() || !f.canRead() || !mustBeJar || f.isDirectory()) continue;
                result = FileUtil.getBestFile(f);
                continue;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return result;
    }

    public static File getBestFile(File file) {
        LangUtil.throwIaxIfNull(file, "file");
        if (file.exists()) {
            try {
                return file.getCanonicalFile();
            }
            catch (IOException e) {
                return file.getAbsoluteFile();
            }
        }
        return file;
    }

    public static String getBestPath(File file) {
        LangUtil.throwIaxIfNull(file, "file");
        if (file.exists()) {
            try {
                return file.getCanonicalPath();
            }
            catch (IOException e) {
                return file.getAbsolutePath();
            }
        }
        return file.getPath();
    }

    public static String[] getAbsolutePaths(File[] files) {
        if (null == files || 0 == files.length) {
            return new String[0];
        }
        String[] result = new String[files.length];
        for (int i = 0; i < result.length; ++i) {
            if (null == files[i]) continue;
            result[i] = files[i].getAbsolutePath();
        }
        return result;
    }

    public static int deleteContents(File dir) {
        return FileUtil.deleteContents(dir, ALL);
    }

    public static int deleteContents(File dir, FileFilter filter) {
        return FileUtil.deleteContents(dir, filter, true);
    }

    public static int deleteContents(File dir, FileFilter filter, boolean deleteEmptyDirs) {
        if (null == dir) {
            throw new IllegalArgumentException("null dir");
        }
        if (!dir.exists() || !dir.canWrite()) {
            return 0;
        }
        if (!dir.isDirectory()) {
            dir.delete();
            return 1;
        }
        String[] fromFiles = dir.list();
        if (fromFiles == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < fromFiles.length; ++i) {
            String string = fromFiles[i];
            File file = new File(dir, string);
            if (null != filter && !filter.accept(file)) continue;
            if (file.isDirectory()) {
                result += FileUtil.deleteContents(file, filter, deleteEmptyDirs);
                String[] fileContent = file.list();
                if (!deleteEmptyDirs || fileContent == null || 0 != fileContent.length) continue;
                file.delete();
                continue;
            }
            file.delete();
            ++result;
        }
        return result;
    }

    public static int copyDir(File fromDir, File toDir) throws IOException {
        return FileUtil.copyDir(fromDir, toDir, null, null);
    }

    public static int copyDir(File fromDir, File toDir, String fromSuffix, String toSuffix) throws IOException {
        return FileUtil.copyDir(fromDir, toDir, fromSuffix, toSuffix, null);
    }

    public static int copyDir(File fromDir, File toDir, final String fromSuffix, String toSuffix, FileFilter delegate) throws IOException {
        String[] fromFiles;
        int slen;
        if (null == fromDir || !fromDir.canRead()) {
            return 0;
        }
        boolean haveSuffix = null != fromSuffix && 0 < fromSuffix.length();
        int n = slen = !haveSuffix ? 0 : fromSuffix.length();
        if (!toDir.exists()) {
            toDir.mkdirs();
        }
        if (!haveSuffix) {
            fromFiles = fromDir.list();
        } else {
            FilenameFilter filter = new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory() || name.endsWith(fromSuffix);
                }
            };
            fromFiles = fromDir.list(filter);
        }
        int result = 0;
        int MAX = null == fromFiles ? 0 : fromFiles.length;
        for (int i = 0; i < MAX; ++i) {
            String filename = fromFiles[i];
            File fromFile = new File(fromDir, filename);
            if (!fromFile.canRead()) continue;
            if (fromFile.isDirectory()) {
                result += FileUtil.copyDir(fromFile, new File(toDir, filename), fromSuffix, toSuffix, delegate);
                continue;
            }
            if (!fromFile.isFile()) continue;
            if (haveSuffix) {
                filename = filename.substring(0, filename.length() - slen);
            }
            if (null != toSuffix) {
                filename = filename + toSuffix;
            }
            File targetFile = new File(toDir, filename);
            if (null == delegate || delegate.accept(targetFile)) {
                FileUtil.copyFile(fromFile, targetFile);
            }
            ++result;
        }
        return result;
    }

    public static String[] listFiles(File srcDir) {
        ArrayList<String> result = new ArrayList<String>();
        if (null != srcDir && srcDir.canRead()) {
            FileUtil.listFiles(srcDir, null, result);
        }
        return result.toArray(new String[0]);
    }

    public static File[] listFiles(File srcDir, FileFilter fileFilter) {
        ArrayList<File> result = new ArrayList<File>();
        if (null != srcDir && srcDir.canRead()) {
            FileUtil.listFiles(srcDir, result, fileFilter);
        }
        return result.toArray(new File[result.size()]);
    }

    public static List<File> listClassFiles(File dir) {
        ArrayList<File> result = new ArrayList<File>();
        if (null != dir && dir.canRead()) {
            FileUtil.listClassFiles(dir, result);
        }
        return result;
    }

    public static File[] getBaseDirFiles(File basedir, String[] paths) {
        return FileUtil.getBaseDirFiles(basedir, paths, null);
    }

    public static File[] getBaseDirFiles(File basedir, String[] paths, String[] suffixes) {
        LangUtil.throwIaxIfNull(basedir, "basedir");
        LangUtil.throwIaxIfNull(paths, "paths");
        File[] result = null;
        if (!LangUtil.isEmpty(suffixes)) {
            ArrayList<File> list = new ArrayList<File>();
            block0: for (int i = 0; i < paths.length; ++i) {
                String path = paths[i];
                for (int j = 0; j < suffixes.length; ++j) {
                    if (!path.endsWith(suffixes[j])) continue;
                    list.add(new File(basedir, paths[i]));
                    continue block0;
                }
            }
            result = list.toArray(new File[0]);
        } else {
            result = new File[paths.length];
            for (int i = 0; i < result.length; ++i) {
                result[i] = FileUtil.newFile(basedir, paths[i]);
            }
        }
        return result;
    }

    private static File newFile(File dir, String path) {
        if (".".equals(path)) {
            return dir;
        }
        if ("..".equals(path)) {
            File parentDir = dir.getParentFile();
            if (null != parentDir) {
                return parentDir;
            }
            return new File(dir, "..");
        }
        return new File(dir, path);
    }

    public static File[] copyFiles(File srcDir, String[] relativePaths, File destDir) throws IllegalArgumentException, IOException {
        String[] paths = relativePaths;
        FileUtil.throwIaxUnlessCanReadDir(srcDir, "srcDir");
        FileUtil.throwIaxUnlessCanWriteDir(destDir, "destDir");
        LangUtil.throwIaxIfNull(paths, "relativePaths");
        File[] result = new File[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            String path = paths[i];
            LangUtil.throwIaxIfNull(path, "relativePaths-entry");
            File src = FileUtil.newFile(srcDir, paths[i]);
            File dest = FileUtil.newFile(destDir, path);
            File destParent = dest.getParentFile();
            if (!destParent.exists()) {
                destParent.mkdirs();
            }
            LangUtil.throwIaxIfFalse(FileUtil.canWriteDir(destParent), "dest-entry-parent");
            FileUtil.copyFile(src, dest);
            result[i] = dest;
        }
        return result;
    }

    public static void copyFile(File fromFile, File toFile) throws IOException {
        LangUtil.throwIaxIfNull(fromFile, "fromFile");
        LangUtil.throwIaxIfNull(toFile, "toFile");
        LangUtil.throwIaxIfFalse(!toFile.equals(fromFile), "same file");
        if (toFile.isDirectory()) {
            FileUtil.throwIaxUnlessCanWriteDir(toFile, "toFile");
            if (fromFile.isFile()) {
                File targFile = new File(toFile, fromFile.getName());
                FileUtil.copyValidFiles(fromFile, targFile);
            } else if (fromFile.isDirectory()) {
                FileUtil.copyDir(fromFile, toFile);
            } else {
                LangUtil.throwIaxIfFalse(false, "not dir or file: " + fromFile);
            }
        } else if (toFile.isFile()) {
            if (fromFile.isDirectory()) {
                LangUtil.throwIaxIfFalse(false, "can't copy to file dir: " + fromFile);
            }
            FileUtil.copyValidFiles(fromFile, toFile);
        } else {
            FileUtil.ensureParentWritable(toFile);
            if (fromFile.isFile()) {
                FileUtil.copyValidFiles(fromFile, toFile);
            } else if (fromFile.isDirectory()) {
                toFile.mkdirs();
                FileUtil.throwIaxUnlessCanWriteDir(toFile, "toFile");
                FileUtil.copyDir(fromFile, toFile);
            } else {
                LangUtil.throwIaxIfFalse(false, "not dir or file: " + fromFile);
            }
        }
    }

    public static File ensureParentWritable(File path) {
        LangUtil.throwIaxIfNull(path, "path");
        File pathParent = path.getParentFile();
        if (null == pathParent) {
            pathParent = DEFAULT_PARENT;
        }
        if (!pathParent.canWrite()) {
            pathParent.mkdirs();
        }
        FileUtil.throwIaxUnlessCanWriteDir(pathParent, "pathParent");
        return pathParent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyValidFiles(File fromFile, File toFile) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(fromFile);
            out = new FileOutputStream(toFile);
            FileUtil.copyStream(in, out);
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    public static void copyStream(DataInputStream in, PrintStream out) throws IOException {
        String s;
        LangUtil.throwIaxIfNull(in, "in");
        LangUtil.throwIaxIfNull(in, "out");
        while (null != (s = in.readLine())) {
            out.println(s);
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        int MAX = 4096;
        byte[] buf = new byte[4096];
        int bytesRead = in.read(buf, 0, 4096);
        while (bytesRead != -1) {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf, 0, 4096);
        }
    }

    public static void copyStream(Reader in, Writer out) throws IOException {
        int MAX = 4096;
        char[] buf = new char[4096];
        int bytesRead = in.read(buf, 0, 4096);
        while (bytesRead != -1) {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf, 0, 4096);
        }
    }

    public static File makeNewChildDir(File parent, String child) {
        if (null == parent || !parent.canWrite() || !parent.isDirectory()) {
            throw new IllegalArgumentException("bad parent: " + parent);
        }
        if (null == child) {
            child = "makeNewChildDir";
        } else if (!FileUtil.isValidFileName(child)) {
            throw new IllegalArgumentException("bad child: " + child);
        }
        File result = new File(parent, child);
        int safety = 1000;
        String suffix = FileUtil.randomFileString();
        while (0 < --safety && result.exists()) {
            result = new File(parent, child + suffix);
            suffix = FileUtil.randomFileString();
        }
        if (result.exists()) {
            System.err.println("exhausted files for child dir in " + parent);
            return null;
        }
        return result.mkdirs() && result.exists() ? result : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static File getTempDir(String name) {
        if (null == name) {
            name = "FileUtil_getTempDir";
        } else if (!FileUtil.isValidFileName(name)) {
            throw new IllegalArgumentException(" invalid: " + name);
        }
        File result = null;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("ignoreMe", ".txt");
            File tempParent = tempFile.getParentFile();
            result = FileUtil.makeNewChildDir(tempParent, name);
        }
        catch (IOException t) {
            result = FileUtil.makeNewChildDir(new File("."), name);
        }
        finally {
            if (null != tempFile) {
                tempFile.delete();
            }
        }
        return result;
    }

    public static URL[] getFileURLs(File[] files) {
        if (null == files || 0 == files.length) {
            return new URL[0];
        }
        URL[] result = new URL[files.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = FileUtil.getFileURL(files[i]);
        }
        return result;
    }

    public static URL getFileURL(File file) {
        LangUtil.throwIaxIfNull(file, "file");
        URL result = null;
        try {
            result = file.toURL();
            if (null != result) {
                return result;
            }
            String url = "file:" + file.getAbsolutePath().replace('\\', '/');
            result = new URL(url + (file.isDirectory() ? "/" : ""));
        }
        catch (MalformedURLException e) {
            String m = "Util.makeURL(\"" + file.getPath() + "\" MUE " + e.getMessage();
            System.err.println(m);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String writeAsString(File file, String contents) {
        String string;
        LangUtil.throwIaxIfNull(file, "file");
        if (null == contents) {
            contents = "";
        }
        Writer out = null;
        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                String string2 = "unable to make parent dir for " + file;
                return string2;
            }
            StringReader in = new StringReader(contents);
            out = new FileWriter(file);
            FileUtil.copyStream(in, out);
            string = null;
        }
        catch (IOException e) {
            String string3 = LangUtil.unqualifiedClassName(e) + " writing " + file + ": " + e.getMessage();
            return string3;
        }
        finally {
            if (null != out) {
                try {
                    out.close();
                }
                catch (IOException iOException) {}
            }
        }
        return string;
    }

    public static boolean[] readBooleanArray(DataInputStream s) throws IOException {
        int len = s.readInt();
        boolean[] ret = new boolean[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = s.readBoolean();
        }
        return ret;
    }

    public static void writeBooleanArray(boolean[] a, DataOutputStream s) throws IOException {
        int len = a.length;
        s.writeInt(len);
        for (int i = 0; i < len; ++i) {
            s.writeBoolean(a[i]);
        }
    }

    public static int[] readIntArray(DataInputStream s) throws IOException {
        int len = s.readInt();
        int[] ret = new int[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = s.readInt();
        }
        return ret;
    }

    public static void writeIntArray(int[] a, DataOutputStream s) throws IOException {
        int len = a.length;
        s.writeInt(len);
        for (int i = 0; i < len; ++i) {
            s.writeInt(a[i]);
        }
    }

    public static String[] readStringArray(DataInputStream s) throws IOException {
        int len = s.readInt();
        String[] ret = new String[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = s.readUTF();
        }
        return ret;
    }

    public static void writeStringArray(String[] a, DataOutputStream s) throws IOException {
        if (a == null) {
            s.writeInt(0);
            return;
        }
        int len = a.length;
        s.writeInt(len);
        for (int i = 0; i < len; ++i) {
            s.writeUTF(a[i]);
        }
    }

    public static String readAsString(File file) throws IOException {
        int ch;
        BufferedReader r = new BufferedReader(new FileReader(file));
        StringBuffer b = new StringBuffer();
        while ((ch = r.read()) != -1) {
            b.append((char)ch);
        }
        r.close();
        return b.toString();
    }

    public static byte[] readAsByteArray(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] ret = FileUtil.readAsByteArray(in);
        in.close();
        return ret;
    }

    public static byte[] readAsByteArray(InputStream inStream) throws IOException {
        int nRead;
        int size = 1024;
        byte[] ba = new byte[size];
        int readSoFar = 0;
        while ((nRead = inStream.read(ba, readSoFar, size - readSoFar)) != -1) {
            if ((readSoFar += nRead) != size) continue;
            int newSize = size * 2;
            byte[] newBa = new byte[newSize];
            System.arraycopy(ba, 0, newBa, 0, size);
            ba = newBa;
            size = newSize;
        }
        byte[] newBa = new byte[readSoFar];
        System.arraycopy(ba, 0, newBa, 0, readSoFar);
        return newBa;
    }

    static String randomFileString() {
        double FILECHARS_length = FILECHARS.length();
        int LEN = 6;
        char[] result = new char[6];
        int index = (int)(Math.random() * 6.0);
        for (int i = 0; i < 6; ++i) {
            if (index >= 6) {
                index = 0;
            }
            result[index++] = FILECHARS.charAt((int)(Math.random() * FILECHARS_length));
        }
        return new String(result);
    }

    public static InputStream getStreamFromZip(String zipFile, String name) {
        try {
            ZipFile zf = new ZipFile(zipFile);
            ZipEntry entry = zf.getEntry(name);
            return zf.getInputStream(entry);
        }
        catch (IOException ioe) {
            return null;
        }
    }

    public static List<String> lineSeek(String sought, List<String> sources, boolean listAll, PrintStream errorSink) {
        if (LangUtil.isEmpty(sought) || LangUtil.isEmpty(sources)) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String path : sources) {
            String error = FileUtil.lineSeek(sought, path, listAll, result);
            if (null == error || null == errorSink) continue;
            errorSink.println(error);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String lineSeek(String sought, String sourcePath, boolean listAll, ArrayList<String> sink) {
        if (LangUtil.isEmpty(sought) || LangUtil.isEmpty(sourcePath)) {
            return "nothing sought";
        }
        if (LangUtil.isEmpty(sourcePath)) {
            return "no sourcePath";
        }
        File file = new File(sourcePath);
        if (!file.canRead() || !file.isFile()) {
            return "sourcePath not a readable file";
        }
        int lineNum = 0;
        FileReader fin = null;
        try {
            String line;
            fin = new FileReader(file);
            BufferedReader reader = new BufferedReader(fin);
            while (null != (line = reader.readLine())) {
                ++lineNum;
                int loc = line.indexOf(sought);
                if (-1 == loc) continue;
                sink.add(sourcePath + ":" + lineNum + ":" + loc);
                if (listAll) continue;
                break;
            }
        }
        catch (IOException e) {
            String string = LangUtil.unqualifiedClassName(e) + " reading " + sourcePath + ":" + lineNum;
            return string;
        }
        finally {
            try {
                if (null != fin) {
                    fin.close();
                }
            }
            catch (IOException iOException) {}
        }
        return null;
    }

    public static BufferedOutputStream makeOutputStream(File file) throws FileNotFoundException {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    public static boolean sleepPastFinalModifiedTime(File[] files) {
        if (null == files || 0 == files.length) {
            return true;
        }
        long delayUntil = System.currentTimeMillis();
        for (int i = 0; i < files.length; ++i) {
            long nextModTime;
            File file = files[i];
            if (null == file || !file.exists() || (nextModTime = file.lastModified()) <= delayUntil) continue;
            delayUntil = nextModTime;
        }
        return LangUtil.sleepUntil(++delayUntil);
    }

    private static void listClassFiles(File baseDir, ArrayList<File> result) {
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.isDirectory()) {
                FileUtil.listClassFiles(f, result);
                continue;
            }
            if (!f.getName().endsWith(".class")) continue;
            result.add(f);
        }
    }

    private static void listFiles(File baseDir, ArrayList<File> result, FileFilter filter) {
        File[] files = baseDir.listFiles();
        boolean skipCVS = !PERMIT_CVS && filter == aspectjSourceFileFilter;
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.isDirectory()) {
                String name;
                if (skipCVS && ("cvs".equals(name = f.getName().toLowerCase()) || "sccs".equals(name))) continue;
                FileUtil.listFiles(f, result, filter);
                continue;
            }
            if (!filter.accept(f)) continue;
            result.add(f);
        }
    }

    private static boolean isValidFileName(String input) {
        return null != input && -1 == input.indexOf(File.pathSeparator);
    }

    private static void listFiles(File baseDir, String dir, ArrayList<String> result) {
        String dirPrefix = null == dir ? "" : dir + "/";
        File dirFile = null == dir ? baseDir : new File(baseDir.getPath() + "/" + dir);
        String[] files = dirFile.list();
        for (int i = 0; i < files.length; ++i) {
            File f = new File(dirFile, files[i]);
            String path = dirPrefix + files[i];
            if (f.isDirectory()) {
                FileUtil.listFiles(baseDir, path, result);
                continue;
            }
            result.add(path);
        }
    }

    private FileUtil() {
    }

    public static List<String> makeClasspath(URL[] urls) {
        LinkedList<String> ret = new LinkedList<String>();
        if (urls != null) {
            for (int i = 0; i < urls.length; ++i) {
                ret.add(FileUtil.toPathString(urls[i]));
            }
        }
        return ret;
    }

    private static String toPathString(URL url) {
        try {
            return url.toURI().getPath();
        }
        catch (URISyntaxException e) {
            System.err.println("Warning!! Malformed URL may cause problems: " + url);
            return url.getPath();
        }
    }

    static {
        String name = FileUtil.class.getName() + ".PERMIT_CVS";
        PERMIT_CVS = LangUtil.getBoolean(name, false);
        aspectjSourceFileFilter = new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".java") || name.endsWith(".aj");
            }
        };
    }

    public static class Pipe
    implements Runnable {
        private final InputStream in;
        private final OutputStream out;
        private final long sleep;
        private ByteArrayOutputStream snoop;
        private long totalWritten;
        private Throwable thrown;
        private boolean halt;
        private final boolean closeInput;
        private final boolean closeOutput;
        private boolean finishStream;
        private boolean done;

        Pipe(InputStream in, OutputStream out) {
            this(in, out, 100L, false, false);
        }

        Pipe(InputStream in, OutputStream out, long sleep, boolean closeInput, boolean closeOutput) {
            LangUtil.throwIaxIfNull(in, "in");
            LangUtil.throwIaxIfNull(out, "out");
            this.in = in;
            this.out = out;
            this.closeInput = closeInput;
            this.closeOutput = closeOutput;
            this.sleep = Math.min(0L, Math.max(60000L, sleep));
        }

        public void setSnoop(ByteArrayOutputStream snoop) {
            this.snoop = snoop;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            this.totalWritten = 0L;
            if (this.halt) {
                return;
            }
            try {
                int MAX = 4096;
                byte[] buf = new byte[4096];
                int count = this.in.read(buf, 0, 4096);
                while (this.halt && this.finishStream && 0 < count || !this.halt && -1 != count) {
                    this.out.write(buf, 0, count);
                    ByteArrayOutputStream mySnoop = this.snoop;
                    if (null != mySnoop) {
                        mySnoop.write(buf, 0, count);
                    }
                    this.totalWritten += (long)count;
                    if (this.halt && !this.finishStream) {
                        break;
                    }
                    if (!this.halt && 0L < this.sleep) {
                        Thread.sleep(this.sleep);
                    }
                    if (this.halt && !this.finishStream) {
                        break;
                    }
                    count = this.in.read(buf, 0, 4096);
                }
            }
            catch (Throwable e) {
                this.thrown = e;
            }
            finally {
                this.halt = true;
                if (this.closeInput) {
                    try {
                        this.in.close();
                    }
                    catch (IOException MAX) {}
                }
                if (this.closeOutput) {
                    try {
                        this.out.close();
                    }
                    catch (IOException MAX) {}
                }
                this.done = true;
                this.completing(this.totalWritten, this.thrown);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean halt(boolean wait, boolean finishStream) {
            if (!this.halt) {
                this.halt = true;
            }
            if (wait) {
                while (!this.done) {
                    Pipe pipe = this;
                    synchronized (pipe) {
                        this.notifyAll();
                    }
                    if (this.done) continue;
                    try {
                        Thread.sleep(5L);
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
            return this.halt;
        }

        public long totalWritten() {
            return this.totalWritten;
        }

        public Throwable getThrown() {
            return this.thrown;
        }

        protected void completing(long totalWritten, Throwable thrown) {
        }
    }
}

