/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathJep247
extends ClasspathJrt {
    protected FileSystem fs = null;
    protected String compliance = null;
    protected long jdklevel;
    protected String releaseInHex = null;
    protected String[] subReleases = null;
    protected Path releasePath = null;
    protected Set<String> packageCache;
    protected File jdkHome;
    protected String modulePath = null;

    public ClasspathJep247(File jdkHome, String release, AccessRuleSet accessRuleSet) {
        super(jdkHome, false, accessRuleSet, null);
        this.compliance = release;
        this.jdklevel = CompilerOptions.releaseToJDKLevel(this.compliance);
        this.jdkHome = jdkHome;
        this.file = new File(new File(jdkHome, "lib"), "jrt-fs.jar");
    }

    @Override
    public List<FileSystem.Classpath> fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, false);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        if (!this.isPackage(qualifiedPackageName, moduleName)) {
            return null;
        }
        try {
            ClassFileReader reader = null;
            byte[] content = null;
            qualifiedBinaryFileName = qualifiedBinaryFileName.replace(".class", ".sig");
            if (this.subReleases != null && this.subReleases.length > 0) {
                String[] stringArray = this.subReleases;
                int n = this.subReleases.length;
                int n2 = 0;
                while (n2 < n) {
                    String rel = stringArray[n2];
                    Path p = this.fs.getPath(rel, qualifiedBinaryFileName);
                    if (!Files.exists(p, new LinkOption[0]) || (content = JRTUtil.safeReadBytes(p)) == null) {
                        ++n2;
                        continue;
                    }
                    break;
                }
            } else {
                content = JRTUtil.safeReadBytes(this.fs.getPath(this.releaseInHex, qualifiedBinaryFileName));
            }
            if (content != null) {
                reader = new ClassFileReader(content, qualifiedBinaryFileName.toCharArray());
                char[] modName = moduleName != null ? moduleName.toCharArray() : null;
                return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName), modName);
            }
        }
        catch (IOException | ClassFormatException exception) {}
        return null;
    }

    @Override
    public void initialize() throws IOException {
        if (this.compliance == null) {
            return;
        }
        this.releaseInHex = Integer.toHexString(Integer.parseInt(this.compliance)).toUpperCase();
        Path filePath = this.jdkHome.toPath().resolve("lib").resolve("ct.sym");
        URI t = filePath.toUri();
        if (!Files.exists(filePath, new LinkOption[0])) {
            return;
        }
        URI uri = URI.create("jar:file:" + t.getRawPath());
        try {
            this.fs = FileSystems.getFileSystem(uri);
        }
        catch (FileSystemNotFoundException fileSystemNotFoundException) {}
        if (this.fs == null) {
            HashMap env = new HashMap();
            this.fs = FileSystems.newFileSystem(uri, env);
        }
        this.releasePath = this.fs.getPath("/", new String[0]);
        if (!Files.exists(this.fs.getPath(this.releaseInHex, new String[0]), new LinkOption[0])) {
            throw new IllegalArgumentException("release " + this.compliance + " is not found in the system");
        }
        super.initialize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void loadModules() {
        if (this.jdklevel <= 0x340000L) {
            super.loadModules();
            return;
        }
        Path modPath = this.fs.getPath(String.valueOf(this.releaseInHex) + "-modules", new String[0]);
        if (!Files.exists(modPath, new LinkOption[0])) {
            throw new IllegalArgumentException("release " + this.compliance + " is not found in the system");
        }
        this.modulePath = String.valueOf(this.file.getPath()) + "|" + modPath.toString();
        Map cache = (Map)ModulesCache.get(this.modulePath);
        if (cache == null) {
            try {
                Throwable throwable = null;
                Object var4_6 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(modPath);){
                    final HashMap newCache = new HashMap();
                    for (Path subdir : stream) {
                        Files.walkFileTree(subdir, (FileVisitor<? super Path>)new FileVisitor<Path>(){

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                                byte[] content = null;
                                if (Files.exists(f, new LinkOption[0])) {
                                    content = JRTUtil.safeReadBytes(f);
                                    if (content == null) {
                                        return FileVisitResult.CONTINUE;
                                    }
                                    ClasspathJep247.this.acceptModule(content, (Map<String, IModule>)newCache);
                                    ClasspathJep247.this.moduleNamesCache.add(JRTUtil.sanitizedFileName(f));
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path f, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                    HashMap hashMap = ModulesCache;
                    synchronized (hashMap) {
                        if (ModulesCache.get(this.modulePath) == null) {
                            ModulesCache.put(this.modulePath, Collections.unmodifiableMap(newCache));
                        }
                    }
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.moduleNamesCache.addAll(cache.keySet());
        }
    }

    @Override
    void acceptModule(ClassFileReader reader, Map<String, IModule> cache) {
        IBinaryModule moduleDecl;
        if (this.jdklevel <= 0x340000L) {
            super.acceptModule(reader, cache);
            return;
        }
        if (reader != null && (moduleDecl = reader.getModuleDeclaration()) != null) {
            cache.put(String.valueOf(moduleDecl.name()), moduleDecl);
        }
    }

    protected void addToPackageCache(String packageName, boolean endsWithSep) {
        if (this.packageCache.contains(packageName)) {
            return;
        }
        this.packageCache.add(packageName);
    }

    @Override
    public synchronized char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        if (this.packageCache == null) {
            this.packageCache = new HashSet<String>(41);
            this.packageCache.add(Util.EMPTY_STRING);
            ArrayList<String> sub = new ArrayList<String>();
            try {
                Throwable throwable = null;
                Object var5_7 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.releasePath);){
                    for (Path subdir : stream) {
                        String rel = JRTUtil.sanitizedFileName(subdir);
                        if (!rel.contains(this.releaseInHex)) continue;
                        sub.add(rel);
                        Files.walkFileTree(subdir, (FileVisitor<? super Path>)new FileVisitor<Path>(){

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                if (dir.getNameCount() <= 1) {
                                    return FileVisitResult.CONTINUE;
                                }
                                Path relative = dir.subpath(1, dir.getNameCount());
                                ClasspathJep247.this.addToPackageCache(relative.toString(), false);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path f, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            this.subReleases = sub.toArray(new String[sub.size()]);
        }
        if (moduleName == null) {
            List<String> mods = JRTUtil.getModulesDeclaringPackage(this.file, qualifiedPackageName, moduleName);
            return CharOperation.toCharArrays(mods);
        }
        return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
    }

    @Override
    public void reset() {
        try {
            super.reset();
            this.fs.close();
        }
        catch (IOException iOException) {}
    }

    @Override
    public String toString() {
        return "Classpath for JEP 247 for JDK " + this.file.getPath();
    }

    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            String path2 = this.getPath();
            char[] rawName = path2.toCharArray();
            if (File.separatorChar == '\\') {
                CharOperation.replace(rawName, '\\', '/');
            }
            this.normalizedPath = CharOperation.subarray(rawName, 0, CharOperation.lastIndexOf('.', rawName));
        }
        return this.normalizedPath;
    }

    @Override
    public String getPath() {
        if (this.path == null) {
            try {
                this.path = this.file.getCanonicalPath();
            }
            catch (IOException iOException) {
                this.path = this.file.getAbsolutePath();
            }
        }
        return this.path;
    }

    @Override
    public int getMode() {
        return 2;
    }
}

