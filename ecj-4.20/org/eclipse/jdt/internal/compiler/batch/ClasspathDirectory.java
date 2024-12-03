/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.batch.ClasspathLocation;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathDirectory
extends ClasspathLocation {
    private Hashtable directoryCache;
    private String[] missingPackageHolder = new String[1];
    private int mode;
    private String encoding;
    private Hashtable<String, Hashtable<String, String>> packageSecondaryTypes = null;
    Map options;

    ClasspathDirectory(File directory, String encoding, int mode, AccessRuleSet accessRuleSet, String destinationPath, Map options) {
        super(accessRuleSet, destinationPath);
        this.mode = mode;
        this.options = options;
        try {
            this.path = directory.getCanonicalPath();
        }
        catch (IOException iOException) {
            this.path = directory.getAbsolutePath();
        }
        if (!this.path.endsWith(File.separator)) {
            this.path = String.valueOf(this.path) + File.separator;
        }
        this.directoryCache = new Hashtable(11);
        this.encoding = encoding;
    }

    String[] directoryList(String qualifiedPackageName) {
        String[] dirList = (String[])this.directoryCache.get(qualifiedPackageName);
        if (dirList == this.missingPackageHolder) {
            return null;
        }
        if (dirList != null) {
            return dirList;
        }
        File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        if (dir.isDirectory()) {
            String parentPackage;
            String packageName;
            int index = qualifiedPackageName.length();
            int last = qualifiedPackageName.lastIndexOf(File.separatorChar);
            while (--index > last && !ScannerHelper.isUpperCase(qualifiedPackageName.charAt(index))) {
            }
            if (index <= last || !(last != -1 ? !this.doesFileExist(packageName = qualifiedPackageName.substring(last + 1), parentPackage = qualifiedPackageName.substring(0, last)) : !this.doesFileExist(qualifiedPackageName, Util.EMPTY_STRING))) {
                dirList = dir.list();
                if (dirList == null) {
                    dirList = CharOperation.NO_STRINGS;
                }
                this.directoryCache.put(qualifiedPackageName, dirList);
                return dirList;
            }
        }
        this.directoryCache.put(qualifiedPackageName, this.missingPackageHolder);
        return null;
    }

    boolean doesFileExist(String fileName, String qualifiedPackageName) {
        String[] dirList = this.directoryList(qualifiedPackageName);
        if (dirList == null) {
            return false;
        }
        int i = dirList.length;
        while (--i >= 0) {
            if (!fileName.equals(dirList[i])) continue;
            return true;
        }
        return false;
    }

    public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
    }

    private NameEnvironmentAnswer findClassInternal(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        boolean sourceExists;
        if (!this.isPackage(qualifiedPackageName, null)) {
            return null;
        }
        String fileName = new String(typeName);
        boolean binaryExists = (this.mode & 2) != 0 && this.doesFileExist(String.valueOf(fileName) + ".class", qualifiedPackageName);
        boolean bl = sourceExists = (this.mode & 1) != 0 && this.doesFileExist(String.valueOf(fileName) + ".java", qualifiedPackageName);
        if (sourceExists && !asBinaryOnly) {
            String fullSourcePath = String.valueOf(this.path) + qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + ".java";
            CompilationUnit unit = new CompilationUnit(null, fullSourcePath, this.encoding, this.destinationPath);
            char[] cArray = unit.module = this.module == null ? null : this.module.name();
            if (!binaryExists) {
                return new NameEnvironmentAnswer(unit, this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
            String fullBinaryPath = String.valueOf(this.path) + qualifiedBinaryFileName;
            long binaryModified = new File(fullBinaryPath).lastModified();
            long sourceModified = new File(fullSourcePath).lastModified();
            if (sourceModified > binaryModified) {
                return new NameEnvironmentAnswer(unit, this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
        }
        if (binaryExists) {
            try {
                String typeSearched;
                ClassFileReader reader = ClassFileReader.read(String.valueOf(this.path) + qualifiedBinaryFileName);
                String string = typeSearched = qualifiedPackageName.length() > 0 ? String.valueOf(qualifiedPackageName.replace(File.separatorChar, '/')) + "/" + fileName : fileName;
                if (!CharOperation.equals(reader.getName(), typeSearched.toCharArray())) {
                    reader = null;
                }
                if (reader != null) {
                    char[] modName = reader.moduleName != null ? reader.moduleName : (char[])(this.module != null ? this.module.name() : null);
                    return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName), modName);
                }
            }
            catch (IOException | ClassFormatException exception) {}
        }
        return null;
    }

    public NameEnvironmentAnswer findSecondaryInClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
        if (CharOperation.equals(TypeConstants.PACKAGE_INFO_NAME, typeName)) {
            return null;
        }
        String typeNameString = new String(typeName);
        String moduleName = this.module != null ? String.valueOf(this.module.name()) : null;
        boolean prereqs = this.options != null && this.isPackage(qualifiedPackageName, moduleName) && (this.mode & 1) != 0 && this.doesFileExist(String.valueOf(typeNameString) + ".java", qualifiedPackageName);
        return prereqs ? null : this.findSourceSecondaryType(typeNameString, qualifiedPackageName, qualifiedBinaryFileName);
    }

    @Override
    public boolean hasAnnotationFileFor(String qualifiedTypeName) {
        int pos = qualifiedTypeName.lastIndexOf(47);
        if (pos != -1 && pos + 1 < qualifiedTypeName.length()) {
            String fileName = String.valueOf(qualifiedTypeName.substring(pos + 1)) + ".eea";
            return this.doesFileExist(fileName, qualifiedTypeName.substring(0, pos));
        }
        return false;
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, false);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        if (File.separatorChar == '/') {
            return this.findClassInternal(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly);
        }
        return this.findClassInternal(typeName, qualifiedPackageName.replace('/', File.separatorChar), qualifiedBinaryFileName.replace('/', File.separatorChar), asBinaryOnly);
    }

    private Hashtable<String, String> getSecondaryTypes(String qualifiedPackageName) {
        File[] listFiles;
        Hashtable<String, String> packageEntry = new Hashtable<String, String>();
        String[] dirList = (String[])this.directoryCache.get(qualifiedPackageName);
        if (dirList == this.missingPackageHolder || dirList == null) {
            return packageEntry;
        }
        File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        File[] fileArray = listFiles = dir.isDirectory() ? dir.listFiles() : null;
        if (listFiles == null) {
            return packageEntry;
        }
        int i = 0;
        int l = listFiles.length;
        while (i < l) {
            String s;
            File f = listFiles[i];
            if (!f.isDirectory() && (s = f.getAbsolutePath()) != null && (s.endsWith(".java") || s.endsWith(".JAVA"))) {
                TypeDeclaration[] types;
                CompilationUnit cu = new CompilationUnit(null, s, this.encoding, this.destinationPath);
                CompilationResult compilationResult = new CompilationResult(s.toCharArray(), 1, 1, 10);
                ProblemReporter problemReporter = new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(), new CompilerOptions(this.options), new DefaultProblemFactory());
                Parser parser = new Parser(problemReporter, false);
                parser.reportSyntaxErrorIsRequired = false;
                CompilationUnitDeclaration unit = parser.parse(cu, compilationResult);
                TypeDeclaration[] typeDeclarationArray = types = unit != null ? unit.types : null;
                if (types != null) {
                    int j = 0;
                    int k = types.length;
                    while (j < k) {
                        char[] name;
                        TypeDeclaration type = types[j];
                        char[] cArray = name = type.isSecondary() ? type.name : null;
                        if (name != null) {
                            packageEntry.put(new String(name), s);
                        }
                        ++j;
                    }
                }
            }
            ++i;
        }
        return packageEntry;
    }

    private NameEnvironmentAnswer findSourceSecondaryType(String typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
        String fileName;
        Hashtable<String, String> packageEntry;
        if (this.packageSecondaryTypes == null) {
            this.packageSecondaryTypes = new Hashtable();
        }
        if ((packageEntry = this.packageSecondaryTypes.get(qualifiedPackageName)) == null) {
            packageEntry = this.getSecondaryTypes(qualifiedPackageName);
            this.packageSecondaryTypes.put(qualifiedPackageName, packageEntry);
        }
        return (fileName = packageEntry.get(typeName)) != null ? new NameEnvironmentAnswer(new CompilationUnit(null, fileName, this.encoding, this.destinationPath), this.fetchAccessRestriction(qualifiedBinaryFileName)) : null;
    }

    @Override
    public char[][][] findTypeNames(String qualifiedPackageName, String moduleName) {
        int length;
        if (!this.isPackage(qualifiedPackageName, moduleName)) {
            return null;
        }
        File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        String[] listFiles = dir.list(new FilenameFilter(){

            @Override
            public boolean accept(File directory1, String name) {
                String fileName = name.toLowerCase();
                return fileName.endsWith(".class") || fileName.endsWith(".java");
            }
        });
        if (listFiles == null || (length = listFiles.length) == 0) {
            return null;
        }
        Set<String> secondary = this.getSecondaryTypes(qualifiedPackageName).keySet();
        char[][][] result = new char[length + secondary.size()][][];
        char[][] packageName = CharOperation.splitOn(File.separatorChar, qualifiedPackageName.toCharArray());
        int i = 0;
        while (i < length) {
            String fileName = listFiles[i];
            int indexOfLastDot = fileName.indexOf(46);
            String typeName = indexOfLastDot > 0 ? fileName.substring(0, indexOfLastDot) : fileName;
            result[i] = CharOperation.arrayConcat(packageName, typeName.toCharArray());
            ++i;
        }
        if (secondary.size() > 0) {
            int idx = length;
            for (String type : secondary) {
                result[idx++] = CharOperation.arrayConcat(packageName, type.toCharArray());
            }
        }
        return result;
    }

    @Override
    public void initialize() throws IOException {
    }

    @Override
    public char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        return this.singletonModuleNameIf(this.directoryList(qp2) != null);
    }

    @Override
    public boolean hasCompilationUnit(String qualifiedPackageName, String moduleName) {
        String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        String[] dirList = this.directoryList(qp2);
        if (dirList != null) {
            String[] stringArray = dirList;
            int n = dirList.length;
            int n2 = 0;
            while (n2 < n) {
                String entry = stringArray[n2];
                String entryLC = entry.toLowerCase();
                if (entryLC.endsWith(".java") || entryLC.endsWith(".class")) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    @Override
    public boolean hasCUDeclaringPackage(String qualifiedPackageName, Function<CompilationUnit, String> pkgNameExtractor) {
        String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        String[] directoryList = this.directoryList(qp2);
        if (directoryList == null) {
            return false;
        }
        return Stream.of(directoryList).anyMatch(entry -> {
            String entryLC = entry.toLowerCase();
            boolean hasDeclaration = false;
            String fullPath = String.valueOf(this.path) + qp2 + "/" + entry;
            String pkgName = null;
            if (entryLC.endsWith(".class")) {
                return true;
            }
            if (entryLC.endsWith(".java")) {
                CompilationUnit cu = new CompilationUnit(null, fullPath, this.encoding);
                pkgName = (String)pkgNameExtractor.apply(cu);
            }
            if (pkgName != null && pkgName.equals(qp2.replace(File.separatorChar, '.'))) {
                hasDeclaration = true;
            }
            return hasDeclaration;
        });
    }

    @Override
    public char[][] listPackages() {
        final HashSet packageNames = new HashSet();
        try {
            final Path basePath = FileSystems.getDefault().getPath(this.path, new String[0]);
            Files.walkFileTree(basePath, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().toLowerCase().endsWith(".class")) {
                        packageNames.add(file.getParent().relativize(basePath).toString().replace('/', '.'));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException iOException) {}
        return (char[][])packageNames.stream().map(String::toCharArray).toArray(n -> new char[n][]);
    }

    @Override
    public void reset() {
        super.reset();
        this.directoryCache = new Hashtable(11);
    }

    public String toString() {
        return "ClasspathDirectory " + this.path;
    }

    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            this.normalizedPath = this.path.toCharArray();
            if (File.separatorChar == '\\') {
                CharOperation.replace(this.normalizedPath, '\\', '/');
            }
        }
        return this.normalizedPath;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public int getMode() {
        return this.mode;
    }

    @Override
    public IModule getModule() {
        return this.module;
    }
}

