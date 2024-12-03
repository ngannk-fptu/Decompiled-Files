/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJep247;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathJep247Jdk12
extends ClasspathJep247 {
    Map<String, IModule> modules;
    static String MODULE_INFO = "module-info.sig";

    public ClasspathJep247Jdk12(File jdkHome, String release, AccessRuleSet accessRuleSet) {
        super(jdkHome, release, accessRuleSet);
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
            char[] foundModName;
            byte[] content;
            ClassFileReader reader;
            block22: {
                reader = null;
                content = null;
                foundModName = null;
                qualifiedBinaryFileName = qualifiedBinaryFileName.replace(".class", ".sig");
                if (this.subReleases != null && this.subReleases.length > 0) {
                    String[] stringArray = this.subReleases;
                    int n = this.subReleases.length;
                    int n2 = 0;
                    while (n2 < n) {
                        block23: {
                            Path p;
                            String rel = stringArray[n2];
                            if (moduleName == null) {
                                p = this.fs.getPath(rel, new String[0]);
                                Throwable throwable = null;
                                Object var15_16 = null;
                                try (DirectoryStream<Path> stream = Files.newDirectoryStream(p);){
                                    for (Path subdir : stream) {
                                        Path f = this.fs.getPath(rel, JRTUtil.sanitizedFileName(subdir), qualifiedBinaryFileName);
                                        if (!Files.exists(f, new LinkOption[0])) continue;
                                        content = JRTUtil.safeReadBytes(f);
                                        foundModName = JRTUtil.sanitizedFileName(subdir).toCharArray();
                                        if (content == null) {
                                            continue;
                                        }
                                        break block22;
                                    }
                                    break block23;
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
                            p = this.fs.getPath(rel, moduleName, qualifiedBinaryFileName);
                            if (Files.exists(p, new LinkOption[0]) && (content = JRTUtil.safeReadBytes(p)) != null) break block22;
                        }
                        ++n2;
                    }
                } else {
                    content = JRTUtil.safeReadBytes(this.fs.getPath(this.releaseInHex, qualifiedBinaryFileName));
                }
            }
            if (content != null) {
                reader = new ClassFileReader(content, qualifiedBinaryFileName.toCharArray());
                char[] modName = moduleName != null ? moduleName.toCharArray() : foundModName;
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
        if (this.fs != null) {
            super.initialize();
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
        ArrayList<String> sub = new ArrayList<String>();
        try {
            Throwable throwable = null;
            Object var6_7 = null;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.releasePath);){
                for (Path subdir : stream) {
                    String rel = JRTUtil.sanitizedFileName(subdir);
                    if (!rel.contains(this.releaseInHex)) continue;
                    sub.add(rel);
                }
                this.subReleases = sub.toArray(new String[sub.size()]);
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
        catch (IOException iOException) {}
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
        Path modPath = this.fs.getPath(this.releaseInHex, new String[0]);
        this.modulePath = String.valueOf(this.file.getPath()) + "|" + modPath.toString();
        this.modules = (Map)ModulesCache.get(this.modulePath);
        if (this.modules == null) {
            try {
                Throwable throwable = null;
                Object var3_5 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.releasePath);){
                    final HashMap newCache = new HashMap();
                    for (Path subdir : stream) {
                        String rel = JRTUtil.sanitizedFileName(subdir);
                        if (!rel.contains(this.releaseInHex)) continue;
                        Files.walkFileTree(subdir, Collections.EMPTY_SET, 2, (FileVisitor<? super Path>)new FileVisitor<Path>(){

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                                if (attrs.isDirectory() || f.getNameCount() < 3) {
                                    return FileVisitResult.CONTINUE;
                                }
                                if (f.getFileName().toString().equals(MODULE_INFO) && Files.exists(f, new LinkOption[0])) {
                                    byte[] content = JRTUtil.safeReadBytes(f);
                                    if (content == null) {
                                        return FileVisitResult.CONTINUE;
                                    }
                                    Path m = f.subpath(1, f.getNameCount() - 1);
                                    String name = JRTUtil.sanitizedFileName(m);
                                    ClasspathJep247Jdk12.this.acceptModule(name, content, newCache);
                                    ClasspathJep247Jdk12.this.moduleNamesCache.add(name);
                                }
                                return FileVisitResult.SKIP_SIBLINGS;
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
                            this.modules = Collections.unmodifiableMap(newCache);
                            ModulesCache.put(this.modulePath, this.modules);
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
            this.moduleNamesCache.addAll(this.modules.keySet());
        }
    }

    @Override
    public Collection<String> getModuleNames(Collection<String> limitModule, Function<String, IModule> getModule) {
        return this.selectModules(this.moduleNamesCache, limitModule, getModule);
    }

    @Override
    public IModule getModule(char[] moduleName) {
        if (this.jdklevel <= 0x340000L) {
            return super.getModule(moduleName);
        }
        if (this.modules != null) {
            return this.modules.get(String.valueOf(moduleName));
        }
        return null;
    }

    void acceptModule(String name, byte[] content, Map<String, IModule> cache) {
        if (content == null) {
            return;
        }
        if (cache.containsKey(name)) {
            return;
        }
        ClassFileReader reader = null;
        try {
            reader = new ClassFileReader(content, "module-info.class".toCharArray());
        }
        catch (ClassFormatException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            this.acceptModule(reader, cache);
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

    @Override
    public synchronized char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        if (this.jdklevel >= 0x350000L) {
            List<String> mods = JRTUtil.getModulesDeclaringPackage(this.file, qualifiedPackageName, moduleName);
            return CharOperation.toCharArrays(mods);
        }
        if (this.packageCache == null) {
            this.packageCache = new HashSet(41);
            this.packageCache.add(Util.EMPTY_STRING);
            try {
                Throwable mods = null;
                Object var4_7 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.releasePath);){
                    for (Path subdir : stream) {
                        String rel = JRTUtil.sanitizedFileName(subdir);
                        if (!rel.contains(this.releaseInHex)) continue;
                        Throwable throwable = null;
                        Object var10_15 = null;
                        try (DirectoryStream<Path> stream2 = Files.newDirectoryStream(subdir);){
                            for (Path subdir2 : stream2) {
                                Files.walkFileTree(subdir2, (FileVisitor<? super Path>)new FileVisitor<Path>(){

                                    @Override
                                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                        if (dir.getNameCount() <= 2) {
                                            return FileVisitResult.CONTINUE;
                                        }
                                        Path relative = dir.subpath(2, dir.getNameCount());
                                        ClasspathJep247Jdk12.this.addToPackageCache(relative.toString(), false);
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
                }
                catch (Throwable throwable) {
                    if (mods == null) {
                        mods = throwable;
                    } else if (mods != throwable) {
                        mods.addSuppressed(throwable);
                    }
                    throw mods;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
    }
}

