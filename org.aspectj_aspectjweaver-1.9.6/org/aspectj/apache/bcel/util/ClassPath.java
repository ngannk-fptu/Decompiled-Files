/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassPath
implements Serializable {
    private static final String JRT_FS = "jrt-fs.jar";
    private static ClassPath SYSTEM_CLASS_PATH = null;
    private PathEntry[] paths;
    private String class_path;

    public static ClassPath getSystemClassPath() {
        if (SYSTEM_CLASS_PATH == null) {
            SYSTEM_CLASS_PATH = new ClassPath();
        }
        return SYSTEM_CLASS_PATH;
    }

    public ClassPath(String class_path) {
        this.class_path = class_path;
        ArrayList<PathEntry> vec = new ArrayList<PathEntry>();
        StringTokenizer tok = new StringTokenizer(class_path, System.getProperty("path.separator"));
        while (tok.hasMoreTokens()) {
            String path = tok.nextToken();
            if (path.equals("")) continue;
            File file = new File(path);
            try {
                if (!file.exists()) continue;
                if (file.isDirectory()) {
                    vec.add(new Dir(path));
                    continue;
                }
                if (file.getName().endsWith(JRT_FS)) {
                    vec.add(new JImage());
                    continue;
                }
                vec.add(new Zip(new ZipFile(file)));
            }
            catch (IOException e) {
                System.err.println("CLASSPATH component " + file + ": " + e);
            }
        }
        this.paths = new PathEntry[vec.size()];
        vec.toArray(this.paths);
    }

    @Deprecated
    public ClassPath() {
        this(ClassPath.getClassPath());
    }

    public String toString() {
        return this.class_path;
    }

    public int hashCode() {
        return this.class_path.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof ClassPath) {
            return this.class_path.equals(((ClassPath)o).class_path);
        }
        return false;
    }

    private static final void getPathComponents(String path, ArrayList<String> list) {
        if (path != null) {
            StringTokenizer tok = new StringTokenizer(path, File.pathSeparator);
            while (tok.hasMoreTokens()) {
                String name = tok.nextToken();
                File file = new File(name);
                if (!file.exists()) continue;
                list.add(name);
            }
        }
    }

    public static final String getClassPath() {
        String class_path = System.getProperty("java.class.path");
        String boot_path = System.getProperty("sun.boot.class.path");
        String ext_path = System.getProperty("java.ext.dirs");
        String vm_version = System.getProperty("java.version");
        ArrayList<String> list = new ArrayList<String>();
        ClassPath.getPathComponents(class_path, list);
        ClassPath.getPathComponents(boot_path, list);
        ArrayList<String> dirs = new ArrayList<String>();
        ClassPath.getPathComponents(ext_path, dirs);
        for (String string : dirs) {
            File ext_dir = new File(string);
            String[] extensions = ext_dir.list(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return (name = name.toLowerCase()).endsWith(".zip") || name.endsWith(".jar");
                }
            });
            if (extensions == null) continue;
            for (String extension : extensions) {
                list.add(ext_dir.toString() + File.separatorChar + extension);
            }
        }
        StringBuffer buf = new StringBuffer();
        Iterator<String> e = list.iterator();
        while (e.hasNext()) {
            buf.append(e.next());
            if (!e.hasNext()) continue;
            buf.append(File.pathSeparatorChar);
        }
        if (vm_version.startsWith("9") || vm_version.startsWith("10") || vm_version.startsWith("11") || vm_version.startsWith("12") || vm_version.startsWith("13") || vm_version.startsWith("14")) {
            buf.insert(0, File.pathSeparatorChar);
            buf.insert(0, System.getProperty("java.home") + File.separator + "lib" + File.separator + JRT_FS);
        }
        return buf.toString().intern();
    }

    public InputStream getInputStream(String name) throws IOException {
        return this.getInputStream(name, ".class");
    }

    public InputStream getInputStream(String name, String suffix) throws IOException {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(name + suffix);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (is != null) {
            return is;
        }
        return this.getClassFile(name, suffix).getInputStream();
    }

    public ClassFile getClassFile(String name, String suffix) throws IOException {
        for (PathEntry path : this.paths) {
            ClassFile cf = path.getClassFile(name, suffix);
            if (cf == null) continue;
            return cf;
        }
        throw new IOException("Couldn't find: " + name + suffix);
    }

    public ClassFile getClassFile(String name) throws IOException {
        return this.getClassFile(name, ".class");
    }

    public byte[] getBytes(String name, String suffix) throws IOException {
        InputStream is = this.getInputStream(name, suffix);
        if (is == null) {
            throw new IOException("Couldn't find: " + name + suffix);
        }
        DataInputStream dis = new DataInputStream(is);
        byte[] bytes = new byte[is.available()];
        dis.readFully(bytes);
        dis.close();
        is.close();
        return bytes;
    }

    public byte[] getBytes(String name) throws IOException {
        return this.getBytes(name, ".class");
    }

    public String getPath(String name) throws IOException {
        int index = name.lastIndexOf(46);
        String suffix = "";
        if (index > 0) {
            suffix = name.substring(index);
            name = name.substring(0, index);
        }
        return this.getPath(name, suffix);
    }

    public String getPath(String name, String suffix) throws IOException {
        return this.getClassFile(name, suffix).getPath();
    }

    private static class Zip
    extends PathEntry {
        private ZipFile zip;

        Zip(ZipFile z) {
            this.zip = z;
        }

        @Override
        ClassFile getClassFile(String name, String suffix) throws IOException {
            final ZipEntry entry = this.zip.getEntry(name.replace('.', '/') + suffix);
            return entry != null ? new ClassFile(){

                @Override
                public InputStream getInputStream() throws IOException {
                    return zip.getInputStream(entry);
                }

                @Override
                public String getPath() {
                    return entry.toString();
                }

                @Override
                public long getTime() {
                    return entry.getTime();
                }

                @Override
                public long getSize() {
                    return entry.getSize();
                }

                @Override
                public String getBase() {
                    return zip.getName();
                }
            } : null;
        }
    }

    private static class JImage
    extends PathEntry {
        private static URI JRT_URI = URI.create("jrt:/");
        private static String MODULES_PATH = "modules";
        private static String JAVA_BASE_PATH = "java.base";
        private FileSystem fs = FileSystems.getFileSystem(JRT_URI);
        private final Map<String, Path> fileMap = this.buildFileMap();

        JImage() {
        }

        private Map<String, Path> buildFileMap() {
            final HashMap<String, Path> fileMap = new HashMap<String, Path>();
            final PathMatcher matcher = this.fs.getPathMatcher("glob:*.class");
            Iterable<Path> roots = this.fs.getRootDirectories();
            for (Path path : roots) {
                try {
                    Files.walkFileTree(path, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (file.getNameCount() > 2 && matcher.matches(file.getFileName())) {
                                Path classPath = file.subpath(2, file.getNameCount());
                                fileMap.put(classPath.toString(), file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return fileMap;
        }

        @Override
        ClassFile getClassFile(String name, String suffix) throws IOException {
            String fileName = name.replace('.', '/') + suffix;
            Path p = this.fileMap.get(fileName);
            if (p == null) {
                return null;
            }
            byte[] bs = Files.readAllBytes(p);
            BasicFileAttributeView bfav = Files.getFileAttributeView(p, BasicFileAttributeView.class, new LinkOption[0]);
            BasicFileAttributes bfas = bfav.readAttributes();
            long time = bfas.lastModifiedTime().toMillis();
            long size = bfas.size();
            ByteBasedClassFile cf = new ByteBasedClassFile(bs, "jimage", fileName, time, size);
            return cf;
        }

        private static class ByteBasedClassFile
        implements ClassFile {
            private byte[] bytes;
            private ByteArrayInputStream bais;
            private String path;
            private String base;
            private long time;
            private long size;

            public ByteBasedClassFile(byte[] bytes, String path, String base, long time, long size) {
                this.bytes = bytes;
                this.path = path;
                this.base = base;
                this.time = time;
                this.size = size;
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
            public String getBase() {
                return this.base;
            }

            @Override
            public long getTime() {
                return this.time;
            }

            @Override
            public long getSize() {
                return this.size;
            }
        }
    }

    private static class Dir
    extends PathEntry {
        private String dir;

        Dir(String d) {
            this.dir = d;
        }

        @Override
        ClassFile getClassFile(String name, String suffix) throws IOException {
            final File file = new File(this.dir + File.separatorChar + name.replace('.', File.separatorChar) + suffix);
            return file.exists() ? new ClassFile(){

                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(file);
                }

                @Override
                public String getPath() {
                    try {
                        return file.getCanonicalPath();
                    }
                    catch (IOException e) {
                        return null;
                    }
                }

                @Override
                public long getTime() {
                    return file.lastModified();
                }

                @Override
                public long getSize() {
                    return file.length();
                }

                @Override
                public String getBase() {
                    return dir;
                }
            } : null;
        }

        public String toString() {
            return this.dir;
        }
    }

    public static interface ClassFile {
        public InputStream getInputStream() throws IOException;

        public String getPath();

        public String getBase();

        public long getTime();

        public long getSize();
    }

    private static abstract class PathEntry
    implements Serializable {
        private PathEntry() {
        }

        abstract ClassFile getClassFile(String var1, String var2) throws IOException;
    }
}

