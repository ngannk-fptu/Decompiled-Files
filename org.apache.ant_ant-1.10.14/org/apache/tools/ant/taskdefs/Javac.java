/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;

public class Javac
extends MatchingTask {
    private static final String FAIL_MSG = "Compile failed; see the compiler error output for details.";
    private static final char GROUP_START_MARK = '{';
    private static final char GROUP_END_MARK = '}';
    private static final char GROUP_SEP_MARK = ',';
    private static final String MODULE_MARKER = "*";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Path src;
    private File destDir;
    private File nativeHeaderDir;
    private Path compileClasspath;
    private Path modulepath;
    private Path upgrademodulepath;
    private Path compileSourcepath;
    private Path moduleSourcepath;
    private String encoding;
    private boolean debug = false;
    private boolean optimize = false;
    private boolean deprecation = false;
    private boolean depend = false;
    private boolean verbose = false;
    private String targetAttribute;
    private String release;
    private Path bootclasspath;
    private Path extdirs;
    private Boolean includeAntRuntime;
    private boolean includeJavaRuntime = false;
    private boolean fork = false;
    private String forkedExecutable = null;
    private boolean nowarn = false;
    private String memoryInitialSize;
    private String memoryMaximumSize;
    private FacadeTaskHelper facade = null;
    protected boolean failOnError = true;
    protected boolean listFiles = false;
    protected File[] compileList = new File[0];
    private Map<String, Long> packageInfos = new HashMap<String, Long>();
    private String source;
    private String debugLevel;
    private File tmpDir;
    private String updatedProperty;
    private String errorProperty;
    private boolean taskSuccess = true;
    private boolean includeDestClasses = true;
    private CompilerAdapter nestedAdapter = null;
    private boolean createMissingPackageInfoClass = true;
    private static final byte[] PACKAGE_INFO_CLASS_HEADER = new byte[]{-54, -2, -70, -66, 0, 0, 0, 49, 0, 7, 7, 0, 5, 7, 0, 6, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 17, 112, 97, 99, 107, 97, 103, 101, 45, 105, 110, 102, 111, 46, 106, 97, 118, 97, 1};
    private static final byte[] PACKAGE_INFO_CLASS_FOOTER = new byte[]{47, 112, 97, 99, 107, 97, 103, 101, 45, 105, 110, 102, 111, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 2, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 2, 0, 4};

    public Javac() {
        this.facade = new FacadeTaskHelper(this.assumedJavaVersion());
    }

    private String assumedJavaVersion() {
        if (JavaEnvUtils.isJavaVersion("1.8")) {
            return "javac1.8";
        }
        if (JavaEnvUtils.isJavaVersion("9")) {
            return "javac9";
        }
        if (JavaEnvUtils.isAtLeastJavaVersion("10")) {
            return "javac10+";
        }
        return "modern";
    }

    public String getDebugLevel() {
        return this.debugLevel;
    }

    public void setDebugLevel(String v) {
        this.debugLevel = v;
    }

    public String getSource() {
        return this.source != null ? this.source : this.getProject().getProperty("ant.build.javac.source");
    }

    public void setSource(String v) {
        this.source = v;
    }

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

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setNativeHeaderDir(File nhDir) {
        this.nativeHeaderDir = nhDir;
    }

    public File getNativeHeaderDir() {
        return this.nativeHeaderDir;
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

    public void setModulesourcepath(Path msp) {
        if (this.moduleSourcepath == null) {
            this.moduleSourcepath = msp;
        } else {
            this.moduleSourcepath.append(msp);
        }
    }

    public Path getModulesourcepath() {
        return this.moduleSourcepath;
    }

    public Path createModulesourcepath() {
        if (this.moduleSourcepath == null) {
            this.moduleSourcepath = new Path(this.getProject());
        }
        return this.moduleSourcepath.createPath();
    }

    public void setModulesourcepathRef(Reference r) {
        this.createModulesourcepath().setRefid(r);
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

    public void setModulepath(Path mp) {
        if (this.modulepath == null) {
            this.modulepath = mp;
        } else {
            this.modulepath.append(mp);
        }
    }

    public Path getModulepath() {
        return this.modulepath;
    }

    public Path createModulepath() {
        if (this.modulepath == null) {
            this.modulepath = new Path(this.getProject());
        }
        return this.modulepath.createPath();
    }

    public void setModulepathRef(Reference r) {
        this.createModulepath().setRefid(r);
    }

    public void setUpgrademodulepath(Path ump) {
        if (this.upgrademodulepath == null) {
            this.upgrademodulepath = ump;
        } else {
            this.upgrademodulepath.append(ump);
        }
    }

    public Path getUpgrademodulepath() {
        return this.upgrademodulepath;
    }

    public Path createUpgrademodulepath() {
        if (this.upgrademodulepath == null) {
            this.upgrademodulepath = new Path(this.getProject());
        }
        return this.upgrademodulepath.createPath();
    }

    public void setUpgrademodulepathRef(Reference r) {
        this.createUpgrademodulepath().setRefid(r);
    }

    public void setBootclasspath(Path bootclasspath) {
        if (this.bootclasspath == null) {
            this.bootclasspath = bootclasspath;
        } else {
            this.bootclasspath.append(bootclasspath);
        }
    }

    public Path getBootclasspath() {
        return this.bootclasspath;
    }

    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(this.getProject());
        }
        return this.bootclasspath.createPath();
    }

    public void setBootClasspathRef(Reference r) {
        this.createBootclasspath().setRefid(r);
    }

    public void setExtdirs(Path extdirs) {
        if (this.extdirs == null) {
            this.extdirs = extdirs;
        } else {
            this.extdirs.append(extdirs);
        }
    }

    public Path getExtdirs() {
        return this.extdirs;
    }

    public Path createExtdirs() {
        if (this.extdirs == null) {
            this.extdirs = new Path(this.getProject());
        }
        return this.extdirs.createPath();
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

    public void setDeprecation(boolean deprecation) {
        this.deprecation = deprecation;
    }

    public boolean getDeprecation() {
        return this.deprecation;
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

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public boolean getOptimize() {
        return this.optimize;
    }

    public void setDepend(boolean depend) {
        this.depend = depend;
    }

    public boolean getDepend() {
        return this.depend;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setTarget(String target) {
        this.targetAttribute = target;
    }

    public String getTarget() {
        return this.targetAttribute != null ? this.targetAttribute : this.getProject().getProperty("ant.build.javac.target");
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRelease() {
        return this.release;
    }

    public void setIncludeantruntime(boolean include) {
        this.includeAntRuntime = include;
    }

    public boolean getIncludeantruntime() {
        return this.includeAntRuntime == null || this.includeAntRuntime != false;
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

    public void setExecutable(String forkExec) {
        this.forkedExecutable = forkExec;
    }

    public String getExecutable() {
        return this.forkedExecutable;
    }

    public boolean isForkedJavac() {
        return this.fork || CompilerAdapterFactory.isForkedJavac(this.getCompiler());
    }

    public String getJavacExecutable() {
        if (this.forkedExecutable == null && this.isForkedJavac()) {
            this.forkedExecutable = this.getSystemJavac();
        } else if (this.forkedExecutable != null && !this.isForkedJavac()) {
            this.forkedExecutable = null;
        }
        return this.forkedExecutable;
    }

    public void setNowarn(boolean flag) {
        this.nowarn = flag;
    }

    public boolean getNowarn() {
        return this.nowarn;
    }

    public ImplementationSpecificArgument createCompilerArg() {
        ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getCurrentCompilerArgs() {
        String chosen = this.facade.getExplicitChoice();
        try {
            String appliedCompiler = this.getCompiler();
            this.facade.setImplementation(appliedCompiler);
            String[] result = this.facade.getArgs();
            String altCompilerName = this.getAltCompilerName(this.facade.getImplementation());
            if (result.length == 0 && altCompilerName != null) {
                this.facade.setImplementation(altCompilerName);
                result = this.facade.getArgs();
            }
            String[] stringArray = result;
            return stringArray;
        }
        finally {
            this.facade.setImplementation(chosen);
        }
    }

    private String getAltCompilerName(String anImplementation) {
        String nextSelected;
        if (CompilerAdapterFactory.isModernJdkCompiler(anImplementation)) {
            return "modern";
        }
        if (CompilerAdapterFactory.isClassicJdkCompiler(anImplementation)) {
            return "classic";
        }
        if ("modern".equalsIgnoreCase(anImplementation) && CompilerAdapterFactory.isModernJdkCompiler(nextSelected = this.assumedJavaVersion())) {
            return nextSelected;
        }
        if ("classic".equalsIgnoreCase(anImplementation)) {
            return this.assumedJavaVersion();
        }
        if (CompilerAdapterFactory.isForkedJavac(anImplementation)) {
            return this.assumedJavaVersion();
        }
        return null;
    }

    public void setTempdir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    public File getTempdir() {
        return this.tmpDir;
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

    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }

    public void add(CompilerAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one compiler adapter");
        }
        this.nestedAdapter = adapter;
    }

    public void setCreateMissingPackageInfoClass(boolean b) {
        this.createMissingPackageInfoClass = b;
    }

    @Override
    public void execute() throws BuildException {
        this.checkParameters();
        this.resetFileLists();
        if (Javac.hasPath(this.src)) {
            this.collectFileListFromSourcePath();
        } else {
            assert (Javac.hasPath(this.moduleSourcepath)) : "Either srcDir or moduleSourcepath must be given";
            this.collectFileListFromModulePath();
        }
        this.compile();
        if (this.updatedProperty != null && this.taskSuccess && this.compileList.length != 0) {
            this.getProject().setNewProperty(this.updatedProperty, "true");
        }
    }

    protected void resetFileLists() {
        this.compileList = new File[0];
        this.packageInfos = new HashMap<String, Long>();
    }

    protected void scanDir(File srcDir, File destDir, String[] files) {
        GlobPatternMapper m = new GlobPatternMapper();
        for (String extension : this.findSupportedFileExtensions()) {
            m.setFrom(extension);
            m.setTo("*.class");
            SourceFileScanner sfs = new SourceFileScanner(this);
            File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);
            if (newFiles.length <= 0) continue;
            this.lookForPackageInfos(srcDir, newFiles);
            File[] newCompileList = new File[this.compileList.length + newFiles.length];
            System.arraycopy(this.compileList, 0, newCompileList, 0, this.compileList.length);
            System.arraycopy(newFiles, 0, newCompileList, this.compileList.length, newFiles.length);
            this.compileList = newCompileList;
        }
    }

    private void collectFileListFromSourcePath() {
        for (String filename : this.src.list()) {
            File srcDir = this.getProject().resolveFile(filename);
            if (!srcDir.exists()) {
                throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", this.getLocation());
            }
            DirectoryScanner ds = this.getDirectoryScanner(srcDir);
            this.scanDir(srcDir, this.destDir != null ? this.destDir : srcDir, ds.getIncludedFiles());
        }
    }

    private void collectFileListFromModulePath() {
        FileUtils fu = FileUtils.getFileUtils();
        for (String pathElement : this.moduleSourcepath.list()) {
            boolean valid = false;
            for (Map.Entry<String, Collection<File>> modules : Javac.resolveModuleSourcePathElement(this.getProject().getBaseDir(), pathElement).entrySet()) {
                String moduleName = modules.getKey();
                for (File srcDir : modules.getValue()) {
                    if (!srcDir.exists()) continue;
                    valid = true;
                    DirectoryScanner ds = this.getDirectoryScanner(srcDir);
                    String[] files = ds.getIncludedFiles();
                    this.scanDir(srcDir, fu.resolveFile(this.destDir, moduleName), files);
                }
            }
            if (valid) continue;
            throw new BuildException("modulesourcepath \"" + pathElement + "\" does not exist!", this.getLocation());
        }
    }

    private String[] findSupportedFileExtensions() {
        String compilerImpl = this.getCompiler();
        CompilerAdapter adapter = this.nestedAdapter != null ? this.nestedAdapter : CompilerAdapterFactory.getCompiler(compilerImpl, this, this.createCompilerClasspath());
        String[] extensions = null;
        if (adapter instanceof CompilerAdapterExtension) {
            extensions = ((CompilerAdapterExtension)((Object)adapter)).getSupportedFileExtensions();
        }
        if (extensions == null) {
            extensions = new String[]{"java"};
        }
        for (int i = 0; i < extensions.length; ++i) {
            if (extensions[i].startsWith("*.")) continue;
            extensions[i] = "*." + extensions[i];
        }
        return extensions;
    }

    public File[] getFileList() {
        return this.compileList;
    }

    protected boolean isJdkCompiler(String compilerImpl) {
        return CompilerAdapterFactory.isJdkCompiler(compilerImpl);
    }

    protected String getSystemJavac() {
        return JavaEnvUtils.getJdkExecutable("javac");
    }

    public void setCompiler(String compiler) {
        this.facade.setImplementation(compiler);
    }

    public String getCompiler() {
        String compilerImpl = this.getCompilerVersion();
        if (this.fork) {
            if (this.isJdkCompiler(compilerImpl)) {
                compilerImpl = "extJavac";
            } else {
                this.log("Since compiler setting isn't classic or modern, ignoring fork setting.", 1);
            }
        }
        return compilerImpl;
    }

    public String getCompilerVersion() {
        this.facade.setMagicValue(this.getProject().getProperty("build.compiler"));
        return this.facade.getImplementation();
    }

    protected void checkParameters() throws BuildException {
        if (Javac.hasPath(this.src)) {
            if (Javac.hasPath(this.moduleSourcepath)) {
                throw new BuildException("modulesourcepath cannot be combined with srcdir attribute!", this.getLocation());
            }
        } else if (Javac.hasPath(this.moduleSourcepath)) {
            if (Javac.hasPath(this.src) || Javac.hasPath(this.compileSourcepath)) {
                throw new BuildException("modulesourcepath cannot be combined with srcdir or sourcepath !", this.getLocation());
            }
            if (this.destDir == null) {
                throw new BuildException("modulesourcepath requires destdir attribute to be set!", this.getLocation());
            }
        } else {
            throw new BuildException("either srcdir or modulesourcepath attribute must be set!", this.getLocation());
        }
        if (this.destDir != null && !this.destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", this.getLocation());
        }
        if (this.includeAntRuntime == null && this.getProject().getProperty("build.sysclasspath") == null) {
            this.log(this.getLocation() + "warning: 'includeantruntime' was not set, defaulting to " + "build.sysclasspath" + "=last; set to false for repeatable builds", 1);
        }
    }

    protected void compile() {
        String compilerImpl = this.getCompiler();
        if (this.compileList.length > 0) {
            this.log("Compiling " + this.compileList.length + " source file" + (this.compileList.length == 1 ? "" : "s") + (this.destDir != null ? " to " + this.destDir : ""));
            if (this.listFiles) {
                for (File element : this.compileList) {
                    this.log(element.getAbsolutePath());
                }
            }
            CompilerAdapter adapter = this.nestedAdapter != null ? this.nestedAdapter : CompilerAdapterFactory.getCompiler(compilerImpl, this, this.createCompilerClasspath());
            adapter.setJavac(this);
            if (adapter.execute()) {
                if (this.createMissingPackageInfoClass) {
                    try {
                        this.generateMissingPackageInfoClasses(this.destDir != null ? this.destDir : this.getProject().resolveFile(this.src.list()[0]));
                    }
                    catch (IOException x) {
                        throw new BuildException(x, this.getLocation());
                    }
                }
            } else {
                this.taskSuccess = false;
                if (this.errorProperty != null) {
                    this.getProject().setNewProperty(this.errorProperty, "true");
                }
                if (this.failOnError) {
                    throw new BuildException(FAIL_MSG, this.getLocation());
                }
                this.log(FAIL_MSG, 0);
            }
        }
    }

    private void lookForPackageInfos(File srcDir, File[] newFiles) {
        for (File f : newFiles) {
            if (!"package-info.java".equals(f.getName())) continue;
            String path = FILE_UTILS.removeLeadingPath(srcDir, f).replace(File.separatorChar, '/');
            String suffix = "/package-info.java";
            if (!path.endsWith("/package-info.java")) {
                this.log("anomalous package-info.java path: " + path, 1);
                continue;
            }
            String pkg = path.substring(0, path.length() - "/package-info.java".length());
            this.packageInfos.put(pkg, f.lastModified());
        }
    }

    private void generateMissingPackageInfoClasses(File dest) throws IOException {
        for (Map.Entry<String, Long> entry : this.packageInfos.entrySet()) {
            String pkg = entry.getKey();
            Long sourceLastMod = entry.getValue();
            File pkgBinDir = new File(dest, pkg.replace('/', File.separatorChar));
            pkgBinDir.mkdirs();
            File pkgInfoClass = new File(pkgBinDir, "package-info.class");
            if (pkgInfoClass.isFile() && pkgInfoClass.lastModified() >= sourceLastMod) continue;
            this.log("Creating empty " + pkgInfoClass);
            OutputStream os = Files.newOutputStream(pkgInfoClass.toPath(), new OpenOption[0]);
            try {
                os.write(PACKAGE_INFO_CLASS_HEADER);
                byte[] name = pkg.getBytes(StandardCharsets.UTF_8);
                int length = name.length + 13;
                os.write((byte)length / 256);
                os.write((byte)length % 256);
                os.write(name);
                os.write(PACKAGE_INFO_CLASS_FOOTER);
            }
            finally {
                if (os == null) continue;
                os.close();
            }
        }
    }

    private static boolean hasPath(Path path) {
        return path != null && !path.isEmpty();
    }

    private static Map<String, Collection<File>> resolveModuleSourcePathElement(File projectDir, String element) {
        TreeMap<String, Collection<File>> result = new TreeMap<String, Collection<File>>();
        for (CharSequence charSequence : Javac.expandGroups(element)) {
            Javac.findModules(projectDir, charSequence.toString(), result);
        }
        return result;
    }

    private static Collection<? extends CharSequence> expandGroups(CharSequence element) {
        ArrayList<StringBuilder> result = new ArrayList<StringBuilder>();
        result.add(new StringBuilder());
        StringBuilder resolved = new StringBuilder();
        block7: for (int i = 0; i < element.length(); ++i) {
            char c = element.charAt(i);
            switch (c) {
                case '{': {
                    int end = Javac.getGroupEndIndex(element, i);
                    if (end < 0) {
                        throw new BuildException(String.format("Unclosed group %s, starting at: %d", element, i));
                    }
                    Collection<? extends CharSequence> parts = Javac.resolveGroup(element.subSequence(i + 1, end));
                    switch (parts.size()) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            resolved.append(parts.iterator().next());
                            break;
                        }
                        default: {
                            ArrayList<StringBuilder> oldRes = result;
                            result = new ArrayList(oldRes.size() * parts.size());
                            for (CharSequence charSequence : parts) {
                                for (CharSequence charSequence2 : oldRes) {
                                    result.add(new StringBuilder(charSequence2).append((CharSequence)resolved).append(charSequence));
                                }
                            }
                            resolved = new StringBuilder();
                        }
                    }
                    i = end;
                    continue block7;
                }
                default: {
                    resolved.append(c);
                }
            }
        }
        for (StringBuilder prefix : result) {
            prefix.append((CharSequence)resolved);
        }
        return result;
    }

    private static Collection<? extends CharSequence> resolveGroup(CharSequence group) {
        ArrayList<CharSequence> result = new ArrayList<CharSequence>();
        int start = 0;
        int depth = 0;
        block5: for (int i = 0; i < group.length(); ++i) {
            char c = group.charAt(i);
            switch (c) {
                case '{': {
                    ++depth;
                    continue block5;
                }
                case '}': {
                    --depth;
                    continue block5;
                }
                case ',': {
                    if (depth != 0) continue block5;
                    result.addAll(Javac.expandGroups(group.subSequence(start, i)));
                    start = i + 1;
                }
            }
        }
        result.addAll(Javac.expandGroups(group.subSequence(start, group.length())));
        return result;
    }

    private static int getGroupEndIndex(CharSequence element, int start) {
        int depth = 0;
        block4: for (int i = start; i < element.length(); ++i) {
            char c = element.charAt(i);
            switch (c) {
                case '{': {
                    ++depth;
                    continue block4;
                }
                case '}': {
                    if (--depth != 0) continue block4;
                    return i;
                }
            }
        }
        return -1;
    }

    private static void findModules(File root, String pattern, Map<String, Collection<File>> collector) {
        int startIndex = (pattern = pattern.replace('/', File.separatorChar).replace('\\', File.separatorChar)).indexOf(MODULE_MARKER);
        if (startIndex == -1) {
            Javac.findModules(root, pattern, null, collector);
            return;
        }
        if (startIndex == 0) {
            throw new BuildException("The modulesourcepath entry must be a folder.");
        }
        int endIndex = startIndex + MODULE_MARKER.length();
        if (pattern.charAt(startIndex - 1) != File.separatorChar) {
            throw new BuildException("The module mark must be preceded by separator");
        }
        if (endIndex < pattern.length() && pattern.charAt(endIndex) != File.separatorChar) {
            throw new BuildException("The module mark must be followed by separator");
        }
        if (pattern.indexOf(MODULE_MARKER, endIndex) != -1) {
            throw new BuildException("The modulesourcepath entry must contain at most one module mark");
        }
        String pathToModule = pattern.substring(0, startIndex);
        String pathInModule = endIndex == pattern.length() ? null : pattern.substring(endIndex + 1);
        Javac.findModules(root, pathToModule, pathInModule, collector);
    }

    private static void findModules(File root, String pathToModule, String pathInModule, Map<String, Collection<File>> collector) {
        File f = FileUtils.getFileUtils().resolveFile(root, pathToModule);
        if (!f.isDirectory()) {
            return;
        }
        for (File module : f.listFiles(File::isDirectory)) {
            String moduleName = module.getName();
            File moduleSourceRoot = pathInModule == null ? module : new File(module, pathInModule);
            Collection moduleRoots = collector.computeIfAbsent(moduleName, k -> new ArrayList());
            moduleRoots.add(moduleSourceRoot);
        }
    }

    public class ImplementationSpecificArgument
    extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
        public void setCompiler(String impl) {
            super.setImplementation(impl);
        }
    }
}

