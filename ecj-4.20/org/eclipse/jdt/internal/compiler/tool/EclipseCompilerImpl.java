/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.tool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJsr199;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileObject;
import org.eclipse.jdt.internal.compiler.tool.ExceptionDiagnostic;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class EclipseCompilerImpl
extends Main {
    private static final CompilationUnit[] NO_UNITS = new CompilationUnit[0];
    private HashMap<CompilationUnit, JavaFileObject> javaFileObjectMap;
    Iterable<? extends JavaFileObject> compilationUnits;
    public JavaFileManager fileManager;
    protected Processor[] processors;
    public DiagnosticListener<? super JavaFileObject> diagnosticListener;

    public EclipseCompilerImpl(PrintWriter out, PrintWriter err, boolean systemExitWhenFinished) {
        super(out, err, systemExitWhenFinished, null, null);
    }

    public boolean call() {
        try {
            try {
                this.handleLocations();
                if (this.proceed) {
                    this.globalProblemsCount = 0;
                    this.globalErrorsCount = 0;
                    this.globalWarningsCount = 0;
                    this.globalTasksCount = 0;
                    this.exportedClassFilesCounter = 0;
                    this.performCompilation();
                }
            }
            catch (IllegalArgumentException e) {
                this.diagnosticListener.report(new ExceptionDiagnostic(e));
                this.logger.logException(e);
                if (this.systemExitWhenFinished) {
                    this.cleanup();
                    System.exit(-1);
                }
                this.cleanup();
                return false;
            }
            catch (RuntimeException e) {
                this.diagnosticListener.report(new ExceptionDiagnostic(e));
                this.logger.logException(e);
                this.cleanup();
                return false;
            }
        }
        finally {
            this.cleanup();
        }
        if (this.failOnWarning && this.globalWarningsCount > 0) {
            return false;
        }
        return this.globalErrorsCount == 0;
    }

    private void cleanup() {
        this.logger.flush();
        this.logger.close();
        this.processors = null;
        try {
            if (this.fileManager != null) {
                this.fileManager.flush();
            }
        }
        catch (IOException iOException) {}
    }

    @Override
    public CompilationUnit[] getCompilationUnits() {
        if (this.compilationUnits == null) {
            return NO_UNITS;
        }
        HashtableOfObject knownFileNames = new HashtableOfObject();
        ArrayList<1> units = new ArrayList<1>();
        int round = 0;
        while (round < 2) {
            int i = 0;
            for (final JavaFileObject javaFileObject : this.compilationUnits) {
                String name = javaFileObject.getName();
                char[] charName = name.toCharArray();
                boolean isModuleInfo = CharOperation.endsWith(charName, TypeConstants.MODULE_INFO_FILE_NAME);
                if (isModuleInfo == (round == 0)) {
                    File file;
                    if (knownFileNames.get(charName) != null) {
                        throw new IllegalArgumentException(this.bind("unit.more", name));
                    }
                    knownFileNames.put(charName, charName);
                    boolean found = false;
                    try {
                        if (this.fileManager.hasLocation(StandardLocation.SOURCE_PATH)) {
                            found = this.fileManager.contains(StandardLocation.SOURCE_PATH, javaFileObject);
                        }
                        if (!found && this.fileManager.hasLocation(StandardLocation.MODULE_SOURCE_PATH)) {
                            found = this.fileManager.contains(StandardLocation.MODULE_SOURCE_PATH, javaFileObject);
                        }
                    }
                    catch (IOException iOException) {}
                    if (!found && !(file = new File(name)).exists()) {
                        throw new IllegalArgumentException(this.bind("unit.missing", name));
                    }
                    CompilationUnit cu = new CompilationUnit(null, name, null, this.destinationPaths[i], EclipseCompilerImpl.shouldIgnoreOptionalProblems(this.ignoreOptionalProblemsFromFolders, name.toCharArray()), this.modNames[i]){

                        @Override
                        public char[] getContents() {
                            try {
                                return javaFileObject.getCharContent(true).toString().toCharArray();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                throw new AbortCompilationUnit(null, e, null);
                            }
                        }
                    };
                    units.add(cu);
                    this.javaFileObjectMap.put(cu, javaFileObject);
                }
                ++i;
            }
            ++round;
        }
        CompilationUnit[] result = new CompilationUnit[units.size()];
        units.toArray(result);
        return result;
    }

    @Override
    public IErrorHandlingPolicy getHandlingPolicy() {
        return new IErrorHandlingPolicy(){

            @Override
            public boolean proceedOnErrors() {
                return false;
            }

            @Override
            public boolean stopOnFirstError() {
                return false;
            }

            @Override
            public boolean ignoreAllErrors() {
                return false;
            }
        };
    }

    @Override
    public IProblemFactory getProblemFactory() {
        return new DefaultProblemFactory(){

            @Override
            public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
                CategorizedProblem problem;
                DiagnosticListener<? super JavaFileObject> diagListener = EclipseCompilerImpl.this.diagnosticListener;
                Diagnostic<JavaFileObject> diagnostic = null;
                if (diagListener != null) {
                    diagnostic = new Diagnostic<JavaFileObject>(){

                        @Override
                        public String getCode() {
                            return Integer.toString(problemId);
                        }

                        @Override
                        public long getColumnNumber() {
                            return columnNumber;
                        }

                        @Override
                        public long getEndPosition() {
                            return endPosition;
                        }

                        @Override
                        public Diagnostic.Kind getKind() {
                            if ((severity & 1) != 0) {
                                return Diagnostic.Kind.ERROR;
                            }
                            if ((severity & 0x20) != 0) {
                                return Diagnostic.Kind.WARNING;
                            }
                            if (false) {
                                return Diagnostic.Kind.MANDATORY_WARNING;
                            }
                            return Diagnostic.Kind.OTHER;
                        }

                        @Override
                        public long getLineNumber() {
                            return lineNumber;
                        }

                        @Override
                        public String getMessage(Locale locale) {
                            if (locale != null) {
                                this.setLocale(locale);
                            }
                            return this.getLocalizedMessage(problemId, problemArguments);
                        }

                        @Override
                        public long getPosition() {
                            return startPosition;
                        }

                        @Override
                        public JavaFileObject getSource() {
                            File f = new File(new String(originatingFileName));
                            if (f.exists()) {
                                return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
                            }
                            return null;
                        }

                        @Override
                        public long getStartPosition() {
                            return startPosition;
                        }
                    };
                }
                if ((problem = super.createProblem(originatingFileName, problemId, problemArguments, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber)) instanceof DefaultProblem && diagnostic != null) {
                    return new Jsr199ProblemWrapper((DefaultProblem)problem, diagnostic, diagListener);
                }
                return problem;
            }

            @Override
            public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, int elaborationID, String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
                CategorizedProblem problem;
                DiagnosticListener<? super JavaFileObject> diagListener = EclipseCompilerImpl.this.diagnosticListener;
                Diagnostic<JavaFileObject> diagnostic = null;
                if (diagListener != null) {
                    diagnostic = new Diagnostic<JavaFileObject>(){

                        @Override
                        public String getCode() {
                            return Integer.toString(problemId);
                        }

                        @Override
                        public long getColumnNumber() {
                            return columnNumber;
                        }

                        @Override
                        public long getEndPosition() {
                            return endPosition;
                        }

                        @Override
                        public Diagnostic.Kind getKind() {
                            if ((severity & 1) != 0) {
                                return Diagnostic.Kind.ERROR;
                            }
                            if ((severity & 0x400) != 0) {
                                return Diagnostic.Kind.NOTE;
                            }
                            if ((severity & 0x20) != 0) {
                                return Diagnostic.Kind.WARNING;
                            }
                            if (false) {
                                return Diagnostic.Kind.MANDATORY_WARNING;
                            }
                            return Diagnostic.Kind.OTHER;
                        }

                        @Override
                        public long getLineNumber() {
                            return lineNumber;
                        }

                        @Override
                        public String getMessage(Locale locale) {
                            if (locale != null) {
                                this.setLocale(locale);
                            }
                            return this.getLocalizedMessage(problemId, problemArguments);
                        }

                        @Override
                        public long getPosition() {
                            return startPosition;
                        }

                        @Override
                        public JavaFileObject getSource() {
                            File f = new File(new String(originatingFileName));
                            if (f.exists()) {
                                return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
                            }
                            return null;
                        }

                        @Override
                        public long getStartPosition() {
                            return startPosition;
                        }
                    };
                }
                if ((problem = super.createProblem(originatingFileName, problemId, problemArguments, elaborationID, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber)) instanceof DefaultProblem && diagnostic != null) {
                    return new Jsr199ProblemWrapper((DefaultProblem)problem, diagnostic, diagListener);
                }
                return problem;
            }
        };
    }

    @Override
    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        super.initialize(outWriter, errWriter, systemExit, customDefaultOptions, compilationProgress);
        this.javaFileObjectMap = new HashMap();
    }

    @Override
    protected void initializeAnnotationProcessorManager() {
        super.initializeAnnotationProcessorManager();
        if (this.batchCompiler.annotationProcessorManager != null && this.processors != null) {
            this.batchCompiler.annotationProcessorManager.setProcessors(this.processors);
        } else if (this.processors != null) {
            throw new UnsupportedOperationException("Cannot handle annotation processing");
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void outputClassFiles(CompilationResult unitResult) {
        if (unitResult == null) return;
        if (unitResult.hasErrors()) {
            if (!this.proceedOnError) return;
        }
        ClassFile[] classFiles = unitResult.getClassFiles();
        boolean generateClasspathStructure = this.fileManager.hasLocation(StandardLocation.CLASS_OUTPUT);
        File outputLocation = null;
        String currentDestinationPath = unitResult.getCompilationUnit().getDestinationPath();
        if (currentDestinationPath == null) {
            currentDestinationPath = this.destinationPath;
        }
        if (currentDestinationPath != null) {
            outputLocation = new File(currentDestinationPath);
            outputLocation.mkdirs();
        }
        int i = 0;
        int fileCount = classFiles.length;
        while (true) {
            String relativeStringName;
            block28: {
                if (i >= fileCount) {
                    this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
                    return;
                }
                ClassFile classFile = classFiles[i];
                char[] filename = classFile.fileName();
                int length = filename.length;
                char[] relativeName = new char[length + 6];
                System.arraycopy(filename, 0, relativeName, 0, length);
                System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
                CharOperation.replace(relativeName, '/', File.separatorChar);
                relativeStringName = new String(relativeName);
                if (this.compilerOptions.verbose) {
                    this.out.println(Messages.bind(Messages.compilation_write, new String[]{String.valueOf(this.exportedClassFilesCounter + 1), relativeStringName}));
                }
                try {
                    void var14_15;
                    char[] modName = unitResult.compilationUnit.getModuleName();
                    Object var14_16 = null;
                    if (modName == null) {
                        StandardLocation standardLocation = StandardLocation.CLASS_OUTPUT;
                    } else {
                        JavaFileManager.Location location = this.fileManager.getLocationForModule((JavaFileManager.Location)StandardLocation.CLASS_OUTPUT, new String(modName));
                    }
                    JavaFileObject javaFileForOutput = this.fileManager.getJavaFileForOutput((JavaFileManager.Location)var14_15, new String(filename), JavaFileObject.Kind.CLASS, this.javaFileObjectMap.get(unitResult.compilationUnit));
                    if (generateClasspathStructure) {
                        if (currentDestinationPath != null) {
                            int index = CharOperation.lastIndexOf(File.separatorChar, relativeName);
                            if (index != -1) {
                                File currentFolder = new File(currentDestinationPath, relativeStringName.substring(0, index));
                                currentFolder.mkdirs();
                            }
                        } else {
                            String path = javaFileForOutput.toUri().getPath();
                            int index = path.lastIndexOf(47);
                            if (index != -1) {
                                File file = new File(path.substring(0, index));
                                file.mkdirs();
                            }
                        }
                    }
                    Throwable throwable = null;
                    Object var17_23 = null;
                    try {
                        OutputStream openOutputStream = javaFileForOutput.openOutputStream();
                        try {
                            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(openOutputStream);){
                                bufferedOutputStream.write(classFile.header, 0, classFile.headerOffset);
                                bufferedOutputStream.write(classFile.contents, 0, classFile.contentsOffset);
                                bufferedOutputStream.flush();
                            }
                            if (openOutputStream == null) break block28;
                        }
                        catch (Throwable throwable2) {
                            if (throwable == null) {
                                throwable = throwable2;
                            } else if (throwable != throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                            if (openOutputStream == null) throw throwable;
                            openOutputStream.close();
                            throw throwable;
                        }
                        openOutputStream.close();
                    }
                    catch (Throwable throwable3) {
                        if (throwable == null) {
                            throwable = throwable3;
                            throw throwable;
                        }
                        if (throwable == throwable3) throw throwable;
                        throwable.addSuppressed(throwable3);
                        throw throwable;
                    }
                }
                catch (IOException e) {
                    this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
                }
            }
            this.logger.logClassFile(generateClasspathStructure, currentDestinationPath, relativeStringName);
            ++this.exportedClassFilesCounter;
            ++i;
        }
    }

    @Override
    protected void setPaths(ArrayList<String> bootclasspaths, String sourcepathClasspathArg, ArrayList<String> sourcepathClasspaths, ArrayList<String> classpaths, String modulePath, String moduleSourcepath, ArrayList<String> extdirsClasspaths, ArrayList<String> endorsedDirClasspaths, String customEncoding) {
        this.validateClasspathOptions(bootclasspaths, endorsedDirClasspaths, extdirsClasspaths);
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    protected void handleLocations() {
        fileSystemClasspaths = new ArrayList<FileSystem.Classpath>();
        eclipseJavaFileManager = null;
        standardJavaFileManager = null;
        javaFileManager = null;
        havePlatformPaths = false;
        haveClassPaths = false;
        if (this.fileManager instanceof EclipseFileManager) {
            eclipseJavaFileManager = (EclipseFileManager)this.fileManager;
        }
        if (this.fileManager instanceof StandardJavaFileManager) {
            standardJavaFileManager = (StandardJavaFileManager)this.fileManager;
        }
        javaFileManager = this.fileManager;
        if (eclipseJavaFileManager != null && (eclipseJavaFileManager.flags & 4) == 0 && (eclipseJavaFileManager.flags & 2) != 0) {
            fileSystemClasspaths.addAll(this.handleEndorseddirs(null));
        }
        locationFiles = null;
        if (standardJavaFileManager != null) {
            locationFiles = standardJavaFileManager.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
            if (locationFiles != null) {
                for (File file : locationFiles) {
                    if (file.isDirectory()) {
                        platformLocations = this.getPlatformLocations(fileSystemClasspaths, file);
                        if (standardJavaFileManager instanceof EclipseFileManager && platformLocations.size() == 1 && (jrt = platformLocations.get(0)) instanceof ClasspathJrt) {
                            try {
                                ((EclipseFileManager)standardJavaFileManager).locationHandler.newSystemLocation((JavaFileManager.Location)StandardLocation.SYSTEM_MODULES, (ClasspathJrt)jrt);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        fileSystemClasspaths.addAll(platformLocations);
                        break;
                    }
                    classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options, this.releaseVersion);
                    if (classpath == null) continue;
                    fileSystemClasspaths.add(classpath);
                    havePlatformPaths = true;
                }
            }
        } else if (javaFileManager != null) {
            javaHome = Util.getJavaHome();
            jdkLevel = Util.getJDKLevel(javaHome);
            if (jdkLevel >= 0x350000L) {
                system = null;
                if (this.releaseVersion != null && this.complianceLevel < jdkLevel) {
                    versionFromJdkLevel = CompilerOptions.versionFromJdkLevel(this.complianceLevel);
                    if (versionFromJdkLevel.length() >= 3) {
                        versionFromJdkLevel = versionFromJdkLevel.substring(2);
                    }
                    system = FileSystem.getOlderSystemRelease(javaHome.getAbsolutePath(), versionFromJdkLevel, null);
                } else {
                    system = FileSystem.getJrtClasspath(javaHome.toString(), null, null, null);
                }
                classpath = new ClasspathJsr199(system, this.fileManager, (JavaFileManager.Location)StandardLocation.PLATFORM_CLASS_PATH);
                fileSystemClasspaths.add(classpath);
            } else {
                classpath = new ClasspathJsr199(this.fileManager, StandardLocation.PLATFORM_CLASS_PATH);
                fileSystemClasspaths.add((FileSystem.Classpath)classpath);
            }
            havePlatformPaths = true;
        }
        if (eclipseJavaFileManager != null && (eclipseJavaFileManager.flags & 1) == 0 && (eclipseJavaFileManager.flags & 2) != 0) {
            fileSystemClasspaths.addAll(this.handleExtdirs(null));
        }
        if (standardJavaFileManager != null) {
            locationFiles = standardJavaFileManager.getLocation(StandardLocation.SOURCE_PATH);
            if (locationFiles != null) {
                for (File file : locationFiles) {
                    classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options, this.releaseVersion);
                    if (classpath == null) continue;
                    fileSystemClasspaths.add(classpath);
                }
            }
            if ((locationFiles = standardJavaFileManager.getLocation(StandardLocation.CLASS_PATH)) != null) {
                for (File file : locationFiles) {
                    classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options, this.releaseVersion);
                    if (classpath == null) continue;
                    fileSystemClasspaths.add(classpath);
                    haveClassPaths = true;
                }
            }
            if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0) {
                try {
                    locationAsPaths = standardJavaFileManager.getLocationAsPaths(StandardLocation.MODULE_SOURCE_PATH);
                    if (locationAsPaths != null) {
                        builder = new StringBuilder();
                        for (Path path : locationAsPaths) {
                            builder.append(path.toFile().getCanonicalPath());
                            builder.append(File.pathSeparator);
                        }
                        modulepaths = this.handleModuleSourcepath(builder.toString());
                        for (Object classpath : modulepaths) {
                            moduleNames = classpath.getModuleNames(null);
                            for (String modName : moduleNames) {
                                p = Paths.get(classpath.getPath(), new String[0]);
                                standardJavaFileManager.setLocationForModule(StandardLocation.MODULE_SOURCE_PATH, modName, Collections.singletonList(p));
                                p = Paths.get(classpath.getDestinationPath(), new String[0]);
                            }
                        }
                        fileSystemClasspaths.addAll(modulepaths);
                    }
                }
                catch (IllegalStateException v0) {
                }
                catch (IllegalArgumentException e) {
                    throw e;
                }
                catch (Exception e) {
                    this.logger.logException(e);
                }
                try {
                    locationFiles = standardJavaFileManager.getLocation(StandardLocation.MODULE_PATH);
                    if (locationFiles == null) ** GOTO lbl165
                    for (File file : locationFiles) {
                        try {
                            modulepaths = this.handleModulepath(file.getCanonicalPath());
                            for (Object classpath : modulepaths) {
                                moduleNames = classpath.getModuleNames(null);
                                for (String string : moduleNames) {
                                    path = Paths.get(classpath.getPath(), new String[0]);
                                    standardJavaFileManager.setLocationForModule(StandardLocation.MODULE_PATH, string, Collections.singletonList(path));
                                }
                            }
                            fileSystemClasspaths.addAll(modulepaths);
                        }
                        catch (IOException e) {
                            throw new AbortCompilationUnit(null, e, null);
                        }
                    }
                }
                catch (IllegalStateException v1) {
                }
                catch (IllegalArgumentException e) {
                    throw e;
                }
                catch (Exception e) {
                    this.logger.logException(e);
                }
            }
        } else if (javaFileManager != null) {
            classpath = null;
            if (this.fileManager.hasLocation(StandardLocation.SOURCE_PATH)) {
                classpath = new ClasspathJsr199(this.fileManager, StandardLocation.SOURCE_PATH);
                fileSystemClasspaths.add(classpath);
            }
            if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0) {
                if (this.fileManager.hasLocation(StandardLocation.UPGRADE_MODULE_PATH)) {
                    classpath = new ClasspathJsr199(this.fileManager, StandardLocation.UPGRADE_MODULE_PATH);
                }
                if (this.fileManager.hasLocation(StandardLocation.SYSTEM_MODULES)) {
                    classpath = new ClasspathJsr199(this.fileManager, StandardLocation.SYSTEM_MODULES);
                    fileSystemClasspaths.add(classpath);
                }
                if (this.fileManager.hasLocation(StandardLocation.PATCH_MODULE_PATH)) {
                    classpath = new ClasspathJsr199(this.fileManager, StandardLocation.PATCH_MODULE_PATH);
                    fileSystemClasspaths.add(classpath);
                }
                if (this.fileManager.hasLocation(StandardLocation.MODULE_SOURCE_PATH)) {
                    classpath = new ClasspathJsr199(this.fileManager, StandardLocation.MODULE_SOURCE_PATH);
                    fileSystemClasspaths.add(classpath);
                }
                if (this.fileManager.hasLocation(StandardLocation.MODULE_PATH)) {
                    classpath = new ClasspathJsr199(this.fileManager, StandardLocation.MODULE_PATH);
                    fileSystemClasspaths.add(classpath);
                }
            }
            classpath = new ClasspathJsr199(this.fileManager, StandardLocation.CLASS_PATH);
            fileSystemClasspaths.add(classpath);
            haveClassPaths = true;
        }
lbl165:
        // 7 sources

        if (this.checkedClasspaths == null) {
            if (!havePlatformPaths) {
                fileSystemClasspaths.addAll(this.handleBootclasspath(null, null));
            }
            if (!haveClassPaths) {
                fileSystemClasspaths.addAll(this.handleClasspath(null, null));
            }
        }
        if ((size = (fileSystemClasspaths = FileSystem.ClasspathNormalizer.normalize(fileSystemClasspaths)).size()) != 0) {
            this.checkedClasspaths = new FileSystem.Classpath[size];
            i = 0;
            for (FileSystem.Classpath classpath : fileSystemClasspaths) {
                this.checkedClasspaths[i++] = classpath;
            }
        }
    }

    protected List<FileSystem.Classpath> getPlatformLocations(ArrayList<FileSystem.Classpath> fileSystemClasspaths, File file) {
        List<FileSystem.Classpath> platformLibraries = Util.collectPlatformLibraries(file);
        return platformLibraries;
    }

    @Override
    protected void loggingExtraProblems() {
        super.loggingExtraProblems();
        for (final CategorizedProblem problem : this.extraProblems) {
            if (this.diagnosticListener == null || this.isIgnored(problem)) continue;
            Diagnostic<JavaFileObject> diagnostic = new Diagnostic<JavaFileObject>(){

                @Override
                public String getCode() {
                    return null;
                }

                @Override
                public long getColumnNumber() {
                    if (problem instanceof DefaultProblem) {
                        return ((DefaultProblem)problem).column;
                    }
                    return -1L;
                }

                @Override
                public long getEndPosition() {
                    if (problem instanceof DefaultProblem) {
                        return ((DefaultProblem)problem).getSourceEnd();
                    }
                    return -1L;
                }

                @Override
                public Diagnostic.Kind getKind() {
                    if (problem.isError()) {
                        return Diagnostic.Kind.ERROR;
                    }
                    if (problem.isWarning()) {
                        return Diagnostic.Kind.WARNING;
                    }
                    if (problem instanceof DefaultProblem && ((DefaultProblem)problem).isInfo()) {
                        return Diagnostic.Kind.NOTE;
                    }
                    return Diagnostic.Kind.OTHER;
                }

                @Override
                public long getLineNumber() {
                    if (problem instanceof DefaultProblem) {
                        return ((DefaultProblem)problem).getSourceLineNumber();
                    }
                    return -1L;
                }

                @Override
                public String getMessage(Locale locale) {
                    return problem.getMessage();
                }

                @Override
                public long getPosition() {
                    if (problem instanceof DefaultProblem) {
                        return ((DefaultProblem)problem).getSourceStart();
                    }
                    return -1L;
                }

                @Override
                public JavaFileObject getSource() {
                    if (problem instanceof DefaultProblem) {
                        char[] originatingName = ((DefaultProblem)problem).getOriginatingFileName();
                        if (originatingName == null) {
                            return null;
                        }
                        File f = new File(new String(originatingName));
                        if (f.exists()) {
                            Charset charset = EclipseCompilerImpl.this.fileManager instanceof EclipseFileManager ? ((EclipseFileManager)EclipseCompilerImpl.this.fileManager).charset : Charset.defaultCharset();
                            return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, charset);
                        }
                        return null;
                    }
                    return null;
                }

                @Override
                public long getStartPosition() {
                    return this.getPosition();
                }
            };
            this.diagnosticListener.report((Diagnostic<? super JavaFileObject>)diagnostic);
        }
    }

    class Jsr199ProblemWrapper
    extends DefaultProblem {
        DefaultProblem original;
        DiagnosticListener<? super JavaFileObject> listener;
        Diagnostic<JavaFileObject> diagnostic;

        public Jsr199ProblemWrapper(DefaultProblem original, Diagnostic<JavaFileObject> diagnostic, DiagnosticListener<? super JavaFileObject> listener) {
            super(original.getOriginatingFileName(), original.getMessage(), original.getID(), original.getArguments(), original.severity, original.getSourceStart(), original.getSourceEnd(), original.getSourceLineNumber(), original.column);
            this.original = original;
            this.listener = listener;
            this.diagnostic = diagnostic;
        }

        @Override
        public void reportError() {
            this.listener.report(this.diagnostic);
        }

        @Override
        public String[] getArguments() {
            return this.original.getArguments();
        }

        @Override
        public int getID() {
            return this.original.getID();
        }

        @Override
        public String getMessage() {
            return this.original.getMessage();
        }

        @Override
        public char[] getOriginatingFileName() {
            return this.original.getOriginatingFileName();
        }

        @Override
        public int getSourceEnd() {
            return this.original.getSourceEnd();
        }

        @Override
        public int getSourceLineNumber() {
            return this.original.getSourceLineNumber();
        }

        @Override
        public int getSourceStart() {
            return this.original.getSourceStart();
        }

        @Override
        public boolean isError() {
            return this.original.isError();
        }

        @Override
        public boolean isWarning() {
            return this.original.isWarning();
        }

        @Override
        public boolean isInfo() {
            return this.original.isInfo();
        }

        @Override
        public void setSourceEnd(int sourceEnd) {
            this.original.setSourceEnd(sourceEnd);
        }

        @Override
        public void setSourceLineNumber(int lineNumber) {
            this.original.setSourceLineNumber(lineNumber);
        }

        @Override
        public void setSourceStart(int sourceStart) {
            this.original.setSourceStart(sourceStart);
        }

        @Override
        public int getCategoryID() {
            return this.original.getCategoryID();
        }

        @Override
        public String getMarkerType() {
            return this.original.getMarkerType();
        }
    }
}

