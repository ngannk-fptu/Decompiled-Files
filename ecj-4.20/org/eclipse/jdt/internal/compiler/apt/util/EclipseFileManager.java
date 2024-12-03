/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipException;
import javax.lang.model.SourceVersion;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.apt.util.Archive;
import org.eclipse.jdt.internal.compiler.apt.util.ArchiveFileObject;
import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileObject;
import org.eclipse.jdt.internal.compiler.apt.util.JrtFileSystem;
import org.eclipse.jdt.internal.compiler.apt.util.ModuleLocationHandler;
import org.eclipse.jdt.internal.compiler.apt.util.Options;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.batch.ModuleFinder;
import org.eclipse.jdt.internal.compiler.env.AccessRule;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class EclipseFileManager
implements StandardJavaFileManager {
    private static final String NO_EXTENSION = "";
    static final int HAS_EXT_DIRS = 1;
    static final int HAS_BOOTCLASSPATH = 2;
    static final int HAS_ENDORSED_DIRS = 4;
    static final int HAS_PROCESSORPATH = 8;
    static final int HAS_PROC_MODULEPATH = 16;
    Map<File, Archive> archivesCache;
    Charset charset;
    Locale locale;
    ModuleLocationHandler locationHandler;
    final Map<JavaFileManager.Location, URLClassLoader> classloaders;
    int flags;
    boolean isOnJvm9;
    File jrtHome;
    JrtFileSystem jrtSystem;
    public ResourceBundle bundle;
    String releaseVersion;

    public EclipseFileManager(Locale locale, Charset charset) {
        this.locale = locale == null ? Locale.getDefault() : locale;
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.locationHandler = new ModuleLocationHandler();
        this.classloaders = new HashMap<JavaFileManager.Location, URLClassLoader>();
        this.archivesCache = new HashMap<File, Archive>();
        this.isOnJvm9 = this.isRunningJvm9();
        try {
            this.initialize(Util.getJavaHome());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
        }
        catch (MissingResourceException missingResourceException) {
            System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
        }
    }

    protected void initialize(File javahome) throws IOException {
        if (this.isOnJvm9) {
            this.jrtSystem = new JrtFileSystem(javahome);
            this.archivesCache.put(javahome, this.jrtSystem);
            this.jrtHome = javahome;
            this.locationHandler.newSystemLocation((JavaFileManager.Location)StandardLocation.SYSTEM_MODULES, this.jrtSystem);
        } else {
            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.getDefaultBootclasspath());
        }
        Iterable<? extends File> defaultClasspath = this.getDefaultClasspath();
        this.setLocation(StandardLocation.CLASS_PATH, defaultClasspath);
        this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, defaultClasspath);
    }

    @Override
    public void close() throws IOException {
        this.locationHandler.close();
        for (Archive archive : this.archivesCache.values()) {
            archive.close();
        }
        this.archivesCache.clear();
        for (URLClassLoader cl : this.classloaders.values()) {
            cl.close();
        }
        this.classloaders.clear();
    }

    private void collectAllMatchingFiles(JavaFileManager.Location location, File file, String normalizedPackageName, Set<JavaFileObject.Kind> kinds, boolean recurse, ArrayList<JavaFileObject> collector) {
        block16: {
            String path;
            block17: {
                String key;
                Archive archive;
                block18: {
                    block15: {
                        if (!file.equals(this.jrtHome)) break block15;
                        if (!(location instanceof ModuleLocationHandler.ModuleLocationWrapper)) break block16;
                        List<JrtFileSystem.JrtFileObject> list = this.jrtSystem.list((ModuleLocationHandler.ModuleLocationWrapper)location, normalizedPackageName, kinds, recurse, this.charset);
                        for (JrtFileSystem.JrtFileObject fo : list) {
                            JavaFileObject.Kind kind = this.getKind(this.getExtension(fo.entryName));
                            if (!kinds.contains((Object)kind)) continue;
                            collector.add(fo);
                        }
                        break block16;
                    }
                    if (!this.isArchive(file)) break block17;
                    archive = this.getArchive(file);
                    if (archive == Archive.UNKNOWN_ARCHIVE) {
                        return;
                    }
                    key = normalizedPackageName;
                    if (!normalizedPackageName.endsWith("/")) {
                        key = String.valueOf(key) + '/';
                    }
                    if (!recurse) break block18;
                    for (String packageName : archive.allPackages()) {
                        List<String[]> types;
                        if (!packageName.startsWith(key) || (types = archive.getTypes(packageName)) == null) continue;
                        for (String[] entry : types) {
                            JavaFileObject.Kind kind = this.getKind(this.getExtension(entry[0]));
                            if (!kinds.contains((Object)kind)) continue;
                            collector.add(archive.getArchiveFileObject(String.valueOf(packageName) + entry[0], entry[1], this.charset));
                        }
                    }
                    break block16;
                }
                List<String[]> types = archive.getTypes(key);
                if (types == null) break block16;
                for (String[] entry : types) {
                    JavaFileObject.Kind kind = this.getKind(this.getExtension(entry[0]));
                    if (!kinds.contains((Object)kind)) continue;
                    collector.add(archive.getArchiveFileObject(String.valueOf(key) + entry[0], entry[1], this.charset));
                }
                break block16;
            }
            File currentFile = new File(file, normalizedPackageName);
            if (!currentFile.exists()) {
                return;
            }
            try {
                path = currentFile.getCanonicalPath();
            }
            catch (IOException iOException) {
                return;
            }
            if (File.separatorChar == '/' ? !path.endsWith(normalizedPackageName) : !path.endsWith(normalizedPackageName.replace('/', File.separatorChar))) {
                return;
            }
            File[] files = currentFile.listFiles();
            if (files != null) {
                File[] fileArray = files;
                int n = files.length;
                int n2 = 0;
                while (n2 < n) {
                    File f = fileArray[n2];
                    if (f.isDirectory() && recurse) {
                        this.collectAllMatchingFiles(location, file, String.valueOf(normalizedPackageName) + '/' + f.getName(), kinds, recurse, collector);
                    } else {
                        JavaFileObject.Kind kind = this.getKind(f);
                        if (kinds.contains((Object)kind)) {
                            collector.add(new EclipseFileObject(String.valueOf(normalizedPackageName) + f.getName(), f.toURI(), kind, this.charset));
                        }
                    }
                    ++n2;
                }
            }
        }
    }

    private Iterable<? extends File> concatFiles(Iterable<? extends File> iterable, Iterable<? extends File> iterable2) {
        ArrayList<File> list = new ArrayList<File>();
        if (iterable2 == null) {
            return iterable;
        }
        Iterator<? extends File> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        iterator = iterable2.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    public void flush() throws IOException {
        for (Archive archive : this.archivesCache.values()) {
            archive.flush();
        }
    }

    private Archive getArchive(File f) {
        Archive archive = this.archivesCache.get(f);
        if (archive == null) {
            archive = Archive.UNKNOWN_ARCHIVE;
            if (f.exists()) {
                try {
                    archive = new Archive(f);
                }
                catch (ZipException zipException) {
                }
                catch (IOException iOException) {}
                if (archive != null) {
                    this.archivesCache.put(f, archive);
                }
            }
            this.archivesCache.put(f, archive);
        }
        return archive;
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        this.validateNonModuleLocation(location);
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            return null;
        }
        URLClassLoader cl = this.classloaders.get(location);
        if (cl == null) {
            ArrayList<URL> allURLs = new ArrayList<URL>();
            for (File file : files) {
                try {
                    allURLs.add(file.toURI().toURL());
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            URL[] uRLArray = new URL[allURLs.size()];
            cl = new URLClassLoader(allURLs.toArray(uRLArray), this.getClass().getClassLoader());
            this.classloaders.put(location, cl);
        }
        return cl;
    }

    private Iterable<? extends File> getPathsFrom(String path) {
        ArrayList paths = new ArrayList();
        ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        for (FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }

    Iterable<? extends File> getDefaultBootclasspath() {
        long jdkLevel;
        ArrayList<File> files = new ArrayList<File>();
        String javaversion = System.getProperty("java.version");
        if (javaversion.length() > 3) {
            javaversion = javaversion.substring(0, 3);
        }
        if ((jdkLevel = CompilerOptions.versionToJdkLevel(javaversion)) < 0x320000L) {
            return null;
        }
        for (FileSystem.Classpath classpath : Util.collectFilesNames()) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }

    Iterable<? extends File> getDefaultClasspath() {
        ArrayList<File> files = new ArrayList<File>();
        String classProp = System.getProperty("java.class.path");
        if (classProp == null || classProp.length() == 0) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            File file = new File(token);
            if (!file.exists()) continue;
            files.add(file);
        }
        return files;
    }

    private Iterable<? extends File> getEndorsedDirsFrom(String path) {
        ArrayList paths = new ArrayList();
        ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        for (FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }

    private Iterable<? extends File> getExtdirsFrom(String path) {
        ArrayList paths = new ArrayList();
        ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        for (FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }

    private String getExtension(File file) {
        String name = file.getName();
        return this.getExtension(name);
    }

    private String getExtension(String name) {
        int index = name.lastIndexOf(46);
        if (index == -1) {
            return NO_EXTENSION;
        }
        return name.substring(index);
    }

    @Override
    public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName) throws IOException {
        this.validateNonModuleLocation(location);
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        String normalizedFileName = this.normalizedFileName(packageName, relativeName);
        for (File file : files) {
            Archive archive;
            if (file.isDirectory()) {
                File f = new File(file, normalizedFileName);
                if (!f.exists()) continue;
                return new EclipseFileObject(String.valueOf(packageName) + File.separator + relativeName, f.toURI(), this.getKind(f), this.charset);
            }
            if (!this.isArchive(file) || (archive = this.getArchive(file)) == Archive.UNKNOWN_ARCHIVE || !archive.contains(normalizedFileName)) continue;
            return archive.getArchiveFileObject(normalizedFileName, null, this.charset);
        }
        return null;
    }

    private String normalizedFileName(String packageName, String relativeName) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.normalized(packageName));
        if (sb.length() > 0) {
            sb.append('/');
        }
        sb.append(relativeName.replace('\\', '/'));
        return sb.toString();
    }

    @Override
    public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        this.validateOutputLocation(location);
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        Iterator<? extends File> iterator = files.iterator();
        if (iterator.hasNext()) {
            File file = iterator.next();
            String normalizedFileName = String.valueOf(this.normalized(packageName)) + '/' + relativeName.replace('\\', '/');
            File f = new File(file, normalizedFileName);
            return new EclipseFileObject(String.valueOf(packageName) + File.separator + relativeName, f.toURI(), this.getKind(f), this.charset);
        }
        throw new IllegalArgumentException("location is empty : " + location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {
        this.validateNonModuleLocation(location);
        if (kind != JavaFileObject.Kind.CLASS && kind != JavaFileObject.Kind.SOURCE) {
            throw new IllegalArgumentException("Invalid kind : " + (Object)((Object)kind));
        }
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        String normalizedFileName = this.normalized(className);
        normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
        for (File file : files) {
            Archive archive;
            if (file.isDirectory()) {
                File f = new File(file, normalizedFileName);
                if (!f.exists()) continue;
                return new EclipseFileObject(className, f.toURI(), kind, this.charset);
            }
            if (!this.isArchive(file) || (archive = this.getArchive(file)) == Archive.UNKNOWN_ARCHIVE || !archive.contains(normalizedFileName)) continue;
            return archive.getArchiveFileObject(normalizedFileName, null, this.charset);
        }
        return null;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        this.validateOutputLocation(location);
        if (kind != JavaFileObject.Kind.CLASS && kind != JavaFileObject.Kind.SOURCE) {
            throw new IllegalArgumentException("Invalid kind : " + (Object)((Object)kind));
        }
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            if (!location.equals(StandardLocation.CLASS_OUTPUT) && !location.equals(StandardLocation.SOURCE_OUTPUT)) {
                throw new IllegalArgumentException("Unknown location : " + location);
            }
            if (sibling != null) {
                String normalizedFileName = this.normalized(className);
                int index = normalizedFileName.lastIndexOf(47);
                if (index != -1) {
                    normalizedFileName = normalizedFileName.substring(index + 1);
                }
                normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
                URI uri = sibling.toUri();
                URI uri2 = null;
                try {
                    String path = uri.getPath();
                    index = path.lastIndexOf(47);
                    if (index != -1) {
                        path = path.substring(0, index + 1);
                        path = String.valueOf(path) + normalizedFileName;
                    }
                    uri2 = new URI(uri.getScheme(), uri.getHost(), path, uri.getFragment());
                }
                catch (URISyntaxException e) {
                    throw new IllegalArgumentException("invalid sibling", e);
                }
                return new EclipseFileObject(className, uri2, kind, this.charset);
            }
            String normalizedFileName = this.normalized(className);
            normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
            File f = new File(System.getProperty("user.dir"), normalizedFileName);
            return new EclipseFileObject(className, f.toURI(), kind, this.charset);
        }
        Iterator<? extends File> iterator = files.iterator();
        if (iterator.hasNext()) {
            File file = iterator.next();
            String normalizedFileName = this.normalized(className);
            normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
            File f = new File(file, normalizedFileName);
            return new EclipseFileObject(className, f.toURI(), kind, this.charset);
        }
        throw new IllegalArgumentException("location is empty : " + location);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(File ... files) {
        return this.getJavaFileObjectsFromFiles(Arrays.asList(files));
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(String ... names) {
        return this.getJavaFileObjectsFromStrings(Arrays.asList(names));
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files) {
        ArrayList<EclipseFileObject> javaFileArrayList = new ArrayList<EclipseFileObject>();
        for (File file : files) {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("file : " + file.getAbsolutePath() + " is a directory");
            }
            javaFileArrayList.add(new EclipseFileObject(file.getAbsolutePath(), file.toURI(), this.getKind(file), this.charset));
        }
        return javaFileArrayList;
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names) {
        ArrayList<File> files = new ArrayList<File>();
        for (String name : names) {
            files.add(new File(name));
        }
        return this.getJavaFileObjectsFromFiles(files);
    }

    public JavaFileObject.Kind getKind(File f) {
        return this.getKind(this.getExtension(f));
    }

    private JavaFileObject.Kind getKind(String extension) {
        if (JavaFileObject.Kind.CLASS.extension.equals(extension)) {
            return JavaFileObject.Kind.CLASS;
        }
        if (JavaFileObject.Kind.SOURCE.extension.equals(extension)) {
            return JavaFileObject.Kind.SOURCE;
        }
        if (JavaFileObject.Kind.HTML.extension.equals(extension)) {
            return JavaFileObject.Kind.HTML;
        }
        return JavaFileObject.Kind.OTHER;
    }

    @Override
    public Iterable<? extends File> getLocation(JavaFileManager.Location location) {
        if (location instanceof ModuleLocationHandler.LocationWrapper) {
            return this.getFiles(((ModuleLocationHandler.LocationWrapper)location).paths);
        }
        ModuleLocationHandler.LocationWrapper loc = this.locationHandler.getLocation(location, NO_EXTENSION);
        if (loc == null) {
            return null;
        }
        return this.getFiles(loc.getPaths());
    }

    private Iterable<? extends File> getOutputDir(String string) {
        if ("none".equals(string)) {
            return null;
        }
        File file = new File(string);
        if (file.exists() && !file.isDirectory()) {
            throw new IllegalArgumentException("file : " + file.getAbsolutePath() + " is not a directory");
        }
        ArrayList<File> list = new ArrayList<File>(1);
        list.add(file);
        return list;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        try {
            switch (current) {
                case "-bootclasspath": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> bootclasspaths = this.getPathsFrom(remaining.next());
                    if (bootclasspaths != null) {
                        Iterable<? extends File> iterable = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                        if ((this.flags & 4) == 0 && (this.flags & 1) == 0) {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, bootclasspaths);
                        } else if ((this.flags & 4) != 0) {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.concatFiles(iterable, bootclasspaths));
                        } else {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.prependFiles(iterable, bootclasspaths));
                        }
                    }
                    this.flags |= 2;
                    return true;
                }
                case "--system": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> classpaths = this.getPathsFrom(remaining.next());
                    if (classpaths == null) return true;
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.SYSTEM_MODULES);
                    if (iterable != null) {
                        this.setLocation(StandardLocation.SYSTEM_MODULES, this.concatFiles(iterable, classpaths));
                        return true;
                    }
                    this.setLocation(StandardLocation.SYSTEM_MODULES, classpaths);
                    return true;
                }
                case "--upgrade-module-path": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> classpaths = this.getPathsFrom(remaining.next());
                    if (classpaths == null) return true;
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.UPGRADE_MODULE_PATH);
                    if (iterable != null) {
                        this.setLocation(StandardLocation.UPGRADE_MODULE_PATH, this.concatFiles(iterable, classpaths));
                        return true;
                    }
                    this.setLocation(StandardLocation.UPGRADE_MODULE_PATH, classpaths);
                    return true;
                }
                case "-classpath": 
                case "-cp": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> classpaths = this.getPathsFrom(remaining.next());
                    if (classpaths == null) return true;
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.CLASS_PATH);
                    if (iterable != null) {
                        this.setLocation(StandardLocation.CLASS_PATH, this.concatFiles(iterable, classpaths));
                    } else {
                        this.setLocation(StandardLocation.CLASS_PATH, classpaths);
                    }
                    if ((this.flags & 8) == 0) {
                        this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, classpaths);
                        return true;
                    }
                    if ((this.flags & 0x10) != 0) return true;
                    if (!this.isOnJvm9) return true;
                    this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_MODULE_PATH, classpaths);
                    return true;
                }
                case "--module-path": 
                case "-p": {
                    Iterable<? extends File> classpaths = this.getPathsFrom(remaining.next());
                    if (classpaths == null) return true;
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.MODULE_PATH);
                    if (iterable != null) {
                        this.setLocation(StandardLocation.MODULE_PATH, this.concatFiles(iterable, classpaths));
                    } else {
                        this.setLocation(StandardLocation.MODULE_PATH, classpaths);
                    }
                    if ((this.flags & 8) == 0) {
                        this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, classpaths);
                        return true;
                    }
                    if ((this.flags & 0x10) != 0) return true;
                    if (!this.isOnJvm9) return true;
                    this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_MODULE_PATH, classpaths);
                    return true;
                }
                case "-encoding": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    this.charset = Charset.forName(remaining.next());
                    return true;
                }
                case "-sourcepath": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> sourcepaths = this.getPathsFrom(remaining.next());
                    if (sourcepaths == null) return true;
                    this.setLocation(StandardLocation.SOURCE_PATH, sourcepaths);
                    return true;
                }
                case "--module-source-path": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> sourcepaths = this.getPathsFrom(remaining.next());
                    if (sourcepaths == null) return true;
                    if (!this.isOnJvm9) return true;
                    this.setLocation(StandardLocation.MODULE_SOURCE_PATH, sourcepaths);
                    return true;
                }
                case "-extdirs": {
                    if (this.isOnJvm9) {
                        throw new IllegalArgumentException();
                    }
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                    this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.concatFiles(iterable, this.getExtdirsFrom(remaining.next())));
                    this.flags |= 1;
                    return true;
                }
                case "-endorseddirs": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> iterable = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                    this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.prependFiles(iterable, this.getEndorsedDirsFrom(remaining.next())));
                    this.flags |= 4;
                    return true;
                }
                case "-d": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> outputDir = this.getOutputDir(remaining.next());
                    if (outputDir == null) return true;
                    this.setLocation(StandardLocation.CLASS_OUTPUT, outputDir);
                    return true;
                }
                case "-s": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> outputDir = this.getOutputDir(remaining.next());
                    if (outputDir == null) return true;
                    this.setLocation(StandardLocation.SOURCE_OUTPUT, outputDir);
                    return true;
                }
                case "-processorpath": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> processorpaths = this.getPathsFrom(remaining.next());
                    if (processorpaths != null) {
                        this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, processorpaths);
                    }
                    this.flags |= 8;
                    return true;
                }
                case "--processor-module-path": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    Iterable<? extends File> processorpaths = this.getPathsFrom(remaining.next());
                    if (processorpaths == null) return true;
                    if (!this.isOnJvm9) return true;
                    this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_MODULE_PATH, processorpaths);
                    this.flags |= 0x10;
                    return true;
                }
                case "--release": {
                    if (!remaining.hasNext()) throw new IllegalArgumentException();
                    this.releaseVersion = remaining.next();
                    return true;
                }
            }
            return false;
        }
        catch (IOException iOException) {}
        return false;
    }

    @Override
    public boolean hasLocation(JavaFileManager.Location location) {
        try {
            return this.getLocationForModule(location, NO_EXTENSION) != null;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file) {
        this.validateNonModuleLocation(location);
        String name = file.getName();
        JavaFileObject javaFileObject = null;
        int index = name.lastIndexOf(46);
        if (index != -1) {
            name = name.substring(0, index);
        }
        try {
            javaFileObject = this.getJavaFileForInput(location, name, file.getKind());
        }
        catch (IOException iOException) {
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        if (javaFileObject == null) {
            return null;
        }
        return name.replace('/', '.');
    }

    private boolean isArchive(File f) {
        String extension = this.getExtension(f);
        return extension.equalsIgnoreCase(".jar") || extension.equalsIgnoreCase(".zip");
    }

    @Override
    public boolean isSameFile(FileObject fileObject1, FileObject fileObject2) {
        if (!(fileObject1 instanceof EclipseFileObject)) {
            throw new IllegalArgumentException("Unsupported file object class : " + fileObject1.getClass());
        }
        if (!(fileObject2 instanceof EclipseFileObject)) {
            throw new IllegalArgumentException("Unsupported file object class : " + fileObject2.getClass());
        }
        return fileObject1.equals(fileObject2);
    }

    @Override
    public int isSupportedOption(String option) {
        return Options.processOptionsFileManager(option);
    }

    @Override
    public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        this.validateNonModuleLocation(location);
        Iterable<? extends File> allFilesInLocations = this.getLocation(location);
        if (allFilesInLocations == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        ArrayList<JavaFileObject> collector = new ArrayList<JavaFileObject>();
        String normalizedPackageName = this.normalized(packageName);
        for (File file : allFilesInLocations) {
            this.collectAllMatchingFiles(location, file, normalizedPackageName, kinds, recurse, collector);
        }
        return collector;
    }

    private String normalized(String className) {
        char[] classNameChars = className.toCharArray();
        int i = 0;
        int max = classNameChars.length;
        while (i < max) {
            switch (classNameChars[i]) {
                case '\\': {
                    classNameChars[i] = 47;
                    break;
                }
                case '.': {
                    classNameChars[i] = 47;
                }
            }
            ++i;
        }
        return new String(classNameChars);
    }

    private Iterable<? extends File> prependFiles(Iterable<? extends File> iterable, Iterable<? extends File> iterable2) {
        if (iterable2 == null) {
            return iterable;
        }
        ArrayList<File> list = new ArrayList<File>();
        Iterator<? extends File> iterator = iterable2.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        if (iterable != null) {
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        return list;
    }

    private boolean isRunningJvm9() {
        return SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0;
    }

    @Override
    public void setLocation(JavaFileManager.Location location, Iterable<? extends File> files) throws IOException {
        if (location.isOutputLocation()) {
            int count = 0;
            Iterator<? extends File> iterator = files.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                ++count;
            }
            if (count != 1) {
                throw new IllegalArgumentException("output location can only have one path");
            }
        }
        this.locationHandler.setLocation(location, NO_EXTENSION, this.getPaths(files));
    }

    public void setLocale(Locale locale) {
        this.locale = locale == null ? Locale.getDefault() : locale;
        try {
            this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
        }
        catch (MissingResourceException e) {
            System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
            throw e;
        }
    }

    public void processPathEntries(int defaultSize, ArrayList paths, String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
        String currentClasspathName = null;
        String currentDestinationPath = null;
        ArrayList<String> currentRuleSpecs = new ArrayList<String>(defaultSize);
        StringTokenizer tokenizer = new StringTokenizer(currentPath, String.valueOf(File.pathSeparator) + "[]", true);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        int state = 0;
        String token = null;
        int cursor = 0;
        int tokensNb = tokens.size();
        int bracket = -1;
        while (cursor < tokensNb && state != 99) {
            if ((token = (String)tokens.get(cursor++)).equals(File.pathSeparator)) {
                switch (state) {
                    case 0: 
                    case 3: 
                    case 10: {
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 8: {
                        state = 3;
                        this.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
                        currentRuleSpecs.clear();
                        break;
                    }
                    case 6: {
                        state = 4;
                        break;
                    }
                    case 7: {
                        throw new IllegalArgumentException(this.bind("configure.incorrectDestinationPathEntry", currentPath));
                    }
                    case 11: {
                        cursor = bracket + 1;
                        state = 5;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            } else if (token.equals("[")) {
                switch (state) {
                    case 0: {
                        currentClasspathName = NO_EXTENSION;
                    }
                    case 1: {
                        bracket = cursor - 1;
                    }
                    case 11: {
                        state = 10;
                        break;
                    }
                    case 2: {
                        state = 9;
                        break;
                    }
                    case 8: {
                        state = 5;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            } else if (token.equals("]")) {
                switch (state) {
                    case 6: {
                        state = 2;
                        break;
                    }
                    case 7: {
                        state = 8;
                        break;
                    }
                    case 10: {
                        state = 11;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            } else {
                switch (state) {
                    case 0: 
                    case 3: {
                        state = 1;
                        currentClasspathName = token;
                        break;
                    }
                    case 5: {
                        if (token.startsWith("-d ")) {
                            if (currentDestinationPath != null) {
                                throw new IllegalArgumentException(this.bind("configure.duplicateDestinationPathEntry", currentPath));
                            }
                            currentDestinationPath = token.substring(3).trim();
                            state = 7;
                            break;
                        }
                    }
                    case 4: {
                        if (currentDestinationPath != null) {
                            throw new IllegalArgumentException(this.bind("configure.accessRuleAfterDestinationPath", currentPath));
                        }
                        state = 6;
                        currentRuleSpecs.add(token);
                        break;
                    }
                    case 9: {
                        if (!token.startsWith("-d ")) {
                            state = 99;
                            break;
                        }
                        currentDestinationPath = token.substring(3).trim();
                        state = 7;
                        break;
                    }
                    case 11: {
                        int i = bracket;
                        while (i < cursor) {
                            currentClasspathName = String.valueOf(currentClasspathName) + (String)tokens.get(i);
                            ++i;
                        }
                        state = 1;
                        break;
                    }
                    case 10: {
                        break;
                    }
                    default: {
                        state = 99;
                    }
                }
            }
            if (state != 11 || cursor != tokensNb) continue;
            cursor = bracket + 1;
            state = 5;
        }
        switch (state) {
            case 3: {
                break;
            }
            case 1: 
            case 2: 
            case 8: {
                this.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
            }
        }
    }

    protected void addNewEntry(ArrayList paths, String currentClasspathName, ArrayList currentRuleSpecs, String customEncoding, String destPath, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
        int rulesSpecsSize = currentRuleSpecs.size();
        AccessRuleSet accessRuleSet = null;
        if (rulesSpecsSize != 0) {
            AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
            boolean rulesOK = true;
            Iterator i = currentRuleSpecs.iterator();
            int j = 0;
            while (i.hasNext()) {
                String ruleSpec = (String)i.next();
                char key = ruleSpec.charAt(0);
                String pattern = ruleSpec.substring(1);
                if (pattern.length() > 0) {
                    switch (key) {
                        case '+': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0);
                            break;
                        }
                        case '~': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0x1000118);
                            break;
                        }
                        case '-': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0x1000133);
                            break;
                        }
                        case '?': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0x1000133, true);
                            break;
                        }
                        default: {
                            rulesOK = false;
                            break;
                        }
                    }
                    continue;
                }
                rulesOK = false;
            }
            if (rulesOK) {
                accessRuleSet = new AccessRuleSet(accessRules, 0, currentClasspathName);
            } else {
                return;
            }
        }
        if ("none".equals(destPath)) {
            destPath = "none";
        }
        if (rejectDestinationPathOnJars && destPath != null && (currentClasspathName.endsWith(".jar") || currentClasspathName.endsWith(".zip"))) {
            throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntryFile", currentClasspathName));
        }
        FileSystem.Classpath currentClasspath = FileSystem.getClasspath(currentClasspathName, customEncoding, isSourceOnly, accessRuleSet, destPath, null, this.releaseVersion);
        if (currentClasspath != null) {
            paths.add(currentClasspath);
        }
    }

    private String bind(String id, String binding) {
        return this.bind(id, new String[]{binding});
    }

    private String bind(String id, String[] arguments) {
        if (id == null) {
            return "No message available";
        }
        String message = null;
        try {
            message = this.bundle.getString(id);
        }
        catch (MissingResourceException missingResourceException) {
            return "Missing message: " + id + " in: " + "org.eclipse.jdt.internal.compiler.batch.messages";
        }
        return MessageFormat.format(message, arguments);
    }

    private Iterable<? extends File> getFiles(Iterable<? extends Path> paths) {
        if (paths == null) {
            return null;
        }
        return () -> new Iterator<File>(paths){
            Iterator<? extends Path> original;
            {
                this.original = iterable.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.original.hasNext();
            }

            @Override
            public File next() {
                return this.original.next().toFile();
            }
        };
    }

    private Iterable<? extends Path> getPaths(Iterable<? extends File> files) {
        if (files == null) {
            return null;
        }
        return () -> new Iterator<Path>(files){
            Iterator<? extends File> original;
            {
                this.original = iterable.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.original.hasNext();
            }

            @Override
            public Path next() {
                return this.original.next().toPath();
            }
        };
    }

    private void validateFileObject(FileObject file) {
    }

    private void validateModuleLocation(JavaFileManager.Location location, String modName) {
        Objects.requireNonNull(location);
        if (modName == null) {
            throw new IllegalArgumentException("module must not be null");
        }
        if (this.isOnJvm9 && !location.isModuleOrientedLocation() && !location.isOutputLocation()) {
            throw new IllegalArgumentException("location is module related :" + location.getName());
        }
    }

    private void validateNonModuleLocation(JavaFileManager.Location location) {
        Objects.requireNonNull(location);
        if (this.isOnJvm9 && location.isModuleOrientedLocation() && location.isOutputLocation()) {
            throw new IllegalArgumentException("location is module related :" + location.getName());
        }
    }

    private void validateOutputLocation(JavaFileManager.Location location) {
        Objects.requireNonNull(location);
        if (!location.isOutputLocation()) {
            throw new IllegalArgumentException("location is not output location :" + location.getName());
        }
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(Path ... paths) {
        return this.getJavaFileObjectsFromPaths((Iterable<? extends Path>)Arrays.asList(paths));
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Iterable<? extends Path> paths) {
        return this.getJavaFileObjectsFromFiles(this.getFiles(paths));
    }

    @Override
    public Iterable<? extends Path> getLocationAsPaths(JavaFileManager.Location location) {
        if (location instanceof ModuleLocationHandler.LocationWrapper) {
            return ((ModuleLocationHandler.LocationWrapper)location).paths;
        }
        ModuleLocationHandler.LocationContainer loc = this.locationHandler.getLocation(location);
        if (loc == null) {
            return null;
        }
        return ((ModuleLocationHandler.LocationWrapper)loc).getPaths();
    }

    @Override
    public void setLocationFromPaths(JavaFileManager.Location location, Collection<? extends Path> paths) throws IOException {
        this.setLocation(location, this.getFiles(paths));
        if (location == StandardLocation.MODULE_PATH) {
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("org.eclipse.jdt.core.compiler.compliance", "9");
            options.put("org.eclipse.jdt.core.compiler.source", "9");
            options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
            CompilerOptions compilerOptions = new CompilerOptions(options);
            ProblemReporter problemReporter = new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(), compilerOptions, new DefaultProblemFactory());
            for (Path path : paths) {
                List<FileSystem.Classpath> mp = ModuleFinder.findModules(path.toFile(), null, new Parser(problemReporter, true), null, true, this.releaseVersion);
                for (FileSystem.Classpath cp : mp) {
                    Collection<String> moduleNames = cp.getModuleNames(null);
                    for (String string : moduleNames) {
                        Path p = Paths.get(cp.getPath(), new String[0]);
                        this.setLocationForModule(StandardLocation.MODULE_PATH, string, Collections.singletonList(p));
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(JavaFileManager.Location location, FileObject fo) throws IOException {
        this.validateFileObject(fo);
        Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        for (File file : files) {
            Archive archive;
            Path filepath;
            if (!(file.isDirectory() ? fo instanceof EclipseFileObject && (filepath = ((EclipseFileObject)fo).f.toPath()).startsWith(Paths.get(file.toURI()).toAbsolutePath()) : this.isArchive(file) && fo instanceof ArchiveFileObject && (archive = this.getArchive(file)) != Archive.UNKNOWN_ARCHIVE && archive.contains(((ArchiveFileObject)fo).entryName))) continue;
            return true;
        }
        return false;
    }

    @Override
    public JavaFileManager.Location getLocationForModule(JavaFileManager.Location location, String moduleName) throws IOException {
        this.validateModuleLocation(location, moduleName);
        JavaFileManager.Location result = this.locationHandler.getLocation(location, moduleName);
        if (result == null && location == StandardLocation.CLASS_OUTPUT) {
            ModuleLocationHandler.LocationWrapper wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.MODULE_SOURCE_PATH, moduleName);
            this.deriveOutputLocationForModules(moduleName, wrapper.paths);
            result = this.getLocationForModule(location, moduleName);
        } else if (result == null && location == StandardLocation.SOURCE_OUTPUT) {
            ModuleLocationHandler.LocationWrapper wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.MODULE_SOURCE_PATH, moduleName);
            this.deriveSourceOutputLocationForModules(moduleName, wrapper.paths);
            result = this.getLocationForModule(location, moduleName);
        }
        return result;
    }

    @Override
    public JavaFileManager.Location getLocationForModule(JavaFileManager.Location location, JavaFileObject fo) {
        this.validateModuleLocation(location, NO_EXTENSION);
        Path path = null;
        if (fo instanceof ArchiveFileObject) {
            path = ((ArchiveFileObject)fo).file.toPath();
            return this.locationHandler.getLocation(location, path);
        }
        if (fo instanceof EclipseFileObject) {
            path = ((EclipseFileObject)fo).f.toPath();
            try {
                path = path.toRealPath(new LinkOption[0]);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            ModuleLocationHandler.LocationContainer container = this.locationHandler.getLocation(location);
            while (path != null) {
                JavaFileManager.Location loc = container.get(path);
                if (loc != null) {
                    return loc;
                }
                path = path.getParent();
            }
        }
        return null;
    }

    @Override
    public <S> ServiceLoader<S> getServiceLoader(JavaFileManager.Location location, Class<S> service) throws IOException {
        return ServiceLoader.load(service, this.getClassLoader(location));
    }

    @Override
    public String inferModuleName(JavaFileManager.Location location) throws IOException {
        if (location instanceof ModuleLocationHandler.ModuleLocationWrapper) {
            ModuleLocationHandler.ModuleLocationWrapper wrapper = (ModuleLocationHandler.ModuleLocationWrapper)location;
            return wrapper.modName;
        }
        return null;
    }

    @Override
    public Iterable<Set<JavaFileManager.Location>> listLocationsForModules(JavaFileManager.Location location) {
        this.validateModuleLocation(location, NO_EXTENSION);
        return this.locationHandler.listLocationsForModules(location);
    }

    @Override
    public Path asPath(FileObject file) {
        this.validateFileObject(file);
        EclipseFileObject eclFile = (EclipseFileObject)file;
        if (eclFile.f != null) {
            return eclFile.f.toPath();
        }
        return null;
    }

    private void deriveOutputLocationForModules(String moduleName, Collection<? extends Path> paths) {
        ModuleLocationHandler.LocationWrapper wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.CLASS_OUTPUT, moduleName);
        if (wrapper == null) {
            Iterator<? extends Path> iterator;
            wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.CLASS_OUTPUT, NO_EXTENSION);
            if (wrapper == null) {
                wrapper = this.locationHandler.getLocation(StandardLocation.CLASS_OUTPUT);
            }
            if (wrapper != null && (iterator = wrapper.paths.iterator()).hasNext()) {
                try {
                    Path path = iterator.next().resolve(moduleName);
                    this.locationHandler.setLocation(StandardLocation.CLASS_OUTPUT, moduleName, Collections.singletonList(path));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deriveSourceOutputLocationForModules(String moduleName, Collection<? extends Path> paths) {
        ModuleLocationHandler.LocationWrapper wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.SOURCE_OUTPUT, moduleName);
        if (wrapper == null) {
            Iterator<? extends Path> iterator;
            wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.SOURCE_OUTPUT, NO_EXTENSION);
            if (wrapper == null) {
                wrapper = this.locationHandler.getLocation(StandardLocation.SOURCE_OUTPUT);
            }
            if (wrapper != null && (iterator = wrapper.paths.iterator()).hasNext()) {
                try {
                    Path path = iterator.next().resolve(moduleName);
                    this.locationHandler.setLocation(StandardLocation.SOURCE_OUTPUT, moduleName, Collections.singletonList(path));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setLocationForModule(JavaFileManager.Location location, String moduleName, Collection<? extends Path> paths) throws IOException {
        Iterator<? extends Path> iterator;
        ModuleLocationHandler.LocationWrapper wrapper;
        this.validateModuleLocation(location, moduleName);
        this.locationHandler.setLocation(location, moduleName, paths);
        if (location == StandardLocation.MODULE_SOURCE_PATH && (wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.CLASS_OUTPUT, moduleName)) == null && (wrapper = this.locationHandler.getLocation((JavaFileManager.Location)StandardLocation.CLASS_OUTPUT, NO_EXTENSION)) != null && (iterator = wrapper.paths.iterator()).hasNext()) {
            Path path = iterator.next().resolve(moduleName);
            this.locationHandler.setLocation(StandardLocation.CLASS_OUTPUT, moduleName, Collections.singletonList(path));
        }
    }
}

