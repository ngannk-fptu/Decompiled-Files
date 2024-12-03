/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ModularRuntimeImage;

public class ClassPath
implements Closeable {
    private static final FilenameFilter ARCHIVE_FILTER = (dir, name) -> (name = name.toLowerCase(Locale.ENGLISH)).endsWith(".zip") || name.endsWith(".jar");
    private static final FilenameFilter MODULES_FILTER = (dir, name) -> {
        name = name.toLowerCase(Locale.ENGLISH);
        return name.endsWith(".jmod");
    };
    public static final ClassPath SYSTEM_CLASS_PATH = new ClassPath(ClassPath.getClassPath());
    private final String classPathString;
    private final ClassPath parent;
    private final List<AbstractPathEntry> paths;

    private static void addJdkModules(String javaHome, List<String> list) {
        String[] modules;
        File modulesDir;
        String modulesPath = System.getProperty("java.modules.path");
        if (modulesPath == null || modulesPath.trim().isEmpty()) {
            modulesPath = javaHome + File.separator + "jmods";
        }
        if ((modulesDir = new File(modulesPath)).exists() && (modules = modulesDir.list(MODULES_FILTER)) != null) {
            for (String module : modules) {
                list.add(modulesDir.getPath() + File.separatorChar + module);
            }
        }
    }

    public static String getClassPath() {
        String classPathProp = System.getProperty("java.class.path");
        String bootClassPathProp = System.getProperty("sun.boot.class.path");
        String extDirs = System.getProperty("java.ext.dirs");
        String javaHome = System.getProperty("java.home");
        ArrayList<String> list = new ArrayList<String>();
        Path modulesPath = Paths.get(javaHome, new String[0]).resolve("lib/modules");
        if (Files.exists(modulesPath, new LinkOption[0]) && Files.isRegularFile(modulesPath, new LinkOption[0])) {
            list.add(modulesPath.toAbsolutePath().toString());
        }
        ClassPath.addJdkModules(javaHome, list);
        ClassPath.getPathComponents(classPathProp, list);
        ClassPath.getPathComponents(bootClassPathProp, list);
        ArrayList<String> dirs = new ArrayList<String>();
        ClassPath.getPathComponents(extDirs, dirs);
        for (String d : dirs) {
            File extDir = new File(d);
            String[] extensions = extDir.list(ARCHIVE_FILTER);
            if (extensions == null) continue;
            for (String extension : extensions) {
                list.add(extDir.getPath() + File.separatorChar + extension);
            }
        }
        return list.stream().collect(Collectors.joining(File.pathSeparator));
    }

    private static void getPathComponents(String path, List<String> list) {
        if (path != null) {
            StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                String name = tokenizer.nextToken();
                File file = new File(name);
                if (!file.exists()) continue;
                list.add(name);
            }
        }
    }

    @Deprecated
    public ClassPath() {
        this(ClassPath.getClassPath());
    }

    public ClassPath(ClassPath parent, String classPathString) {
        this.parent = parent;
        this.classPathString = Objects.requireNonNull(classPathString, "classPathString");
        this.paths = new ArrayList<AbstractPathEntry>();
        StringTokenizer tokenizer = new StringTokenizer(classPathString, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            if (path.isEmpty()) continue;
            File file = new File(path);
            try {
                if (!file.exists()) continue;
                if (file.isDirectory()) {
                    this.paths.add(new Dir(path));
                    continue;
                }
                if (path.endsWith(".jmod")) {
                    this.paths.add(new Module(new ZipFile(file)));
                    continue;
                }
                if (path.endsWith(ModularRuntimeImage.MODULES_PATH)) {
                    this.paths.add(new JrtModules(ModularRuntimeImage.MODULES_PATH));
                    continue;
                }
                this.paths.add(new Jar(new ZipFile(file)));
            }
            catch (IOException e) {
                if (!path.endsWith(".zip") && !path.endsWith(".jar")) continue;
                System.err.println("CLASSPATH component " + file + ": " + e);
            }
        }
    }

    public ClassPath(String classPath) {
        this(null, classPath);
    }

    @Override
    public void close() throws IOException {
        for (AbstractPathEntry path : this.paths) {
            path.close();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ClassPath other = (ClassPath)obj;
        return Objects.equals(this.classPathString, other.classPathString);
    }

    public byte[] getBytes(String name) throws IOException {
        return this.getBytes(name, ".class");
    }

    /*
     * Loose catch block
     */
    public byte[] getBytes(String name, String suffix) throws IOException {
        try (FilterInputStream dis = null;){
            try (InputStream inputStream = this.getInputStream(name, suffix);){
                if (inputStream == null) {
                    throw new IOException("Couldn't find: " + name + suffix);
                }
                dis = new DataInputStream(inputStream);
                byte[] bytes = new byte[inputStream.available()];
                ((DataInputStream)dis).readFully(bytes);
                byte[] byArray = bytes;
                return byArray;
            }
            {
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
    }

    public ClassFile getClassFile(String name) throws IOException {
        return this.getClassFile(name, ".class");
    }

    public ClassFile getClassFile(String name, String suffix) throws IOException {
        ClassFile cf = null;
        if (this.parent != null) {
            cf = this.parent.getClassFileInternal(name, suffix);
        }
        if (cf == null) {
            cf = this.getClassFileInternal(name, suffix);
        }
        if (cf != null) {
            return cf;
        }
        throw new IOException("Couldn't find: " + name + suffix);
    }

    private ClassFile getClassFileInternal(String name, String suffix) {
        for (AbstractPathEntry path : this.paths) {
            ClassFile cf = path.getClassFile(name, suffix);
            if (cf == null) continue;
            return cf;
        }
        return null;
    }

    public InputStream getInputStream(String name) throws IOException {
        return this.getInputStream(Utility.packageToPath(name), ".class");
    }

    public InputStream getInputStream(String name, String suffix) throws IOException {
        try {
            InputStream inputStream;
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream inputStream2 = inputStream = classLoader == null ? null : classLoader.getResourceAsStream(name + suffix);
            if (inputStream != null) {
                return inputStream;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.getClassFile(name, suffix).getInputStream();
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

    public URL getResource(String name) {
        for (AbstractPathEntry path : this.paths) {
            URL url = path.getResource(name);
            if (url == null) continue;
            return url;
        }
        return null;
    }

    public InputStream getResourceAsStream(String name) {
        for (AbstractPathEntry path : this.paths) {
            InputStream is = path.getResourceAsStream(name);
            if (is == null) continue;
            return is;
        }
        return null;
    }

    public Enumeration<URL> getResources(String name) {
        Vector<URL> results = new Vector<URL>();
        for (AbstractPathEntry path : this.paths) {
            URL url = path.getResource(name);
            if (url == null) continue;
            results.add(url);
        }
        return results.elements();
    }

    public int hashCode() {
        return this.classPathString.hashCode();
    }

    public String toString() {
        if (this.parent != null) {
            return this.parent + File.pathSeparator + this.classPathString;
        }
        return this.classPathString;
    }

    private static class Module
    extends AbstractZip {
        Module(ZipFile zip) {
            super(zip);
        }

        @Override
        protected String toEntryName(String name, String suffix) {
            return "classes/" + Utility.packageToPath(name) + suffix;
        }
    }

    private static class JrtModules
    extends AbstractPathEntry {
        private final ModularRuntimeImage modularRuntimeImage = new ModularRuntimeImage();
        private final JrtModule[] modules;

        public JrtModules(String path) throws IOException {
            this.modules = (JrtModule[])this.modularRuntimeImage.list(path).stream().map(JrtModule::new).toArray(JrtModule[]::new);
        }

        @Override
        public void close() throws IOException {
            if (this.modules != null) {
                for (JrtModule module : this.modules) {
                    module.close();
                }
            }
            if (this.modularRuntimeImage != null) {
                this.modularRuntimeImage.close();
            }
        }

        @Override
        ClassFile getClassFile(String name, String suffix) {
            for (JrtModule module : this.modules) {
                ClassFile classFile = module.getClassFile(name, suffix);
                if (classFile == null) continue;
                return classFile;
            }
            return null;
        }

        @Override
        URL getResource(String name) {
            for (JrtModule module : this.modules) {
                URL url = module.getResource(name);
                if (url == null) continue;
                return url;
            }
            return null;
        }

        @Override
        InputStream getResourceAsStream(String name) {
            for (JrtModule module : this.modules) {
                InputStream inputStream = module.getResourceAsStream(name);
                if (inputStream == null) continue;
                return inputStream;
            }
            return null;
        }

        public String toString() {
            return Arrays.toString(this.modules);
        }
    }

    private static class JrtModule
    extends AbstractPathEntry {
        private final Path modulePath;

        public JrtModule(Path modulePath) {
            this.modulePath = Objects.requireNonNull(modulePath, "modulePath");
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        ClassFile getClassFile(String name, String suffix) {
            final Path resolved = this.modulePath.resolve(Utility.packageToPath(name) + suffix);
            if (Files.exists(resolved, new LinkOption[0])) {
                return new ClassFile(){

                    @Override
                    public String getBase() {
                        return Objects.toString(resolved.getFileName(), null);
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return Files.newInputStream(resolved, new OpenOption[0]);
                    }

                    @Override
                    public String getPath() {
                        return resolved.toString();
                    }

                    @Override
                    public long getSize() {
                        try {
                            return Files.size(resolved);
                        }
                        catch (IOException e) {
                            return 0L;
                        }
                    }

                    @Override
                    public long getTime() {
                        try {
                            return Files.getLastModifiedTime(resolved, new LinkOption[0]).toMillis();
                        }
                        catch (IOException e) {
                            return 0L;
                        }
                    }
                };
            }
            return null;
        }

        @Override
        URL getResource(String name) {
            Path resovled = this.modulePath.resolve(name);
            try {
                return Files.exists(resovled, new LinkOption[0]) ? new URL("jrt:" + this.modulePath + "/" + name) : null;
            }
            catch (MalformedURLException e) {
                return null;
            }
        }

        @Override
        InputStream getResourceAsStream(String name) {
            try {
                return Files.newInputStream(this.modulePath.resolve(name), new OpenOption[0]);
            }
            catch (IOException e) {
                return null;
            }
        }

        public String toString() {
            return this.modulePath.toString();
        }
    }

    private static class Jar
    extends AbstractZip {
        Jar(ZipFile zip) {
            super(zip);
        }

        @Override
        protected String toEntryName(String name, String suffix) {
            return Utility.packageToPath(name) + suffix;
        }
    }

    private static class Dir
    extends AbstractPathEntry {
        private final String dir;

        Dir(String d) {
            this.dir = d;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        ClassFile getClassFile(String name, String suffix) {
            final File file = new File(this.dir + File.separatorChar + name.replace('.', File.separatorChar) + suffix);
            return file.exists() ? new ClassFile(){

                @Override
                public String getBase() {
                    return dir;
                }

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
                public long getSize() {
                    return file.length();
                }

                @Override
                public long getTime() {
                    return file.lastModified();
                }
            } : null;
        }

        @Override
        URL getResource(String name) {
            File file = this.toFile(name);
            try {
                return file.exists() ? file.toURI().toURL() : null;
            }
            catch (MalformedURLException e) {
                return null;
            }
        }

        @Override
        InputStream getResourceAsStream(String name) {
            File file = this.toFile(name);
            try {
                return file.exists() ? new FileInputStream(file) : null;
            }
            catch (IOException e) {
                return null;
            }
        }

        private File toFile(String name) {
            return new File(this.dir + File.separatorChar + name.replace('/', File.separatorChar));
        }

        public String toString() {
            return this.dir;
        }
    }

    public static interface ClassFile {
        public String getBase();

        public InputStream getInputStream() throws IOException;

        public String getPath();

        public long getSize();

        public long getTime();
    }

    private static abstract class AbstractZip
    extends AbstractPathEntry {
        private final ZipFile zipFile;

        AbstractZip(ZipFile zipFile) {
            this.zipFile = Objects.requireNonNull(zipFile, "zipFile");
        }

        @Override
        public void close() throws IOException {
            if (this.zipFile != null) {
                this.zipFile.close();
            }
        }

        @Override
        ClassFile getClassFile(String name, String suffix) {
            final ZipEntry entry = this.zipFile.getEntry(this.toEntryName(name, suffix));
            if (entry == null) {
                return null;
            }
            return new ClassFile(){

                @Override
                public String getBase() {
                    return zipFile.getName();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return zipFile.getInputStream(entry);
                }

                @Override
                public String getPath() {
                    return entry.toString();
                }

                @Override
                public long getSize() {
                    return entry.getSize();
                }

                @Override
                public long getTime() {
                    return entry.getTime();
                }
            };
        }

        @Override
        URL getResource(String name) {
            ZipEntry entry = this.zipFile.getEntry(name);
            try {
                return entry != null ? new URL("jar:file:" + this.zipFile.getName() + "!/" + name) : null;
            }
            catch (MalformedURLException e) {
                return null;
            }
        }

        @Override
        InputStream getResourceAsStream(String name) {
            ZipEntry entry = this.zipFile.getEntry(name);
            try {
                return entry != null ? this.zipFile.getInputStream(entry) : null;
            }
            catch (IOException e) {
                return null;
            }
        }

        protected abstract String toEntryName(String var1, String var2);

        public String toString() {
            return this.zipFile.getName();
        }
    }

    private static abstract class AbstractPathEntry
    implements Closeable {
        private AbstractPathEntry() {
        }

        abstract ClassFile getClassFile(String var1, String var2);

        abstract URL getResource(String var1);

        abstract InputStream getResourceAsStream(String var1);
    }
}

