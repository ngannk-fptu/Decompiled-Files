/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Locator
 */
package org.apache.tools.ant.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ResourceUtils;

public class FileUtils {
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
    private static final int EXPAND_SPACE = 50;
    private static final FileUtils PRIMARY_INSTANCE = new FileUtils();
    private static Random rand = new Random(System.currentTimeMillis() + Runtime.getRuntime().freeMemory());
    private static final boolean ON_NETWARE = Os.isFamily("netware");
    private static final boolean ON_DOS = Os.isFamily("dos");
    private static final boolean ON_WIN9X = Os.isFamily("win9x");
    private static final boolean ON_WINDOWS = Os.isFamily("windows");
    static final int BUF_SIZE = 8192;
    public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000L;
    public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000L;
    public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1L;
    private static final FileAttribute<?>[] TMPFILE_ATTRIBUTES = new FileAttribute[]{PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE))};
    private static final FileAttribute<?>[] TMPDIR_ATTRIBUTES = new FileAttribute[]{PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE))};
    private static final FileAttribute<?>[] NO_TMPFILE_ATTRIBUTES = new FileAttribute[0];
    private final Object cacheFromUriLock = new Object();
    private String cacheFromUriRequest = null;
    private String cacheFromUriResponse = null;

    @Deprecated
    public static FileUtils newFileUtils() {
        return new FileUtils();
    }

    public static FileUtils getFileUtils() {
        return PRIMARY_INSTANCE;
    }

    protected FileUtils() {
    }

    public URL getFileURL(File file) throws MalformedURLException {
        return new URL(file.toURI().toASCIIString());
    }

    public void copyFile(String sourceFile, String destFile) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), null, false, false);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, false, false);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters, boolean overwrite) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, overwrite, false);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters, boolean overwrite, boolean preserveLastModified) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, overwrite, preserveLastModified);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters, boolean overwrite, boolean preserveLastModified, String encoding) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, overwrite, preserveLastModified, encoding);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, String encoding, Project project) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, filterChains, overwrite, preserveLastModified, encoding, project);
    }

    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, String inputEncoding, String outputEncoding, Project project) throws IOException {
        this.copyFile(new File(sourceFile), new File(destFile), filters, filterChains, overwrite, preserveLastModified, inputEncoding, outputEncoding, project);
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        this.copyFile(sourceFile, destFile, null, false, false);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters) throws IOException {
        this.copyFile(sourceFile, destFile, filters, false, false);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, boolean overwrite) throws IOException {
        this.copyFile(sourceFile, destFile, filters, overwrite, false);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, boolean overwrite, boolean preserveLastModified) throws IOException {
        this.copyFile(sourceFile, destFile, filters, overwrite, preserveLastModified, null);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, boolean overwrite, boolean preserveLastModified, String encoding) throws IOException {
        this.copyFile(sourceFile, destFile, filters, null, overwrite, preserveLastModified, encoding, null);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, String encoding, Project project) throws IOException {
        this.copyFile(sourceFile, destFile, filters, filterChains, overwrite, preserveLastModified, encoding, encoding, project);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, String inputEncoding, String outputEncoding, Project project) throws IOException {
        this.copyFile(sourceFile, destFile, filters, filterChains, overwrite, preserveLastModified, false, inputEncoding, outputEncoding, project);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding, String outputEncoding, Project project) throws IOException {
        this.copyFile(sourceFile, destFile, filters, filterChains, overwrite, preserveLastModified, append, inputEncoding, outputEncoding, project, false);
    }

    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding, String outputEncoding, Project project, boolean force) throws IOException {
        ResourceUtils.copyResource(new FileResource(sourceFile), new FileResource(destFile), filters, filterChains, overwrite, preserveLastModified, append, inputEncoding, outputEncoding, project, force);
    }

    public void setFileLastModified(File file, long time) {
        ResourceUtils.setLastModified(new FileResource(file), time);
    }

    public File resolveFile(File file, String filename) {
        if (!FileUtils.isAbsolutePath(filename)) {
            char sep = File.separatorChar;
            if (FileUtils.isContextRelativePath(filename = filename.replace('/', sep).replace('\\', sep))) {
                file = null;
                String udir = System.getProperty("user.dir");
                if (filename.charAt(0) == sep && udir.charAt(0) == sep) {
                    filename = this.dissect(udir)[0] + filename.substring(1);
                }
            }
            filename = new File(file, filename).getAbsolutePath();
        }
        return this.normalize(filename);
    }

    public static boolean isContextRelativePath(String filename) {
        if (!ON_DOS && !ON_NETWARE || filename.isEmpty()) {
            return false;
        }
        char sep = File.separatorChar;
        filename = filename.replace('/', sep).replace('\\', sep);
        char c = filename.charAt(0);
        int len = filename.length();
        return c == sep && (len == 1 || filename.charAt(1) != sep) || Character.isLetter(c) && len > 1 && filename.charAt(1) == ':' && (len == 2 || filename.charAt(2) != sep);
    }

    public static boolean isAbsolutePath(String filename) {
        if (filename.isEmpty()) {
            return false;
        }
        int len = filename.length();
        char sep = File.separatorChar;
        filename = filename.replace('/', sep).replace('\\', sep);
        char c = filename.charAt(0);
        if (!ON_DOS && !ON_NETWARE) {
            return c == sep;
        }
        if (c == sep) {
            if (!ON_DOS || len <= 4 || filename.charAt(1) != sep) {
                return false;
            }
            int nextsep = filename.indexOf(sep, 2);
            return nextsep > 2 && nextsep + 1 < len;
        }
        int colon = filename.indexOf(58);
        return Character.isLetter(c) && colon == 1 && filename.length() > 2 && filename.charAt(2) == sep || ON_NETWARE && colon > 0;
    }

    public static String translatePath(String toProcess) {
        if (toProcess == null || toProcess.isEmpty()) {
            return "";
        }
        StringBuilder path = new StringBuilder(toProcess.length() + 50);
        PathTokenizer tokenizer = new PathTokenizer(toProcess);
        while (tokenizer.hasMoreTokens()) {
            String pathComponent = tokenizer.nextToken();
            pathComponent = pathComponent.replace('/', File.separatorChar);
            pathComponent = pathComponent.replace('\\', File.separatorChar);
            if (path.length() > 0) {
                path.append(File.pathSeparatorChar);
            }
            path.append(pathComponent);
        }
        return path.toString();
    }

    public File normalize(String path) {
        Stack<String> s = new Stack<String>();
        String[] dissect = this.dissect(path);
        s.push(dissect[0]);
        StringTokenizer tok = new StringTokenizer(dissect[1], File.separator);
        while (tok.hasMoreTokens()) {
            String thisToken = tok.nextToken();
            if (".".equals(thisToken)) continue;
            if ("..".equals(thisToken)) {
                if (s.size() < 2) {
                    return new File(path);
                }
                s.pop();
                continue;
            }
            s.push(thisToken);
        }
        StringBuilder sb = new StringBuilder();
        int size = s.size();
        for (int i = 0; i < size; ++i) {
            if (i > 1) {
                sb.append(File.separatorChar);
            }
            sb.append((String)s.elementAt(i));
        }
        return new File(sb.toString());
    }

    public String[] dissect(String path) {
        String root;
        char sep = File.separatorChar;
        if (!FileUtils.isAbsolutePath(path = path.replace('/', sep).replace('\\', sep))) {
            throw new BuildException(path + " is not an absolute path");
        }
        int colon = path.indexOf(58);
        if (colon > 0 && (ON_DOS || ON_NETWARE)) {
            int next = colon + 1;
            root = path.substring(0, next);
            char[] ca = path.toCharArray();
            root = root + sep;
            next = ca[next] == sep ? next + 1 : next;
            StringBuilder sbPath = new StringBuilder();
            for (int i = next; i < ca.length; ++i) {
                if (ca[i] == sep && ca[i - 1] == sep) continue;
                sbPath.append(ca[i]);
            }
            path = sbPath.toString();
        } else if (path.length() > 1 && path.charAt(1) == sep) {
            int nextsep = path.indexOf(sep, 2);
            root = (nextsep = path.indexOf(sep, nextsep + 1)) > 2 ? path.substring(0, nextsep + 1) : path;
            path = path.substring(root.length());
        } else {
            root = File.separator;
            path = path.substring(1);
        }
        return new String[]{root, path};
    }

    public String toVMSPath(File f) {
        String path = this.normalize(f.getAbsolutePath()).getPath();
        String name = f.getName();
        boolean isAbsolute = path.charAt(0) == File.separatorChar;
        boolean isDirectory = f.isDirectory() && !name.regionMatches(true, name.length() - 4, ".DIR", 0, 4);
        String device = null;
        StringBuilder directory = null;
        String file = null;
        int index = 0;
        if (isAbsolute) {
            index = path.indexOf(File.separatorChar, 1);
            if (index == -1) {
                return path.substring(1) + ":[000000]";
            }
            device = path.substring(1, index++);
        }
        if (isDirectory) {
            directory = new StringBuilder(path.substring(index).replace(File.separatorChar, '.'));
        } else {
            int dirEnd = path.lastIndexOf(File.separatorChar);
            if (dirEnd == -1 || dirEnd < index) {
                file = path.substring(index);
            } else {
                directory = new StringBuilder(path.substring(index, dirEnd).replace(File.separatorChar, '.'));
                index = dirEnd + 1;
                if (path.length() > index) {
                    file = path.substring(index);
                }
            }
        }
        if (!isAbsolute && directory != null) {
            directory.insert(0, '.');
        }
        String osPath = (device != null ? device + ":" : "") + (directory != null ? "[" + directory + "]" : "") + (file != null ? file : "");
        return osPath;
    }

    @Deprecated
    public File createTempFile(String prefix, String suffix, File parentDir) {
        return this.createTempFile(prefix, suffix, parentDir, false, false);
    }

    @Deprecated
    public File createTempFile(String prefix, String suffix, File parentDir, boolean deleteOnExit, boolean createFile) {
        return this.createTempFile(null, prefix, suffix, parentDir, deleteOnExit, createFile);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File createTempFile(Project project, String prefix, String suffix, File parentDir, boolean deleteOnExit, boolean createFile) {
        File result;
        String p = null;
        if (parentDir != null) {
            p = parentDir.getPath();
        } else if (project != null && project.getProperty("ant.tmpdir") != null) {
            p = project.getProperty("ant.tmpdir");
        } else if (project != null && deleteOnExit) {
            if (project.getProperty("ant.auto.tmpdir") != null) {
                p = project.getProperty("ant.auto.tmpdir");
            } else {
                Path systemTempDirPath = new File(System.getProperty("java.io.tmpdir")).toPath();
                PosixFileAttributeView systemTempDirPosixAttributes = Files.getFileAttributeView(systemTempDirPath, PosixFileAttributeView.class, new LinkOption[0]);
                if (systemTempDirPosixAttributes != null) {
                    try {
                        File projectTempDir = Files.createTempDirectory(systemTempDirPath, "ant", TMPDIR_ATTRIBUTES).toFile();
                        projectTempDir.deleteOnExit();
                        p = projectTempDir.getAbsolutePath();
                        project.setProperty("ant.auto.tmpdir", p);
                    }
                    catch (IOException projectTempDir) {
                        // empty catch block
                    }
                }
            }
        }
        String parent = p != null ? p : System.getProperty("java.io.tmpdir");
        prefix = String.valueOf(prefix);
        suffix = String.valueOf(suffix);
        if (createFile) {
            try {
                Path parentPath = new File(parent).toPath();
                PosixFileAttributeView parentPosixAttributes = Files.getFileAttributeView(parentPath, PosixFileAttributeView.class, new LinkOption[0]);
                result = Files.createTempFile(parentPath, prefix, suffix, parentPosixAttributes != null ? TMPFILE_ATTRIBUTES : NO_TMPFILE_ATTRIBUTES).toFile();
            }
            catch (IOException e) {
                throw new BuildException("Could not create tempfile in " + parent, e);
            }
        }
        DecimalFormat fmt = new DecimalFormat("#####");
        Random random = rand;
        synchronized (random) {
            while ((result = new File(parent, prefix + fmt.format(rand.nextInt(Integer.MAX_VALUE)) + suffix)).exists()) {
            }
        }
        if (deleteOnExit) {
            result.deleteOnExit();
        }
        return result;
    }

    @Deprecated
    public File createTempFile(String prefix, String suffix, File parentDir, boolean deleteOnExit) {
        return this.createTempFile(prefix, suffix, parentDir, deleteOnExit, false);
    }

    public boolean contentEquals(File f1, File f2) throws IOException {
        return this.contentEquals(f1, f2, false);
    }

    public boolean contentEquals(File f1, File f2, boolean textfile) throws IOException {
        return ResourceUtils.contentEquals(new FileResource(f1), new FileResource(f2), textfile);
    }

    @Deprecated
    public File getParentFile(File f) {
        return f == null ? null : f.getParentFile();
    }

    public static String readFully(Reader rdr) throws IOException {
        return FileUtils.readFully(rdr, 8192);
    }

    public static String readFully(Reader rdr, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
        char[] buffer = new char[bufferSize];
        int bufferLength = 0;
        StringBuilder textBuffer = new StringBuilder();
        while (bufferLength != -1) {
            bufferLength = rdr.read(buffer);
            if (bufferLength <= 0) continue;
            textBuffer.append(buffer, 0, bufferLength);
        }
        return textBuffer.length() == 0 ? null : textBuffer.toString();
    }

    public static String safeReadFully(Reader reader) throws IOException {
        String ret = FileUtils.readFully(reader);
        return ret == null ? "" : ret;
    }

    public boolean createNewFile(File f) throws IOException {
        return f.createNewFile();
    }

    public boolean createNewFile(File f, boolean mkdirs) throws IOException {
        File parent = f.getParentFile();
        if (mkdirs && !parent.exists()) {
            parent.mkdirs();
        }
        return f.createNewFile();
    }

    @Deprecated
    public boolean isSymbolicLink(File parent, String name) throws IOException {
        if (parent == null) {
            return Files.isSymbolicLink(Paths.get(name, new String[0]));
        }
        return Files.isSymbolicLink(Paths.get(parent.toPath().toString(), name));
    }

    public String removeLeadingPath(File leading, File path) {
        String p;
        String l = this.normalize(leading.getAbsolutePath()).getAbsolutePath();
        if (l.equals(p = this.normalize(path.getAbsolutePath()).getAbsolutePath())) {
            return "";
        }
        if (!l.endsWith(File.separator)) {
            l = l + File.separator;
        }
        return p.startsWith(l) ? p.substring(l.length()) : p;
    }

    public boolean isLeadingPath(File leading, File path) {
        String up;
        String p;
        String l = this.normalize(leading.getAbsolutePath()).getAbsolutePath();
        if (l.equals(p = this.normalize(path.getAbsolutePath()).getAbsolutePath())) {
            return true;
        }
        if (!l.endsWith(File.separator)) {
            l = l + File.separator;
        }
        if (l.contains(up = File.separator + ".." + File.separator) || p.contains(up) || (p + File.separator).contains(up)) {
            return false;
        }
        return p.startsWith(l);
    }

    public boolean isLeadingPath(File leading, File path, boolean resolveSymlinks) throws IOException {
        if (!resolveSymlinks) {
            return this.isLeadingPath(leading, path);
        }
        File l = leading.getCanonicalFile();
        File p = path.getCanonicalFile();
        do {
            if (!l.equals(p)) continue;
            return true;
        } while ((p = p.getParentFile()) != null);
        return false;
    }

    public String toURI(String path) {
        return new File(path).toURI().toASCIIString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String fromURI(String uri) {
        Object object = this.cacheFromUriLock;
        synchronized (object) {
            if (uri.equals(this.cacheFromUriRequest)) {
                return this.cacheFromUriResponse;
            }
            String path = Locator.fromURI((String)uri);
            String ret = FileUtils.isAbsolutePath(path) ? this.normalize(path).getAbsolutePath() : path;
            this.cacheFromUriRequest = uri;
            this.cacheFromUriResponse = ret;
            return ret;
        }
    }

    public boolean fileNameEquals(File f1, File f2) {
        return this.normalize(f1.getAbsolutePath()).getAbsolutePath().equals(this.normalize(f2.getAbsolutePath()).getAbsolutePath());
    }

    public boolean areSame(File f1, File f2) throws IOException {
        if (f1 == null && f2 == null) {
            return true;
        }
        if (f1 == null || f2 == null) {
            return false;
        }
        return this.fileNameEquals(f1, f2) || this.isSameFile(f1, f2);
    }

    private boolean isSameFile(File f1, File f2) throws IOException {
        if (f1.exists()) {
            try {
                return f2.exists() && Files.isSameFile(f1.toPath(), f2.toPath());
            }
            catch (NoSuchFileException e) {
                return false;
            }
        }
        File f1Normalized = this.normalize(f1.getAbsolutePath());
        File f2Normalized = this.normalize(f2.getAbsolutePath());
        return f1Normalized.getCanonicalFile().equals(f2Normalized.getCanonicalFile());
    }

    public void rename(File from, File to) throws IOException {
        this.rename(from, to, false);
    }

    public void rename(File from, File to, boolean keepTargetFilePermissions) throws IOException {
        Set<PosixFilePermission> existingFilePermissions = null;
        from = this.normalize(from.getAbsolutePath()).getCanonicalFile();
        to = this.normalize(to.getAbsolutePath());
        if (!from.exists()) {
            System.err.println("Cannot rename nonexistent file " + from);
            return;
        }
        if (from.getAbsolutePath().equals(to.getAbsolutePath())) {
            System.err.println("Rename of " + from + " to " + to + " is a no-op.");
            return;
        }
        if (keepTargetFilePermissions) {
            try {
                existingFilePermissions = Files.getPosixFilePermissions(to.toPath(), new LinkOption[0]);
            }
            catch (IOException | UnsupportedOperationException exception) {
                // empty catch block
            }
        }
        if (to.exists() && !this.areSame(from, to) && !this.tryHardToDelete(to)) {
            throw new IOException("Failed to delete " + to + " while trying to rename " + from);
        }
        File parent = to.getParentFile();
        if (!(parent == null || parent.isDirectory() || parent.mkdirs() || parent.isDirectory())) {
            throw new IOException("Failed to create directory " + parent + " while trying to rename " + from);
        }
        if (!from.renameTo(to)) {
            this.copyFile(from, to);
            if (!this.tryHardToDelete(from)) {
                throw new IOException("Failed to delete " + from + " while trying to rename it.");
            }
        }
        if (existingFilePermissions != null) {
            Files.setPosixFilePermissions(to.toPath(), existingFilePermissions);
        }
    }

    public long getFileTimestampGranularity() {
        if (ON_WIN9X) {
            return 2000L;
        }
        if (ON_WINDOWS) {
            return 1L;
        }
        if (ON_DOS) {
            return 2000L;
        }
        return 1000L;
    }

    public boolean hasErrorInCase(File localFile) {
        if (!(localFile = this.normalize(localFile.getAbsolutePath())).exists()) {
            return false;
        }
        String localFileName = localFile.getName();
        FilenameFilter ff = (dir, name) -> name.equalsIgnoreCase(localFileName) && !name.equals(localFileName);
        String[] names = localFile.getParentFile().list(ff);
        return names != null && names.length == 1;
    }

    public boolean isUpToDate(File source, File dest, long granularity) {
        if (!dest.exists()) {
            return false;
        }
        long sourceTime = source.lastModified();
        long destTime = dest.lastModified();
        return this.isUpToDate(sourceTime, destTime, granularity);
    }

    public boolean isUpToDate(File source, File dest) {
        return this.isUpToDate(source, dest, this.getFileTimestampGranularity());
    }

    public boolean isUpToDate(long sourceTime, long destTime, long granularity) {
        return destTime != -1L && destTime >= sourceTime + granularity;
    }

    public boolean isUpToDate(long sourceTime, long destTime) {
        return this.isUpToDate(sourceTime, destTime, this.getFileTimestampGranularity());
    }

    public static void close(Writer device) {
        FileUtils.close((AutoCloseable)device);
    }

    public static void close(Reader device) {
        FileUtils.close((AutoCloseable)device);
    }

    public static void close(OutputStream device) {
        FileUtils.close((AutoCloseable)device);
    }

    public static void close(InputStream device) {
        FileUtils.close((AutoCloseable)device);
    }

    public static void close(Channel device) {
        FileUtils.close((AutoCloseable)device);
    }

    public static void close(URLConnection conn) {
        if (conn != null) {
            try {
                if (conn instanceof JarURLConnection) {
                    JarURLConnection juc = (JarURLConnection)conn;
                    JarFile jf = juc.getJarFile();
                    jf.close();
                } else if (conn instanceof HttpURLConnection) {
                    ((HttpURLConnection)conn).disconnect();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static void close(AutoCloseable ac) {
        if (null != ac) {
            try {
                ac.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static void delete(File file) {
        if (file != null) {
            file.delete();
        }
    }

    public boolean tryHardToDelete(File f) {
        return this.tryHardToDelete(f, ON_WINDOWS);
    }

    public boolean tryHardToDelete(File f, boolean runGC) {
        if (!f.delete()) {
            if (runGC) {
                System.gc();
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return f.delete();
        }
        return true;
    }

    public static String getRelativePath(File fromFile, File toFile) throws Exception {
        int same;
        String fromPath = fromFile.getCanonicalPath();
        String toPath = toFile.getCanonicalPath();
        String[] fromPathStack = FileUtils.getPathStack(fromPath);
        String[] toPathStack = FileUtils.getPathStack(toPath);
        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                return FileUtils.getPath(Arrays.asList(toPathStack));
            }
        } else {
            return FileUtils.getPath(Arrays.asList(toPathStack));
        }
        int minLength = Math.min(fromPathStack.length, toPathStack.length);
        for (same = 1; same < minLength && fromPathStack[same].equals(toPathStack[same]); ++same) {
        }
        ArrayList<String> relativePathStack = new ArrayList<String>();
        for (int i = same; i < fromPathStack.length; ++i) {
            relativePathStack.add("..");
        }
        relativePathStack.addAll(Arrays.asList(toPathStack).subList(same, toPathStack.length));
        return FileUtils.getPath(relativePathStack);
    }

    public static String[] getPathStack(String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');
        return normalizedPath.split("/");
    }

    public static String getPath(List<String> pathStack) {
        return FileUtils.getPath(pathStack, '/');
    }

    public static String getPath(List<String> pathStack, char separatorChar) {
        return pathStack.stream().collect(Collectors.joining(Character.toString(separatorChar)));
    }

    public String getDefaultEncoding() {
        InputStreamReader is = new InputStreamReader(new InputStream(){

            @Override
            public int read() {
                return -1;
            }
        });
        try {
            String string = is.getEncoding();
            return string;
        }
        finally {
            FileUtils.close(is);
        }
    }

    public static OutputStream newOutputStream(Path path, boolean append) throws IOException {
        if (append) {
            return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        }
        return Files.newOutputStream(path, new OpenOption[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Optional<Boolean> isCaseSensitiveFileSystem(Path path) {
        boolean caseSensitive;
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        String mixedCaseFileNamePrefix = "aNt";
        Path mixedCaseTmpFile = null;
        try {
            if (Files.isRegularFile(path, new LinkOption[0])) {
                mixedCaseTmpFile = Files.createTempFile(path.getParent(), "aNt", null, new FileAttribute[0]);
            } else if (Files.isDirectory(path, new LinkOption[0])) {
                mixedCaseTmpFile = Files.createTempFile(path, "aNt", null, new FileAttribute[0]);
            } else {
                Optional<Boolean> optional = Optional.empty();
                return optional;
            }
            Path lowerCasePath = Paths.get(mixedCaseTmpFile.toString().toLowerCase(Locale.US), new String[0]);
            try {
                caseSensitive = !Files.isSameFile(mixedCaseTmpFile, lowerCasePath);
            }
            catch (NoSuchFileException nsfe) {
                caseSensitive = true;
            }
        }
        catch (IOException ioe) {
            System.err.println("Could not determine the case sensitivity of the filesystem for path " + path + " due to " + ioe);
            Optional<Boolean> optional = Optional.empty();
            return optional;
        }
        finally {
            if (mixedCaseTmpFile != null) {
                FileUtils.delete(mixedCaseTmpFile.toFile());
            }
        }
        return Optional.of(caseSensitive);
    }
}

