/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.BasicModule;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.PackageExportImpl;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ModuleFinder {
    public static List<FileSystem.Classpath> findModules(File f, String destinationPath, Parser parser, Map<String, String> options, boolean isModulepath, String release) {
        ArrayList<FileSystem.Classpath> collector = new ArrayList<FileSystem.Classpath>();
        ModuleFinder.scanForModules(destinationPath, parser, options, isModulepath, false, collector, f, release);
        return collector;
    }

    protected static FileSystem.Classpath findModule(File file, String destinationPath, Parser parser, Map<String, String> options, boolean isModulepath, String release) {
        FileSystem.Classpath modulePath = FileSystem.getClasspath(file.getAbsolutePath(), null, !isModulepath, null, destinationPath == null ? null : String.valueOf(destinationPath) + File.separator + file.getName(), options, release);
        if (modulePath != null) {
            ModuleFinder.scanForModule(modulePath, file, parser, isModulepath, release);
        }
        return modulePath;
    }

    protected static void scanForModules(String destinationPath, Parser parser, Map<String, String> options, boolean isModulepath, boolean thisAnAutomodule, List<FileSystem.Classpath> collector, File file, String release) {
        FileSystem.Classpath entry = FileSystem.getClasspath(file.getAbsolutePath(), null, !isModulepath, null, destinationPath == null ? null : String.valueOf(destinationPath) + File.separator + file.getName(), options, release);
        if (entry != null) {
            IModule module = ModuleFinder.scanForModule(entry, file, parser, thisAnAutomodule, release);
            if (module != null) {
                collector.add(entry);
            } else if (file.isDirectory()) {
                File[] files;
                File[] fileArray = files = file.listFiles();
                int n = files.length;
                int n2 = 0;
                while (n2 < n) {
                    File f = fileArray[n2];
                    ModuleFinder.scanForModules(destinationPath, parser, options, isModulepath, isModulepath, collector, f, release);
                    ++n2;
                }
            }
        }
    }

    protected static IModule scanForModule(FileSystem.Classpath modulePath, final File file, Parser parser, boolean considerAutoModules, String release) {
        String fileName;
        IModule module;
        block16: {
            block15: {
                String fileName2;
                module = null;
                if (!file.isDirectory()) break block15;
                String[] list = file.list(new FilenameFilter(){

                    @Override
                    public boolean accept(File dir, String name) {
                        return dir == file && (name.equalsIgnoreCase("module-info.class") || name.equalsIgnoreCase("module-info.java"));
                    }
                });
                if (list.length <= 0) break block16;
                switch (fileName2 = list[0]) {
                    case "module-info.class": {
                        module = ModuleFinder.extractModuleFromClass(new File(file, fileName2), modulePath);
                        break;
                    }
                    case "module-info.java": {
                        module = ModuleFinder.extractModuleFromSource(new File(file, fileName2), parser, modulePath);
                        if (module == null) {
                            return null;
                        }
                        String modName = new String(module.name());
                        if (!modName.equals(file.getName())) {
                            throw new IllegalArgumentException("module name " + modName + " does not match expected name " + file.getName());
                        }
                        break block16;
                    }
                }
                break block16;
            }
            String moduleDescPath = ModuleFinder.getModulePathForArchive(file);
            if (moduleDescPath != null) {
                module = ModuleFinder.extractModuleFromArchive(file, modulePath, moduleDescPath, release);
            }
        }
        if (considerAutoModules && module == null && !(modulePath instanceof ClasspathJrt) && !file.isDirectory() && !(fileName = ModuleFinder.getFileName(file)).isEmpty()) {
            module = IModule.createAutomatic(fileName, file.isFile(), ModuleFinder.getManifest(file));
        }
        if (module != null) {
            modulePath.acceptModule(module);
        }
        return module;
    }

    private static Manifest getManifest(File file) {
        if (ModuleFinder.getModulePathForArchive(file) == null) {
            return null;
        }
        try {
            Throwable throwable = null;
            Object var2_3 = null;
            try (JarFile jar = new JarFile(file);){
                return jar.getManifest();
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
        catch (IOException iOException) {
            return null;
        }
    }

    private static String getFileName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(46);
        if (index == -1) {
            return name;
        }
        return name.substring(0, index);
    }

    protected static String[] extractAddonRead(String option) {
        StringTokenizer tokenizer = new StringTokenizer(option, "=");
        String source = null;
        String target = null;
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        source = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        target = tokenizer.nextToken();
        return new String[]{source, target};
    }

    /*
     * Enabled aggressive block sorting
     */
    protected static AddExport extractAddonExport(String option) {
        StringTokenizer tokenizer = new StringTokenizer(option, "/");
        String source = null;
        String pack = null;
        ArrayList<String> targets = new ArrayList<String>();
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        source = tokenizer.nextToken("/");
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        pack = tokenizer.nextToken("/=");
        while (tokenizer.hasMoreTokens()) {
            targets.add(tokenizer.nextToken("=,"));
        }
        PackageExportImpl export = new PackageExportImpl();
        export.pack = pack.toCharArray();
        export.exportedTo = new char[targets.size()][];
        int i = 0;
        while (i < export.exportedTo.length) {
            export.exportedTo[i] = ((String)targets.get(i)).toCharArray();
            ++i;
        }
        return new AddExport(source, export);
    }

    private static String getModulePathForArchive(File file) {
        int format = Util.archiveFormat(file.getAbsolutePath());
        if (format == 0) {
            return "module-info.class";
        }
        if (format == 1) {
            return "classes/module-info.class";
        }
        return null;
    }

    /*
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static IModule extractModuleFromArchive(File file, FileSystem.Classpath pathEntry, String path, String release) {
        ZipFile zipFile;
        block13: {
            ClassFileReader reader;
            IModule module;
            String releasePath;
            ZipEntry entry;
            zipFile = null;
            zipFile = new ZipFile(file);
            if (release != null && (entry = zipFile.getEntry(releasePath = "META-INF/versions/" + release + "/" + path)) != null) {
                path = releasePath;
            }
            if ((module = ModuleFinder.getModule(reader = ClassFileReader.read(zipFile, path))) == null) break block13;
            IBinaryModule iBinaryModule = reader.getModuleDeclaration();
            if (zipFile == null) return iBinaryModule;
            try {
                zipFile.close();
                return iBinaryModule;
            }
            catch (IOException iOException) {}
            return iBinaryModule;
        }
        if (zipFile == null) return null;
        try {
            zipFile.close();
            return null;
        }
        catch (IOException iOException) {}
        return null;
        catch (IOException | ClassFormatException exception) {
            try {
                if (zipFile == null) return null;
            }
            catch (Throwable throwable) {
                if (zipFile == null) throw throwable;
                try {
                    zipFile.close();
                    throw throwable;
                }
                catch (IOException iOException) {}
                throw throwable;
            }
            try {
                zipFile.close();
                return null;
            }
            catch (IOException iOException) {}
            return null;
        }
    }

    private static IModule extractModuleFromClass(File classfilePath, FileSystem.Classpath pathEntry) {
        try {
            ClassFileReader reader = ClassFileReader.read(classfilePath);
            IModule module = ModuleFinder.getModule(reader);
            if (module != null) {
                return reader.getModuleDeclaration();
            }
            return null;
        }
        catch (IOException | ClassFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static IModule getModule(ClassFileReader classfile) {
        if (classfile != null) {
            return classfile.getModuleDeclaration();
        }
        return null;
    }

    private static IModule extractModuleFromSource(File file, Parser parser, FileSystem.Classpath pathEntry) {
        CompilationResult compilationResult;
        CompilationUnit cu = new CompilationUnit(null, file.getAbsolutePath(), null, pathEntry.getDestinationPath());
        CompilationUnitDeclaration unit = parser.parse(cu, compilationResult = new CompilationResult(cu, 0, 1, 10));
        if (unit.isModuleInfo() && unit.moduleDeclaration != null) {
            cu.module = unit.moduleDeclaration.moduleName;
            return new BasicModule(unit.moduleDeclaration, pathEntry);
        }
        return null;
    }

    static class AddExport {
        public final String sourceModuleName;
        public final IModule.IPackageExport export;

        public AddExport(String moduleName, IModule.IPackageExport export) {
            this.sourceModuleName = moduleName;
            this.export = export;
        }
    }
}

