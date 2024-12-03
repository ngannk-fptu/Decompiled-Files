/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.AntClassLoader
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.RuntimeConfigurable
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.taskdefs.Execute
 *  org.apache.tools.ant.taskdefs.Javac
 *  org.apache.tools.ant.taskdefs.MatchingTask
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.Reference
 *  org.apache.tools.ant.util.FileNameMapper
 *  org.apache.tools.ant.util.GlobPatternMapper
 *  org.apache.tools.ant.util.SourceFileScanner
 */
package org.codehaus.groovy.ant;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyResourceLoader;
import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.GroovyInternalPosixParser;
import groovyjarjarcommonscli.Options;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.codehaus.groovy.ant.FileSystemCompilerFacade;
import org.codehaus.groovy.ant.LoggingHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceExtensionHandler;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.codehaus.groovy.tools.ErrorReporter;
import org.codehaus.groovy.tools.FileSystemCompiler;
import org.codehaus.groovy.tools.RootLoader;
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit;

public class Groovyc
extends MatchingTask {
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private final LoggingHelper log = new LoggingHelper((Task)this);
    private Path src;
    private File destDir;
    private Path compileClasspath;
    private Path compileSourcepath;
    private String encoding;
    private boolean stacktrace = false;
    private boolean verbose = false;
    private boolean includeAntRuntime = true;
    private boolean includeJavaRuntime = false;
    private boolean fork = false;
    private File forkJavaHome;
    private String forkedExecutable = null;
    private String memoryInitialSize;
    private String memoryMaximumSize;
    private String scriptExtension = "*.groovy";
    private String targetBytecode = null;
    protected boolean failOnError = true;
    protected boolean listFiles = false;
    protected File[] compileList = new File[0];
    private String updatedProperty;
    private String errorProperty;
    private boolean taskSuccess = true;
    private boolean includeDestClasses = true;
    protected CompilerConfiguration configuration;
    private Javac javac;
    private boolean jointCompilation;
    private List<File> temporaryFiles = new ArrayList<File>(2);
    private File stubDir;
    private boolean keepStubs;
    private boolean forceLookupUnnamedFiles;
    private boolean useIndy;
    private String scriptBaseClass;
    private String configscript;
    private Set<String> scriptExtensions = new LinkedHashSet<String>();

    public Path createSrc() {
        if (this.src == null) {
            this.src = new Path(this.getProject());
        }
        return this.src.createPath();
    }

    protected Path recreateSrc() {
        this.src = null;
        return this.createSrc();
    }

    public void setSrcdir(Path srcDir) {
        if (this.src == null) {
            this.src = srcDir;
        } else {
            this.src.append(srcDir);
        }
    }

    public Path getSrcdir() {
        return this.src;
    }

    public void setScriptExtension(String scriptExtension) {
        this.scriptExtension = scriptExtension.startsWith("*.") ? scriptExtension : (scriptExtension.startsWith(".") ? "*" + scriptExtension : "*." + scriptExtension);
    }

    public String getScriptExtension() {
        return this.scriptExtension;
    }

    public void setTargetBytecode(String version) {
        if ("1.4".equals(version) || "1.5".equals(version)) {
            this.targetBytecode = version;
        }
    }

    public String getTargetBytecode() {
        return this.targetBytecode;
    }

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setSourcepath(Path sourcepath) {
        if (this.compileSourcepath == null) {
            this.compileSourcepath = sourcepath;
        } else {
            this.compileSourcepath.append(sourcepath);
        }
    }

    public Path getSourcepath() {
        return this.compileSourcepath;
    }

    public Path createSourcepath() {
        if (this.compileSourcepath == null) {
            this.compileSourcepath = new Path(this.getProject());
        }
        return this.compileSourcepath.createPath();
    }

    public void setSourcepathRef(Reference r) {
        this.createSourcepath().setRefid(r);
    }

    public void setClasspath(Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        } else {
            this.compileClasspath.append(classpath);
        }
    }

    public Path getClasspath() {
        return this.compileClasspath;
    }

    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setListfiles(boolean list) {
        this.listFiles = list;
    }

    public boolean getListfiles() {
        return this.listFiles;
    }

    public void setFailonerror(boolean fail) {
        this.failOnError = fail;
    }

    public void setProceed(boolean proceed) {
        this.failOnError = !proceed;
    }

    public boolean getFailonerror() {
        return this.failOnError;
    }

    public void setMemoryInitialSize(String memoryInitialSize) {
        this.memoryInitialSize = memoryInitialSize;
    }

    public String getMemoryInitialSize() {
        return this.memoryInitialSize;
    }

    public void setMemoryMaximumSize(String memoryMaximumSize) {
        this.memoryMaximumSize = memoryMaximumSize;
    }

    public String getMemoryMaximumSize() {
        return this.memoryMaximumSize;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setIncludeantruntime(boolean include) {
        this.includeAntRuntime = include;
    }

    public boolean getIncludeantruntime() {
        return this.includeAntRuntime;
    }

    public void setIncludejavaruntime(boolean include) {
        this.includeJavaRuntime = include;
    }

    public boolean getIncludejavaruntime() {
        return this.includeJavaRuntime;
    }

    public void setFork(boolean f) {
        this.fork = f;
    }

    public void setJavaHome(File home) {
        this.forkJavaHome = home;
    }

    public void setExecutable(String forkExecPath) {
        this.forkedExecutable = forkExecPath;
    }

    public String getExecutable() {
        return this.forkedExecutable;
    }

    public void setUpdatedProperty(String updatedProperty) {
        this.updatedProperty = updatedProperty;
    }

    public void setErrorProperty(String errorProperty) {
        this.errorProperty = errorProperty;
    }

    public void setIncludeDestClasses(boolean includeDestClasses) {
        this.includeDestClasses = includeDestClasses;
    }

    public boolean isIncludeDestClasses() {
        return this.includeDestClasses;
    }

    public boolean getTaskSuccess() {
        return this.taskSuccess;
    }

    public void addConfiguredJavac(Javac javac) {
        this.javac = javac;
        this.jointCompilation = true;
    }

    public void setStacktrace(boolean stacktrace) {
        this.stacktrace = stacktrace;
    }

    public void setIndy(boolean useIndy) {
        this.useIndy = useIndy;
    }

    public boolean getIndy() {
        return this.useIndy;
    }

    public void setScriptBaseClass(String scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public String getScriptBaseClass() {
        return this.scriptBaseClass;
    }

    public String getConfigscript() {
        return this.configscript;
    }

    public void setConfigscript(String configscript) {
        this.configscript = configscript;
    }

    public void setStubdir(File stubDir) {
        this.jointCompilation = true;
        this.stubDir = stubDir;
    }

    public File getStubdir() {
        return this.stubDir;
    }

    public void setKeepStubs(boolean keepStubs) {
        this.keepStubs = keepStubs;
    }

    public boolean getKeepStubs() {
        return this.keepStubs;
    }

    public void setForceLookupUnnamedFiles(boolean forceLookupUnnamedFiles) {
        this.forceLookupUnnamedFiles = forceLookupUnnamedFiles;
    }

    public boolean getForceLookupUnnamedFiles() {
        return this.forceLookupUnnamedFiles;
    }

    public void execute() throws BuildException {
        String[] list;
        this.checkParameters();
        this.resetFileLists();
        this.loadRegisteredScriptExtensions();
        if (this.javac != null) {
            this.jointCompilation = true;
        }
        for (String filename : list = this.src.list()) {
            File file = this.getProject().resolveFile(filename);
            if (!file.exists()) {
                throw new BuildException("srcdir \"" + file.getPath() + "\" does not exist!", this.getLocation());
            }
            DirectoryScanner ds = this.getDirectoryScanner(file);
            String[] files = ds.getIncludedFiles();
            this.scanDir(file, this.destDir != null ? this.destDir : file, files);
        }
        this.compile();
        if (this.updatedProperty != null && this.taskSuccess && this.compileList.length != 0) {
            this.getProject().setNewProperty(this.updatedProperty, "true");
        }
    }

    protected void resetFileLists() {
        this.compileList = new File[0];
        this.scriptExtensions = new LinkedHashSet<String>();
    }

    protected void scanDir(File srcDir, File destDir, String[] files) {
        File[] newFiles;
        GlobPatternMapper m = new GlobPatternMapper();
        SourceFileScanner sfs = new SourceFileScanner((Task)this);
        for (String extension : this.getScriptExtensions()) {
            m.setFrom("*." + extension);
            m.setTo("*.class");
            newFiles = sfs.restrictAsFiles(files, srcDir, destDir, (FileNameMapper)m);
            this.addToCompileList(newFiles);
        }
        if (this.jointCompilation) {
            m.setFrom("*.java");
            m.setTo("*.class");
            newFiles = sfs.restrictAsFiles(files, srcDir, destDir, (FileNameMapper)m);
            this.addToCompileList(newFiles);
        }
    }

    protected void addToCompileList(File[] newFiles) {
        if (newFiles.length > 0) {
            File[] newCompileList = new File[this.compileList.length + newFiles.length];
            System.arraycopy(this.compileList, 0, newCompileList, 0, this.compileList.length);
            System.arraycopy(newFiles, 0, newCompileList, this.compileList.length, newFiles.length);
            this.compileList = newCompileList;
        }
    }

    public File[] getFileList() {
        return this.compileList;
    }

    protected void checkParameters() throws BuildException {
        if (this.src == null) {
            throw new BuildException("srcdir attribute must be set!", this.getLocation());
        }
        if (this.src.size() == 0) {
            throw new BuildException("srcdir attribute must be set!", this.getLocation());
        }
        if (this.destDir != null && !this.destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", this.getLocation());
        }
        if (this.encoding != null && !Charset.isSupported(this.encoding)) {
            throw new BuildException("encoding \"" + this.encoding + "\" not supported.");
        }
    }

    private void listFiles() {
        if (this.listFiles) {
            for (File srcFile : this.compileList) {
                this.log.info(srcFile.getAbsolutePath());
            }
        }
    }

    private List<String> extractJointOptions(Path classpath) {
        ArrayList<String> jointOptions = new ArrayList<String>();
        if (!this.jointCompilation) {
            return jointOptions;
        }
        RuntimeConfigurable rc = this.javac.getRuntimeConfigurableWrapper();
        Iterator iterator = rc.getAttributeMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o1;
            Map.Entry e = o1 = iterator.next();
            String key = e.getKey().toString();
            String value = this.getProject().replaceProperties(e.getValue().toString());
            if (key.contains("debug")) {
                String level = "";
                if (this.javac.getDebugLevel() != null) {
                    level = ":" + this.javac.getDebugLevel();
                }
                jointOptions.add("-Fg" + level);
                continue;
            }
            if (key.contains("debugLevel")) continue;
            if (key.contains("nowarn") || key.contains("verbose") || key.contains("deprecation")) {
                if (!"on".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"yes".equalsIgnoreCase(value)) continue;
                jointOptions.add("-F" + key);
                continue;
            }
            if (key.contains("classpath")) {
                classpath.add(this.javac.getClasspath());
                continue;
            }
            if (key.contains("depend") || key.contains("extdirs") || key.contains("encoding") || key.contains("source") || key.contains("target") || key.contains("verbose")) {
                jointOptions.add("-J" + key + "=" + value);
                continue;
            }
            this.log.warn("The option " + key + " cannot be set on the contained <javac> element. The option will be ignored");
        }
        Enumeration children = rc.getChildren();
        while (children.hasMoreElements()) {
            RuntimeConfigurable childrc = (RuntimeConfigurable)children.nextElement();
            if (!childrc.getElementTag().equals("compilerarg")) continue;
            for (Map.Entry o : childrc.getAttributeMap().entrySet()) {
                Map.Entry e = o;
                String key = e.getKey().toString();
                if (!key.equals("value")) continue;
                String value = this.getProject().replaceProperties(e.getValue().toString());
                StringTokenizer st = new StringTokenizer(value, " ");
                while (st.hasMoreTokens()) {
                    String replaced;
                    String optionStr = st.nextToken();
                    if (optionStr.equals(replaced = optionStr.replace("-X", "-FX"))) {
                        replaced = optionStr.replace("-W", "-FW");
                    }
                    jointOptions.add(replaced);
                }
            }
        }
        return jointOptions;
    }

    private void doForkCommandLineList(List<String> commandLineList, Path classpath, String separator) {
        if (!this.fork) {
            return;
        }
        if (this.includeAntRuntime) {
            classpath.addExisting(new Path(this.getProject()).concatSystemClasspath("last"));
        }
        if (this.includeJavaRuntime) {
            classpath.addJavaRuntime();
        }
        if (this.forkedExecutable != null && !this.forkedExecutable.equals("")) {
            commandLineList.add(this.forkedExecutable);
        } else {
            String javaHome = this.forkJavaHome != null ? this.forkJavaHome.getPath() : System.getProperty("java.home");
            commandLineList.add(javaHome + separator + "bin" + separator + "java");
        }
        commandLineList.add("-classpath");
        commandLineList.add(this.getClasspathRelative(classpath));
        String fileEncodingProp = System.getProperty("file.encoding");
        if (fileEncodingProp != null && !fileEncodingProp.equals("")) {
            commandLineList.add("-Dfile.encoding=" + fileEncodingProp);
        }
        if (this.targetBytecode != null) {
            commandLineList.add("-Dgroovy.target.bytecode=" + this.targetBytecode);
        }
        if (this.memoryInitialSize != null && !this.memoryInitialSize.equals("")) {
            commandLineList.add("-Xms" + this.memoryInitialSize);
        }
        if (this.memoryMaximumSize != null && !this.memoryMaximumSize.equals("")) {
            commandLineList.add("-Xmx" + this.memoryMaximumSize);
        }
        if (!"*.groovy".equals(this.getScriptExtension())) {
            String tmpExtension = this.getScriptExtension();
            if (tmpExtension.startsWith("*.")) {
                tmpExtension = tmpExtension.substring(1);
            }
            commandLineList.add("-Dgroovy.default.scriptExtension=" + tmpExtension);
        }
        commandLineList.add(FileSystemCompilerFacade.class.getName());
        if (this.forceLookupUnnamedFiles) {
            commandLineList.add("--forceLookupUnnamedFiles");
        }
    }

    private String getClasspathRelative(Path classpath) {
        String baseDir = this.getProject().getBaseDir().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        for (String next : classpath.list()) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            if (next.startsWith(baseDir)) {
                sb.append(".").append(next.substring(baseDir.length()));
                continue;
            }
            sb.append(next);
        }
        return sb.toString();
    }

    private void doNormalCommandLineList(List<String> commandLineList, List<String> jointOptions, Path classpath) {
        if (!this.fork) {
            commandLineList.add("--classpath");
            commandLineList.add(classpath.toString());
        }
        if (this.jointCompilation) {
            commandLineList.add("-j");
            commandLineList.addAll(jointOptions);
        }
        if (this.destDir != null) {
            commandLineList.add("-d");
            commandLineList.add(this.destDir.getPath());
        }
        if (this.encoding != null) {
            commandLineList.add("--encoding");
            commandLineList.add(this.encoding);
        }
        if (this.stacktrace) {
            commandLineList.add("-e");
        }
        if (this.useIndy) {
            commandLineList.add("--indy");
        }
        if (this.scriptBaseClass != null) {
            commandLineList.add("-b");
            commandLineList.add(this.scriptBaseClass);
        }
        if (this.configscript != null) {
            commandLineList.add("--configscript");
            commandLineList.add(this.configscript);
        }
    }

    private void addSourceFiles(List<String> commandLineList) {
        int count = 0;
        if (this.fork) {
            for (File file : this.compileList) {
                count += file.getPath().length();
            }
            for (Object e : commandLineList) {
                count += e.toString().length();
            }
            count += this.compileList.length;
            count += commandLineList.size();
        }
        if (this.fork && count > Short.MAX_VALUE) {
            try {
                File tempFile = File.createTempFile("groovyc-files-", ".txt");
                this.temporaryFiles.add(tempFile);
                PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
                for (File srcFile : this.compileList) {
                    printWriter.println(srcFile.getPath());
                }
                printWriter.close();
                commandLineList.add("@" + tempFile.getPath());
            }
            catch (IOException e) {
                this.log.error("Error creating file list", e);
            }
        } else {
            for (File file : this.compileList) {
                commandLineList.add(file.getPath());
            }
        }
    }

    private String[] makeCommandLine(List<String> commandLineList) {
        this.log.verbose("Compilation arguments:\n" + DefaultGroovyMethods.join(commandLineList, "\n"));
        return commandLineList.toArray(new String[commandLineList.size()]);
    }

    private void runForked(String[] commandLine) {
        Execute executor = new Execute();
        executor.setAntRun(this.getProject());
        executor.setWorkingDirectory(this.getProject().getBaseDir());
        executor.setCommandline(commandLine);
        try {
            executor.execute();
        }
        catch (IOException ioe) {
            throw new BuildException("Error running forked groovyc.", (Throwable)ioe);
        }
        int returnCode = executor.getExitValue();
        if (returnCode != 0) {
            this.taskSuccess = false;
            if (this.errorProperty != null) {
                this.getProject().setNewProperty(this.errorProperty, "true");
            }
            if (this.failOnError) {
                throw new BuildException("Forked groovyc returned error code: " + returnCode);
            }
            this.log.error("Forked groovyc returned error code: " + returnCode);
        }
    }

    private void runCompiler(String[] commandLine) {
        try {
            Options options = FileSystemCompiler.createCompilationOptions();
            GroovyInternalPosixParser cliParser = new GroovyInternalPosixParser();
            CommandLine cli = cliParser.parse(options, commandLine);
            this.configuration = FileSystemCompiler.generateCompilerConfigurationFromOptions(cli);
            this.configuration.setScriptExtensions(this.getScriptExtensions());
            String tmpExtension = this.getScriptExtension();
            if (tmpExtension.startsWith("*.")) {
                tmpExtension = tmpExtension.substring(1);
            }
            this.configuration.setDefaultScriptExtension(tmpExtension);
            String[] filenames = FileSystemCompiler.generateFileNamesFromOptions(cli);
            boolean fileNameErrors = filenames == null;
            boolean bl = fileNameErrors = fileNameErrors && !FileSystemCompiler.validateFiles(filenames);
            if (this.targetBytecode != null) {
                this.configuration.setTargetBytecode(this.targetBytecode);
            }
            if (!fileNameErrors) {
                FileSystemCompiler.doCompilation(this.configuration, this.makeCompileUnit(), filenames, this.forceLookupUnnamedFiles);
            }
        }
        catch (Exception re) {
            Throwable t = re;
            if (re.getClass() == RuntimeException.class && re.getCause() != null) {
                t = re.getCause();
            }
            StringWriter writer = new StringWriter();
            new ErrorReporter(t, false).write(new PrintWriter(writer));
            String message = writer.toString();
            this.taskSuccess = false;
            if (this.errorProperty != null) {
                this.getProject().setNewProperty(this.errorProperty, "true");
            }
            if (this.failOnError) {
                this.log.error(message);
                throw new BuildException("Compilation Failed", t, this.getLocation());
            }
            this.log.error(message);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void compile() {
        if (this.compileList.length == 0) {
            return;
        }
        try {
            this.log.info("Compiling " + this.compileList.length + " source file" + (this.compileList.length == 1 ? "" : "s") + (this.destDir != null ? " to " + this.destDir : ""));
            this.listFiles();
            Path classpath = this.getClasspath() != null ? this.getClasspath() : new Path(this.getProject());
            List<String> jointOptions = this.extractJointOptions(classpath);
            String separator = System.getProperty("file.separator");
            ArrayList<String> commandLineList = new ArrayList<String>();
            this.doForkCommandLineList(commandLineList, classpath, separator);
            this.doNormalCommandLineList(commandLineList, jointOptions, classpath);
            this.addSourceFiles(commandLineList);
            String[] commandLine = this.makeCommandLine(commandLineList);
            if (this.fork) {
                this.runForked(commandLine);
            } else {
                this.runCompiler(commandLine);
            }
        }
        finally {
            for (File temporaryFile : this.temporaryFiles) {
                try {
                    FileSystemCompiler.deleteRecursive(temporaryFile);
                }
                catch (Throwable t) {
                    System.err.println("error: could not delete temp files - " + temporaryFile.getPath());
                }
            }
        }
    }

    protected CompilationUnit makeCompileUnit() {
        Map<String, Object> options = this.configuration.getJointCompilationOptions();
        if (options != null) {
            if (this.keepStubs) {
                options.put("keepStubs", Boolean.TRUE);
            }
            if (this.stubDir != null) {
                options.put("stubDir", this.stubDir);
            } else {
                try {
                    File tempStubDir = DefaultGroovyStaticMethods.createTempDir(null, "groovy-generated-", "-java-source");
                    this.temporaryFiles.add(tempStubDir);
                    options.put("stubDir", tempStubDir);
                }
                catch (IOException ioe) {
                    throw new BuildException((Throwable)ioe);
                }
            }
            return new JavaAwareCompilationUnit(this.configuration, this.buildClassLoaderFor());
        }
        return new CompilationUnit(this.configuration, null, this.buildClassLoaderFor());
    }

    protected GroovyClassLoader buildClassLoaderFor() {
        if (!this.fork && !this.getIncludeantruntime()) {
            throw new IllegalArgumentException("The includeAntRuntime=false option is not compatible with fork=false");
        }
        final ClassLoader parent = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Groovyc.this.getIncludeantruntime() ? this.getClass().getClassLoader() : new AntClassLoader((ClassLoader)new RootLoader(EMPTY_URL_ARRAY, null), Groovyc.this.getProject(), Groovyc.this.getClasspath());
            }
        });
        if (parent instanceof AntClassLoader) {
            AntClassLoader antLoader = (AntClassLoader)parent;
            String[] pathElm = antLoader.getClasspath().split(File.pathSeparator);
            List<String> classpath = this.configuration.getClasspath();
            for (String cpEntry : classpath) {
                boolean found = false;
                for (String path : pathElm) {
                    if (!cpEntry.equals(path)) continue;
                    found = true;
                    break;
                }
                if (found || !new File(cpEntry).exists()) continue;
                try {
                    antLoader.addPathElement(cpEntry);
                }
                catch (BuildException e) {
                    this.log.warn("The classpath entry " + cpEntry + " is not a valid Java resource");
                }
            }
        }
        GroovyClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>(){

            @Override
            public GroovyClassLoader run() {
                return new GroovyClassLoader(parent, Groovyc.this.configuration);
            }
        });
        if (!this.forceLookupUnnamedFiles) {
            loader.setResourceLoader(new GroovyResourceLoader(){

                @Override
                public URL loadGroovySource(String filename) throws MalformedURLException {
                    return null;
                }
            });
        }
        return loader;
    }

    private Set<String> getScriptExtensions() {
        return this.scriptExtensions;
    }

    private void loadRegisteredScriptExtensions() {
        if (this.scriptExtensions.isEmpty()) {
            this.scriptExtensions.add(this.getScriptExtension().substring(2));
            Path classpath = this.getClasspath() != null ? this.getClasspath() : new Path(this.getProject());
            String[] pe = classpath.list();
            GroovyClassLoader loader = new GroovyClassLoader(((Object)((Object)this)).getClass().getClassLoader());
            for (String file : pe) {
                loader.addClasspath(file);
            }
            this.scriptExtensions.addAll(SourceExtensionHandler.getRegisteredExtensions(loader));
        }
    }
}

