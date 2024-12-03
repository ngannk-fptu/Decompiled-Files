/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.JrtFileSystemWithOlderRelease;
import org.eclipse.jdt.internal.compiler.util.RuntimeIOException;

class JrtFileSystem {
    private final Map<String, String> packageToModule = new HashMap<String, String>();
    private final Map<String, List<String>> packageToModules = new HashMap<String, List<String>>();
    private final Map<Path, Optional<byte[]>> classCache = new ConcurrentHashMap<Path, Optional<byte[]>>(10007);
    FileSystem fs;
    Path modRoot;
    String jdkHome;

    public static JrtFileSystem getNewJrtFileSystem(File jrt, String release) throws IOException {
        return release == null ? new JrtFileSystem(jrt) : new JrtFileSystemWithOlderRelease(jrt, release);
    }

    JrtFileSystem(File jrt) throws IOException {
        this.initialize(jrt);
    }

    void initialize(File jrt) throws IOException {
        URL jrtPath = null;
        this.jdkHome = null;
        if (jrt.toString().endsWith("jrt-fs.jar")) {
            jrtPath = jrt.toPath().toUri().toURL();
            this.jdkHome = jrt.getParentFile().getParent();
        } else {
            this.jdkHome = jrt.toPath().toString();
            jrtPath = Paths.get(this.jdkHome, "lib", "jrt-fs.jar").toUri().toURL();
        }
        JRTUtil.MODULE_TO_LOAD = System.getProperty("modules.to.load");
        String javaVersion = System.getProperty("java.version");
        if (javaVersion != null && javaVersion.startsWith("1.8")) {
            URLClassLoader loader = new URLClassLoader(new URL[]{jrtPath});
            HashMap env = new HashMap();
            this.fs = FileSystems.newFileSystem(JRTUtil.JRT_URI, env, (ClassLoader)loader);
        } else {
            HashMap<String, String> env = new HashMap<String, String>();
            env.put("java.home", this.jdkHome);
            this.fs = FileSystems.newFileSystem(JRTUtil.JRT_URI, env);
        }
        this.modRoot = this.fs.getPath("/modules", new String[0]);
        this.walkJrtForModules();
    }

    public List<String> getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        List<String> list;
        qualifiedPackageName = qualifiedPackageName.replace('.', '/');
        String module = this.packageToModule.get(qualifiedPackageName);
        if (moduleName == null) {
            if (module == null) {
                return null;
            }
            if (module == "MU") {
                return this.packageToModules.get(qualifiedPackageName);
            }
            return Collections.singletonList(module);
        }
        if (module != null && (module == "MU" ? (list = this.packageToModules.get(qualifiedPackageName)).contains(moduleName) : module.equals(moduleName))) {
            return Collections.singletonList(moduleName);
        }
        return null;
    }

    public String[] getModules(String fileName) {
        int idx = fileName.lastIndexOf(47);
        String pack = null;
        pack = idx != -1 ? fileName.substring(0, idx) : "";
        String module = this.packageToModule.get(pack);
        if (module != null) {
            if (module == "MU") {
                List<String> list = this.packageToModules.get(pack);
                return list.toArray(new String[0]);
            }
            return new String[]{module};
        }
        return JRTUtil.DEFAULT_MODULE;
    }

    public boolean hasClassFile(String qualifiedPackageName, String module) {
        if (module == null) {
            return false;
        }
        String knownModule = this.packageToModule.get(qualifiedPackageName);
        if (knownModule == null || knownModule != "MU" && !knownModule.equals(module)) {
            return false;
        }
        Path packagePath = this.fs.getPath("/modules", module, qualifiedPackageName);
        if (!Files.exists(packagePath, new LinkOption[0])) {
            return false;
        }
        try {
            return Files.list(packagePath).anyMatch(filePath -> filePath.toString().endsWith(".class") || filePath.toString().endsWith(".CLASS"));
        }
        catch (IOException iOException) {
            return false;
        }
    }

    public InputStream getContentFromJrt(String fileName, String module) throws IOException {
        String[] modules;
        if (module != null) {
            byte[] fileBytes = this.getFileBytes(fileName, module);
            if (fileBytes == null) {
                return null;
            }
            return new ByteArrayInputStream(fileBytes);
        }
        String[] stringArray = modules = this.getModules(fileName);
        int n = modules.length;
        int n2 = 0;
        while (n2 < n) {
            String mod = stringArray[n2];
            byte[] fileBytes = this.getFileBytes(fileName, mod);
            if (fileBytes != null) {
                return new ByteArrayInputStream(fileBytes);
            }
            ++n2;
        }
        return null;
    }

    private ClassFileReader getClassfile(String fileName, Predicate<String> moduleNameFilter) throws IOException, ClassFormatException {
        String[] modules = this.getModules(fileName);
        byte[] content = null;
        String module = null;
        String[] stringArray = modules;
        int n = modules.length;
        int n2 = 0;
        while (n2 < n) {
            String mod = stringArray[n2];
            if ((moduleNameFilter == null || moduleNameFilter.test(mod)) && (content = this.getFileBytes(fileName, mod)) != null) {
                module = mod;
                break;
            }
            ++n2;
        }
        if (content != null) {
            ClassFileReader reader = new ClassFileReader(content, fileName.toCharArray());
            reader.moduleName = module.toCharArray();
            return reader;
        }
        return null;
    }

    byte[] getClassfileContent(String fileName, String module) throws IOException {
        byte[] content = null;
        if (module != null) {
            content = this.getFileBytes(fileName, module);
        } else {
            String[] modules;
            String[] stringArray = modules = this.getModules(fileName);
            int n = modules.length;
            int n2 = 0;
            while (n2 < n) {
                String mod = stringArray[n2];
                content = this.getFileBytes(fileName, mod);
                if (content != null) break;
                ++n2;
            }
        }
        return content;
    }

    private byte[] getFileBytes(String fileName, String module) throws IOException {
        Path path = this.fs.getPath("/modules", module, fileName);
        if (JRTUtil.DISABLE_CACHE) {
            return JRTUtil.safeReadBytes(path);
        }
        try {
            Optional bytes = this.classCache.computeIfAbsent(path, key -> {
                try {
                    return Optional.ofNullable(JRTUtil.safeReadBytes(key));
                }
                catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            });
            return bytes.orElse(null);
        }
        catch (RuntimeIOException rio) {
            throw rio.getCause();
        }
    }

    public ClassFileReader getClassfile(String fileName, String module, Predicate<String> moduleNameFilter) throws IOException, ClassFormatException {
        ClassFileReader reader = null;
        if (module == null) {
            reader = this.getClassfile(fileName, moduleNameFilter);
        } else {
            byte[] content = this.getFileBytes(fileName, module);
            if (content != null) {
                reader = new ClassFileReader(content, fileName.toCharArray());
                reader.moduleName = module.toCharArray();
            }
        }
        return reader;
    }

    public ClassFileReader getClassfile(String fileName, IModule module) throws IOException, ClassFormatException {
        ClassFileReader reader = null;
        if (module == null) {
            reader = this.getClassfile(fileName, (Predicate<String>)null);
        } else {
            byte[] content = this.getFileBytes(fileName, new String(module.name()));
            if (content != null) {
                reader = new ClassFileReader(content, fileName.toCharArray());
            }
        }
        return reader;
    }

    void walkJrtForModules() throws IOException {
        Iterable<Path> roots = this.fs.getRootDirectories();
        for (Path path : roots) {
            try {
                Throwable throwable = null;
                Object var5_7 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);){
                    for (final Path subdir : stream) {
                        if (subdir.toString().equals("/modules")) continue;
                        Files.walkFileTree(subdir, (FileVisitor<? super Path>)new JRTUtil.AbstractFileVisitor<Path>(){

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Path relative = subdir.relativize(file);
                                JrtFileSystem.this.cachePackage(relative.getParent().toString(), relative.getFileName().toString());
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
            catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    void walkModuleImage(final JRTUtil.JrtFileVisitor<Path> visitor, final int notify) throws IOException {
        Files.walkFileTree(this.modRoot, (FileVisitor<? super Path>)new JRTUtil.AbstractFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                int count = dir.getNameCount();
                if (count == 1) {
                    return FileVisitResult.CONTINUE;
                }
                if (count == 2) {
                    Path mod = dir.getName(1);
                    if (JRTUtil.MODULE_TO_LOAD != null && JRTUtil.MODULE_TO_LOAD.length() > 0 && JRTUtil.MODULE_TO_LOAD.indexOf(mod.toString()) == -1) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return (notify & 4) == 0 ? FileVisitResult.CONTINUE : visitor.visitModule(dir, JRTUtil.sanitizedFileName(mod));
                }
                if ((notify & 2) == 0) {
                    return FileVisitResult.CONTINUE;
                }
                return visitor.visitPackage(dir.subpath(2, count), dir.getName(1), attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if ((notify & 1) == 0) {
                    return FileVisitResult.CONTINUE;
                }
                int count = file.getNameCount();
                if (count == 3) {
                    JrtFileSystem.this.cachePackage("", file.getName(1).toString());
                }
                return visitor.visitFile(file.subpath(2, count), file.getName(1), attrs);
            }
        });
    }

    synchronized void cachePackage(String packageName, String module) {
        String currentModule = this.packageToModule.get(packageName = packageName.replace('.', '/'));
        if (currentModule == null) {
            this.packageToModule.put(packageName.intern(), module.intern());
            return;
        }
        if (currentModule.equals(module)) {
            return;
        }
        if (currentModule == "MU") {
            List<String> list = this.packageToModules.get(packageName);
            if (!list.contains(module)) {
                if (JRTUtil.JAVA_BASE.equals(module)) {
                    list.add(0, JRTUtil.JAVA_BASE);
                } else {
                    list.add(module.intern());
                }
            }
        } else {
            ArrayList<String> list = new ArrayList<String>();
            if (JRTUtil.JAVA_BASE == currentModule || JRTUtil.JAVA_BASE.equals(currentModule)) {
                list.add(currentModule.intern());
                list.add(module.intern());
            } else {
                list.add(module.intern());
                list.add(currentModule.intern());
            }
            packageName = packageName.intern();
            this.packageToModules.put(packageName, list);
            this.packageToModule.put(packageName, "MU");
        }
    }
}

