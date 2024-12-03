/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.lang.model.SourceVersion;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.batch.ClasspathDirectory;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJep247;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJep247Jdk12;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJmod;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.ClasspathMultiReleaseJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.ModuleFinder;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment;
import org.eclipse.jdt.internal.compiler.env.IModulePathEntry;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class FileSystem
implements IModuleAwareNameEnvironment,
SuffixConstants {
    public static ArrayList<Classpath> EMPTY_CLASSPATH = new ArrayList();
    protected Classpath[] classpaths;
    protected IModule module;
    Set<String> knownFileNames;
    protected boolean annotationsFromClasspath;
    private static HashMap<File, Classpath> JRT_CLASSPATH_CACHE = null;
    protected Map<String, Classpath> moduleLocations = new HashMap<String, Classpath>();
    Map<String, IUpdatableModule.UpdatesByKind> moduleUpdates = new HashMap<String, IUpdatableModule.UpdatesByKind>();
    static boolean isJRE12Plus = false;
    private boolean hasLimitModules = false;

    static {
        try {
            isJRE12Plus = SourceVersion.valueOf("RELEASE_12") != null;
        }
        catch (IllegalArgumentException illegalArgumentException) {}
    }

    public FileSystem(String[] classpathNames, String[] initialFileNames, String encoding) {
        this(classpathNames, initialFileNames, encoding, null);
    }

    protected FileSystem(String[] classpathNames, String[] initialFileNames, String encoding, Collection<String> limitModules) {
        int classpathSize = classpathNames.length;
        this.classpaths = new Classpath[classpathSize];
        int counter = 0;
        this.hasLimitModules = limitModules != null && !limitModules.isEmpty();
        int i = 0;
        while (i < classpathSize) {
            Classpath classpath = FileSystem.getClasspath(classpathNames[i], encoding, null, null, null);
            try {
                classpath.initialize();
                for (String moduleName : classpath.getModuleNames(limitModules)) {
                    this.moduleLocations.put(moduleName, classpath);
                }
                this.classpaths[counter++] = classpath;
            }
            catch (IOException iOException) {}
            ++i;
        }
        if (counter != classpathSize) {
            this.classpaths = new Classpath[counter];
            System.arraycopy(this.classpaths, 0, this.classpaths, 0, counter);
        }
        this.initializeKnownFileNames(initialFileNames);
    }

    protected FileSystem(Classpath[] paths, String[] initialFileNames, boolean annotationsFromClasspath, Set<String> limitedModules) {
        int length = paths.length;
        int counter = 0;
        this.classpaths = new Classpath[length];
        this.hasLimitModules = limitedModules != null && !limitedModules.isEmpty();
        int i = 0;
        while (i < length) {
            Classpath classpath = paths[i];
            try {
                classpath.initialize();
                for (String moduleName : classpath.getModuleNames(limitedModules)) {
                    this.moduleLocations.put(moduleName, classpath);
                }
                this.classpaths[counter++] = classpath;
            }
            catch (IOException | InvalidPathException exception) {}
            ++i;
        }
        if (counter != length) {
            this.classpaths = new Classpath[counter];
            System.arraycopy(this.classpaths, 0, this.classpaths, 0, counter);
        }
        this.initializeModuleLocations(limitedModules);
        this.initializeKnownFileNames(initialFileNames);
        this.annotationsFromClasspath = annotationsFromClasspath;
    }

    private void initializeModuleLocations(Set<String> limitedModules) {
        if (limitedModules == null) {
            Classpath[] classpathArray = this.classpaths;
            int n = this.classpaths.length;
            int n2 = 0;
            while (n2 < n) {
                Classpath c = classpathArray[n2];
                for (String moduleName : c.getModuleNames(null)) {
                    this.moduleLocations.put(moduleName, c);
                }
                ++n2;
            }
        } else {
            Classpath c;
            HashMap<String, Classpath> moduleMap = new HashMap<String, Classpath>();
            Classpath[] classpathArray = this.classpaths;
            int n = this.classpaths.length;
            int n3 = 0;
            while (n3 < n) {
                c = classpathArray[n3];
                for (String moduleName : c.getModuleNames(null)) {
                    moduleMap.put(moduleName, c);
                }
                ++n3;
            }
            classpathArray = this.classpaths;
            n = this.classpaths.length;
            n3 = 0;
            while (n3 < n) {
                c = classpathArray[n3];
                for (String moduleName : c.getModuleNames(limitedModules, m -> this.getModuleFromEnvironment(m.toCharArray()))) {
                    Classpath classpath = (Classpath)moduleMap.get(moduleName);
                    this.moduleLocations.put(moduleName, classpath);
                }
                ++n3;
            }
        }
    }

    protected FileSystem(Classpath[] paths, String[] initialFileNames, boolean annotationsFromClasspath) {
        this(paths, initialFileNames, annotationsFromClasspath, null);
    }

    public static Classpath getClasspath(String classpathName, String encoding, AccessRuleSet accessRuleSet) {
        return FileSystem.getClasspath(classpathName, encoding, false, accessRuleSet, null, null, null);
    }

    public static Classpath getClasspath(String classpathName, String encoding, AccessRuleSet accessRuleSet, Map<String, String> options, String release) {
        return FileSystem.getClasspath(classpathName, encoding, false, accessRuleSet, null, options, release);
    }

    public static Classpath getJrtClasspath(String jdkHome, String encoding, AccessRuleSet accessRuleSet, Map<String, String> options) {
        return new ClasspathJrt(new File(FileSystem.convertPathSeparators(jdkHome)), true, accessRuleSet, null);
    }

    public static Classpath getOlderSystemRelease(String jdkHome, String release, AccessRuleSet accessRuleSet) {
        return isJRE12Plus ? new ClasspathJep247Jdk12(new File(FileSystem.convertPathSeparators(jdkHome)), release, accessRuleSet) : new ClasspathJep247(new File(FileSystem.convertPathSeparators(jdkHome)), release, accessRuleSet);
    }

    public static Classpath getClasspath(String classpathName, String encoding, boolean isSourceOnly, AccessRuleSet accessRuleSet, String destinationPath, Map<String, String> options, String release) {
        Classpath result = null;
        File file = new File(FileSystem.convertPathSeparators(classpathName));
        if (file.isDirectory()) {
            if (file.exists()) {
                result = new ClasspathDirectory(file, encoding, isSourceOnly ? 1 : 3, accessRuleSet, destinationPath == null || destinationPath == "none" ? destinationPath : FileSystem.convertPathSeparators(destinationPath), options);
            }
        } else {
            int format = Util.archiveFormat(classpathName);
            if (format == 0) {
                if (isSourceOnly) {
                    result = new ClasspathSourceJar(file, true, accessRuleSet, encoding, destinationPath == null || destinationPath == "none" ? destinationPath : FileSystem.convertPathSeparators(destinationPath));
                } else if (destinationPath == null) {
                    if (classpathName.endsWith("jrt-fs.jar")) {
                        if (JRT_CLASSPATH_CACHE == null) {
                            JRT_CLASSPATH_CACHE = new HashMap();
                        } else {
                            result = JRT_CLASSPATH_CACHE.get(file);
                        }
                        if (result == null) {
                            result = new ClasspathJrt(file, true, accessRuleSet, null);
                            try {
                                result.initialize();
                            }
                            catch (IOException iOException) {}
                            JRT_CLASSPATH_CACHE.put(file, result);
                        }
                    } else {
                        result = release == null ? new ClasspathJar(file, true, accessRuleSet, null) : new ClasspathMultiReleaseJar(file, true, accessRuleSet, destinationPath, release);
                    }
                }
            } else if (format == 1) {
                return new ClasspathJmod(file, true, accessRuleSet, null);
            }
        }
        return result;
    }

    private void initializeKnownFileNames(String[] initialFileNames) {
        if (initialFileNames == null) {
            this.knownFileNames = new HashSet<String>(0);
            return;
        }
        this.knownFileNames = new HashSet<String>(initialFileNames.length * 2);
        int i = initialFileNames.length;
        while (--i >= 0) {
            File compilationUnitFile = new File(initialFileNames[i]);
            char[] fileName = null;
            try {
                fileName = compilationUnitFile.getCanonicalPath().toCharArray();
            }
            catch (IOException iOException) {
                continue;
            }
            char[] matchingPathName = null;
            int lastIndexOf = CharOperation.lastIndexOf('.', fileName);
            if (lastIndexOf != -1) {
                fileName = CharOperation.subarray(fileName, 0, lastIndexOf);
            }
            CharOperation.replace(fileName, '\\', '/');
            boolean globalPathMatches = false;
            int j = 0;
            int max = this.classpaths.length;
            while (j < max) {
                char[] matchCandidate = this.classpaths[j].normalizedPath();
                boolean currentPathMatch = false;
                if (this.classpaths[j] instanceof ClasspathDirectory && CharOperation.prefixEquals(matchCandidate, fileName)) {
                    currentPathMatch = true;
                    if (matchingPathName == null) {
                        matchingPathName = matchCandidate;
                    } else if (currentPathMatch) {
                        if (matchCandidate.length > matchingPathName.length) {
                            matchingPathName = matchCandidate;
                        }
                    } else if (!globalPathMatches && matchCandidate.length < matchingPathName.length) {
                        matchingPathName = matchCandidate;
                    }
                    if (currentPathMatch) {
                        globalPathMatches = true;
                    }
                }
                ++j;
            }
            if (matchingPathName == null) {
                this.knownFileNames.add(new String(fileName));
            } else {
                this.knownFileNames.add(new String(CharOperation.subarray(fileName, matchingPathName.length, fileName.length)));
            }
            matchingPathName = null;
        }
    }

    public void scanForModules(Parser parser) {
        int i = 0;
        int max = this.classpaths.length;
        while (i < max) {
            File file = new File(this.classpaths[i].getPath());
            IModule iModule = ModuleFinder.scanForModule(this.classpaths[i], file, parser, false, null);
            if (iModule != null) {
                this.moduleLocations.put(String.valueOf(iModule.name()), this.classpaths[i]);
            }
            ++i;
        }
    }

    @Override
    public void cleanup() {
        int i = 0;
        int max = this.classpaths.length;
        while (i < max) {
            this.classpaths[i].reset();
            ++i;
        }
    }

    private static String convertPathSeparators(String path) {
        return File.separatorChar == '/' ? path.replace('\\', '/') : path.replace('/', '\\');
    }

    /*
     * Unable to fully structure code
     */
    private NameEnvironmentAnswer findClass(String qualifiedTypeName, char[] typeName, boolean asBinaryOnly, char[] moduleName) {
        answer = this.internalFindClass(qualifiedTypeName, typeName, asBinaryOnly, moduleName);
        if (this.annotationsFromClasspath && answer != null && answer.getBinaryType() instanceof ClassFileReader) {
            i = 0;
            length = this.classpaths.length;
            while (i < length) {
                classpathEntry = this.classpaths[i];
                if (classpathEntry.hasAnnotationFileFor(qualifiedTypeName)) {
                    block16: {
                        zip = classpathEntry instanceof ClasspathJar != false ? ((ClasspathJar)classpathEntry).zipFile : null;
                        shouldClose = false;
                        if (zip == null) {
                            zip = ExternalAnnotationDecorator.getAnnotationZipFile(classpathEntry.getPath(), null);
                            shouldClose = true;
                        }
                        answer.setBinaryType(ExternalAnnotationDecorator.create(answer.getBinaryType(), classpathEntry.getPath(), qualifiedTypeName, zip));
                        var12_11 = answer;
                        if (!shouldClose || zip == null) break block16;
                        try {
                            zip.close();
                        }
                        catch (IOException v0) {}
                    }
                    return var12_11;
                    catch (IOException v1) {
                        try {
                            ** if (!shouldClose || zip == null) goto lbl-1000
                        }
                        catch (Throwable var11_12) {
                            if (shouldClose && zip != null) {
                                try {
                                    zip.close();
                                }
                                catch (IOException v3) {}
                            }
                            throw var11_12;
                        }
lbl-1000:
                        // 1 sources

                        {
                            try {
                                zip.close();
                            }
                            catch (IOException v2) {}
                        }
lbl-1000:
                        // 2 sources

                        {
                        }
                    }
                }
                ++i;
            }
            answer.setBinaryType(new ExternalAnnotationDecorator(answer.getBinaryType(), null));
        }
        return answer;
    }

    private NameEnvironmentAnswer internalFindClass(String qualifiedTypeName, char[] typeName, boolean asBinaryOnly, char[] moduleName) {
        if (this.knownFileNames.contains(qualifiedTypeName)) {
            return null;
        }
        String qualifiedBinaryFileName = String.valueOf(qualifiedTypeName) + ".class";
        String qualifiedPackageName = qualifiedTypeName.length() == typeName.length ? Util.EMPTY_STRING : qualifiedBinaryFileName.substring(0, qualifiedTypeName.length() - typeName.length - 1);
        IModuleAwareNameEnvironment.LookupStrategy strategy = IModuleAwareNameEnvironment.LookupStrategy.get(moduleName);
        if (strategy == IModuleAwareNameEnvironment.LookupStrategy.Named) {
            String moduleNameString;
            Classpath classpath;
            if (this.moduleLocations != null && (classpath = this.moduleLocations.get(moduleNameString = String.valueOf(moduleName))) != null) {
                return classpath.findClass(typeName, qualifiedPackageName, moduleNameString, qualifiedBinaryFileName);
            }
            return null;
        }
        String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        NameEnvironmentAnswer suggestedAnswer = null;
        if (qualifiedPackageName == qp2) {
            int i = 0;
            int length = this.classpaths.length;
            while (i < length) {
                NameEnvironmentAnswer answer;
                if (strategy.matches(this.classpaths[i], Classpath::hasModule) && (answer = this.classpaths[i].findClass(typeName, qualifiedPackageName, null, qualifiedBinaryFileName, asBinaryOnly)) != null && (answer.moduleName() == null || this.moduleLocations.containsKey(String.valueOf(answer.moduleName())))) {
                    if (!answer.ignoreIfBetter()) {
                        if (answer.isBetter(suggestedAnswer)) {
                            return answer;
                        }
                    } else if (answer.isBetter(suggestedAnswer)) {
                        suggestedAnswer = answer;
                    }
                }
                ++i;
            }
        } else {
            String qb2 = qualifiedBinaryFileName.replace('/', File.separatorChar);
            int i = 0;
            int length = this.classpaths.length;
            while (i < length) {
                Classpath p = this.classpaths[i];
                if (strategy.matches(p, Classpath::hasModule)) {
                    NameEnvironmentAnswer answer;
                    NameEnvironmentAnswer nameEnvironmentAnswer = answer = !(p instanceof ClasspathDirectory) ? p.findClass(typeName, qualifiedPackageName, null, qualifiedBinaryFileName, asBinaryOnly) : p.findClass(typeName, qp2, null, qb2, asBinaryOnly);
                    if (answer != null && (answer.moduleName() == null || this.moduleLocations.containsKey(String.valueOf(answer.moduleName())))) {
                        if (!answer.ignoreIfBetter()) {
                            if (answer.isBetter(suggestedAnswer)) {
                                return answer;
                            }
                        } else if (answer.isBetter(suggestedAnswer)) {
                            suggestedAnswer = answer;
                        }
                    }
                }
                ++i;
            }
        }
        return suggestedAnswer;
    }

    @Override
    public NameEnvironmentAnswer findType(char[][] compoundName, char[] moduleName) {
        if (compoundName != null) {
            return this.findClass(new String(CharOperation.concatWith(compoundName, '/')), compoundName[compoundName.length - 1], false, moduleName);
        }
        return null;
    }

    public char[][][] findTypeNames(char[][] packageName) {
        Object result;
        block10: {
            String qualifiedPackageName2;
            result = null;
            if (packageName == null) break block10;
            String qualifiedPackageName = new String(CharOperation.concatWith(packageName, '/'));
            String string = qualifiedPackageName2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
            if (qualifiedPackageName == qualifiedPackageName2) {
                int i = 0;
                int length = this.classpaths.length;
                while (i < length) {
                    char[][][] answers = this.classpaths[i].findTypeNames(qualifiedPackageName, null);
                    if (answers != null) {
                        if (result == null) {
                            result = answers;
                        } else {
                            int resultLength = ((char[][][])result).length;
                            int answersLength = answers.length;
                            char[][][] cArray = result;
                            char[][][] cArrayArray = new char[answersLength + resultLength][][];
                            result = cArrayArray;
                            System.arraycopy(cArray, 0, cArrayArray, 0, resultLength);
                            System.arraycopy(answers, 0, result, resultLength, answersLength);
                        }
                    }
                    ++i;
                }
            } else {
                int i = 0;
                int length = this.classpaths.length;
                while (i < length) {
                    char[][][] answers;
                    Classpath p = this.classpaths[i];
                    char[][][] cArray = answers = !(p instanceof ClasspathDirectory) ? p.findTypeNames(qualifiedPackageName, null) : p.findTypeNames(qualifiedPackageName2, null);
                    if (answers != null) {
                        if (result == null) {
                            result = answers;
                        } else {
                            int resultLength = ((char[][][])result).length;
                            int answersLength = answers.length;
                            char[][][] cArray2 = result;
                            char[][][] cArrayArray = new char[answersLength + resultLength][][];
                            result = cArrayArray;
                            System.arraycopy(cArray2, 0, cArrayArray, 0, resultLength);
                            System.arraycopy(answers, 0, result, resultLength, answersLength);
                        }
                    }
                    ++i;
                }
            }
        }
        return result;
    }

    @Override
    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName, char[] moduleName) {
        if (typeName != null) {
            return this.findClass(new String(CharOperation.concatWith(packageName, typeName, '/')), typeName, false, moduleName);
        }
        return null;
    }

    @Override
    public char[][] getModulesDeclaringPackage(char[][] packageName, char[] moduleName) {
        String qualifiedPackageName = new String(CharOperation.concatWith(packageName, '/'));
        String moduleNameString = String.valueOf(moduleName);
        IModuleAwareNameEnvironment.LookupStrategy strategy = IModuleAwareNameEnvironment.LookupStrategy.get(moduleName);
        if (strategy == IModuleAwareNameEnvironment.LookupStrategy.Named) {
            Classpath classpath;
            if (this.moduleLocations != null && (classpath = this.moduleLocations.get(moduleNameString)) != null && classpath.isPackage(qualifiedPackageName, moduleNameString)) {
                return new char[][]{moduleName};
            }
            return null;
        }
        char[][] allNames = null;
        boolean hasUnobserable = false;
        Classpath[] classpathArray = this.classpaths;
        int n = this.classpaths.length;
        int n2 = 0;
        while (n2 < n) {
            Classpath cp = classpathArray[n2];
            if (strategy.matches(cp, Classpath::hasModule)) {
                if (strategy == IModuleAwareNameEnvironment.LookupStrategy.Unnamed) {
                    if (cp.isPackage(qualifiedPackageName, moduleNameString)) {
                        return new char[][]{ModuleBinding.UNNAMED};
                    }
                } else {
                    char[][] declaringModules = cp.getModulesDeclaringPackage(qualifiedPackageName, null);
                    if (declaringModules != null) {
                        if (cp instanceof ClasspathJrt && this.hasLimitModules) {
                            hasUnobserable |= (declaringModules = this.filterModules(declaringModules)) == null;
                        }
                        allNames = allNames == null ? declaringModules : CharOperation.arrayConcat(allNames, declaringModules);
                    }
                }
            }
            ++n2;
        }
        if (allNames == null && hasUnobserable) {
            return new char[][]{ModuleBinding.UNOBSERVABLE};
        }
        return allNames;
    }

    private char[][] filterModules(char[][] declaringModules) {
        char[][] filtered = (char[][])Arrays.stream(declaringModules).filter(m -> this.moduleLocations.containsKey(new String((char[])m))).toArray(n -> new char[n][]);
        if (filtered.length == 0) {
            return null;
        }
        return filtered;
    }

    private Parser getParser() {
        HashMap<String, String> opts = new HashMap<String, String>();
        opts.put("org.eclipse.jdt.core.compiler.source", "9");
        return new Parser(new ProblemReporter(DefaultErrorHandlingPolicies.exitOnFirstError(), new CompilerOptions(opts), new DefaultProblemFactory(Locale.getDefault())), false);
    }

    @Override
    public boolean hasCompilationUnit(char[][] qualifiedPackageName, char[] moduleName, boolean checkCUs) {
        String qPackageName = String.valueOf(CharOperation.concatWith(qualifiedPackageName, '/'));
        String moduleNameString = String.valueOf(moduleName);
        IModuleAwareNameEnvironment.LookupStrategy strategy = IModuleAwareNameEnvironment.LookupStrategy.get(moduleName);
        Parser parser = checkCUs ? this.getParser() : null;
        Function<CompilationUnit, String> pkgNameExtractor = sourceUnit -> {
            String pkgName = null;
            CompilationResult compilationResult = new CompilationResult((ICompilationUnit)sourceUnit, 0, 0, 1);
            char[][] name = parser.parsePackageDeclaration(sourceUnit.getContents(), compilationResult);
            if (name != null) {
                pkgName = CharOperation.toString(name);
            }
            return pkgName;
        };
        switch (strategy) {
            case Named: {
                Classpath location;
                if (this.moduleLocations != null && (location = this.moduleLocations.get(moduleNameString)) != null) {
                    return checkCUs ? location.hasCUDeclaringPackage(qPackageName, pkgNameExtractor) : location.hasCompilationUnit(qPackageName, moduleNameString);
                }
                return false;
            }
        }
        int i = 0;
        while (i < this.classpaths.length) {
            Classpath location = this.classpaths[i];
            if (strategy.matches(location, Classpath::hasModule) && location.hasCompilationUnit(qPackageName, moduleNameString)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public IModule getModule(char[] name) {
        if (this.module != null && CharOperation.equals(name, this.module.name())) {
            return this.module;
        }
        if (this.moduleLocations.containsKey(new String(name))) {
            Classpath[] classpathArray = this.classpaths;
            int n = this.classpaths.length;
            int n2 = 0;
            while (n2 < n) {
                Classpath classpath = classpathArray[n2];
                IModule mod = classpath.getModule(name);
                if (mod != null) {
                    return mod;
                }
                ++n2;
            }
        }
        return null;
    }

    public IModule getModuleFromEnvironment(char[] name) {
        if (this.module != null && CharOperation.equals(name, this.module.name())) {
            return this.module;
        }
        Classpath[] classpathArray = this.classpaths;
        int n = this.classpaths.length;
        int n2 = 0;
        while (n2 < n) {
            Classpath classpath = classpathArray[n2];
            IModule mod = classpath.getModule(name);
            if (mod != null) {
                return mod;
            }
            ++n2;
        }
        return null;
    }

    @Override
    public char[][] getAllAutomaticModules() {
        HashSet<char[]> set = new HashSet<char[]>();
        int i = 0;
        int l = this.classpaths.length;
        while (i < l) {
            if (this.classpaths[i].isAutomaticModule()) {
                set.add(this.classpaths[i].getModule().name());
            }
            ++i;
        }
        return (char[][])set.toArray((T[])new char[set.size()][]);
    }

    @Override
    public char[][] listPackages(char[] moduleName) {
        switch (IModuleAwareNameEnvironment.LookupStrategy.get(moduleName)) {
            case Named: {
                Classpath classpath = this.moduleLocations.get(new String(moduleName));
                if (classpath != null) {
                    return classpath.listPackages();
                }
                return CharOperation.NO_CHAR_CHAR;
            }
        }
        throw new UnsupportedOperationException("can list packages only of a named module");
    }

    void addModuleUpdate(String moduleName, Consumer<IUpdatableModule> update, IUpdatableModule.UpdateKind kind) {
        IUpdatableModule.UpdatesByKind updates = this.moduleUpdates.get(moduleName);
        if (updates == null) {
            updates = new IUpdatableModule.UpdatesByKind();
            this.moduleUpdates.put(moduleName, updates);
        }
        updates.getList(kind, true).add(update);
    }

    @Override
    public void applyModuleUpdates(IUpdatableModule compilerModule, IUpdatableModule.UpdateKind kind) {
        IUpdatableModule.UpdatesByKind updates;
        char[] name = compilerModule.name();
        if (name != ModuleBinding.UNNAMED && (updates = this.moduleUpdates.get(String.valueOf(name))) != null) {
            for (Consumer<IUpdatableModule> update : updates.getList(kind, false)) {
                update.accept(compilerModule);
            }
        }
    }

    public static interface Classpath
    extends IModulePathEntry {
        public char[][][] findTypeNames(String var1, String var2);

        public NameEnvironmentAnswer findClass(char[] var1, String var2, String var3, String var4);

        public NameEnvironmentAnswer findClass(char[] var1, String var2, String var3, String var4, boolean var5);

        public boolean isPackage(String var1, String var2);

        default public boolean hasModule() {
            return this.getModule() != null;
        }

        default public boolean hasCUDeclaringPackage(String qualifiedPackageName, Function<CompilationUnit, String> pkgNameExtractor) {
            return this.hasCompilationUnit(qualifiedPackageName, null);
        }

        public List<Classpath> fetchLinkedJars(ClasspathSectionProblemReporter var1);

        public void reset();

        public char[] normalizedPath();

        public String getPath();

        public void initialize() throws IOException;

        public boolean hasAnnotationFileFor(String var1);

        public void acceptModule(IModule var1);

        public String getDestinationPath();

        public Collection<String> getModuleNames(Collection<String> var1);

        public Collection<String> getModuleNames(Collection<String> var1, Function<String, IModule> var2);
    }

    public static class ClasspathNormalizer {
        public static ArrayList<Classpath> normalize(ArrayList<Classpath> classpaths) {
            ArrayList<Classpath> normalizedClasspath = new ArrayList<Classpath>();
            HashSet<Classpath> cache = new HashSet<Classpath>();
            for (Classpath classpath : classpaths) {
                if (cache.contains(classpath)) continue;
                normalizedClasspath.add(classpath);
                cache.add(classpath);
            }
            return normalizedClasspath;
        }
    }

    public static interface ClasspathSectionProblemReporter {
        public void invalidClasspathSection(String var1);

        public void multipleClasspathSections(String var1);
    }
}

