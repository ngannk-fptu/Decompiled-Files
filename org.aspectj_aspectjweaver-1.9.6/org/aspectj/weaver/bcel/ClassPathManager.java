/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.util.SoftHashMap;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class ClassPathManager {
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(ClassPathManager.class);
    private static int maxOpenArchives = -1;
    private static URI JRT_URI = URI.create("jrt:/");
    private static final int MAXOPEN_DEFAULT = 1000;
    private List<Entry> entries;
    private List<ZipFile> openArchives = new ArrayList<ZipFile>();

    public ClassPathManager(List<String> classpath, IMessageHandler handler) {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", (Object)this, new Object[]{classpath == null ? "null" : classpath.toString(), handler});
        }
        this.entries = new ArrayList<Entry>();
        for (String classpathEntry : classpath) {
            this.addPath(classpathEntry, handler);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("<init>");
        }
    }

    protected ClassPathManager() {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addPath(String name, IMessageHandler handler) {
        File f = new File(name);
        if (!f.isDirectory()) {
            if (!f.isFile()) {
                if (!name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip")) {
                    MessageUtil.info(handler, WeaverMessages.format("zipfileEntryMissing", name));
                    return;
                } else {
                    MessageUtil.info(handler, WeaverMessages.format("directoryEntryMissing", name));
                }
                return;
            }
            try {
                if (name.toLowerCase().endsWith("jrt-fs.jar")) {
                    if (!LangUtil.is18VMOrGreater()) return;
                    this.entries.add(new JImageEntry(name));
                    return;
                }
                this.entries.add(new ZipFileEntry(f));
                return;
            }
            catch (IOException ioe) {
                MessageUtil.warn(handler, WeaverMessages.format("zipfileEntryInvalid", name, ioe.getMessage()));
                return;
            }
        } else {
            this.entries.add(new DirEntry(f));
        }
    }

    public ClassFile find(UnresolvedType type) {
        if (trace.isTraceEnabled()) {
            trace.enter("find", (Object)this, type);
        }
        String name = type.getName();
        Iterator<Entry> i = this.entries.iterator();
        while (i.hasNext()) {
            Entry entry = i.next();
            try {
                ClassFile ret = entry.find(name);
                if (trace.isTraceEnabled()) {
                    trace.event("searching for " + type + " in " + entry.toString());
                }
                if (ret == null) continue;
                if (trace.isTraceEnabled()) {
                    trace.exit("find", ret);
                }
                return ret;
            }
            catch (IOException ioe) {
                if (trace.isTraceEnabled()) {
                    trace.error("Removing classpath entry for " + entry, ioe);
                }
                i.remove();
            }
        }
        if (trace.isTraceEnabled()) {
            trace.exit("find", null);
        }
        return null;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        boolean start = true;
        Iterator<Entry> i = this.entries.iterator();
        while (i.hasNext()) {
            if (start) {
                start = false;
            } else {
                buf.append(File.pathSeparator);
            }
            buf.append(i.next());
        }
        return buf.toString();
    }

    static boolean hasClassExtension(String name) {
        return name.toLowerCase().endsWith(".class");
    }

    public void closeArchives() {
        for (Entry entry : this.entries) {
            if (entry instanceof ZipFileEntry) {
                ((ZipFileEntry)entry).close();
            }
            this.openArchives.clear();
        }
    }

    private static String getSystemPropertyWithoutSecurityException(String aPropertyName, String aDefaultValue) {
        try {
            return System.getProperty(aPropertyName, aDefaultValue);
        }
        catch (SecurityException ex) {
            return aDefaultValue;
        }
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    static {
        String openzipsString = ClassPathManager.getSystemPropertyWithoutSecurityException("org.aspectj.weaver.openarchives", Integer.toString(1000));
        maxOpenArchives = Integer.parseInt(openzipsString);
        if (maxOpenArchives < 20) {
            maxOpenArchives = 1000;
        }
    }

    class ZipFileEntry
    extends Entry {
        private File file;
        private ZipFile zipFile;

        public ZipFileEntry(File file) throws IOException {
            this.file = file;
        }

        public ZipFileEntry(ZipFile zipFile) {
            this.zipFile = zipFile;
        }

        public ZipFile getZipFile() {
            return this.zipFile;
        }

        @Override
        public ClassFile find(String name) throws IOException {
            this.ensureOpen();
            String key = name.replace('.', '/') + ".class";
            ZipEntry entry = this.zipFile.getEntry(key);
            if (entry != null) {
                return new ZipEntryClassFile(this, entry);
            }
            return null;
        }

        public List<ZipEntryClassFile> getAllClassFiles() throws IOException {
            this.ensureOpen();
            ArrayList<ZipEntryClassFile> ret = new ArrayList<ZipEntryClassFile>();
            Enumeration<? extends ZipEntry> e = this.zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                String name = entry.getName();
                if (!ClassPathManager.hasClassExtension(name)) continue;
                ret.add(new ZipEntryClassFile(this, entry));
            }
            return ret;
        }

        private void ensureOpen() throws IOException {
            if (this.zipFile != null && ClassPathManager.this.openArchives.contains(this.zipFile) && this.isReallyOpen()) {
                return;
            }
            if (ClassPathManager.this.openArchives.size() >= maxOpenArchives) {
                this.closeSomeArchives(ClassPathManager.this.openArchives.size() / 10);
            }
            this.zipFile = new ZipFile(this.file);
            if (!this.isReallyOpen()) {
                throw new FileNotFoundException("Can't open archive: " + this.file.getName() + " (size() check failed)");
            }
            ClassPathManager.this.openArchives.add(this.zipFile);
        }

        private boolean isReallyOpen() {
            try {
                this.zipFile.size();
                return true;
            }
            catch (IllegalStateException ex) {
                return false;
            }
        }

        public void closeSomeArchives(int n) {
            for (int i = n - 1; i >= 0; --i) {
                ZipFile zf = (ZipFile)ClassPathManager.this.openArchives.get(i);
                try {
                    zf.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                ClassPathManager.this.openArchives.remove(i);
            }
        }

        public void close() {
            if (this.zipFile == null) {
                return;
            }
            try {
                ClassPathManager.this.openArchives.remove(this.zipFile);
                this.zipFile.close();
            }
            catch (IOException ioe) {
                throw new BCException("Can't close archive: " + this.file.getName(), ioe);
            }
            finally {
                this.zipFile = null;
            }
        }

        public String toString() {
            return this.file.getName();
        }
    }

    static class JImageEntry
    extends Entry {
        private static Map<String, JImageState> states = new HashMap<String, JImageState>();
        private JImageState state;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public JImageEntry(String jrtFsPath) {
            this.state = states.get(jrtFsPath);
            if (this.state == null) {
                Map<String, JImageState> map = states;
                synchronized (map) {
                    if (this.state == null) {
                        URL jrtPath = null;
                        try {
                            jrtPath = new File(jrtFsPath).toPath().toUri().toURL();
                        }
                        catch (MalformedURLException e) {
                            System.out.println("Unexpected problem processing " + jrtFsPath + " bad classpath entry? skipping:" + e.getMessage());
                            return;
                        }
                        String jdkHome = new File(jrtFsPath).getParentFile().getParent();
                        FileSystem fs = null;
                        try {
                            if (LangUtil.is19VMOrGreater()) {
                                HashMap<String, String> env = new HashMap<String, String>();
                                env.put("java.home", jdkHome);
                                fs = FileSystems.newFileSystem(JRT_URI, env);
                            } else {
                                URLClassLoader loader = new URLClassLoader(new URL[]{jrtPath});
                                HashMap env = new HashMap();
                                fs = FileSystems.newFileSystem(JRT_URI, env, (ClassLoader)loader);
                            }
                            this.state = new JImageState(jrtFsPath, fs);
                            states.put(jrtFsPath, this.state);
                            this.buildPackageMap();
                        }
                        catch (Throwable t) {
                            throw new IllegalStateException("Unexpectedly unable to initialize a JRT filesystem", t);
                        }
                    }
                }
            }
        }

        private synchronized void buildPackageMap() {
            if (!this.state.packageCacheInitialized) {
                this.state.packageCacheInitialized = true;
                Iterable<Path> roots = this.state.fs.getRootDirectories();
                PackageCacheBuilderVisitor visitor = new PackageCacheBuilderVisitor();
                try {
                    for (Path path : roots) {
                        Files.walkFileTree(path, visitor);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private Path searchForFileAndCache(Path startPath, String name) {
            TypeIdentifier locator = new TypeIdentifier(name);
            try {
                Files.walkFileTree(startPath, locator);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return locator.found;
        }

        @Override
        public ClassFile find(String name) throws IOException {
            String fileName = name.replace('.', '/') + ".class";
            Path file = this.state.fileCache.get(fileName);
            if (file == null) {
                int idx = fileName.lastIndexOf(47);
                if (idx == -1) {
                    return null;
                }
                Path packageStart = null;
                String packageName = null;
                if (idx != -1 && (packageStart = this.state.packageCache.get(packageName = fileName.substring(0, idx))) != null) {
                    file = this.searchForFileAndCache(packageStart, fileName);
                }
            }
            if (file == null) {
                return null;
            }
            byte[] bs = Files.readAllBytes(file);
            ByteBasedClassFile cf = new ByteBasedClassFile(bs, fileName);
            return cf;
        }

        Map<String, Path> getPackageCache() {
            return this.state.packageCache;
        }

        Map<String, Path> getFileCache() {
            return this.state.fileCache;
        }

        class TypeIdentifier
        extends SimpleFileVisitor<Path> {
            private String name;
            public Path found;
            public int filesSearchedCount;

            public TypeIdentifier(String name) {
                this.name = name;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                int fnc;
                Path filePath;
                String filePathString;
                ++this.filesSearchedCount;
                if (file.getNameCount() > 2 && file.toString().endsWith(".class") && (filePathString = (filePath = file.subpath(2, fnc = file.getNameCount())).toString()).equals(this.name)) {
                    ((JImageEntry)JImageEntry.this).state.fileCache.put(filePathString, file);
                    this.found = file;
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        }

        class PackageCacheBuilderVisitor
        extends SimpleFileVisitor<Path> {
            PackageCacheBuilderVisitor() {
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                int fnc;
                if (file.getNameCount() > 3 && file.toString().endsWith(".class") && (fnc = file.getNameCount()) > 3) {
                    Path packagePath = file.subpath(2, fnc - 1);
                    String packagePathString = packagePath.toString();
                    ((JImageEntry)JImageEntry.this).state.packageCache.put(packagePathString, file.subpath(0, fnc - 1));
                }
                return FileVisitResult.CONTINUE;
            }
        }

        static class JImageState {
            private final String jrtFsPath;
            private final FileSystem fs;
            Map<String, Path> fileCache = new SoftHashMap<String, Path>();
            boolean packageCacheInitialized = false;
            Map<String, Path> packageCache = new HashMap<String, Path>();

            public JImageState(String jrtFsPath, FileSystem fs) {
                this.jrtFsPath = jrtFsPath;
                this.fs = fs;
            }
        }
    }

    static class ZipEntryClassFile
    extends ClassFile {
        private ZipEntry entry;
        private ZipFileEntry zipFile;
        private InputStream is;

        public ZipEntryClassFile(ZipFileEntry zipFile, ZipEntry entry) {
            this.zipFile = zipFile;
            this.entry = entry;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            this.is = this.zipFile.getZipFile().getInputStream(this.entry);
            return this.is;
        }

        @Override
        public void close() {
            try {
                if (this.is != null) {
                    this.is.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                this.is = null;
            }
        }

        @Override
        public String getPath() {
            return this.entry.getName();
        }
    }

    class DirEntry
    extends Entry {
        private String dirPath;

        public DirEntry(File dir) {
            this.dirPath = dir.getPath();
        }

        public DirEntry(String dirPath) {
            this.dirPath = dirPath;
        }

        @Override
        public ClassFile find(String name) {
            File f = new File(this.dirPath + File.separator + name.replace('.', File.separatorChar) + ".class");
            if (f.isFile()) {
                return new FileClassFile(f);
            }
            return null;
        }

        public String toString() {
            return this.dirPath;
        }
    }

    static class FileClassFile
    extends ClassFile {
        private File file;
        private FileInputStream fis;

        public FileClassFile(File file) {
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            this.fis = new FileInputStream(this.file);
            return this.fis;
        }

        @Override
        public void close() {
            try {
                if (this.fis != null) {
                    this.fis.close();
                }
            }
            catch (IOException ioe) {
                throw new BCException("Can't close class file : " + this.file.getName(), ioe);
            }
            finally {
                this.fis = null;
            }
        }

        @Override
        public String getPath() {
            return this.file.getPath();
        }
    }

    static class ByteBasedClassFile
    extends ClassFile {
        private byte[] bytes;
        private ByteArrayInputStream bais;
        private String path;

        public ByteBasedClassFile(byte[] bytes, String path) {
            this.bytes = bytes;
            this.path = path;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            this.bais = new ByteArrayInputStream(this.bytes);
            return this.bais;
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public void close() {
            if (this.bais != null) {
                try {
                    this.bais.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.bais = null;
            }
        }
    }

    static abstract class Entry {
        Entry() {
        }

        public abstract ClassFile find(String var1) throws IOException;
    }

    public static abstract class ClassFile {
        public abstract InputStream getInputStream() throws IOException;

        public abstract String getPath();

        public abstract void close();
    }
}

