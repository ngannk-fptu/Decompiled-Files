/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.BasicModule;
import org.eclipse.jdt.internal.compiler.batch.BatchCompilerRequestor;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileFinder;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.ModuleFinder;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRule;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.GenericXMLWriter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Main
implements ProblemSeverities,
SuffixConstants {
    private static final String ANNOTATION_SOURCE_CLASSPATH = "CLASSPATH";
    boolean enableJavadocOn;
    boolean warnJavadocOn;
    boolean warnAllJavadocOn;
    public Compiler batchCompiler;
    public ResourceBundle bundle;
    protected FileSystem.Classpath[] checkedClasspaths;
    protected IModule module;
    private String moduleVersion;
    protected List<String> annotationPaths;
    protected boolean annotationsFromClasspath;
    private List<String> addonExports = Collections.EMPTY_LIST;
    private List<String> addonReads = Collections.EMPTY_LIST;
    public Set<String> rootModules = Collections.EMPTY_SET;
    public Set<String> limitedModules;
    public Locale compilerLocale;
    public CompilerOptions compilerOptions;
    public CompilationProgress progress;
    public String destinationPath;
    public String[] destinationPaths;
    protected boolean enablePreview;
    protected String releaseVersion;
    private boolean didSpecifySource;
    private boolean didSpecifyTarget;
    public String[] encodings;
    public int exportedClassFilesCounter;
    public String[] filenames;
    public String[] modNames;
    public String[] classNames;
    public int globalErrorsCount;
    public int globalProblemsCount;
    public int globalTasksCount;
    public int globalWarningsCount;
    public int globalInfoCount;
    private File javaHomeCache;
    private boolean javaHomeChecked = false;
    private boolean primaryNullAnnotationsSeen = false;
    public long lineCount0;
    public String log;
    public Logger logger;
    public int maxProblems;
    public Map<String, String> options;
    protected long complianceLevel;
    public char[][] ignoreOptionalProblemsFromFolders;
    protected PrintWriter out;
    public boolean proceed = true;
    public boolean proceedOnError = false;
    public boolean failOnWarning = false;
    public boolean produceRefInfo = false;
    public int currentRepetition;
    public int maxRepetition;
    public boolean showProgress = false;
    public long startTime;
    public ArrayList<String> pendingErrors;
    public boolean systemExitWhenFinished = true;
    public static final int TIMING_DISABLED = 0;
    public static final int TIMING_ENABLED = 1;
    public static final int TIMING_DETAILED = 2;
    public int timing = 0;
    public CompilerStats[] compilerStats;
    public boolean verbose = false;
    private String[] expandedCommandLine;
    private PrintWriter err;
    protected ArrayList<CategorizedProblem> extraProblems;
    public static final String bundleName = "org.eclipse.jdt.internal.compiler.batch.messages";
    public static final int DEFAULT_SIZE_CLASSPATH = 4;
    public static final String NONE = "none";

    public static boolean compile(String commandLine) {
        return new Main(new PrintWriter(System.out), new PrintWriter(System.err), false, null, null).compile(Main.tokenize(commandLine));
    }

    public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter) {
        return new Main(outWriter, errWriter, false, null, null).compile(Main.tokenize(commandLine));
    }

    public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress) {
        return new Main(outWriter, errWriter, false, null, progress).compile(commandLineArguments);
    }

    public static File[][] getLibrariesFiles(File[] files) {
        FilenameFilter filter = new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return Util.archiveFormat(name) > -1;
            }
        };
        int filesLength = files.length;
        File[][] result = new File[filesLength][];
        int i = 0;
        while (i < filesLength) {
            File currentFile = files[i];
            if (currentFile.exists() && currentFile.isDirectory()) {
                result[i] = currentFile.listFiles(filter);
            }
            ++i;
        }
        return result;
    }

    public static void main(String[] argv) {
        new Main(new PrintWriter(System.out), new PrintWriter(System.err), true, null, null).compile(argv);
    }

    public static String[] tokenize(String commandLine) {
        int count = 0;
        String[] arguments = new String[10];
        StringTokenizer tokenizer = new StringTokenizer(commandLine, " \"", true);
        String token = Util.EMPTY_STRING;
        boolean insideQuotes = false;
        boolean startNewToken = true;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.equals(" ")) {
                if (insideQuotes) {
                    int n = count - 1;
                    arguments[n] = String.valueOf(arguments[n]) + token;
                    startNewToken = false;
                    continue;
                }
                startNewToken = true;
                continue;
            }
            if (token.equals("\"")) {
                if (!insideQuotes && startNewToken) {
                    if (count == arguments.length) {
                        String[] stringArray = arguments;
                        arguments = new String[count * 2];
                        System.arraycopy(stringArray, 0, arguments, 0, count);
                    }
                    arguments[count++] = Util.EMPTY_STRING;
                }
                insideQuotes = !insideQuotes;
                startNewToken = false;
                continue;
            }
            if (insideQuotes) {
                int n = count - 1;
                arguments[n] = String.valueOf(arguments[n]) + token;
            } else if (token.length() > 0 && !startNewToken) {
                int n = count - 1;
                arguments[n] = String.valueOf(arguments[n]) + token;
            } else {
                String trimmedToken;
                if (count == arguments.length) {
                    String[] stringArray = arguments;
                    arguments = new String[count * 2];
                    System.arraycopy(stringArray, 0, arguments, 0, count);
                }
                if ((trimmedToken = token.trim()).length() != 0) {
                    arguments[count++] = trimmedToken;
                }
            }
            startNewToken = false;
        }
        String[] stringArray = arguments;
        arguments = new String[count];
        System.arraycopy(stringArray, 0, arguments, 0, count);
        return arguments;
    }

    public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished) {
        this(outWriter, errWriter, systemExitWhenFinished, null, null);
    }

    public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions) {
        this(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null);
    }

    public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        this.initialize(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
        this.relocalize();
    }

    public void addExtraProblems(CategorizedProblem problem) {
        if (this.extraProblems == null) {
            this.extraProblems = new ArrayList();
        }
        this.extraProblems.add(problem);
    }

    protected void addNewEntry(ArrayList<FileSystem.Classpath> paths, String currentClasspathName, ArrayList<String> currentRuleSpecs, String customEncoding, String destPath, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
        int rulesSpecsSize = currentRuleSpecs.size();
        AccessRuleSet accessRuleSet = null;
        if (rulesSpecsSize != 0) {
            AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
            boolean rulesOK = true;
            Iterator<String> i = currentRuleSpecs.iterator();
            int j = 0;
            while (i.hasNext()) {
                String ruleSpec = i.next();
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
                if (currentClasspathName.length() != 0) {
                    this.addPendingErrors(this.bind("configure.incorrectClasspath", currentClasspathName));
                }
                return;
            }
        }
        if (NONE.equals(destPath)) {
            destPath = NONE;
        }
        if (rejectDestinationPathOnJars && destPath != null && Util.archiveFormat(currentClasspathName) > -1) {
            throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntryFile", currentClasspathName));
        }
        FileSystem.Classpath currentClasspath = FileSystem.getClasspath(currentClasspathName, customEncoding, isSourceOnly, accessRuleSet, destPath, this.options, this.releaseVersion);
        if (currentClasspath != null) {
            paths.add(currentClasspath);
        } else if (currentClasspathName.length() != 0) {
            this.addPendingErrors(this.bind("configure.incorrectClasspath", currentClasspathName));
        }
    }

    void addPendingErrors(String message) {
        if (this.pendingErrors == null) {
            this.pendingErrors = new ArrayList();
        }
        this.pendingErrors.add(message);
    }

    public String bind(String id) {
        return this.bind(id, (String[])null);
    }

    public String bind(String id, String binding) {
        return this.bind(id, new String[]{binding});
    }

    public String bind(String id, String binding1, String binding2) {
        return this.bind(id, new String[]{binding1, binding2});
    }

    public String bind(String id, String[] arguments) {
        if (id == null) {
            return "No message available";
        }
        String message = null;
        try {
            message = this.bundle.getString(id);
        }
        catch (MissingResourceException missingResourceException) {
            return "Missing message: " + id + " in: " + bundleName;
        }
        return MessageFormat.format(message, arguments);
    }

    private boolean checkVMVersion(long minimalSupportedVersion) {
        int majorVersion;
        String classFileVersion = System.getProperty("java.class.version");
        if (classFileVersion == null) {
            return false;
        }
        int index = classFileVersion.indexOf(46);
        if (index == -1) {
            return false;
        }
        try {
            majorVersion = Integer.parseInt(classFileVersion.substring(0, index));
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
        return ClassFileConstants.getComplianceLevelForJavaVersion(majorVersion) >= minimalSupportedVersion;
    }

    public boolean compile(String[] argv) {
        try {
            try {
                this.configure(argv);
                if (this.progress != null) {
                    this.progress.begin(this.filenames == null ? 0 : this.filenames.length * this.maxRepetition);
                }
                if (this.proceed) {
                    if (this.showProgress) {
                        this.logger.compiling();
                    }
                    this.currentRepetition = 0;
                    while (this.currentRepetition < this.maxRepetition) {
                        this.globalProblemsCount = 0;
                        this.globalErrorsCount = 0;
                        this.globalWarningsCount = 0;
                        this.globalInfoCount = 0;
                        this.globalTasksCount = 0;
                        this.exportedClassFilesCounter = 0;
                        if (this.maxRepetition > 1) {
                            this.logger.flush();
                            this.logger.logRepetition(this.currentRepetition, this.maxRepetition);
                        }
                        this.performCompilation();
                        ++this.currentRepetition;
                    }
                    if (this.compilerStats != null) {
                        this.logger.logAverage();
                    }
                    if (this.showProgress) {
                        this.logger.printNewLine();
                    }
                }
                if (this.systemExitWhenFinished) {
                    this.logger.flush();
                    this.logger.close();
                    if (this.failOnWarning && this.globalWarningsCount > 0) {
                        System.exit(-1);
                    }
                    System.exit(this.globalErrorsCount > 0 ? -1 : 0);
                }
            }
            catch (Exception e) {
                this.logger.logException(e);
                if (this.systemExitWhenFinished) {
                    this.logger.flush();
                    this.logger.close();
                    System.exit(-1);
                }
                this.logger.flush();
                this.logger.close();
                if (this.progress != null) {
                    this.progress.done();
                }
                return false;
            }
        }
        finally {
            this.logger.flush();
            this.logger.close();
            if (this.progress != null) {
                this.progress.done();
            }
        }
        if (this.progress == null || !this.progress.isCanceled()) {
            if (this.failOnWarning && this.globalWarningsCount > 0) {
                return false;
            }
            if (this.globalErrorsCount == 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    public void configure(String[] argv) {
        if (argv == null || argv.length == 0) {
            this.printUsage();
            return;
        }
        bootclasspaths = new ArrayList<String>(4);
        sourcepathClasspathArg = null;
        modulepathArg = null;
        moduleSourcepathArg = null;
        sourcepathClasspaths = new ArrayList<String>(4);
        classpaths = new ArrayList<String>(4);
        extdirsClasspaths = null;
        endorsedDirClasspaths = null;
        this.annotationPaths = null;
        this.annotationsFromClasspath = false;
        index = -1;
        filesCount = 0;
        classCount = 0;
        argCount = argv.length;
        mode = 0;
        this.maxRepetition = 0;
        printUsageRequired = false;
        usageSection = null;
        printVersionRequired = false;
        didSpecifyDeprecation = false;
        didSpecifyCompliance = false;
        didSpecifyDisabledAnnotationProcessing = false;
        customEncoding = null;
        customDestinationPath = null;
        currentSourceDirectory = null;
        currentArg = Util.EMPTY_STRING;
        moduleName = null;
        specifiedEncodings = null;
        needExpansion = false;
        i = 0;
        while (i < argCount) {
            if (argv[i].startsWith("@")) {
                needExpansion = true;
                break;
            }
            ++i;
        }
        newCommandLineArgs = null;
        if (needExpansion) {
            newCommandLineArgs = new String[argCount];
            index = 0;
            i = 0;
            while (i < argCount) {
                newArgs = null;
                arg = argv[i].trim();
                if (arg.startsWith("@")) {
                    try {
                        reader = new LineNumberReader(new StringReader(new String(Util.getFileCharContent(new File(arg.substring(1)), null))));
                        buffer = new StringBuffer();
                        while ((line = reader.readLine()) != null) {
                            if ((line = line.trim()).startsWith("#")) continue;
                            buffer.append(line).append(" ");
                        }
                        newArgs = Main.tokenize(buffer.toString());
                    }
                    catch (IOException v0) {
                        throw new IllegalArgumentException(this.bind("configure.invalidexpansionargumentname", arg));
                    }
                }
                if (newArgs != null) {
                    newCommandLineArgsLength = newCommandLineArgs.length;
                    newArgsLength = newArgs.length;
                    v1 = newCommandLineArgs;
                    newCommandLineArgs = new String[newCommandLineArgsLength + newArgsLength - 1];
                    System.arraycopy(v1, 0, newCommandLineArgs, 0, index);
                    System.arraycopy(newArgs, 0, newCommandLineArgs, index, newArgsLength);
                    index += newArgsLength;
                } else {
                    newCommandLineArgs[index++] = arg;
                }
                ++i;
            }
            index = -1;
        } else {
            newCommandLineArgs = argv;
            i = 0;
            while (i < argCount) {
                newCommandLineArgs[i] = newCommandLineArgs[i].trim();
                ++i;
            }
        }
        argCount = newCommandLineArgs.length;
        this.expandedCommandLine = newCommandLineArgs;
        block74: while (++index < argCount) {
            if (customEncoding != null) {
                throw new IllegalArgumentException(this.bind("configure.unexpectedCustomEncoding", currentArg, customEncoding));
            }
            currentArg = newCommandLineArgs[index];
            switch (mode) {
                case 0: {
                    if (currentArg.startsWith("-nowarn")) {
                        switch (currentArg.length()) {
                            case 7: {
                                this.disableAll(0);
                                break;
                            }
                            case 8: {
                                throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                            }
                            default: {
                                foldersStart = currentArg.indexOf(91) + 1;
                                foldersEnd = currentArg.lastIndexOf(93);
                                if (foldersStart <= 8 || foldersEnd == -1 || foldersStart > foldersEnd || foldersEnd < currentArg.length() - 1) {
                                    throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                                }
                                folders = currentArg.substring(foldersStart, foldersEnd);
                                if (folders.length() > 0) {
                                    currentFolders = Main.decodeIgnoreOptionalProblemsFromFolders(folders);
                                    if (this.ignoreOptionalProblemsFromFolders != null) {
                                        length = this.ignoreOptionalProblemsFromFolders.length + currentFolders.length;
                                        tempFolders = new char[length][];
                                        System.arraycopy(this.ignoreOptionalProblemsFromFolders, 0, tempFolders, 0, this.ignoreOptionalProblemsFromFolders.length);
                                        System.arraycopy(currentFolders, 0, tempFolders, this.ignoreOptionalProblemsFromFolders.length, currentFolders.length);
                                        this.ignoreOptionalProblemsFromFolders = tempFolders;
                                        break;
                                    }
                                    this.ignoreOptionalProblemsFromFolders = currentFolders;
                                    break;
                                }
                                throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                            }
                        }
                        mode = 0;
                        break;
                    }
                    if (currentArg.startsWith("[")) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                    }
                    if (currentArg.endsWith("]")) {
                        encodingStart = currentArg.indexOf(91) + 1;
                        if (encodingStart <= 1) {
                            throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                        }
                        encodingEnd = currentArg.length() - 1;
                        if (encodingStart >= 1) {
                            if (encodingStart < encodingEnd) {
                                customEncoding = currentArg.substring(encodingStart, encodingEnd);
                                try {
                                    new InputStreamReader((InputStream)new ByteArrayInputStream(new byte[0]), customEncoding);
                                }
                                catch (UnsupportedEncodingException e) {
                                    throw new IllegalArgumentException(this.bind("configure.unsupportedEncoding", customEncoding), e);
                                }
                            }
                            currentArg = currentArg.substring(0, encodingStart - 1);
                        }
                    }
                    if (currentArg.endsWith(".java")) {
                        if (moduleName == null && (mod = this.extractModuleDesc(currentArg)) != null) {
                            moduleName = new String(mod.name());
                            this.module = mod;
                        }
                        if (this.filenames == null) {
                            this.filenames = new String[argCount - index];
                            this.encodings = new String[argCount - index];
                            this.modNames = new String[argCount - index];
                            this.destinationPaths = new String[argCount - index];
                        } else if (filesCount == this.filenames.length) {
                            length = this.filenames.length;
                            this.filenames = new String[length + argCount - index];
                            System.arraycopy(this.filenames, 0, this.filenames, 0, length);
                            this.encodings = new String[length + argCount - index];
                            System.arraycopy(this.encodings, 0, this.encodings, 0, length);
                            this.destinationPaths = new String[length + argCount - index];
                            System.arraycopy(this.destinationPaths, 0, this.destinationPaths, 0, length);
                            this.modNames = new String[length + argCount - index];
                            System.arraycopy(this.modNames, 0, this.modNames, 0, length);
                        }
                        this.filenames[filesCount] = currentArg;
                        this.modNames[filesCount] = moduleName;
                        this.encodings[filesCount++] = customEncoding;
                        customEncoding = null;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-log")) {
                        if (this.log != null) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateLog", currentArg));
                        }
                        mode = 5;
                        break;
                    }
                    if (currentArg.equals("-repeat")) {
                        if (this.maxRepetition > 0) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateRepeat", currentArg));
                        }
                        mode = 6;
                        break;
                    }
                    if (currentArg.equals("-maxProblems")) {
                        if (this.maxProblems > 0) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateMaxProblems", currentArg));
                        }
                        mode = 11;
                        break;
                    }
                    if (currentArg.equals("--release")) {
                        mode = 30;
                        break;
                    }
                    if (currentArg.equals("-source")) {
                        mode = 7;
                        break;
                    }
                    if (currentArg.equals("-encoding")) {
                        mode = 8;
                        break;
                    }
                    if (currentArg.startsWith("-") && (version = this.optionStringToVersion(currentArg.substring(1))) != null) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateCompliance", currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put("org.eclipse.jdt.core.compiler.compliance", version);
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-15") || currentArg.equals("-15.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateCompliance", currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put("org.eclipse.jdt.core.compiler.compliance", "15");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-16") || currentArg.equals("-16.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateCompliance", currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put("org.eclipse.jdt.core.compiler.compliance", "16");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-d")) {
                        if (this.destinationPath != null) {
                            errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if (index + 1 < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(this.bind("configure.duplicateOutputPath", errorMessage.toString()));
                        }
                        mode = 3;
                        break;
                    }
                    if (currentArg.equals("-classpath") || currentArg.equals("-cp")) {
                        mode = 1;
                        break;
                    }
                    if (currentArg.equals("-bootclasspath")) {
                        if (bootclasspaths.size() > 0) {
                            errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if (index + 1 < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(this.bind("configure.duplicateBootClasspath", errorMessage.toString()));
                        }
                        mode = 9;
                        break;
                    }
                    if (currentArg.equals("--enable-preview")) {
                        this.enablePreview = true;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("--system")) {
                        mode = 27;
                        break;
                    }
                    if (currentArg.equals("--module-path") || currentArg.equals("-p") || currentArg.equals("--processor-module-path")) {
                        mode = 23;
                        break;
                    }
                    if (currentArg.equals("--module-source-path")) {
                        if (sourcepathClasspathArg != null) {
                            throw new IllegalArgumentException(this.bind("configure.OneOfModuleOrSourcePath"));
                        }
                        mode = 24;
                        break;
                    }
                    if (currentArg.equals("--add-exports")) {
                        mode = 25;
                        break;
                    }
                    if (currentArg.equals("--add-reads")) {
                        mode = 26;
                        break;
                    }
                    if (currentArg.equals("--add-modules")) {
                        mode = 29;
                        break;
                    }
                    if (currentArg.equals("--limit-modules")) {
                        mode = 31;
                        break;
                    }
                    if (currentArg.equals("--module-version")) {
                        mode = 32;
                        break;
                    }
                    if (currentArg.equals("-sourcepath")) {
                        if (sourcepathClasspathArg != null) {
                            errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if (index + 1 < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(this.bind("configure.duplicateSourcepath", errorMessage.toString()));
                        }
                        if (moduleSourcepathArg != null) {
                            throw new IllegalArgumentException(this.bind("configure.OneOfModuleOrSourcePath"));
                        }
                        mode = 13;
                        break;
                    }
                    if (currentArg.equals("-extdirs")) {
                        if (extdirsClasspaths != null) {
                            errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if (index + 1 < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(this.bind("configure.duplicateExtDirs", errorMessage.toString()));
                        }
                        mode = 12;
                        break;
                    }
                    if (currentArg.equals("-endorseddirs")) {
                        if (endorsedDirClasspaths != null) {
                            errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if (index + 1 < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(this.bind("configure.duplicateEndorsedDirs", errorMessage.toString()));
                        }
                        mode = 15;
                        break;
                    }
                    if (currentArg.equals("-progress")) {
                        mode = 0;
                        this.showProgress = true;
                        break;
                    }
                    if (!currentArg.startsWith("-proceedOnError")) ** GOTO lbl323
                    mode = 0;
                    length = currentArg.length();
                    if (length <= 15) ** GOTO lbl319
                    if (currentArg.equals("-proceedOnError:Fatal")) {
                        this.options.put("org.eclipse.jdt.core.compiler.problem.fatalOptionalError", "enabled");
                    } else {
                        throw new IllegalArgumentException(this.bind("configure.invalidWarningConfiguration", currentArg));
lbl319:
                        // 1 sources

                        this.options.put("org.eclipse.jdt.core.compiler.problem.fatalOptionalError", "disabled");
                    }
                    this.proceedOnError = true;
                    break;
lbl323:
                    // 1 sources

                    if (currentArg.equals("-failOnWarning")) {
                        mode = 0;
                        this.failOnWarning = true;
                        break;
                    }
                    if (currentArg.equals("-time")) {
                        mode = 0;
                        this.timing = 1;
                        break;
                    }
                    if (currentArg.equals("-time:detail")) {
                        mode = 0;
                        this.timing = 3;
                        break;
                    }
                    if (currentArg.equals("-version") || currentArg.equals("-v")) {
                        this.logger.logVersion(true);
                        this.proceed = false;
                        return;
                    }
                    if (currentArg.equals("-showversion")) {
                        printVersionRequired = true;
                        mode = 0;
                        break;
                    }
                    if ("-deprecation".equals(currentArg)) {
                        didSpecifyDeprecation = true;
                        this.options.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-help") || currentArg.equals("-?")) {
                        printUsageRequired = true;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-help:warn") || currentArg.equals("-?:warn")) {
                        printUsageRequired = true;
                        usageSection = "misc.usage.warn";
                        break;
                    }
                    if (currentArg.equals("-noExit")) {
                        this.systemExitWhenFinished = false;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-verbose")) {
                        this.verbose = true;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-referenceInfo")) {
                        this.produceRefInfo = true;
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-inlineJSR")) {
                        mode = 0;
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode", "enabled");
                        break;
                    }
                    if (currentArg.equals("-parameters")) {
                        mode = 0;
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.methodParameters", "generate");
                        break;
                    }
                    if (currentArg.equals("-genericsignature")) {
                        mode = 0;
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.lambda.genericSignature", "generate");
                        break;
                    }
                    if (currentArg.startsWith("-g")) {
                        mode = 0;
                        debugOption = currentArg;
                        length = currentArg.length();
                        if (length == 2) {
                            this.options.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                            this.options.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                            this.options.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
                            break;
                        }
                        if (length > 3) {
                            this.options.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
                            this.options.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
                            this.options.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
                            if (length == 7 && debugOption.equals("-g:none")) continue block74;
                            tokenizer = new StringTokenizer(debugOption.substring(3, debugOption.length()), ",");
                            while (tokenizer.hasMoreTokens()) {
                                token = tokenizer.nextToken();
                                if (token.equals("vars")) {
                                    this.options.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                                    continue;
                                }
                                if (token.equals("lines")) {
                                    this.options.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                                    continue;
                                }
                                if (token.equals("source")) {
                                    this.options.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
                                    continue;
                                }
                                throw new IllegalArgumentException(this.bind("configure.invalidDebugOption", debugOption));
                            }
                            continue block74;
                        }
                        throw new IllegalArgumentException(this.bind("configure.invalidDebugOption", debugOption));
                    }
                    if (currentArg.startsWith("-info")) {
                        mode = 0;
                        infoOption = currentArg;
                        length = currentArg.length();
                        if (length == 10 && infoOption.equals("-info:none")) {
                            this.disableAll(1024);
                            break;
                        }
                        if (length <= 6) {
                            throw new IllegalArgumentException(this.bind("configure.invalidInfoConfiguration", infoOption));
                        }
                        switch (infoOption.charAt(6)) {
                            case '+': {
                                infoTokenStart = 7;
                                isEnabling = true;
                                break;
                            }
                            case '-': {
                                infoTokenStart = 7;
                                isEnabling = false;
                                break;
                            }
                            default: {
                                this.disableAll(1024);
                                infoTokenStart = 6;
                                isEnabling = true;
                            }
                        }
                        tokenizer = new StringTokenizer(infoOption.substring(infoTokenStart, infoOption.length()), ",");
                        tokenCounter = 0;
                        while (tokenizer.hasMoreTokens()) {
                            token = tokenizer.nextToken();
                            ++tokenCounter;
                            switch (token.charAt(0)) {
                                case '+': {
                                    isEnabling = true;
                                    token = token.substring(1);
                                    break;
                                }
                                case '-': {
                                    isEnabling = false;
                                    token = token.substring(1);
                                }
                            }
                            this.handleInfoToken(token, isEnabling);
                        }
                        if (tokenCounter != 0) continue block74;
                        throw new IllegalArgumentException(this.bind("configure.invalidInfoOption", currentArg));
                    }
                    if (currentArg.startsWith("-warn")) {
                        mode = 0;
                        warningOption = currentArg;
                        length = currentArg.length();
                        if (length == 10 && warningOption.equals("-warn:none")) {
                            this.disableAll(0);
                            break;
                        }
                        if (length <= 6) {
                            throw new IllegalArgumentException(this.bind("configure.invalidWarningConfiguration", warningOption));
                        }
                        switch (warningOption.charAt(6)) {
                            case '+': {
                                warnTokenStart = 7;
                                isEnabling = true;
                                break;
                            }
                            case '-': {
                                warnTokenStart = 7;
                                isEnabling = false;
                                break;
                            }
                            default: {
                                this.disableAll(0);
                                warnTokenStart = 6;
                                isEnabling = true;
                            }
                        }
                        tokenizer = new StringTokenizer(warningOption.substring(warnTokenStart, warningOption.length()), ",");
                        tokenCounter = 0;
                        if (didSpecifyDeprecation) {
                            this.options.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
                        }
                        while (tokenizer.hasMoreTokens()) {
                            token = tokenizer.nextToken();
                            ++tokenCounter;
                            switch (token.charAt(0)) {
                                case '+': {
                                    isEnabling = true;
                                    token = token.substring(1);
                                    break;
                                }
                                case '-': {
                                    isEnabling = false;
                                    token = token.substring(1);
                                }
                            }
                            this.handleWarningToken(token, isEnabling);
                        }
                        if (tokenCounter != 0) continue block74;
                        throw new IllegalArgumentException(this.bind("configure.invalidWarningOption", currentArg));
                    }
                    if (currentArg.startsWith("-err")) {
                        mode = 0;
                        errorOption = currentArg;
                        length = currentArg.length();
                        if (length <= 5) {
                            throw new IllegalArgumentException(this.bind("configure.invalidErrorConfiguration", errorOption));
                        }
                        switch (errorOption.charAt(5)) {
                            case '+': {
                                errorTokenStart = 6;
                                isEnabling = true;
                                break;
                            }
                            case '-': {
                                errorTokenStart = 6;
                                isEnabling = false;
                                break;
                            }
                            default: {
                                this.disableAll(1);
                                errorTokenStart = 5;
                                isEnabling = true;
                            }
                        }
                        tokenizer = new StringTokenizer(errorOption.substring(errorTokenStart, errorOption.length()), ",");
                        tokenCounter = 0;
                        while (tokenizer.hasMoreTokens()) {
                            token = tokenizer.nextToken();
                            ++tokenCounter;
                            switch (token.charAt(0)) {
                                case '+': {
                                    isEnabling = true;
                                    token = token.substring(1);
                                    break;
                                }
                                case '-': {
                                    isEnabling = false;
                                    token = token.substring(1);
                                }
                            }
                            this.handleErrorToken(token, isEnabling);
                        }
                        if (tokenCounter != 0) continue block74;
                        throw new IllegalArgumentException(this.bind("configure.invalidErrorOption", currentArg));
                    }
                    if (currentArg.equals("-target")) {
                        mode = 4;
                        break;
                    }
                    if (currentArg.equals("-preserveAllLocals")) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.unusedLocal", "preserve");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-enableJavadoc")) {
                        mode = 0;
                        this.enableJavadocOn = true;
                        break;
                    }
                    if (currentArg.equals("-Xemacs")) {
                        mode = 0;
                        this.logger.setEmacs();
                        break;
                    }
                    if (currentArg.startsWith("-A")) {
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-processorpath")) {
                        mode = 17;
                        break;
                    }
                    if (currentArg.equals("-processor")) {
                        mode = 18;
                        break;
                    }
                    if (currentArg.equals("--processor-module-path")) {
                        mode = 28;
                        break;
                    }
                    if (currentArg.equals("-proc:only")) {
                        this.options.put("org.eclipse.jdt.core.compiler.generateClassFiles", "disabled");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-proc:none")) {
                        didSpecifyDisabledAnnotationProcessing = true;
                        this.options.put("org.eclipse.jdt.core.compiler.processAnnotations", "disabled");
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-s")) {
                        mode = 19;
                        break;
                    }
                    if (currentArg.equals("-XprintProcessorInfo") || currentArg.equals("-XprintRounds")) {
                        mode = 0;
                        break;
                    }
                    if (currentArg.startsWith("-X")) {
                        mode = 0;
                        break;
                    }
                    if (currentArg.startsWith("-J")) {
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-O")) {
                        mode = 0;
                        break;
                    }
                    if (currentArg.equals("-classNames")) {
                        mode = 20;
                        break;
                    }
                    if (currentArg.equals("-properties")) {
                        mode = 21;
                        break;
                    }
                    if (currentArg.equals("-missingNullDefault")) {
                        this.options.put("org.eclipse.jdt.core.compiler.annotation.missingNonNullByDefaultAnnotation", "warning");
                        break;
                    }
                    if (currentArg.equals("-annotationpath")) {
                        mode = 22;
                        break;
                    }
                    ** GOTO lbl855
                }
                case 4: {
                    if (this.didSpecifyTarget) {
                        throw new IllegalArgumentException(this.bind("configure.duplicateTarget", currentArg));
                    }
                    if (this.releaseVersion != null) {
                        throw new IllegalArgumentException(this.bind("configure.unsupportedWithRelease", "-target"));
                    }
                    this.didSpecifyTarget = true;
                    if (currentArg.equals("1.1")) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.1");
                    } else if (currentArg.equals("1.2")) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2");
                    } else if (currentArg.equals("jsr14")) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "jsr14");
                    } else if (currentArg.equals("cldc1.1")) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "cldc1.1");
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode", "enabled");
                    } else {
                        version = this.optionStringToVersion(currentArg);
                        if (version != null) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", version);
                        } else {
                            throw new IllegalArgumentException(this.bind("configure.targetJDK", currentArg));
                        }
                    }
                    mode = 0;
                    break;
                }
                case 5: {
                    this.log = currentArg;
                    mode = 0;
                    break;
                }
                case 6: {
                    try {
                        this.maxRepetition = Integer.parseInt(currentArg);
                        if (this.maxRepetition <= 0) {
                            throw new IllegalArgumentException(this.bind("configure.repetition", currentArg));
                        }
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException(this.bind("configure.repetition", currentArg), e);
                    }
                    mode = 0;
                    break;
                }
                case 11: {
                    try {
                        this.maxProblems = Integer.parseInt(currentArg);
                        if (this.maxProblems <= 0) {
                            throw new IllegalArgumentException(this.bind("configure.maxProblems", currentArg));
                        }
                        this.options.put("org.eclipse.jdt.core.compiler.maxProblemPerUnit", currentArg);
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException(this.bind("configure.maxProblems", currentArg), e);
                    }
                    mode = 0;
                    break;
                }
                case 30: {
                    this.releaseVersion = currentArg;
                    releaseToJDKLevel = CompilerOptions.releaseToJDKLevel(currentArg);
                    if (releaseToJDKLevel == 0L) {
                        throw new IllegalArgumentException(this.bind("configure.unsupportedReleaseVersion", currentArg));
                    }
                    this.complianceLevel = releaseToJDKLevel;
                    versionAsString = CompilerOptions.versionFromJdkLevel(releaseToJDKLevel);
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", versionAsString);
                    this.options.put("org.eclipse.jdt.core.compiler.source", versionAsString);
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", versionAsString);
                    mode = 0;
                    break;
                }
                case 7: {
                    if (this.didSpecifySource) {
                        throw new IllegalArgumentException(this.bind("configure.duplicateSource", currentArg));
                    }
                    if (this.releaseVersion != null) {
                        throw new IllegalArgumentException(this.bind("configure.unsupportedWithRelease", "-source"));
                    }
                    this.didSpecifySource = true;
                    version = this.optionStringToVersion(currentArg);
                    if (version == null) {
                        throw new IllegalArgumentException(this.bind("configure.source", currentArg));
                    }
                    this.options.put("org.eclipse.jdt.core.compiler.source", version);
                    mode = 0;
                    break;
                }
                case 8: {
                    if (specifiedEncodings != null) {
                        if (!specifiedEncodings.contains(currentArg)) {
                            if (specifiedEncodings.size() > 1) {
                                this.logger.logWarning(this.bind("configure.differentencodings", currentArg, Main.getAllEncodings(specifiedEncodings)));
                            } else {
                                this.logger.logWarning(this.bind("configure.differentencoding", currentArg, Main.getAllEncodings(specifiedEncodings)));
                            }
                        }
                    } else {
                        specifiedEncodings = new HashSet<String>();
                    }
                    try {
                        new InputStreamReader((InputStream)new ByteArrayInputStream(new byte[0]), currentArg);
                    }
                    catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException(this.bind("configure.unsupportedEncoding", currentArg), e);
                    }
                    specifiedEncodings.add(currentArg);
                    this.options.put("org.eclipse.jdt.core.encoding", currentArg);
                    mode = 0;
                    break;
                }
                case 3: {
                    this.setDestinationPath(currentArg.equals("none") != false ? "none" : currentArg);
                    mode = 0;
                    break;
                }
                case 27: {
                    mode = 0;
                    this.setJavaHome(currentArg);
                    break;
                }
                case 23: {
                    mode = 0;
                    modulepaths = new String[1];
                    index += this.processPaths(newCommandLineArgs, index, currentArg, modulepaths);
                    modulepathArg = modulepaths[0];
                    break;
                }
                case 24: {
                    mode = 0;
                    moduleSourcepaths = new String[1];
                    index += this.processPaths(newCommandLineArgs, index, currentArg, moduleSourcepaths);
                    moduleSourcepathArg = moduleSourcepaths[0];
                    break;
                }
                case 25: {
                    mode = 0;
                    if (this.addonExports == Collections.EMPTY_LIST) {
                        this.addonExports = new ArrayList<String>();
                    }
                    this.addonExports.add(currentArg);
                    break;
                }
                case 26: {
                    mode = 0;
                    if (this.addonReads == Collections.EMPTY_LIST) {
                        this.addonReads = new ArrayList<String>();
                    }
                    this.addonReads.add(currentArg);
                    break;
                }
                case 29: {
                    mode = 0;
                    if (this.rootModules == Collections.EMPTY_SET) {
                        this.rootModules = new HashSet<String>();
                    }
                    tokenizer = new StringTokenizer(currentArg, ",");
                    while (tokenizer.hasMoreTokens()) {
                        this.rootModules.add(tokenizer.nextToken().trim());
                    }
                    continue block74;
                }
                case 31: {
                    mode = 0;
                    tokenizer = new StringTokenizer(currentArg, ",");
                    while (tokenizer.hasMoreTokens()) {
                        if (this.limitedModules == null) {
                            this.limitedModules = new HashSet<String>();
                        }
                        this.limitedModules.add(tokenizer.nextToken().trim());
                    }
                    continue block74;
                }
                case 32: {
                    mode = 0;
                    this.moduleVersion = this.validateModuleVersion(currentArg);
                    break;
                }
                case 1: {
                    mode = 0;
                    index += this.processPaths(newCommandLineArgs, index, currentArg, classpaths);
                    break;
                }
                case 9: {
                    mode = 0;
                    index += this.processPaths(newCommandLineArgs, index, currentArg, bootclasspaths);
                    break;
                }
                case 13: {
                    mode = 0;
                    sourcePaths = new String[1];
                    index += this.processPaths(newCommandLineArgs, index, currentArg, sourcePaths);
                    sourcepathClasspathArg = sourcePaths[0];
                    break;
                }
                case 12: {
                    if (currentArg.indexOf("[-d") != -1) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntry", "-extdir"));
                    }
                    tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
                    extdirsClasspaths = new ArrayList<String>(4);
                    while (tokenizer.hasMoreTokens()) {
                        extdirsClasspaths.add(tokenizer.nextToken());
                    }
                    mode = 0;
                    break;
                }
                case 15: {
                    if (currentArg.indexOf("[-d") != -1) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntry", "-endorseddirs"));
                    }
                    tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
                    endorsedDirClasspaths = new ArrayList<String>(4);
                    while (tokenizer.hasMoreTokens()) {
                        endorsedDirClasspaths.add(tokenizer.nextToken());
                    }
                    mode = 0;
                    break;
                }
                case 16: {
                    if (!currentArg.endsWith("]")) ** GOTO lbl811
                    customDestinationPath = currentArg.substring(0, currentArg.length() - 1);
                    ** GOTO lbl855
lbl811:
                    // 1 sources

                    throw new IllegalArgumentException(this.bind("configure.incorrectDestinationPathEntry", "[-d " + currentArg));
                }
                case 17: {
                    mode = 0;
                    break;
                }
                case 18: {
                    mode = 0;
                    break;
                }
                case 28: {
                    mode = 0;
                    break;
                }
                case 19: {
                    mode = 0;
                    break;
                }
                case 20: {
                    tokenizer = new StringTokenizer(currentArg, ",");
                    if (this.classNames == null) {
                        this.classNames = new String[4];
                    }
                    while (tokenizer.hasMoreTokens()) {
                        if (this.classNames.length == classCount) {
                            this.classNames = new String[classCount * 2];
                            System.arraycopy(this.classNames, 0, this.classNames, 0, classCount);
                        }
                        this.classNames[classCount++] = tokenizer.nextToken();
                    }
                    mode = 0;
                    break;
                }
                case 21: {
                    this.initializeWarnings(currentArg);
                    mode = 0;
                    break;
                }
                case 22: {
                    mode = 0;
                    if (currentArg.isEmpty() || currentArg.charAt(0) == '-') {
                        throw new IllegalArgumentException(this.bind("configure.missingAnnotationPath", currentArg));
                    }
                    if ("CLASSPATH".equals(currentArg)) {
                        this.annotationsFromClasspath = true;
                        break;
                    }
                    if (this.annotationPaths == null) {
                        this.annotationPaths = new ArrayList<String>();
                    }
                    tokens = new StringTokenizer(currentArg, File.pathSeparator);
                    while (tokens.hasMoreTokens()) {
                        this.annotationPaths.add(tokens.nextToken());
                    }
                    continue block74;
                }
lbl855:
                // 3 sources

                default: {
                    if (customDestinationPath == null) {
                        if (File.separatorChar != '/') {
                            currentArg = currentArg.replace('/', File.separatorChar);
                        }
                        if (currentArg.endsWith("[-d")) {
                            currentSourceDirectory = currentArg.substring(0, currentArg.length() - 3);
                            mode = 16;
                            break;
                        }
                        currentSourceDirectory = currentArg;
                    }
                    if (!(dir = new File(currentSourceDirectory)).isDirectory()) {
                        throw new IllegalArgumentException(this.bind("configure.unrecognizedOption", currentSourceDirectory));
                    }
                    result = FileFinder.find(dir, ".java");
                    if ("none".equals(customDestinationPath)) {
                        customDestinationPath = "none";
                    }
                    if (this.filenames != null) {
                        length = result.length;
                        this.filenames = new String[length + filesCount];
                        System.arraycopy(this.filenames, 0, this.filenames, 0, filesCount);
                        this.encodings = new String[length + filesCount];
                        System.arraycopy(this.encodings, 0, this.encodings, 0, filesCount);
                        this.destinationPaths = new String[length + filesCount];
                        System.arraycopy(this.destinationPaths, 0, this.destinationPaths, 0, filesCount);
                        this.modNames = new String[length + filesCount];
                        System.arraycopy(this.modNames, 0, this.modNames, 0, filesCount);
                        System.arraycopy(result, 0, this.filenames, filesCount, length);
                        i = 0;
                        while (i < length) {
                            this.encodings[filesCount + i] = customEncoding;
                            this.destinationPaths[filesCount + i] = customDestinationPath;
                            this.modNames[filesCount + i] = moduleName;
                            ++i;
                        }
                        filesCount += length;
                        customEncoding = null;
                        customDestinationPath = null;
                        currentSourceDirectory = null;
                    } else {
                        this.filenames = result;
                        filesCount = this.filenames.length;
                        this.encodings = new String[filesCount];
                        this.destinationPaths = new String[filesCount];
                        this.modNames = new String[filesCount];
                        i = 0;
                        while (i < filesCount) {
                            this.encodings[i] = customEncoding;
                            this.destinationPaths[i] = customDestinationPath;
                            ++i;
                        }
                        customEncoding = null;
                        customDestinationPath = null;
                        currentSourceDirectory = null;
                    }
                    mode = 0;
                }
            }
        }
        if (this.enablePreview) {
            this.options.put("org.eclipse.jdt.core.compiler.problem.enablePreviewFeatures", "enabled");
        }
        if (this.enableJavadocOn) {
            this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
        } else if (this.warnJavadocOn || this.warnAllJavadocOn) {
            this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
            this.options.put("org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference", "disabled");
            this.options.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference", "disabled");
        }
        if (this.warnJavadocOn) {
            this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTags", "enabled");
            this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef", "enabled");
            this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef", "enabled");
            this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility", "private");
        }
        if (printUsageRequired || filesCount == 0 && classCount == 0) {
            if (usageSection == null) {
                this.printUsage();
            } else {
                this.printUsage(usageSection);
            }
            this.proceed = false;
            return;
        }
        if (this.log != null) {
            this.logger.setLog(this.log);
        } else {
            this.showProgress = false;
        }
        this.logger.logVersion(printVersionRequired);
        this.validateOptions(didSpecifyCompliance);
        if (!didSpecifyDisabledAnnotationProcessing && CompilerOptions.versionToJdkLevel(this.options.get("org.eclipse.jdt.core.compiler.compliance")) >= 0x320000L) {
            this.options.put("org.eclipse.jdt.core.compiler.processAnnotations", "enabled");
        }
        this.logger.logCommandLineArguments(newCommandLineArgs);
        this.logger.logOptions(this.options);
        if (this.maxRepetition == 0) {
            this.maxRepetition = 1;
        }
        if (this.maxRepetition >= 3 && (this.timing & 1) != 0) {
            this.compilerStats = new CompilerStats[this.maxRepetition];
        }
        if (filesCount != 0) {
            this.filenames = new String[filesCount];
            System.arraycopy(this.filenames, 0, this.filenames, 0, filesCount);
        }
        if (classCount != 0) {
            this.classNames = new String[classCount];
            System.arraycopy(this.classNames, 0, this.classNames, 0, classCount);
        }
        this.setPaths(bootclasspaths, sourcepathClasspathArg, sourcepathClasspaths, classpaths, modulepathArg, moduleSourcepathArg, extdirsClasspaths, endorsedDirClasspaths, customEncoding);
        if (specifiedEncodings != null && specifiedEncodings.size() > 1) {
            this.logger.logWarning(this.bind("configure.multipleencodings", this.options.get("org.eclipse.jdt.core.encoding"), Main.getAllEncodings(specifiedEncodings)));
        }
        if (this.pendingErrors != null) {
            for (String message : this.pendingErrors) {
                this.logger.logPendingError(message);
            }
            this.pendingErrors = null;
        }
    }

    private String optionStringToVersion(String currentArg) {
        switch (currentArg) {
            case "1.3": {
                return "1.3";
            }
            case "1.4": {
                return "1.4";
            }
            case "5": 
            case "1.5": 
            case "5.0": {
                return "1.5";
            }
            case "6": 
            case "1.6": 
            case "6.0": {
                return "1.6";
            }
            case "7": 
            case "1.7": 
            case "7.0": {
                return "1.7";
            }
            case "8": 
            case "1.8": 
            case "8.0": {
                return "1.8";
            }
            case "9": 
            case "1.9": 
            case "9.0": {
                return "9";
            }
            case "10": 
            case "10.0": {
                return "10";
            }
            case "11": 
            case "11.0": {
                return "11";
            }
            case "12": 
            case "12.0": {
                return "12";
            }
            case "13": 
            case "13.0": {
                return "13";
            }
            case "14": 
            case "14.0": {
                return "14";
            }
            case "15": 
            case "15.0": {
                return "15";
            }
            case "16": 
            case "16.0": {
                return "16";
            }
        }
        return null;
    }

    private String validateModuleVersion(String versionString) {
        try {
            Class<?> versionClass = Class.forName("java.lang.module.ModuleDescriptor$Version");
            Method method = versionClass.getMethod("parse", String.class);
            try {
                method.invoke(null, versionString);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    throw (IllegalArgumentException)e.getCause();
                }
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException exception) {
            this.logger.logWarning(this.bind("configure.no.ModuleDescriptorVersionparse"));
        }
        return versionString;
    }

    private Parser getNewParser() {
        return new Parser(new ProblemReporter(this.getHandlingPolicy(), new CompilerOptions(this.options), this.getProblemFactory()), false);
    }

    private IModule extractModuleDesc(String fileName) {
        IModule mod = null;
        HashMap<String, String> opts = new HashMap<String, String>(this.options);
        opts.put("org.eclipse.jdt.core.compiler.source", this.options.get("org.eclipse.jdt.core.compiler.compliance"));
        Parser parser = new Parser(new ProblemReporter(this.getHandlingPolicy(), new CompilerOptions(opts), this.getProblemFactory()), false);
        if (fileName.toLowerCase().endsWith("module-info.java")) {
            CompilationUnit cu = new CompilationUnit(null, fileName, null);
            CompilationResult compilationResult = new CompilationResult(cu, 0, 1, 10);
            CompilationUnitDeclaration unit = parser.parse(cu, compilationResult);
            if (unit.isModuleInfo() && unit.moduleDeclaration != null) {
                mod = new BasicModule(unit.moduleDeclaration, null);
            }
        } else if (fileName.toLowerCase().endsWith("module-info.class")) {
            try {
                ClassFileReader reader = ClassFileReader.read(fileName);
                mod = reader.getModuleDeclaration();
            }
            catch (IOException | ClassFormatException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(this.bind("configure.invalidModuleDescriptor", fileName));
            }
        }
        return mod;
    }

    private static char[][] decodeIgnoreOptionalProblemsFromFolders(String folders) {
        StringTokenizer tokenizer = new StringTokenizer(folders, File.pathSeparator);
        char[][] result = new char[2 * tokenizer.countTokens()][];
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            String fileName = tokenizer.nextToken();
            File file = new File(fileName);
            if (file.exists()) {
                String absolutePath = file.getAbsolutePath();
                result[count++] = absolutePath.toCharArray();
                try {
                    String canonicalPath = file.getCanonicalPath();
                    if (absolutePath.equals(canonicalPath)) continue;
                    result[count++] = canonicalPath.toCharArray();
                }
                catch (IOException iOException) {}
                continue;
            }
            result[count++] = fileName.toCharArray();
        }
        if (count < result.length) {
            char[][] shortened = new char[count][];
            System.arraycopy(result, 0, shortened, 0, count);
            result = shortened;
        }
        return result;
    }

    private static String getAllEncodings(Set<String> encodings) {
        int size = encodings.size();
        Object[] allEncodings = new String[size];
        encodings.toArray(allEncodings);
        Arrays.sort(allEncodings);
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while (i < size) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append((String)allEncodings[i]);
            ++i;
        }
        return String.valueOf(buffer);
    }

    private void initializeWarnings(String propertiesFile) {
        File file = new File(propertiesFile);
        if (!file.exists()) {
            throw new IllegalArgumentException(this.bind("configure.missingwarningspropertiesfile", propertiesFile));
        }
        BufferedInputStream stream = null;
        Properties properties = null;
        try {
            try {
                stream = new BufferedInputStream(new FileInputStream(propertiesFile));
                properties = new Properties();
                properties.load(stream);
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(this.bind("configure.ioexceptionwarningspropertiesfile", propertiesFile));
            }
        }
        catch (Throwable throwable) {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
            throw throwable;
        }
        if (stream != null) {
            try {
                stream.close();
            }
            catch (IOException iOException) {}
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (!key.startsWith("org.eclipse.jdt.core.compiler.")) continue;
            this.options.put(key, entry.getValue().toString());
        }
        if (!properties.containsKey("org.eclipse.jdt.core.compiler.debug.localVariable")) {
            this.options.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
        }
        if (!properties.containsKey("org.eclipse.jdt.core.compiler.codegen.unusedLocal")) {
            this.options.put("org.eclipse.jdt.core.compiler.codegen.unusedLocal", "preserve");
        }
        if (!properties.containsKey("org.eclipse.jdt.core.compiler.doc.comment.support")) {
            this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
        }
        if (!properties.containsKey("org.eclipse.jdt.core.compiler.problem.forbiddenReference")) {
            this.options.put("org.eclipse.jdt.core.compiler.problem.forbiddenReference", "error");
        }
    }

    protected void enableAll(int severity) {
        String newValue = null;
        switch (severity) {
            case 1: {
                newValue = "error";
                break;
            }
            case 0: {
                newValue = "warning";
            }
        }
        Map.Entry[] entries = this.options.entrySet().toArray(new Map.Entry[this.options.size()]);
        int i = 0;
        int max = entries.length;
        while (i < max) {
            Map.Entry entry = entries[i];
            if (((String)entry.getValue()).equals("ignore")) {
                this.options.put((String)entry.getKey(), newValue);
            }
            ++i;
        }
        this.options.put("org.eclipse.jdt.core.compiler.taskTags", Util.EMPTY_STRING);
        if (newValue != null) {
            this.options.remove(newValue);
        }
    }

    protected void disableAll(int severity) {
        String checkedValue = null;
        switch (severity) {
            case 1: {
                checkedValue = "error";
                break;
            }
            case 0: {
                checkedValue = "warning";
                break;
            }
            case 1024: {
                checkedValue = "info";
            }
        }
        Set<Map.Entry<String, String>> entrySet = this.options.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (!entry.getValue().equals(checkedValue)) continue;
            this.options.put(entry.getKey(), "ignore");
        }
        if (checkedValue != null) {
            this.options.put(checkedValue, "ignore");
        }
        if (severity == 0) {
            this.disableAll(1024);
        }
    }

    public String extractDestinationPathFromSourceFile(CompilationResult result) {
        String outputPathName;
        File output;
        char[] fileName;
        int lastIndex;
        ICompilationUnit compilationUnit = result.compilationUnit;
        if (compilationUnit != null && (lastIndex = CharOperation.lastIndexOf(File.separatorChar, fileName = compilationUnit.getFileName())) != -1 && (output = new File(outputPathName = new String(fileName, 0, lastIndex))).exists() && output.isDirectory()) {
            return outputPathName;
        }
        return System.getProperty("user.dir");
    }

    public ICompilerRequestor getBatchRequestor() {
        return new BatchCompilerRequestor(this);
    }

    public CompilationUnit[] getCompilationUnits() {
        int fileCount = this.filenames.length;
        CompilationUnit[] units = new CompilationUnit[fileCount];
        HashtableOfObject knownFileNames = new HashtableOfObject(fileCount);
        String defaultEncoding = this.options.get("org.eclipse.jdt.core.encoding");
        if (Util.EMPTY_STRING.equals(defaultEncoding)) {
            defaultEncoding = null;
        }
        int round = 0;
        while (round < 2) {
            int i = 0;
            while (i < fileCount) {
                char[] charName = this.filenames[i].toCharArray();
                boolean isModuleInfo = CharOperation.endsWith(charName, TypeConstants.MODULE_INFO_FILE_NAME);
                if (isModuleInfo == (round == 0)) {
                    String fileName;
                    if (knownFileNames.get(charName) != null) {
                        throw new IllegalArgumentException(this.bind("unit.more", this.filenames[i]));
                    }
                    knownFileNames.put(charName, charName);
                    File file = new File(this.filenames[i]);
                    if (!file.exists()) {
                        throw new IllegalArgumentException(this.bind("unit.missing", this.filenames[i]));
                    }
                    String encoding = this.encodings[i];
                    if (encoding == null) {
                        encoding = defaultEncoding;
                    }
                    try {
                        fileName = file.getCanonicalPath();
                    }
                    catch (IOException iOException) {
                        fileName = this.filenames[i];
                    }
                    Function<String, String> annotationPathProvider = null;
                    if (this.annotationsFromClasspath) {
                        annotationPathProvider = qualifiedTypeName -> {
                            FileSystem.Classpath[] classpathArray = this.checkedClasspaths;
                            int n = this.checkedClasspaths.length;
                            int n2 = 0;
                            while (n2 < n) {
                                FileSystem.Classpath classpathEntry = classpathArray[n2];
                                if (classpathEntry.hasAnnotationFileFor(qualifiedTypeName.replace('.', '/'))) {
                                    return classpathEntry.getPath();
                                }
                                ++n2;
                            }
                            return null;
                        };
                    } else if (this.annotationPaths != null) {
                        annotationPathProvider = qualifiedTypeName -> {
                            String eeaFileName = String.valueOf('/') + qualifiedTypeName.replace('.', '/') + ".eea";
                            for (String annotationPath : this.annotationPaths) {
                                if (!new File(String.valueOf(annotationPath) + eeaFileName).exists()) continue;
                                return annotationPath;
                            }
                            return null;
                        };
                    }
                    units[i] = new CompilationUnit(null, fileName, encoding, this.destinationPaths[i], Main.shouldIgnoreOptionalProblems(this.ignoreOptionalProblemsFromFolders, fileName.toCharArray()), this.modNames[i], annotationPathProvider);
                }
                ++i;
            }
            ++round;
        }
        return units;
    }

    public IErrorHandlingPolicy getHandlingPolicy() {
        return new IErrorHandlingPolicy(){

            @Override
            public boolean proceedOnErrors() {
                return Main.this.proceedOnError;
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

    private void setJavaHome(String javaHome) {
        File release = new File(javaHome, "release");
        Properties prop = new Properties();
        try {
            Throwable throwable = null;
            Object var5_6 = null;
            try (FileReader reader = new FileReader(release);){
                prop.load(reader);
                String ver = prop.getProperty("JAVA_VERSION");
                if (ver != null) {
                    ver = ver.replace("\"", "");
                }
                this.javaHomeCache = new File(javaHome);
                this.javaHomeChecked = true;
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
            throw new IllegalArgumentException(this.bind("configure.invalidSystem", javaHome));
        }
    }

    public File getJavaHome() {
        if (!this.javaHomeChecked) {
            this.javaHomeChecked = true;
            this.javaHomeCache = Util.getJavaHome();
        }
        return this.javaHomeCache;
    }

    public FileSystem getLibraryAccess() {
        FileSystem nameEnvironment = new FileSystem(this.checkedClasspaths, this.filenames, this.annotationsFromClasspath && "enabled".equals(this.options.get("org.eclipse.jdt.core.compiler.annotation.nullanalysis")), this.limitedModules);
        nameEnvironment.module = this.module;
        this.processAddonModuleOptions(nameEnvironment);
        return nameEnvironment;
    }

    public IProblemFactory getProblemFactory() {
        return new DefaultProblemFactory(this.compilerLocale);
    }

    protected ArrayList<FileSystem.Classpath> handleBootclasspath(ArrayList<String> bootclasspaths, String customEncoding) {
        int bootclasspathsSize;
        ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>(4);
        if (bootclasspaths != null && (bootclasspathsSize = bootclasspaths.size()) != 0) {
            result = new ArrayList(bootclasspathsSize);
            for (String path : bootclasspaths) {
                this.processPathEntries(4, result, path, customEncoding, false, true);
            }
        } else {
            try {
                Util.collectVMBootclasspath(result, this.javaHomeCache);
            }
            catch (IllegalStateException illegalStateException) {
                throw new IllegalArgumentException(this.bind("configure.invalidSystem", this.javaHomeCache.toString()));
            }
        }
        return result;
    }

    private void processAddonModuleOptions(FileSystem env) {
        HashMap<String, IModule.IPackageExport[]> exports = new HashMap<String, IModule.IPackageExport[]>();
        for (String option : this.addonExports) {
            ModuleFinder.AddExport addExport = ModuleFinder.extractAddonExport(option);
            if (addExport != null) {
                String modName = addExport.sourceModuleName;
                IModule.IPackageExport export = addExport.export;
                IModule.IPackageExport[] existing = (IModule.IPackageExport[])exports.get(modName);
                if (existing == null) {
                    existing = new IModule.IPackageExport[]{export};
                    exports.put(modName, existing);
                } else {
                    IModule.IPackageExport[] iPackageExportArray = existing;
                    int n = existing.length;
                    int n2 = 0;
                    while (n2 < n) {
                        IModule.IPackageExport iPackageExport = iPackageExportArray[n2];
                        if (CharOperation.equals(iPackageExport.name(), export.name())) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateExport"));
                        }
                        ++n2;
                    }
                    IModule.IPackageExport[] updated = new IModule.IPackageExport[existing.length + 1];
                    System.arraycopy(existing, 0, updated, 0, existing.length);
                    updated[existing.length] = export;
                    exports.put(modName, updated);
                }
                env.addModuleUpdate(modName, m -> m.addExports(export.name(), export.targets()), IUpdatableModule.UpdateKind.PACKAGE);
                continue;
            }
            throw new IllegalArgumentException(this.bind("configure.invalidModuleOption", "--add-exports " + option));
        }
        for (String option : this.addonReads) {
            String[] result = ModuleFinder.extractAddonRead(option);
            if (result != null && result.length == 2) {
                env.addModuleUpdate(result[0], m -> m.addReads(result[1].toCharArray()), IUpdatableModule.UpdateKind.MODULE);
                continue;
            }
            throw new IllegalArgumentException(this.bind("configure.invalidModuleOption", "--add-reads " + option));
        }
    }

    protected ArrayList<FileSystem.Classpath> handleModulepath(String arg) {
        ArrayList<String> modulePaths = this.processModulePathEntries(arg);
        ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
        if (modulePaths != null && modulePaths.size() > 0) {
            for (String path : modulePaths) {
                File file = new File(path);
                if (file.isDirectory()) {
                    result.addAll(ModuleFinder.findModules(file, null, this.getNewParser(), this.options, true, this.releaseVersion));
                    continue;
                }
                FileSystem.Classpath modulePath = ModuleFinder.findModule(file, null, this.getNewParser(), this.options, true, this.releaseVersion);
                if (modulePath == null) continue;
                result.add(modulePath);
            }
        }
        return result;
    }

    protected ArrayList<FileSystem.Classpath> handleModuleSourcepath(String arg) {
        ArrayList<String> modulePaths = this.processModulePathEntries(arg);
        ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
        if (modulePaths != null && modulePaths.size() != 0) {
            if (this.destinationPath == null) {
                this.addPendingErrors(this.bind("configure.missingDestinationPath"));
            }
            String[] paths = new String[modulePaths.size()];
            modulePaths.toArray(paths);
            int i = 0;
            while (i < paths.length) {
                File dir = new File(paths[i]);
                if (dir.isDirectory()) {
                    List<FileSystem.Classpath> modules = ModuleFinder.findModules(dir, this.destinationPath, this.getNewParser(), this.options, false, this.releaseVersion);
                    for (FileSystem.Classpath classpath : modules) {
                        result.add(classpath);
                        Path modLocation = Paths.get(classpath.getPath(), new String[0]).toAbsolutePath();
                        String destPath = classpath.getDestinationPath();
                        IModule mod = classpath.getModule();
                        String moduleName = mod == null ? null : new String(mod.name());
                        int j = 0;
                        while (j < this.filenames.length) {
                            try {
                                Path filePath = new File(this.filenames[j]).getCanonicalFile().toPath();
                                if (filePath.startsWith(modLocation)) {
                                    this.modNames[j] = moduleName;
                                    this.destinationPaths[j] = destPath;
                                }
                            }
                            catch (IOException iOException) {
                                this.modNames[j] = "";
                            }
                            ++j;
                        }
                    }
                }
                ++i;
            }
            int j = 0;
            while (j < this.filenames.length) {
                if (this.modNames[j] == null) {
                    throw new IllegalArgumentException(this.bind("configure.notOnModuleSourcePath", new String[]{this.filenames[j]}));
                }
                ++j;
            }
        }
        return result;
    }

    protected ArrayList<FileSystem.Classpath> handleClasspath(ArrayList<String> classpaths, String customEncoding) {
        ArrayList<FileSystem.Classpath> initial = new ArrayList<FileSystem.Classpath>(4);
        if (classpaths != null && classpaths.size() > 0) {
            for (String path : classpaths) {
                this.processPathEntries(4, initial, path, customEncoding, false, true);
            }
        } else {
            String classProp = System.getProperty("java.class.path");
            if (classProp == null || classProp.length() == 0) {
                this.addPendingErrors(this.bind("configure.noClasspath"));
                FileSystem.Classpath classpath = FileSystem.getClasspath(System.getProperty("user.dir"), customEncoding, null, this.options, this.releaseVersion);
                if (classpath != null) {
                    initial.add(classpath);
                }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    FileSystem.Classpath currentClasspath = FileSystem.getClasspath(token, customEncoding, null, this.options, this.releaseVersion);
                    if (currentClasspath != null) {
                        initial.add(currentClasspath);
                        continue;
                    }
                    if (token.length() == 0) continue;
                    this.addPendingErrors(this.bind("configure.incorrectClasspath", token));
                }
            }
        }
        ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
        HashMap<String, FileSystem.Classpath> knownNames = new HashMap<String, FileSystem.Classpath>();
        FileSystem.ClasspathSectionProblemReporter problemReporter = new FileSystem.ClasspathSectionProblemReporter(){

            @Override
            public void invalidClasspathSection(String jarFilePath) {
                Main.this.addPendingErrors(Main.this.bind("configure.invalidClasspathSection", jarFilePath));
            }

            @Override
            public void multipleClasspathSections(String jarFilePath) {
                Main.this.addPendingErrors(Main.this.bind("configure.multipleClasspathSections", jarFilePath));
            }
        };
        while (!initial.isEmpty()) {
            FileSystem.Classpath current = initial.remove(0);
            String currentPath = current.getPath();
            if (knownNames.get(currentPath) != null) continue;
            knownNames.put(currentPath, current);
            result.add(current);
            List<FileSystem.Classpath> linkedJars = current.fetchLinkedJars(problemReporter);
            if (linkedJars == null) continue;
            initial.addAll(0, linkedJars);
        }
        return result;
    }

    protected ArrayList<FileSystem.Classpath> handleEndorseddirs(ArrayList<String> endorsedDirClasspaths) {
        File javaHome = this.getJavaHome();
        if (endorsedDirClasspaths == null) {
            endorsedDirClasspaths = new ArrayList(4);
            String endorsedDirsStr = System.getProperty("java.endorsed.dirs");
            if (endorsedDirsStr == null) {
                if (javaHome != null) {
                    endorsedDirClasspaths.add(String.valueOf(javaHome.getAbsolutePath()) + "/lib/endorsed");
                }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(endorsedDirsStr, File.pathSeparator);
                while (tokenizer.hasMoreTokens()) {
                    endorsedDirClasspaths.add(tokenizer.nextToken());
                }
            }
        }
        if (endorsedDirClasspaths.size() != 0) {
            ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
            File[] directoriesToCheck = new File[endorsedDirClasspaths.size()];
            int i = 0;
            while (i < directoriesToCheck.length) {
                directoriesToCheck[i] = new File(endorsedDirClasspaths.get(i));
                ++i;
            }
            File[][] endorsedDirsJars = Main.getLibrariesFiles(directoriesToCheck);
            if (endorsedDirsJars != null) {
                int i2 = 0;
                int max = endorsedDirsJars.length;
                while (i2 < max) {
                    File[] current = endorsedDirsJars[i2];
                    if (current != null) {
                        int j = 0;
                        int max2 = current.length;
                        while (j < max2) {
                            FileSystem.Classpath classpath = FileSystem.getClasspath(current[j].getAbsolutePath(), null, null, this.options, this.releaseVersion);
                            if (classpath != null) {
                                result.add(classpath);
                            }
                            ++j;
                        }
                    } else if (directoriesToCheck[i2].isFile()) {
                        this.addPendingErrors(this.bind("configure.incorrectEndorsedDirsEntry", directoriesToCheck[i2].getAbsolutePath()));
                    }
                    ++i2;
                }
            }
            return result;
        }
        return FileSystem.EMPTY_CLASSPATH;
    }

    protected ArrayList<FileSystem.Classpath> handleExtdirs(ArrayList<String> extdirsClasspaths) {
        File javaHome = this.getJavaHome();
        if (extdirsClasspaths == null) {
            extdirsClasspaths = new ArrayList(4);
            String extdirsStr = System.getProperty("java.ext.dirs");
            if (extdirsStr == null) {
                extdirsClasspaths.add(String.valueOf(javaHome.getAbsolutePath()) + "/lib/ext");
            } else {
                StringTokenizer tokenizer = new StringTokenizer(extdirsStr, File.pathSeparator);
                while (tokenizer.hasMoreTokens()) {
                    extdirsClasspaths.add(tokenizer.nextToken());
                }
            }
        }
        if (extdirsClasspaths.size() != 0) {
            ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
            File[] directoriesToCheck = new File[extdirsClasspaths.size()];
            int i = 0;
            while (i < directoriesToCheck.length) {
                directoriesToCheck[i] = new File(extdirsClasspaths.get(i));
                ++i;
            }
            File[][] extdirsJars = Main.getLibrariesFiles(directoriesToCheck);
            if (extdirsJars != null) {
                int i2 = 0;
                int max = extdirsJars.length;
                while (i2 < max) {
                    File[] current = extdirsJars[i2];
                    if (current != null) {
                        int j = 0;
                        int max2 = current.length;
                        while (j < max2) {
                            FileSystem.Classpath classpath = FileSystem.getClasspath(current[j].getAbsolutePath(), null, null, this.options, this.releaseVersion);
                            if (classpath != null) {
                                result.add(classpath);
                            }
                            ++j;
                        }
                    } else if (directoriesToCheck[i2].isFile()) {
                        this.addPendingErrors(this.bind("configure.incorrectExtDirsEntry", directoriesToCheck[i2].getAbsolutePath()));
                    }
                    ++i2;
                }
            }
            return result;
        }
        return FileSystem.EMPTY_CLASSPATH;
    }

    protected boolean isIgnored(IProblem problem) {
        char[] fileName;
        String key;
        if (problem == null) {
            return true;
        }
        if (problem.isError()) {
            return false;
        }
        String string = key = problem.isInfo() ? "info" : "warning";
        if ("ignore".equals(this.options.get(key))) {
            return true;
        }
        if (this.ignoreOptionalProblemsFromFolders != null && (fileName = problem.getOriginatingFileName()) != null) {
            return Main.shouldIgnoreOptionalProblems(this.ignoreOptionalProblemsFromFolders, fileName);
        }
        return false;
    }

    protected void handleInfoToken(String token, boolean isEnabling) {
        this.handleErrorOrWarningToken(token, isEnabling, 1024);
    }

    protected void handleWarningToken(String token, boolean isEnabling) {
        this.handleErrorOrWarningToken(token, isEnabling, 0);
    }

    protected void handleErrorToken(String token, boolean isEnabling) {
        this.handleErrorOrWarningToken(token, isEnabling, 1);
    }

    private void setSeverity(String compilerOptions, int severity, boolean isEnabling) {
        if (isEnabling) {
            switch (severity) {
                case 1: {
                    this.options.put(compilerOptions, "error");
                    break;
                }
                case 0: {
                    this.options.put(compilerOptions, "warning");
                    break;
                }
                case 1024: {
                    this.options.put(compilerOptions, "info");
                    break;
                }
                default: {
                    this.options.put(compilerOptions, "ignore");
                    break;
                }
            }
        } else {
            switch (severity) {
                case 1: {
                    String currentValue = this.options.get(compilerOptions);
                    if (!"error".equals(currentValue)) break;
                    this.options.put(compilerOptions, "ignore");
                    break;
                }
                case 0: {
                    String currentValue = this.options.get(compilerOptions);
                    if (!"warning".equals(currentValue)) break;
                    this.options.put(compilerOptions, "ignore");
                    break;
                }
                case 1024: {
                    String currentValue = this.options.get(compilerOptions);
                    if (!"info".equals(currentValue)) break;
                    this.options.put(compilerOptions, "ignore");
                    break;
                }
                default: {
                    this.options.put(compilerOptions, "ignore");
                }
            }
        }
    }

    private void handleErrorOrWarningToken(String token, boolean isEnabling, int severity) {
        if (token.length() == 0) {
            return;
        }
        switch (token.charAt(0)) {
            case 'a': {
                if (token.equals("allDeprecation")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.deprecation", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.terminalDeprecation", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", isEnabling ? "enabled" : "disabled");
                    this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("allJavadoc")) {
                    this.warnAllJavadocOn = this.warnJavadocOn = isEnabling;
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.invalidJavadoc", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocTags", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocComments", severity, isEnabling);
                    return;
                }
                if (token.equals("assertIdentifier")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.assertIdentifier", severity, isEnabling);
                    return;
                }
                if (token.equals("allDeadCode")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.deadCode", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("allOver-ann")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotationForInterfaceMethodImplementation", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("all-static-method")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.reportMethodCanBeStatic", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.reportMethodCanBePotentiallyStatic", severity, isEnabling);
                    return;
                }
                if (!token.equals("all")) break;
                if (isEnabling) {
                    this.enableAll(severity);
                } else {
                    this.disableAll(severity);
                }
                return;
            }
            case 'b': {
                if (!token.equals("boxing")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.autoboxing", severity, isEnabling);
                return;
            }
            case 'c': {
                if (token.equals("constructorName")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.methodWithConstructorName", severity, isEnabling);
                    return;
                }
                if (token.equals("conditionAssign")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment", severity, isEnabling);
                    return;
                }
                if (token.equals("compareIdentical")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.comparingIdentical", severity, isEnabling);
                    return;
                }
                if (!token.equals("charConcat")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", severity, isEnabling);
                return;
            }
            case 'd': {
                if (token.equals("deprecation")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.deprecation", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "disabled");
                    this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "disabled");
                    return;
                }
                if (token.equals("dep-ann")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation", severity, isEnabling);
                    return;
                }
                if (token.equals("discouraged")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.discouragedReference", severity, isEnabling);
                    return;
                }
                if (!token.equals("deadCode")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.deadCode", severity, isEnabling);
                this.options.put("org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement", "disabled");
                return;
            }
            case 'e': {
                if (token.equals("enumSwitch")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", severity, isEnabling);
                    return;
                }
                if (token.equals("enumSwitchPedantic")) {
                    if (isEnabling) {
                        switch (severity) {
                            case 1: {
                                this.setSeverity("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", severity, isEnabling);
                                break;
                            }
                            case 0: {
                                if (!"ignore".equals(this.options.get("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch"))) break;
                                this.setSeverity("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", severity, isEnabling);
                            }
                        }
                    }
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingEnumCaseDespiteDefault", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("emptyBlock")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock", severity, isEnabling);
                    return;
                }
                if (token.equals("enumIdentifier")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.enumIdentifier", severity, isEnabling);
                    return;
                }
                if (!token.equals("exports")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.APILeak", severity, isEnabling);
                return;
            }
            case 'f': {
                if (token.equals("fieldHiding")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.fieldHiding", severity, isEnabling);
                    return;
                }
                if (token.equals("finalBound")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.finalParameterBound", severity, isEnabling);
                    return;
                }
                if (token.equals("finally")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally", severity, isEnabling);
                    return;
                }
                if (token.equals("forbidden")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.forbiddenReference", severity, isEnabling);
                    return;
                }
                if (!token.equals("fallthrough")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.fallthroughCase", severity, isEnabling);
                return;
            }
            case 'h': {
                if (token.equals("hiding")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.localVariableHiding", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.fieldHiding", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.typeParameterHiding", severity, isEnabling);
                    return;
                }
                if (!token.equals("hashCode")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod", severity, isEnabling);
                return;
            }
            case 'i': {
                if (token.equals("indirectStatic")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", severity, isEnabling);
                    return;
                }
                if (token.equals("inheritNullAnnot")) {
                    this.options.put("org.eclipse.jdt.core.compiler.annotation.inheritNullAnnotations", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("intfNonInherited") || token.equals("interfaceNonInherited")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod", severity, isEnabling);
                    return;
                }
                if (token.equals("intfAnnotation")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.annotationSuperInterface", severity, isEnabling);
                    return;
                }
                if (token.equals("intfRedundant")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", severity, isEnabling);
                    return;
                }
                if (token.equals("includeAssertNull")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.includeNullInfoFromAsserts", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("invalidJavadoc")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.invalidJavadoc", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTags", isEnabling ? "enabled" : "disabled");
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef", isEnabling ? "enabled" : "disabled");
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef", isEnabling ? "enabled" : "disabled");
                    if (isEnabling) {
                        this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
                        this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility", "private");
                    }
                    return;
                }
                if (token.equals("invalidJavadocTag")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTags", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("invalidJavadocTagDep")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("invalidJavadocTagNotVisible")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (!token.startsWith("invalidJavadocTagVisibility")) break;
                int start = token.indexOf(40);
                int end = token.indexOf(41);
                String visibility = null;
                if (isEnabling && start >= 0 && end >= 0 && start < end) {
                    visibility = token.substring(start + 1, end).trim();
                }
                if (visibility != null && visibility.equals("public") || visibility.equals("private") || visibility.equals("protected") || visibility.equals("default")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility", visibility);
                    return;
                }
                throw new IllegalArgumentException(this.bind("configure.invalidJavadocTagVisibility", token));
            }
            case 'j': {
                if (!token.equals("javadoc")) break;
                this.warnJavadocOn = isEnabling;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.invalidJavadoc", severity, isEnabling);
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocTags", severity, isEnabling);
                return;
            }
            case 'l': {
                if (!token.equals("localHiding")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.localVariableHiding", severity, isEnabling);
                return;
            }
            case 'm': {
                if (token.equals("maskedCatchBlock") || token.equals("maskedCatchBlocks")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", severity, isEnabling);
                    return;
                }
                if (token.equals("missingJavadocTags")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocTags", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding", isEnabling ? "enabled" : "disabled");
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsMethodTypeParameters", isEnabling ? "enabled" : "disabled");
                    if (isEnabling) {
                        this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
                        this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility", "private");
                    }
                    return;
                }
                if (token.equals("missingJavadocTagsOverriding")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("missingJavadocTagsMethod")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsMethodTypeParameters", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.startsWith("missingJavadocTagsVisibility")) {
                    int start = token.indexOf(40);
                    int end = token.indexOf(41);
                    String visibility = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        visibility = token.substring(start + 1, end).trim();
                    }
                    if (visibility != null && visibility.equals("public") || visibility.equals("private") || visibility.equals("protected") || visibility.equals("default")) {
                        this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility", visibility);
                        return;
                    }
                    throw new IllegalArgumentException(this.bind("configure.missingJavadocTagsVisibility", token));
                }
                if (token.equals("missingJavadocComments")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocComments", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding", isEnabling ? "enabled" : "disabled");
                    if (isEnabling) {
                        this.options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
                        this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility", "private");
                    }
                    return;
                }
                if (token.equals("missingJavadocCommentsOverriding")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingJavadocComments", severity, isEnabling);
                    this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.startsWith("missingJavadocCommentsVisibility")) {
                    int start = token.indexOf(40);
                    int end = token.indexOf(41);
                    String visibility = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        visibility = token.substring(start + 1, end).trim();
                    }
                    if (visibility != null && visibility.equals("public") || visibility.equals("private") || visibility.equals("protected") || visibility.equals("default")) {
                        this.options.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility", visibility);
                        return;
                    }
                    throw new IllegalArgumentException(this.bind("configure.missingJavadocCommentsVisibility", token));
                }
                if (!token.equals("module")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.unstableAutoModuleName", severity, isEnabling);
                return;
            }
            case 'n': {
                if (token.equals("nls")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral", severity, isEnabling);
                    return;
                }
                if (token.equals("noEffectAssign")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.noEffectAssignment", severity, isEnabling);
                    return;
                }
                if (token.equals("noImplicitStringConversion")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", severity, isEnabling);
                    return;
                }
                if (token.equals("null")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullReference", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.potentialNullReference", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantNullCheck", severity, isEnabling);
                    return;
                }
                if (token.equals("nullDereference")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullReference", severity, isEnabling);
                    if (!isEnabling) {
                        this.setSeverity("org.eclipse.jdt.core.compiler.problem.potentialNullReference", 256, isEnabling);
                        this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantNullCheck", 256, isEnabling);
                    }
                    return;
                }
                if (token.equals("nullAnnotConflict")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullAnnotationInferenceConflict", severity, isEnabling);
                    return;
                }
                if (token.equals("nullAnnotRedundant")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantNullAnnotation", severity, isEnabling);
                    return;
                }
                if (token.startsWith("nullAnnot")) {
                    String annotationNames = Util.EMPTY_STRING;
                    int start = token.indexOf(40);
                    int end = token.indexOf(41);
                    String nonNullAnnotName = null;
                    String nullableAnnotName = null;
                    String nonNullByDefaultAnnotName = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        boolean isPrimarySet = !this.primaryNullAnnotationsSeen;
                        annotationNames = token.substring(start + 1, end).trim();
                        int separator1 = annotationNames.indexOf(124);
                        if (separator1 == -1) {
                            throw new IllegalArgumentException(this.bind("configure.invalidNullAnnot", token));
                        }
                        nullableAnnotName = annotationNames.substring(0, separator1).trim();
                        if (isPrimarySet && nullableAnnotName.length() == 0) {
                            throw new IllegalArgumentException(this.bind("configure.invalidNullAnnot", token));
                        }
                        int separator2 = annotationNames.indexOf(124, separator1 + 1);
                        if (separator2 == -1) {
                            throw new IllegalArgumentException(this.bind("configure.invalidNullAnnot", token));
                        }
                        nonNullAnnotName = annotationNames.substring(separator1 + 1, separator2).trim();
                        if (isPrimarySet && nonNullAnnotName.length() == 0) {
                            throw new IllegalArgumentException(this.bind("configure.invalidNullAnnot", token));
                        }
                        nonNullByDefaultAnnotName = annotationNames.substring(separator2 + 1).trim();
                        if (isPrimarySet && nonNullByDefaultAnnotName.length() == 0) {
                            throw new IllegalArgumentException(this.bind("configure.invalidNullAnnot", token));
                        }
                        if (isPrimarySet) {
                            this.primaryNullAnnotationsSeen = true;
                            this.options.put("org.eclipse.jdt.core.compiler.annotation.nullable", nullableAnnotName);
                            this.options.put("org.eclipse.jdt.core.compiler.annotation.nonnull", nonNullAnnotName);
                            this.options.put("org.eclipse.jdt.core.compiler.annotation.nonnullbydefault", nonNullByDefaultAnnotName);
                        } else {
                            if (nullableAnnotName.length() > 0) {
                                String nullableList = this.options.get("org.eclipse.jdt.core.compiler.annotation.nullable.secondary");
                                nullableList = nullableList.isEmpty() ? nullableAnnotName : String.valueOf(nullableList) + ',' + nullableAnnotName;
                                this.options.put("org.eclipse.jdt.core.compiler.annotation.nullable.secondary", nullableList);
                            }
                            if (nonNullAnnotName.length() > 0) {
                                String nonnullList = this.options.get("org.eclipse.jdt.core.compiler.annotation.nonnull.secondary");
                                nonnullList = nonnullList.isEmpty() ? nonNullAnnotName : String.valueOf(nonnullList) + ',' + nonNullAnnotName;
                                this.options.put("org.eclipse.jdt.core.compiler.annotation.nonnull.secondary", nonnullList);
                            }
                            if (nonNullByDefaultAnnotName.length() > 0) {
                                String nnbdList = this.options.get("org.eclipse.jdt.core.compiler.annotation.nonnullbydefault.secondary");
                                nnbdList = nnbdList.isEmpty() ? nonNullByDefaultAnnotName : String.valueOf(nnbdList) + ',' + nonNullByDefaultAnnotName;
                                this.options.put("org.eclipse.jdt.core.compiler.annotation.nonnullbydefault.secondary", nnbdList);
                            }
                        }
                    }
                    this.options.put("org.eclipse.jdt.core.compiler.annotation.nullanalysis", isEnabling ? "enabled" : "disabled");
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullSpecViolation", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullAnnotationInferenceConflict", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullUncheckedConversion", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantNullAnnotation", severity, isEnabling);
                    return;
                }
                if (token.equals("nullUncheckedConversion")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.nullUncheckedConversion", severity, isEnabling);
                    return;
                }
                if (!token.equals("nonnullNotRepeated")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.nonnullParameterAnnotationDropped", severity, isEnabling);
                return;
            }
            case 'o': {
                if (token.equals("over-sync")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod", severity, isEnabling);
                    return;
                }
                if (!token.equals("over-ann")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation", severity, isEnabling);
                this.options.put("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotationForInterfaceMethodImplementation", "disabled");
                return;
            }
            case 'p': {
                if (token.equals("pkgDefaultMethod") || token.equals("packageDefaultMethod")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod", severity, isEnabling);
                    return;
                }
                if (!token.equals("paramAssign")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.parameterAssignment", severity, isEnabling);
                return;
            }
            case 'r': {
                if (token.equals("raw")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.rawTypeReference", severity, isEnabling);
                    return;
                }
                if (token.equals("redundantSuperinterface")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", severity, isEnabling);
                    return;
                }
                if (token.equals("resource")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unclosedCloseable", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.potentiallyUnclosedCloseable", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.explicitlyClosedAutoCloseable", severity, isEnabling);
                    return;
                }
                if (!token.equals("removal")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.terminalDeprecation", severity, isEnabling);
                this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "disabled");
                this.options.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "disabled");
                return;
            }
            case 's': {
                if (token.equals("specialParamHiding")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.specialParameterHidingField", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("syntheticAccess") || token.equals("synthetic-access")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation", severity, isEnabling);
                    return;
                }
                if (token.equals("staticReceiver")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", severity, isEnabling);
                    return;
                }
                if (token.equals("syncOverride")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod", severity, isEnabling);
                    return;
                }
                if (token.equals("semicolon")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.emptyStatement", severity, isEnabling);
                    return;
                }
                if (token.equals("serial")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingSerialVersion", severity, isEnabling);
                    return;
                }
                if (token.equals("suppress")) {
                    switch (severity) {
                        case 0: {
                            this.options.put("org.eclipse.jdt.core.compiler.problem.suppressWarnings", isEnabling ? "enabled" : "disabled");
                            this.options.put("org.eclipse.jdt.core.compiler.problem.suppressOptionalErrors", "disabled");
                            break;
                        }
                        case 1: {
                            this.options.put("org.eclipse.jdt.core.compiler.problem.suppressWarnings", isEnabling ? "enabled" : "disabled");
                            this.options.put("org.eclipse.jdt.core.compiler.problem.suppressOptionalErrors", isEnabling ? "enabled" : "disabled");
                        }
                    }
                    return;
                }
                if (token.equals("static-access")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", severity, isEnabling);
                    return;
                }
                if (token.equals("super")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation", severity, isEnabling);
                    return;
                }
                if (token.equals("static-method")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.reportMethodCanBeStatic", severity, isEnabling);
                    return;
                }
                if (token.equals("switchDefault")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.missingDefaultCase", severity, isEnabling);
                    return;
                }
                if (!token.equals("syntacticAnalysis")) break;
                this.options.put("org.eclipse.jdt.core.compiler.problem.syntacticNullAnalysisForFields", isEnabling ? "enabled" : "disabled");
                return;
            }
            case 't': {
                if (token.startsWith("tasks")) {
                    String taskTags = Util.EMPTY_STRING;
                    int start = token.indexOf(40);
                    int end = token.indexOf(41);
                    if (start >= 0 && end >= 0 && start < end) {
                        taskTags = token.substring(start + 1, end).trim();
                        taskTags = taskTags.replace('|', ',');
                    }
                    if (taskTags.length() == 0) {
                        throw new IllegalArgumentException(this.bind("configure.invalidTaskTag", token));
                    }
                    this.options.put("org.eclipse.jdt.core.compiler.taskTags", isEnabling ? taskTags : Util.EMPTY_STRING);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.tasks", severity, isEnabling);
                    return;
                }
                if (!token.equals("typeHiding")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.typeParameterHiding", severity, isEnabling);
                return;
            }
            case 'u': {
                if (token.equals("unusedLocal") || token.equals("unusedLocals")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedLocal", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedArgument") || token.equals("unusedArguments")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedParameter", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedExceptionParam")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedExceptionParameter", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedImport") || token.equals("unusedImports")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedImport", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedAllocation")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedObjectAllocation", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedPrivate")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedLabel")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedLabel", severity, isEnabling);
                    return;
                }
                if (token.equals("uselessTypeCheck")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck", severity, isEnabling);
                    return;
                }
                if (token.equals("unchecked") || token.equals("unsafe")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation", severity, isEnabling);
                    return;
                }
                if (token.equals("unlikelyCollectionMethodArgumentType")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unlikelyCollectionMethodArgumentType", severity, isEnabling);
                    return;
                }
                if (token.equals("unlikelyEqualsArgumentType")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unlikelyEqualsArgumentType", severity, isEnabling);
                    return;
                }
                if (token.equals("unnecessaryElse")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unnecessaryElse", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedThrown")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedThrownWhenOverriding")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unusedThrownIncludeDocComment")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unusedThrownExemptExceptionThrowable")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionExemptExceptionAndThrowable", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unqualifiedField") || token.equals("unqualified-field-access")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess", severity, isEnabling);
                    return;
                }
                if (token.equals("unused")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.deadCode", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantSpecificationOfTypeArguments", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedExceptionParameter", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedImport", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedLabel", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedLocal", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedObjectAllocation", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedParameter", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedTypeParameter", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedParam")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedParameter", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedTypeParameter")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedTypeParameter", severity, isEnabling);
                    return;
                }
                if (token.equals("unusedParamIncludeDoc")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unusedParamOverriding")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unusedParamImplementing")) {
                    this.options.put("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract", isEnabling ? "enabled" : "disabled");
                    return;
                }
                if (token.equals("unusedTypeArgs")) {
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation", severity, isEnabling);
                    this.setSeverity("org.eclipse.jdt.core.compiler.problem.redundantSpecificationOfTypeArguments", severity, isEnabling);
                    return;
                }
                if (!token.equals("unavoidableGenericProblems")) break;
                this.options.put("org.eclipse.jdt.core.compiler.problem.unavoidableGenericTypeProblems", isEnabling ? "enabled" : "disabled");
                return;
            }
            case 'v': {
                if (!token.equals("varargsCast")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast", severity, isEnabling);
                return;
            }
            case 'w': {
                if (!token.equals("warningToken")) break;
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.unhandledWarningToken", severity, isEnabling);
                this.setSeverity("org.eclipse.jdt.core.compiler.problem.unusedWarningToken", severity, isEnabling);
                return;
            }
        }
        String message = null;
        switch (severity) {
            case 1024: {
                message = this.bind("configure.invalidInfo", token);
                break;
            }
            case 0: {
                message = this.bind("configure.invalidWarning", token);
                break;
            }
            case 1: {
                message = this.bind("configure.invalidError", token);
            }
        }
        this.addPendingErrors(message);
    }

    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit) {
        this.initialize(outWriter, errWriter, systemExit, null, null);
    }

    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions) {
        this.initialize(outWriter, errWriter, systemExit, customDefaultOptions, null);
    }

    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        this.logger = new Logger(this, outWriter, errWriter);
        this.proceed = true;
        this.out = outWriter;
        this.err = errWriter;
        this.systemExitWhenFinished = systemExit;
        this.options = new CompilerOptions().getMap();
        this.ignoreOptionalProblemsFromFolders = null;
        this.progress = compilationProgress;
        if (customDefaultOptions != null) {
            this.didSpecifySource = customDefaultOptions.get("org.eclipse.jdt.core.compiler.source") != null;
            this.didSpecifyTarget = customDefaultOptions.get("org.eclipse.jdt.core.compiler.codegen.targetPlatform") != null;
            for (Map.Entry<String, String> entry : customDefaultOptions.entrySet()) {
                this.options.put(entry.getKey(), entry.getValue());
            }
        } else {
            this.didSpecifySource = false;
            this.didSpecifyTarget = false;
        }
        this.classNames = null;
    }

    protected void initializeAnnotationProcessorManager() {
        String className = "org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager";
        try {
            Class<?> c = Class.forName(className);
            AbstractAnnotationProcessorManager annotationManager = (AbstractAnnotationProcessorManager)c.newInstance();
            annotationManager.configure(this, this.expandedCommandLine);
            annotationManager.setErr(this.err);
            annotationManager.setOut(this.out);
            this.batchCompiler.annotationProcessorManager = annotationManager;
        }
        catch (ClassNotFoundException | InstantiationException reflectiveOperationException) {
            this.logger.logUnavaibleAPT(className);
            throw new AbortCompilation();
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new AbortCompilation();
        }
        catch (UnsupportedClassVersionError unsupportedClassVersionError) {
            this.logger.logIncorrectVMVersionForAnnotationProcessing();
        }
    }

    private static boolean isParentOf(char[] folderName, char[] fileName) {
        if (folderName.length >= fileName.length) {
            return false;
        }
        if (fileName[folderName.length] != '\\' && fileName[folderName.length] != '/') {
            return false;
        }
        int i = folderName.length - 1;
        while (i >= 0) {
            if (folderName[i] != fileName[i]) {
                return false;
            }
            --i;
        }
        return true;
    }

    public void outputClassFiles(CompilationResult unitResult) {
        if (unitResult != null && (!unitResult.hasErrors() || this.proceedOnError)) {
            ClassFile[] classFiles = unitResult.getClassFiles();
            String currentDestinationPath = null;
            boolean generateClasspathStructure = false;
            CompilationUnit compilationUnit = (CompilationUnit)unitResult.compilationUnit;
            if (compilationUnit.destinationPath == null) {
                if (this.destinationPath == null) {
                    currentDestinationPath = this.extractDestinationPathFromSourceFile(unitResult);
                } else if (this.destinationPath != NONE) {
                    currentDestinationPath = this.destinationPath;
                    generateClasspathStructure = true;
                }
            } else if (compilationUnit.destinationPath != NONE) {
                currentDestinationPath = compilationUnit.destinationPath;
                generateClasspathStructure = true;
            }
            if (currentDestinationPath != null) {
                int i = 0;
                int fileCount = classFiles.length;
                while (i < fileCount) {
                    ClassFile classFile = classFiles[i];
                    char[] filename = classFile.fileName();
                    int length = filename.length;
                    char[] relativeName = new char[length + 6];
                    System.arraycopy(filename, 0, relativeName, 0, length);
                    System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
                    CharOperation.replace(relativeName, '/', File.separatorChar);
                    String relativeStringName = new String(relativeName);
                    try {
                        if (this.compilerOptions.verbose) {
                            this.out.println(Messages.bind(Messages.compilation_write, new String[]{String.valueOf(this.exportedClassFilesCounter + 1), relativeStringName}));
                        }
                        Util.writeToDisk(generateClasspathStructure, currentDestinationPath, relativeStringName, classFile);
                        this.logger.logClassFile(generateClasspathStructure, currentDestinationPath, relativeStringName);
                        ++this.exportedClassFilesCounter;
                    }
                    catch (IOException e) {
                        this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
                    }
                    ++i;
                }
                this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
            }
        }
    }

    public void performCompilation() {
        this.startTime = System.currentTimeMillis();
        FileSystem environment = this.getLibraryAccess();
        try {
            this.compilerOptions = new CompilerOptions(this.options);
            this.compilerOptions.performMethodsFullRecovery = false;
            this.compilerOptions.performStatementsRecovery = false;
            this.batchCompiler = new Compiler(environment, this.getHandlingPolicy(), this.compilerOptions, this.getBatchRequestor(), this.getProblemFactory(), this.out, this.progress);
            this.batchCompiler.remainingIterations = this.maxRepetition - this.currentRepetition;
            String setting = System.getProperty("jdt.compiler.useSingleThread");
            boolean bl = this.batchCompiler.useSingleThread = setting != null && setting.equals("true");
            if (this.compilerOptions.complianceLevel >= 0x320000L && this.compilerOptions.processAnnotations) {
                if (this.checkVMVersion(0x320000L)) {
                    this.initializeAnnotationProcessorManager();
                    if (this.classNames != null) {
                        this.batchCompiler.setBinaryTypes(this.processClassNames(this.batchCompiler.lookupEnvironment));
                    }
                } else {
                    this.logger.logIncorrectVMVersionForAnnotationProcessing();
                }
                if (this.checkVMVersion(0x350000L)) {
                    this.initRootModules(this.batchCompiler.lookupEnvironment, environment);
                }
            }
            this.compilerOptions.verbose = this.verbose;
            this.compilerOptions.produceReferenceInfo = this.produceRefInfo;
            try {
                this.logger.startLoggingSources();
                this.batchCompiler.compile(this.getCompilationUnits());
            }
            finally {
                this.logger.endLoggingSources();
            }
            if (this.extraProblems != null) {
                this.loggingExtraProblems();
                this.extraProblems = null;
            }
            if (this.compilerStats != null) {
                this.compilerStats[this.currentRepetition] = this.batchCompiler.stats;
            }
            this.logger.printStats();
        }
        finally {
            environment.cleanup();
        }
    }

    protected void loggingExtraProblems() {
        this.logger.loggingExtraProblems(this);
    }

    public void printUsage() {
        this.printUsage("misc.usage");
    }

    private void printUsage(String sectionID) {
        this.logger.logUsage(this.bind(sectionID, new String[]{System.getProperty("path.separator"), this.bind("compiler.name"), this.bind("compiler.version"), this.bind("compiler.copyright")}));
        this.logger.flush();
    }

    private void initRootModules(LookupEnvironment environment, FileSystem fileSystem) {
        ModuleBinding mod;
        HashMap<String, String> map = new HashMap<String, String>();
        for (String m : this.rootModules) {
            PlainPackageBinding[] exports;
            mod = environment.getModule(m.toCharArray());
            if (mod == null) {
                throw new IllegalArgumentException(this.bind("configure.invalidModuleName", m));
            }
            PlainPackageBinding[] plainPackageBindingArray = exports = mod.getExports();
            int n = exports.length;
            int n2 = 0;
            while (n2 < n) {
                PlainPackageBinding packageBinding = plainPackageBindingArray[n2];
                String qName = CharOperation.toString(packageBinding.compoundName);
                String existing = (String)map.get(qName);
                if (existing != null) {
                    throw new IllegalArgumentException(this.bind("configure.packageConflict", new String[]{qName, existing, m}));
                }
                map.put(qName, m);
                ++n2;
            }
        }
        if (this.limitedModules != null) {
            for (String m : this.limitedModules) {
                mod = environment.getModule(m.toCharArray());
                if (mod != null) continue;
                throw new IllegalArgumentException(this.bind("configure.invalidModuleName", m));
            }
        }
        environment.moduleVersion = this.moduleVersion;
    }

    private ReferenceBinding[] processClassNames(LookupEnvironment environment) {
        ModuleBinding mod;
        int i;
        int length = this.classNames.length;
        ReferenceBinding[] referenceBindings = new ReferenceBinding[length];
        ModuleBinding[] modules = new ModuleBinding[length];
        HashSet<ModuleBinding> modSet = new HashSet<ModuleBinding>();
        String[] typeNames = new String[length];
        if (this.complianceLevel <= 0x340000L) {
            typeNames = this.classNames;
        } else {
            i = 0;
            while (i < length) {
                String currentName = this.classNames[i];
                int idx = currentName.indexOf(47);
                mod = null;
                if (idx > 0) {
                    String m = currentName.substring(0, idx);
                    mod = environment.getModule(m.toCharArray());
                    if (mod == null) {
                        throw new IllegalArgumentException(this.bind("configure.invalidModuleName", m));
                    }
                    modules[i] = mod;
                    modSet.add(mod);
                    currentName = currentName.substring(idx + 1);
                }
                typeNames[i] = currentName;
                ++i;
            }
        }
        i = 0;
        while (i < length) {
            ReferenceBinding type;
            Object compoundName = null;
            String cls = typeNames[i];
            if (cls.indexOf(46) != -1) {
                char[] typeName = cls.toCharArray();
                compoundName = CharOperation.splitOn('.', typeName);
            } else {
                compoundName = new char[][]{cls.toCharArray()};
            }
            mod = modules[i];
            ReferenceBinding referenceBinding = type = mod != null ? environment.getType((char[][])compoundName, mod) : environment.getType((char[][])compoundName);
            if (type != null && type.isValidBinding()) {
                if (type.isBinaryBinding()) {
                    referenceBindings[i] = type;
                    type.superclass();
                }
            } else {
                throw new IllegalArgumentException(this.bind("configure.invalidClassName", this.classNames[i]));
            }
            ++i;
        }
        return referenceBindings;
    }

    private ArrayList<String> processModulePathEntries(String arg) {
        ArrayList<String> paths = new ArrayList<String>();
        if (arg == null) {
            return paths;
        }
        StringTokenizer tokenizer = new StringTokenizer(arg, File.pathSeparator, false);
        while (tokenizer.hasMoreTokens()) {
            paths.add(tokenizer.nextToken());
        }
        return paths;
    }

    public void processPathEntries(int defaultSize, ArrayList<FileSystem.Classpath> paths, String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
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
                        currentClasspathName = "";
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
                break;
            }
            default: {
                if (currentPath.length() == 0) break;
                this.addPendingErrors(this.bind("configure.incorrectClasspath", currentPath));
            }
        }
    }

    private int processPaths(String[] args, int index, String currentArg, ArrayList<String> paths) {
        int localIndex = index;
        int count = 0;
        int i = 0;
        int max = currentArg.length();
        while (i < max) {
            switch (currentArg.charAt(i)) {
                case '[': {
                    ++count;
                    break;
                }
                case ']': {
                    --count;
                }
            }
            ++i;
        }
        if (count != 0) {
            if (count > 1) {
                throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
            }
            StringBuffer currentPath = new StringBuffer(currentArg);
            while (true) {
                if (localIndex >= args.length) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                }
                String nextArg = args[++localIndex];
                int i2 = 0;
                int max2 = nextArg.length();
                while (i2 < max2) {
                    switch (nextArg.charAt(i2)) {
                        case '[': {
                            if (count > 1) {
                                throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", nextArg));
                            }
                            ++count;
                            break;
                        }
                        case ']': {
                            --count;
                        }
                    }
                    ++i2;
                }
                if (count == 0) {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                    paths.add(currentPath.toString());
                    return localIndex - index;
                }
                if (count < 0) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", nextArg));
                }
                currentPath.append(' ');
                currentPath.append(nextArg);
            }
        }
        paths.add(currentArg);
        return localIndex - index;
    }

    private int processPaths(String[] args, int index, String currentArg, String[] paths) {
        int localIndex = index;
        int count = 0;
        int i = 0;
        int max = currentArg.length();
        while (i < max) {
            switch (currentArg.charAt(i)) {
                case '[': {
                    ++count;
                    break;
                }
                case ']': {
                    --count;
                }
            }
            ++i;
        }
        if (count != 0) {
            StringBuffer currentPath = new StringBuffer(currentArg);
            while (true) {
                if (++localIndex >= args.length) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                }
                String nextArg = args[localIndex];
                int i2 = 0;
                int max2 = nextArg.length();
                while (i2 < max2) {
                    switch (nextArg.charAt(i2)) {
                        case '[': {
                            if (count > 1) {
                                throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                            }
                            ++count;
                            break;
                        }
                        case ']': {
                            --count;
                        }
                    }
                    ++i2;
                }
                if (count == 0) {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                    paths[0] = currentPath.toString();
                    return localIndex - index;
                }
                if (count < 0) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                }
                currentPath.append(' ');
                currentPath.append(nextArg);
            }
        }
        paths[0] = currentArg;
        return localIndex - index;
    }

    public void relocalize() {
        this.relocalize(Locale.getDefault());
    }

    private void relocalize(Locale locale) {
        this.compilerLocale = locale;
        try {
            this.bundle = ResourceBundleFactory.getBundle(locale);
        }
        catch (MissingResourceException e) {
            System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + locale);
            throw e;
        }
    }

    public void setDestinationPath(String dest) {
        this.destinationPath = dest;
    }

    public void setLocale(Locale locale) {
        this.relocalize(locale);
    }

    protected void setPaths(ArrayList<String> bootclasspaths, String sourcepathClasspathArg, ArrayList<String> sourcepathClasspaths, ArrayList<String> classpaths, String modulePath, String moduleSourcepath, ArrayList<String> extdirsClasspaths, ArrayList<String> endorsedDirClasspaths, String customEncoding) {
        if (this.complianceLevel == 0L) {
            String version = this.options.get("org.eclipse.jdt.core.compiler.compliance");
            this.complianceLevel = CompilerOptions.versionToJdkLevel(version);
        }
        ArrayList<FileSystem.Classpath> allPaths = null;
        long jdkLevel = this.validateClasspathOptions(bootclasspaths, endorsedDirClasspaths, extdirsClasspaths);
        if (this.releaseVersion != null && this.complianceLevel < jdkLevel) {
            allPaths = new ArrayList();
            allPaths.add(FileSystem.getOlderSystemRelease(this.javaHomeCache.getAbsolutePath(), this.releaseVersion, null));
        } else {
            allPaths = this.handleBootclasspath(bootclasspaths, customEncoding);
        }
        ArrayList<FileSystem.Classpath> cp = this.handleClasspath(classpaths, customEncoding);
        ArrayList<FileSystem.Classpath> mp = this.handleModulepath(modulePath);
        ArrayList<FileSystem.Classpath> msp = this.handleModuleSourcepath(moduleSourcepath);
        ArrayList<FileSystem.Classpath> sourcepaths = new ArrayList<FileSystem.Classpath>();
        if (sourcepathClasspathArg != null) {
            this.processPathEntries(4, sourcepaths, sourcepathClasspathArg, customEncoding, true, false);
        }
        ArrayList<FileSystem.Classpath> extdirs = this.handleExtdirs(extdirsClasspaths);
        ArrayList<FileSystem.Classpath> endorsed = this.handleEndorseddirs(endorsedDirClasspaths);
        allPaths.addAll(0, endorsed);
        allPaths.addAll(extdirs);
        allPaths.addAll(sourcepaths);
        allPaths.addAll(cp);
        allPaths.addAll(mp);
        allPaths.addAll(msp);
        allPaths = FileSystem.ClasspathNormalizer.normalize(allPaths);
        this.checkedClasspaths = new FileSystem.Classpath[allPaths.size()];
        allPaths.toArray(this.checkedClasspaths);
        this.logger.logClasspath(this.checkedClasspaths);
        if (this.annotationPaths != null && "enabled".equals(this.options.get("org.eclipse.jdt.core.compiler.annotation.nullanalysis"))) {
            FileSystem.Classpath[] classpathArray = this.checkedClasspaths;
            int n = this.checkedClasspaths.length;
            int n2 = 0;
            while (n2 < n) {
                FileSystem.Classpath c = classpathArray[n2];
                if (c instanceof ClasspathJar) {
                    ((ClasspathJar)c).annotationPaths = this.annotationPaths;
                } else if (c instanceof ClasspathJrt) {
                    ((ClasspathJrt)c).annotationPaths = this.annotationPaths;
                }
                ++n2;
            }
        }
    }

    public static final boolean shouldIgnoreOptionalProblems(char[][] folderNames, char[] fileName) {
        if (folderNames == null || fileName == null) {
            return false;
        }
        int i = 0;
        int max = folderNames.length;
        while (i < max) {
            char[] folderName = folderNames[i];
            if (Main.isParentOf(folderName, fileName)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    protected long validateClasspathOptions(ArrayList<String> bootclasspaths, ArrayList<String> endorsedDirClasspaths, ArrayList<String> extdirsClasspaths) {
        long jdkLevel;
        if (this.complianceLevel > 0x340000L) {
            if (bootclasspaths != null && bootclasspaths.size() > 0) {
                throw new IllegalArgumentException(this.bind("configure.unsupportedOption", "-bootclasspath"));
            }
            if (extdirsClasspaths != null && extdirsClasspaths.size() > 0) {
                throw new IllegalArgumentException(this.bind("configure.unsupportedOption", "-extdirs"));
            }
            if (endorsedDirClasspaths != null && endorsedDirClasspaths.size() > 0) {
                throw new IllegalArgumentException(this.bind("configure.unsupportedOption", "-endorseddirs"));
            }
        }
        if ((jdkLevel = Util.getJDKLevel(this.getJavaHome())) < 0x350000L && this.releaseVersion != null) {
            throw new IllegalArgumentException(this.bind("configure.unsupportedReleaseOption"));
        }
        return jdkLevel;
    }

    protected void validateOptions(boolean didSpecifyCompliance) {
        String version;
        if (didSpecifyCompliance) {
            String source;
            version = this.options.get("org.eclipse.jdt.core.compiler.compliance");
            if (this.releaseVersion != null) {
                throw new IllegalArgumentException(this.bind("configure.unsupportedWithRelease", version));
            }
            if ("1.3".equals(version)) {
                if (!this.didSpecifySource) {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.3");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.1");
                }
            } else if ("1.4".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2");
                        }
                    } else if ("1.4".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.3");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2");
                    }
                }
            } else if ("1.5".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.5");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5");
                    }
                }
            } else if ("1.6".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if (("1.5".equals(source) || "1.6".equals(source)) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.6");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                    }
                }
            } else if ("1.7".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) || "1.6".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                        }
                    } else if ("1.7".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.7");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                    }
                }
            } else if ("1.8".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) || "1.6".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                        }
                    } else if ("1.7".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                        }
                    } else if ("1.8".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "1.8");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                    }
                }
            } else if ("9".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) || "1.6".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                        }
                    } else if ("1.7".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                        }
                    } else if ("1.8".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                        }
                    } else if ("9".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "9");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
                    }
                }
            } else if ("10".equals(version)) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) || "1.6".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                        }
                    } else if ("1.7".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                        }
                    } else if ("1.8".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                        }
                    } else if ("9".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
                        }
                    } else if ("10".equals(source) && !this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "10");
                    }
                } else {
                    this.options.put("org.eclipse.jdt.core.compiler.source", "10");
                    if (!this.didSpecifyTarget) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "10");
                    }
                }
            } else if (!this.didSpecifyTarget) {
                if (this.didSpecifySource) {
                    source = this.options.get("org.eclipse.jdt.core.compiler.source");
                    if ("1.3".equals(source) || "1.4".equals(source)) {
                        if (!this.didSpecifyTarget) {
                            this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                        }
                    } else if ("1.5".equals(source) || "1.6".equals(source)) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                    } else if (CompilerOptions.versionToJdkLevel(source) >= 0x330000L) {
                        this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", source);
                    }
                } else if (CompilerOptions.versionToJdkLevel(version) > 0x360000L) {
                    this.options.put("org.eclipse.jdt.core.compiler.source", version);
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", version);
                }
            }
        } else if (this.didSpecifySource) {
            version = this.options.get("org.eclipse.jdt.core.compiler.source");
            if ("1.4".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.4");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
                }
            } else if ("1.5".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.5");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5");
                }
            } else if ("1.6".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
                }
            } else if ("1.7".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.7");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
                }
            } else if ("1.8".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.8");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
                }
            } else if ("9".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "9");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "9");
                }
            } else if ("10".equals(version)) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", "10");
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "10");
                }
            } else if (CompilerOptions.versionToJdkLevel(version) > 0x360000L) {
                if (!didSpecifyCompliance) {
                    this.options.put("org.eclipse.jdt.core.compiler.compliance", version);
                }
                if (!this.didSpecifyTarget) {
                    this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", version);
                }
            }
        }
        String sourceVersion = this.options.get("org.eclipse.jdt.core.compiler.source");
        if (this.complianceLevel == 0L) {
            String compliance = this.options.get("org.eclipse.jdt.core.compiler.compliance");
            this.complianceLevel = CompilerOptions.versionToJdkLevel(compliance);
        }
        if (sourceVersion.equals("10") && this.complianceLevel < 0x360000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "10"));
        }
        if (sourceVersion.equals("9") && this.complianceLevel < 0x350000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "9"));
        }
        if (sourceVersion.equals("1.8") && this.complianceLevel < 0x340000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.8"));
        }
        if (sourceVersion.equals("1.7") && this.complianceLevel < 0x330000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.7"));
        }
        if (sourceVersion.equals("1.6") && this.complianceLevel < 0x320000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.6"));
        }
        if (sourceVersion.equals("1.5") && this.complianceLevel < 0x310000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.5"));
        }
        if (sourceVersion.equals("1.4") && this.complianceLevel < 0x300000L) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.4"));
        }
        long ver = CompilerOptions.versionToJdkLevel(sourceVersion);
        if (this.complianceLevel < ver) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get("org.eclipse.jdt.core.compiler.compliance"), sourceVersion));
        }
        if (this.enablePreview && this.complianceLevel != ClassFileConstants.getLatestJDKLevel()) {
            throw new IllegalArgumentException(this.bind("configure.unsupportedPreview"));
        }
        if (this.didSpecifyTarget) {
            String targetVersion = this.options.get("org.eclipse.jdt.core.compiler.codegen.targetPlatform");
            if ("jsr14".equals(targetVersion)) {
                if (CompilerOptions.versionToJdkLevel(sourceVersion) < 0x310000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForGenericSource", targetVersion, sourceVersion));
                }
            } else if ("cldc1.1".equals(targetVersion)) {
                if (this.didSpecifySource && CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x300000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleSourceForCldcTarget", targetVersion, sourceVersion));
                }
                if (this.complianceLevel >= 0x310000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForCldcTarget", targetVersion, sourceVersion));
                }
            } else {
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x340000L && CompilerOptions.versionToJdkLevel(targetVersion) < 0x340000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, "1.8"));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x330000L && CompilerOptions.versionToJdkLevel(targetVersion) < 0x330000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, "1.7"));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x320000L && CompilerOptions.versionToJdkLevel(targetVersion) < 0x320000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, "1.6"));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x310000L && CompilerOptions.versionToJdkLevel(targetVersion) < 0x310000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, "1.5"));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= 0x300000L && CompilerOptions.versionToJdkLevel(targetVersion) < 0x300000L) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, "1.4"));
                }
                if (this.complianceLevel < CompilerOptions.versionToJdkLevel(targetVersion)) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForTarget", this.options.get("org.eclipse.jdt.core.compiler.compliance"), targetVersion));
                }
            }
        }
    }

    public static class Logger {
        private PrintWriter err;
        private PrintWriter log;
        private Main main;
        private PrintWriter out;
        int tagBits;
        private static final String CLASS = "class";
        private static final String CLASS_FILE = "classfile";
        private static final String CLASSPATH = "classpath";
        private static final String CLASSPATH_FILE = "FILE";
        private static final String CLASSPATH_FOLDER = "FOLDER";
        private static final String CLASSPATH_ID = "id";
        private static final String CLASSPATH_JAR = "JAR";
        private static final String CLASSPATHS = "classpaths";
        private static final String COMMAND_LINE_ARGUMENT = "argument";
        private static final String COMMAND_LINE_ARGUMENTS = "command_line";
        private static final String COMPILER = "compiler";
        private static final String COMPILER_COPYRIGHT = "copyright";
        private static final String COMPILER_NAME = "name";
        private static final String COMPILER_VERSION = "version";
        public static final int EMACS = 2;
        private static final String ERROR = "ERROR";
        private static final String ERROR_TAG = "error";
        private static final String WARNING_TAG = "warning";
        private static final String EXCEPTION = "exception";
        private static final String EXTRA_PROBLEM_TAG = "extra_problem";
        private static final String EXTRA_PROBLEMS = "extra_problems";
        private static final HashtableOfInt FIELD_TABLE = new HashtableOfInt();
        private static final String KEY = "key";
        private static final String MESSAGE = "message";
        private static final String NUMBER_OF_CLASSFILES = "number_of_classfiles";
        private static final String NUMBER_OF_ERRORS = "errors";
        private static final String NUMBER_OF_LINES = "number_of_lines";
        private static final String NUMBER_OF_PROBLEMS = "problems";
        private static final String NUMBER_OF_TASKS = "tasks";
        private static final String NUMBER_OF_WARNINGS = "warnings";
        private static final String NUMBER_OF_INFOS = "infos";
        private static final String OPTION = "option";
        private static final String OPTIONS = "options";
        private static final String OUTPUT = "output";
        private static final String PACKAGE = "package";
        private static final String PATH = "path";
        private static final String PROBLEM_ARGUMENT = "argument";
        private static final String PROBLEM_ARGUMENT_VALUE = "value";
        private static final String PROBLEM_ARGUMENTS = "arguments";
        private static final String PROBLEM_CATEGORY_ID = "categoryID";
        private static final String ID = "id";
        private static final String PROBLEM_ID = "problemID";
        private static final String PROBLEM_LINE = "line";
        private static final String PROBLEM_OPTION_KEY = "optionKey";
        private static final String PROBLEM_MESSAGE = "message";
        private static final String PROBLEM_SEVERITY = "severity";
        private static final String PROBLEM_SOURCE_END = "charEnd";
        private static final String PROBLEM_SOURCE_START = "charStart";
        private static final String PROBLEM_SUMMARY = "problem_summary";
        private static final String PROBLEM_TAG = "problem";
        private static final String PROBLEMS = "problems";
        private static final String SOURCE = "source";
        private static final String SOURCE_CONTEXT = "source_context";
        private static final String SOURCE_END = "sourceEnd";
        private static final String SOURCE_START = "sourceStart";
        private static final String SOURCES = "sources";
        private static final String STATS = "stats";
        private static final String TASK = "task";
        private static final String TASKS = "tasks";
        private static final String TIME = "time";
        private static final String VALUE = "value";
        private static final String WARNING = "WARNING";
        private static final String INFO = "INFO";
        public static final int XML = 1;
        private static final String XML_DTD_DECLARATION = "<!DOCTYPE compiler PUBLIC \"-//Eclipse.org//DTD Eclipse JDT 3.2.006 Compiler//EN\" \"http://www.eclipse.org/jdt/core/compiler_32_006.dtd\">";

        static {
            try {
                Class<IProblem> c = IProblem.class;
                Field[] fields = c.getFields();
                int i = 0;
                int max = fields.length;
                while (i < max) {
                    Field field = fields[i];
                    if (field.getType().equals(Integer.TYPE)) {
                        Integer value = (Integer)field.get(null);
                        int key2 = value & 0x1FFFFF;
                        if (key2 == 0) {
                            key2 = Integer.MAX_VALUE;
                        }
                        FIELD_TABLE.put(key2, field.getName());
                    }
                    ++i;
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                e.printStackTrace();
            }
        }

        public Logger(Main main, PrintWriter out, PrintWriter err) {
            this.out = out;
            this.err = err;
            this.main = main;
        }

        public String buildFileName(String outputPath, String relativeFileName) {
            char fileSeparatorChar = File.separatorChar;
            String fileSeparator = File.separator;
            outputPath = outputPath.replace('/', fileSeparatorChar);
            StringBuffer outDir = new StringBuffer(outputPath);
            if (!outputPath.endsWith(fileSeparator)) {
                outDir.append(fileSeparator);
            }
            StringTokenizer tokenizer = new StringTokenizer(relativeFileName, fileSeparator);
            String token = tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()) {
                outDir.append(token).append(fileSeparator);
                token = tokenizer.nextToken();
            }
            return outDir.append(token).toString();
        }

        public void close() {
            if (this.log != null) {
                if ((this.tagBits & 1) != 0) {
                    this.endTag(COMPILER);
                    this.flush();
                }
                this.log.close();
            }
        }

        public void compiling() {
            this.printlnOut(this.main.bind("progress.compiling"));
        }

        private void endLoggingExtraProblems() {
            this.endTag(EXTRA_PROBLEMS);
        }

        private void endLoggingProblems() {
            this.endTag("problems");
        }

        public void endLoggingSource() {
            if ((this.tagBits & 1) != 0) {
                this.endTag(SOURCE);
            }
        }

        public void endLoggingSources() {
            if ((this.tagBits & 1) != 0) {
                this.endTag(SOURCES);
            }
        }

        public void endLoggingTasks() {
            if ((this.tagBits & 1) != 0) {
                this.endTag("tasks");
            }
        }

        private void endTag(String name) {
            if (this.log != null) {
                ((GenericXMLWriter)this.log).endTag(name, true, true);
            }
        }

        private String errorReportSource(CategorizedProblem problem, char[] unitSource, int bits) {
            char c;
            int length;
            int startPosition = problem.getSourceStart();
            int endPosition = problem.getSourceEnd();
            if (unitSource == null && problem.getOriginatingFileName() != null) {
                try {
                    unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
                }
                catch (IOException iOException) {}
            }
            if (startPosition > endPosition || startPosition < 0 && endPosition < 0 || unitSource == null || (length = unitSource.length) == 0) {
                return Messages.problem_noSourceInformation;
            }
            StringBuffer errorBuffer = new StringBuffer();
            if ((bits & 2) == 0) {
                errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(problem.getSourceLineNumber())));
                errorBuffer.append(Util.LINE_SEPARATOR);
            }
            errorBuffer.append('\t');
            int begin = startPosition >= length ? length - 1 : startPosition;
            while (begin > 0) {
                c = unitSource[begin - 1];
                if (c == '\n' || c == '\r') break;
                --begin;
            }
            int end = endPosition >= length ? length - 1 : endPosition;
            while (end + 1 < length) {
                c = unitSource[end + 1];
                if (c == '\r' || c == '\n') break;
                ++end;
            }
            while ((c = unitSource[begin]) == ' ' || c == '\t') {
                ++begin;
            }
            errorBuffer.append(unitSource, begin, end - begin + 1);
            errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
            int i = begin;
            while (i < startPosition) {
                errorBuffer.append(unitSource[i] == '\t' ? (char)'\t' : ' ');
                ++i;
            }
            i = startPosition;
            while (i <= (endPosition >= length ? length - 1 : endPosition)) {
                errorBuffer.append('^');
                ++i;
            }
            return errorBuffer.toString();
        }

        private void extractContext(CategorizedProblem problem, char[] unitSource) {
            char c;
            int length;
            int startPosition = problem.getSourceStart();
            int endPosition = problem.getSourceEnd();
            if (unitSource == null && problem.getOriginatingFileName() != null) {
                try {
                    unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
                }
                catch (IOException iOException) {}
            }
            if (startPosition > endPosition || startPosition < 0 && endPosition < 0 || unitSource == null || (length = unitSource.length) <= 0 || endPosition > length) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("value", Messages.problem_noSourceInformation);
                parameters.put(SOURCE_START, "-1");
                parameters.put(SOURCE_END, "-1");
                this.printTag(SOURCE_CONTEXT, parameters, true, true);
                return;
            }
            int begin = startPosition >= length ? length - 1 : startPosition;
            while (begin > 0) {
                c = unitSource[begin - 1];
                if (c == '\n' || c == '\r') break;
                --begin;
            }
            int end = endPosition >= length ? length - 1 : endPosition;
            while (end + 1 < length) {
                c = unitSource[end + 1];
                if (c == '\r' || c == '\n') break;
                ++end;
            }
            while ((c = unitSource[begin]) == ' ' || c == '\t') {
                ++begin;
            }
            while ((c = unitSource[end]) == ' ' || c == '\t') {
                --end;
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append(unitSource, begin, end - begin + 1);
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("value", String.valueOf(buffer));
            parameters.put(SOURCE_START, Integer.toString(startPosition - begin));
            parameters.put(SOURCE_END, Integer.toString(endPosition - begin));
            this.printTag(SOURCE_CONTEXT, parameters, true, true);
        }

        public void flush() {
            this.out.flush();
            this.err.flush();
            if (this.log != null) {
                this.log.flush();
            }
        }

        private String getFieldName(int id) {
            int key2 = id & 0x1FFFFF;
            if (key2 == 0) {
                key2 = Integer.MAX_VALUE;
            }
            return (String)FIELD_TABLE.get(key2);
        }

        private String getProblemOptionKey(int problemID) {
            int irritant = ProblemReporter.getIrritant(problemID);
            return CompilerOptions.optionKeyFromIrritant(irritant);
        }

        public void logAverage() {
            Arrays.sort(this.main.compilerStats);
            long lineCount = this.main.compilerStats[0].lineCount;
            int length = this.main.maxRepetition;
            long sum = 0L;
            long parseSum = 0L;
            long resolveSum = 0L;
            long analyzeSum = 0L;
            long generateSum = 0L;
            int i = 1;
            int max = length - 1;
            while (i < max) {
                CompilerStats stats = this.main.compilerStats[i];
                sum += stats.elapsedTime();
                parseSum += stats.parseTime;
                resolveSum += stats.resolveTime;
                analyzeSum += stats.analyzeTime;
                generateSum += stats.generateTime;
                ++i;
            }
            long time = sum / (long)(length - 2);
            long parseTime = parseSum / (long)(length - 2);
            long resolveTime = resolveSum / (long)(length - 2);
            long analyzeTime = analyzeSum / (long)(length - 2);
            long generateTime = generateSum / (long)(length - 2);
            this.printlnOut(this.main.bind("compile.averageTime", new String[]{String.valueOf(lineCount), String.valueOf(time), String.valueOf((double)((int)((double)lineCount * 10000.0 / (double)time)) / 10.0)}));
            if ((this.main.timing & 2) != 0) {
                this.printlnOut(this.main.bind("compile.detailedTime", new String[]{String.valueOf(parseTime), String.valueOf((double)((int)((double)parseTime * 1000.0 / (double)time)) / 10.0), String.valueOf(resolveTime), String.valueOf((double)((int)((double)resolveTime * 1000.0 / (double)time)) / 10.0), String.valueOf(analyzeTime), String.valueOf((double)((int)((double)analyzeTime * 1000.0 / (double)time)) / 10.0), String.valueOf(generateTime), String.valueOf((double)((int)((double)generateTime * 1000.0 / (double)time)) / 10.0)}));
            }
        }

        public void logClassFile(boolean generatePackagesStructure, String outputPath, String relativeFileName) {
            if ((this.tagBits & 1) != 0) {
                String fileName = null;
                if (generatePackagesStructure) {
                    fileName = this.buildFileName(outputPath, relativeFileName);
                } else {
                    char fileSeparatorChar = File.separatorChar;
                    String fileSeparator = File.separator;
                    outputPath = outputPath.replace('/', fileSeparatorChar);
                    int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
                    if (indexOfPackageSeparator == -1) {
                        fileName = outputPath.endsWith(fileSeparator) ? String.valueOf(outputPath) + relativeFileName : String.valueOf(outputPath) + fileSeparator + relativeFileName;
                    } else {
                        int length = relativeFileName.length();
                        fileName = outputPath.endsWith(fileSeparator) ? String.valueOf(outputPath) + relativeFileName.substring(indexOfPackageSeparator + 1, length) : String.valueOf(outputPath) + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
                    }
                }
                File f = new File(fileName);
                try {
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put(PATH, f.getCanonicalPath());
                    this.printTag(CLASS_FILE, parameters, true, true);
                }
                catch (IOException e) {
                    this.logNoClassFileCreated(outputPath, relativeFileName, e);
                }
            }
        }

        public void logClasspath(FileSystem.Classpath[] classpaths) {
            int length;
            if (classpaths == null) {
                return;
            }
            if ((this.tagBits & 1) != 0 && (length = classpaths.length) != 0) {
                this.printTag(CLASSPATHS, new HashMap<String, Object>(), true, false);
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                int i = 0;
                while (i < length) {
                    String classpath = classpaths[i].getPath();
                    parameters.put(PATH, classpath);
                    File f = new File(classpath);
                    String id = null;
                    if (f.isFile()) {
                        int kind = Util.archiveFormat(classpath);
                        switch (kind) {
                            case 0: {
                                id = CLASSPATH_JAR;
                                break;
                            }
                            default: {
                                id = CLASSPATH_FILE;
                                break;
                            }
                        }
                    } else if (f.isDirectory()) {
                        id = CLASSPATH_FOLDER;
                    }
                    if (id != null) {
                        parameters.put("id", id);
                        this.printTag(CLASSPATH, parameters, true, true);
                    }
                    ++i;
                }
                this.endTag(CLASSPATHS);
            }
        }

        public void logCommandLineArguments(String[] commandLineArguments) {
            int length;
            if (commandLineArguments == null) {
                return;
            }
            if ((this.tagBits & 1) != 0 && (length = commandLineArguments.length) != 0) {
                this.printTag(COMMAND_LINE_ARGUMENTS, new HashMap<String, Object>(), true, false);
                int i = 0;
                while (i < length) {
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("value", commandLineArguments[i]);
                    this.printTag("argument", parameters, true, true);
                    ++i;
                }
                this.endTag(COMMAND_LINE_ARGUMENTS);
            }
        }

        public void logException(Exception e) {
            String message;
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
            String stackTrace = writer.toString();
            if ((this.tagBits & 1) != 0) {
                LineNumberReader reader = new LineNumberReader(new StringReader(stackTrace));
                int i = 0;
                StringBuffer buffer = new StringBuffer();
                String message2 = e.getMessage();
                if (message2 != null) {
                    buffer.append(message2).append(Util.LINE_SEPARATOR);
                }
                try {
                    String line;
                    while ((line = reader.readLine()) != null && i < 4) {
                        buffer.append(line).append(Util.LINE_SEPARATOR);
                        ++i;
                    }
                    reader.close();
                }
                catch (IOException iOException) {}
                message2 = buffer.toString();
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", message2);
                parameters.put(CLASS, e.getClass());
                this.printTag(EXCEPTION, parameters, true, true);
            }
            if ((message = e.getMessage()) == null) {
                this.printlnErr(stackTrace);
            } else {
                this.printlnErr(message);
            }
        }

        private void logExtraProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount) {
            char[] originatingFileName = problem.getOriginatingFileName();
            if (originatingFileName == null) {
                String severity = problem.isError() ? "requestor.extraerror" : (problem.isInfo() ? "requestor.extrainfo" : "requestor.extrawarning");
                this.printErr(this.main.bind(severity, Integer.toString(globalErrorCount)));
                this.printErr(" ");
                this.printlnErr(problem.getMessage());
            } else {
                String fileName = new String(originatingFileName);
                if ((this.tagBits & 2) != 0) {
                    String severity = problem.isError() ? "output.emacs.error" : (problem.isInfo() ? "output.emacs.info" : "output.emacs.warning");
                    String result = String.valueOf(fileName) + ":" + problem.getSourceLineNumber() + ": " + this.main.bind(severity) + ": " + problem.getMessage();
                    this.printlnErr(result);
                    String errorReportSource = this.errorReportSource(problem, null, this.tagBits);
                    this.printlnErr(errorReportSource);
                } else {
                    if (localErrorCount == 0) {
                        this.printlnErr("----------");
                    }
                    String severity = problem.isError() ? "requestor.error" : (problem.isInfo() ? "requestor.info" : "requestor.warning");
                    this.printErr(this.main.bind(severity, Integer.toString(globalErrorCount), fileName));
                    String errorReportSource = this.errorReportSource(problem, null, 0);
                    this.printlnErr(errorReportSource);
                    this.printlnErr(problem.getMessage());
                    this.printlnErr("----------");
                }
            }
        }

        public void loggingExtraProblems(Main currentMain) {
            ArrayList<CategorizedProblem> problems = currentMain.extraProblems;
            int count = problems.size();
            int localProblemCount = 0;
            if (count != 0) {
                CategorizedProblem problem;
                int errors = 0;
                int warnings = 0;
                int infos = 0;
                int i = 0;
                while (i < count) {
                    problem = problems.get(i);
                    if (!this.main.isIgnored(problem)) {
                        ++currentMain.globalProblemsCount;
                        this.logExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
                        ++localProblemCount;
                        if (problem.isError()) {
                            ++errors;
                            ++currentMain.globalErrorsCount;
                        } else if (problem.isInfo()) {
                            ++currentMain.globalInfoCount;
                            ++infos;
                        } else {
                            ++currentMain.globalWarningsCount;
                            ++warnings;
                        }
                    }
                    ++i;
                }
                if ((this.tagBits & 1) != 0 && errors + warnings + infos != 0) {
                    this.startLoggingExtraProblems(count);
                    i = 0;
                    while (i < count) {
                        problem = problems.get(i);
                        if (!this.main.isIgnored(problem) && problem.getID() != 536871362) {
                            this.logXmlExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
                        }
                        ++i;
                    }
                    this.endLoggingExtraProblems();
                }
            }
        }

        public void logUnavaibleAPT(String className) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", this.main.bind("configure.unavailableAPT", className));
                this.printTag(ERROR_TAG, parameters, true, true);
            }
            this.printlnErr(this.main.bind("configure.unavailableAPT", className));
        }

        public void logIncorrectVMVersionForAnnotationProcessing() {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", this.main.bind("configure.incorrectVMVersionforAPT"));
                this.printTag(ERROR_TAG, parameters, true, true);
            }
            this.printlnErr(this.main.bind("configure.incorrectVMVersionforAPT"));
        }

        public void logNoClassFileCreated(String outputDir, String relativeFileName, IOException e) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", this.main.bind("output.noClassFileCreated", new String[]{outputDir, relativeFileName, e.getMessage()}));
                this.printTag(ERROR_TAG, parameters, true, true);
            }
            this.printlnErr(this.main.bind("output.noClassFileCreated", new String[]{outputDir, relativeFileName, e.getMessage()}));
        }

        public void logNumberOfClassFilesGenerated(int exportedClassFilesCounter) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("value", exportedClassFilesCounter);
                this.printTag(NUMBER_OF_CLASSFILES, parameters, true, true);
            }
            if (exportedClassFilesCounter == 1) {
                this.printlnOut(this.main.bind("compile.oneClassFileGenerated"));
            } else {
                this.printlnOut(this.main.bind("compile.severalClassFilesGenerated", String.valueOf(exportedClassFilesCounter)));
            }
        }

        public void logOptions(Map<String, String> options) {
            if ((this.tagBits & 1) != 0) {
                this.printTag(OPTIONS, new HashMap<String, Object>(), true, false);
                Set<Map.Entry<String, String>> entriesSet = options.entrySet();
                Map.Entry[] entries = entriesSet.toArray(new Map.Entry[entriesSet.size()]);
                Arrays.sort(entries, new Comparator<Map.Entry<String, String>>(){

                    @Override
                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                        Map.Entry<String, String> entry1 = o1;
                        Map.Entry<String, String> entry2 = o2;
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                });
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                int i = 0;
                int max = entries.length;
                while (i < max) {
                    Map.Entry entry = entries[i];
                    String key = (String)entry.getKey();
                    parameters.put(KEY, key);
                    parameters.put("value", entry.getValue());
                    this.printTag(OPTION, parameters, true, true);
                    ++i;
                }
                this.endTag(OPTIONS);
            }
        }

        public void logPendingError(String error) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", error);
                this.printTag(ERROR_TAG, parameters, true, true);
            }
            this.printlnErr(error);
        }

        public void logWarning(String message) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", message);
                this.printTag(WARNING_TAG, parameters, true, true);
            }
            this.printlnOut(message);
        }

        private void logProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount, char[] unitSource) {
            if (problem instanceof DefaultProblem) {
                ((DefaultProblem)problem).reportError();
            }
            if ((this.tagBits & 2) != 0) {
                String severity = problem.isError() ? "output.emacs.error" : (problem.isInfo() ? "output.emacs.info" : "output.emacs.warning");
                String result = String.valueOf(new String(problem.getOriginatingFileName())) + ":" + problem.getSourceLineNumber() + ": " + this.main.bind(severity) + ": " + problem.getMessage();
                this.printlnErr(result);
                String errorReportSource = this.errorReportSource(problem, unitSource, this.tagBits);
                if (errorReportSource.length() != 0) {
                    this.printlnErr(errorReportSource);
                }
            } else {
                if (localErrorCount == 0) {
                    this.printlnErr("----------");
                }
                String severity = problem.isError() ? "requestor.error" : (problem.isInfo() ? "requestor.info" : "requestor.warning");
                this.printErr(this.main.bind(severity, Integer.toString(globalErrorCount), new String(problem.getOriginatingFileName())));
                try {
                    String errorReportSource = this.errorReportSource(problem, unitSource, 0);
                    this.printlnErr(errorReportSource);
                    this.printlnErr(problem.getMessage());
                }
                catch (Exception exception) {
                    this.printlnErr(this.main.bind("requestor.notRetrieveErrorMessage", problem.toString()));
                }
                this.printlnErr("----------");
            }
        }

        public int logProblems(CategorizedProblem[] problems, char[] unitSource, Main currentMain) {
            int count = problems.length;
            int localErrorCount = 0;
            int localProblemCount = 0;
            if (count != 0) {
                CategorizedProblem problem;
                int errors = 0;
                int warnings = 0;
                int infos = 0;
                int tasks = 0;
                int i = 0;
                while (i < count) {
                    problem = problems[i];
                    if (problem != null) {
                        ++currentMain.globalProblemsCount;
                        this.logProblem(problem, localProblemCount, currentMain.globalProblemsCount, unitSource);
                        ++localProblemCount;
                        if (problem.isError()) {
                            ++localErrorCount;
                            ++errors;
                            ++currentMain.globalErrorsCount;
                        } else if (problem.getID() == 536871362) {
                            ++currentMain.globalTasksCount;
                            ++tasks;
                        } else if (problem.isInfo()) {
                            ++currentMain.globalInfoCount;
                            ++infos;
                        } else {
                            ++currentMain.globalWarningsCount;
                            ++warnings;
                        }
                    }
                    ++i;
                }
                if ((this.tagBits & 1) != 0) {
                    if (errors + warnings + infos != 0) {
                        this.startLoggingProblems(errors, warnings, infos);
                        i = 0;
                        while (i < count) {
                            problem = problems[i];
                            if (problem != null && problem.getID() != 536871362) {
                                this.logXmlProblem(problem, unitSource);
                            }
                            ++i;
                        }
                        this.endLoggingProblems();
                    }
                    if (tasks != 0) {
                        this.startLoggingTasks(tasks);
                        i = 0;
                        while (i < count) {
                            problem = problems[i];
                            if (problem != null && problem.getID() == 536871362) {
                                this.logXmlTask(problem, unitSource);
                            }
                            ++i;
                        }
                        this.endLoggingTasks();
                    }
                }
            }
            return localErrorCount;
        }

        public void logProblemsSummary(int globalProblemsCount, int globalErrorsCount, int globalWarningsCount, int globalInfoCount, int globalTasksCount) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("problems", globalProblemsCount);
                parameters.put(NUMBER_OF_ERRORS, globalErrorsCount);
                parameters.put(NUMBER_OF_WARNINGS, globalWarningsCount);
                parameters.put(NUMBER_OF_INFOS, globalInfoCount);
                parameters.put("tasks", globalTasksCount);
                this.printTag(PROBLEM_SUMMARY, parameters, true, true);
            }
            if (globalProblemsCount == 1) {
                String message = null;
                message = globalErrorsCount == 1 ? this.main.bind("compile.oneError") : (globalInfoCount == 1 ? this.main.bind("compile.oneInfo") : this.main.bind("compile.oneWarning"));
                this.printErr(this.main.bind("compile.oneProblem", message));
            } else {
                int warningsNumber;
                String errorMessage = null;
                String warningMessage = null;
                String infoMessage = null;
                if (globalErrorsCount > 0) {
                    errorMessage = globalErrorsCount == 1 ? this.main.bind("compile.oneError") : this.main.bind("compile.severalErrors", String.valueOf(globalErrorsCount));
                }
                if ((warningsNumber = globalWarningsCount + globalTasksCount) > 0) {
                    warningMessage = warningsNumber == 1 ? this.main.bind("compile.oneWarning") : this.main.bind("compile.severalWarnings", String.valueOf(warningsNumber));
                }
                if (globalInfoCount == 1) {
                    infoMessage = this.main.bind("compile.oneInfo");
                } else if (globalInfoCount > 1) {
                    infoMessage = this.main.bind("compile.severalInfos", String.valueOf(globalInfoCount));
                }
                if (globalProblemsCount == globalInfoCount || globalProblemsCount == globalErrorsCount || globalProblemsCount == globalWarningsCount) {
                    String msg = errorMessage != null ? errorMessage : (warningMessage != null ? warningMessage : infoMessage);
                    this.printErr(this.main.bind("compile.severalProblemsErrorsOrWarnings", String.valueOf(globalProblemsCount), msg));
                } else if (globalInfoCount == 0) {
                    this.printErr(this.main.bind("compile.severalProblemsErrorsAndWarnings", new String[]{String.valueOf(globalProblemsCount), errorMessage, warningMessage}));
                } else {
                    if (errorMessage == null) {
                        errorMessage = this.main.bind("compile.severalErrors", String.valueOf(globalErrorsCount));
                    }
                    if (warningMessage == null) {
                        warningMessage = this.main.bind("compile.severalWarnings", String.valueOf(warningsNumber));
                    }
                    this.printErr(this.main.bind("compile.severalProblems", new String[]{String.valueOf(globalProblemsCount), errorMessage, warningMessage, infoMessage}));
                }
            }
            if (this.main.failOnWarning && globalWarningsCount > 0) {
                this.printErr("\n");
                this.printErr(this.main.bind("compile.failOnWarning"));
            }
            if ((this.tagBits & 1) == 0) {
                this.printlnErr();
            }
        }

        public void logProgress() {
            this.printOut('.');
        }

        public void logRepetition(int i, int repetitions) {
            this.printlnOut(this.main.bind("compile.repetition", String.valueOf(i + 1), String.valueOf(repetitions)));
        }

        public void logTiming(CompilerStats compilerStats) {
            long time = compilerStats.elapsedTime();
            long lineCount = compilerStats.lineCount;
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("value", time);
                this.printTag(TIME, parameters, true, true);
                parameters.put("value", lineCount);
                this.printTag(NUMBER_OF_LINES, parameters, true, true);
            }
            if (lineCount != 0L) {
                this.printlnOut(this.main.bind("compile.instantTime", new String[]{String.valueOf(lineCount), String.valueOf(time), String.valueOf((double)((int)((double)lineCount * 10000.0 / (double)time)) / 10.0)}));
            } else {
                this.printlnOut(this.main.bind("compile.totalTime", new String[]{String.valueOf(time)}));
            }
            if ((this.main.timing & 2) != 0) {
                this.printlnOut(this.main.bind("compile.detailedTime", new String[]{String.valueOf(compilerStats.parseTime), String.valueOf((double)((int)((double)compilerStats.parseTime * 1000.0 / (double)time)) / 10.0), String.valueOf(compilerStats.resolveTime), String.valueOf((double)((int)((double)compilerStats.resolveTime * 1000.0 / (double)time)) / 10.0), String.valueOf(compilerStats.analyzeTime), String.valueOf((double)((int)((double)compilerStats.analyzeTime * 1000.0 / (double)time)) / 10.0), String.valueOf(compilerStats.generateTime), String.valueOf((double)((int)((double)compilerStats.generateTime * 1000.0 / (double)time)) / 10.0)}));
            }
        }

        public void logUsage(String usage) {
            this.printlnOut(usage);
        }

        public void logVersion(boolean printToOut) {
            if (this.log != null && (this.tagBits & 1) == 0) {
                String version = this.main.bind("misc.version", new String[]{this.main.bind("compiler.name"), this.main.bind("compiler.version"), this.main.bind("compiler.copyright")});
                this.log.println("# " + version);
                if (printToOut) {
                    this.out.println(version);
                    this.out.flush();
                }
            } else if (printToOut) {
                String version = this.main.bind("misc.version", new String[]{this.main.bind("compiler.name"), this.main.bind("compiler.version"), this.main.bind("compiler.copyright")});
                this.out.println(version);
                this.out.flush();
            }
        }

        public void logWrongJDK() {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("message", this.main.bind("configure.requiresJDK1.2orAbove"));
                this.printTag(ERROR, parameters, true, true);
            }
            this.printlnErr(this.main.bind("configure.requiresJDK1.2orAbove"));
        }

        private void logXmlExtraProblem(CategorizedProblem problem, int globalErrorCount, int localErrorCount) {
            int sourceStart = problem.getSourceStart();
            int sourceEnd = problem.getSourceEnd();
            boolean isError = problem.isError();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(PROBLEM_SEVERITY, isError ? ERROR : (problem.isInfo() ? INFO : WARNING));
            parameters.put(PROBLEM_LINE, problem.getSourceLineNumber());
            parameters.put(PROBLEM_SOURCE_START, sourceStart);
            parameters.put(PROBLEM_SOURCE_END, sourceEnd);
            this.printTag(EXTRA_PROBLEM_TAG, parameters, true, false);
            parameters.put("value", problem.getMessage());
            this.printTag("message", parameters, true, true);
            this.extractContext(problem, null);
            this.endTag(EXTRA_PROBLEM_TAG);
        }

        private void logXmlProblem(CategorizedProblem problem, char[] unitSource) {
            int severity;
            int sourceStart = problem.getSourceStart();
            int sourceEnd = problem.getSourceEnd();
            int id = problem.getID();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", this.getFieldName(id));
            parameters.put(PROBLEM_ID, id);
            boolean isError = problem.isError();
            int n = severity = isError ? 1 : 0;
            parameters.put(PROBLEM_SEVERITY, isError ? ERROR : (problem.isInfo() ? INFO : WARNING));
            parameters.put(PROBLEM_LINE, problem.getSourceLineNumber());
            parameters.put(PROBLEM_SOURCE_START, sourceStart);
            parameters.put(PROBLEM_SOURCE_END, sourceEnd);
            String problemOptionKey = this.getProblemOptionKey(id);
            if (problemOptionKey != null) {
                parameters.put(PROBLEM_OPTION_KEY, problemOptionKey);
            }
            int categoryID = ProblemReporter.getProblemCategory(severity, id);
            parameters.put(PROBLEM_CATEGORY_ID, categoryID);
            this.printTag(PROBLEM_TAG, parameters, true, false);
            parameters.put("value", problem.getMessage());
            this.printTag("message", parameters, true, true);
            this.extractContext(problem, unitSource);
            String[] arguments = problem.getArguments();
            int length = arguments.length;
            if (length != 0) {
                parameters = new HashMap();
                this.printTag(PROBLEM_ARGUMENTS, parameters, true, false);
                int i = 0;
                while (i < length) {
                    parameters = new HashMap();
                    parameters.put("value", arguments[i]);
                    this.printTag("argument", parameters, true, true);
                    ++i;
                }
                this.endTag(PROBLEM_ARGUMENTS);
            }
            this.endTag(PROBLEM_TAG);
        }

        private void logXmlTask(CategorizedProblem problem, char[] unitSource) {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(PROBLEM_LINE, problem.getSourceLineNumber());
            parameters.put(PROBLEM_SOURCE_START, problem.getSourceStart());
            parameters.put(PROBLEM_SOURCE_END, problem.getSourceEnd());
            String problemOptionKey = this.getProblemOptionKey(problem.getID());
            if (problemOptionKey != null) {
                parameters.put(PROBLEM_OPTION_KEY, problemOptionKey);
            }
            this.printTag(TASK, parameters, true, false);
            parameters.put("value", problem.getMessage());
            this.printTag("message", parameters, true, true);
            this.extractContext(problem, unitSource);
            this.endTag(TASK);
        }

        private void printErr(String s) {
            this.err.print(s);
            if ((this.tagBits & 1) == 0 && this.log != null) {
                this.log.print(s);
            }
        }

        private void printlnErr() {
            this.err.println();
            if ((this.tagBits & 1) == 0 && this.log != null) {
                this.log.println();
            }
        }

        private void printlnErr(String s) {
            this.err.println(s);
            if ((this.tagBits & 1) == 0 && this.log != null) {
                this.log.println(s);
            }
        }

        private void printlnOut(String s) {
            this.out.println(s);
            if ((this.tagBits & 1) == 0 && this.log != null) {
                this.log.println(s);
            }
        }

        public void printNewLine() {
            this.out.println();
        }

        private void printOut(char c) {
            this.out.print(c);
        }

        public void printStats() {
            boolean isTimed;
            boolean bl = isTimed = (this.main.timing & 1) != 0;
            if ((this.tagBits & 1) != 0) {
                this.printTag(STATS, new HashMap<String, Object>(), true, false);
            }
            if (isTimed) {
                CompilerStats compilerStats = this.main.batchCompiler.stats;
                compilerStats.startTime = this.main.startTime;
                compilerStats.endTime = System.currentTimeMillis();
                this.logTiming(compilerStats);
            }
            if (this.main.globalProblemsCount > 0) {
                this.logProblemsSummary(this.main.globalProblemsCount, this.main.globalErrorsCount, this.main.globalWarningsCount, this.main.globalInfoCount, this.main.globalTasksCount);
            }
            if (this.main.exportedClassFilesCounter != 0 && (this.main.showProgress || isTimed || this.main.verbose)) {
                this.logNumberOfClassFilesGenerated(this.main.exportedClassFilesCounter);
            }
            if ((this.tagBits & 1) != 0) {
                this.endTag(STATS);
            }
        }

        private void printTag(String name, HashMap<String, Object> params, boolean insertNewLine, boolean closeTag) {
            if (this.log != null) {
                ((GenericXMLWriter)this.log).printTag(name, params, true, insertNewLine, closeTag);
            }
            if (params != null) {
                params.clear();
            }
        }

        public void setEmacs() {
            this.tagBits |= 2;
        }

        public void setLog(String logFileName) {
            Date date = new Date();
            DateFormat dateFormat = DateFormat.getDateTimeInstance(3, 1, Locale.getDefault());
            try {
                int index = logFileName.lastIndexOf(46);
                if (index != -1) {
                    if (logFileName.substring(index).toLowerCase().equals(".xml")) {
                        this.log = new GenericXMLWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(logFileName, false), "UTF-8"), Util.LINE_SEPARATOR, true);
                        this.tagBits |= 1;
                        this.log.println("<!-- " + dateFormat.format(date) + " -->");
                        this.log.println(XML_DTD_DECLARATION);
                        HashMap<String, Object> parameters = new HashMap<String, Object>();
                        parameters.put(COMPILER_NAME, this.main.bind("compiler.name"));
                        parameters.put(COMPILER_VERSION, this.main.bind("compiler.version"));
                        parameters.put(COMPILER_COPYRIGHT, this.main.bind("compiler.copyright"));
                        this.printTag(COMPILER, parameters, true, false);
                    } else {
                        this.log = new PrintWriter(new FileOutputStream(logFileName, false));
                        this.log.println("# " + dateFormat.format(date));
                    }
                } else {
                    this.log = new PrintWriter(new FileOutputStream(logFileName, false));
                    this.log.println("# " + dateFormat.format(date));
                }
            }
            catch (FileNotFoundException e) {
                throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLog", logFileName), e);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLogInvalidEncoding", logFileName), e);
            }
        }

        private void startLoggingExtraProblems(int count) {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("problems", count);
            this.printTag(EXTRA_PROBLEMS, parameters, true, false);
        }

        private void startLoggingProblems(int errors, int warnings, int infos) {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("problems", errors + warnings);
            parameters.put(NUMBER_OF_ERRORS, errors);
            parameters.put(NUMBER_OF_WARNINGS, warnings);
            parameters.put(NUMBER_OF_INFOS, infos);
            this.printTag("problems", parameters, true, false);
        }

        public void startLoggingSource(CompilationResult compilationResult) {
            if ((this.tagBits & 1) != 0) {
                ICompilationUnit compilationUnit = compilationResult.compilationUnit;
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                if (compilationUnit != null) {
                    char[][] packageName;
                    char[] fileName = compilationUnit.getFileName();
                    File f = new File(new String(fileName));
                    if (fileName != null) {
                        parameters.put(PATH, f.getAbsolutePath());
                    }
                    if ((packageName = compilationResult.packageName) != null) {
                        parameters.put(PACKAGE, new String(CharOperation.concatWith(packageName, File.separatorChar)));
                    }
                    CompilationUnit unit = (CompilationUnit)compilationUnit;
                    String destinationPath = unit.destinationPath;
                    if (destinationPath == null) {
                        destinationPath = this.main.destinationPath;
                    }
                    if (destinationPath != null && destinationPath != Main.NONE) {
                        if (File.separatorChar == '/') {
                            parameters.put(OUTPUT, destinationPath);
                        } else {
                            parameters.put(OUTPUT, destinationPath.replace('/', File.separatorChar));
                        }
                    }
                }
                this.printTag(SOURCE, parameters, true, false);
            }
        }

        public void startLoggingSources() {
            if ((this.tagBits & 1) != 0) {
                this.printTag(SOURCES, new HashMap<String, Object>(), true, false);
            }
        }

        public void startLoggingTasks(int tasks) {
            if ((this.tagBits & 1) != 0) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("tasks", tasks);
                this.printTag("tasks", parameters, true, false);
            }
        }
    }

    public static class ResourceBundleFactory {
        private static HashMap<Locale, ResourceBundle> Cache = new HashMap();

        public static synchronized ResourceBundle getBundle(Locale locale) {
            ResourceBundle bundle = Cache.get(locale);
            if (bundle == null) {
                bundle = ResourceBundle.getBundle(Main.bundleName, locale);
                Cache.put(locale, bundle);
            }
            return bundle;
        }
    }
}

