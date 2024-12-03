/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.util.Vector;
import java.util.stream.Stream;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;

public class Rmic
extends MatchingTask {
    public static final String ERROR_RMIC_FAILED = "Rmic failed; see the compiler error output for details.";
    public static final String ERROR_UNABLE_TO_VERIFY_CLASS = "Unable to verify class ";
    public static final String ERROR_NOT_FOUND = ". It could not be found.";
    public static final String ERROR_NOT_DEFINED = ". It is not defined.";
    public static final String ERROR_LOADING_CAUSED_EXCEPTION = ". Loading caused Exception: ";
    public static final String ERROR_NO_BASE_EXISTS = "base or destdir does not exist: ";
    public static final String ERROR_NOT_A_DIR = "base or destdir is not a directory:";
    public static final String ERROR_BASE_NOT_SET = "base or destdir attribute must be set!";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File baseDir;
    private File destDir;
    private String classname;
    private File sourceBase;
    private String stubVersion;
    private Path compileClasspath;
    private Path extDirs;
    private boolean verify = false;
    private boolean filtering = false;
    private boolean iiop = false;
    private String iiopOpts;
    private boolean idl = false;
    private String idlOpts;
    private boolean debug = false;
    private boolean includeAntRuntime = true;
    private boolean includeJavaRuntime = false;
    private Vector<String> compileList = new Vector();
    private AntClassLoader loader = null;
    private FacadeTaskHelper facade = new FacadeTaskHelper("default");
    private String executable = null;
    private boolean listFiles = false;
    private RmicAdapter nestedAdapter = null;

    public void setBase(File base) {
        this.baseDir = base;
    }

    public void setDestdir(File destdir) {
        this.destDir = destdir;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public File getOutputDir() {
        if (this.getDestdir() != null) {
            return this.getDestdir();
        }
        return this.getBase();
    }

    public File getBase() {
        return this.baseDir;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setSourceBase(File sourceBase) {
        this.sourceBase = sourceBase;
    }

    public File getSourceBase() {
        return this.sourceBase;
    }

    public void setStubVersion(String stubVersion) {
        this.stubVersion = stubVersion;
    }

    public String getStubVersion() {
        return this.stubVersion;
    }

    public void setFiltering(boolean filter) {
        this.filtering = filter;
    }

    public boolean getFiltering() {
        return this.filtering;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public synchronized void setClasspath(Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        } else {
            this.compileClasspath.append(classpath);
        }
    }

    public synchronized Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath.createPath();
    }

    public void setClasspathRef(Reference pathRef) {
        this.createClasspath().setRefid(pathRef);
    }

    public Path getClasspath() {
        return this.compileClasspath;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public boolean getVerify() {
        return this.verify;
    }

    public void setIiop(boolean iiop) {
        this.iiop = iiop;
    }

    public boolean getIiop() {
        return this.iiop;
    }

    public void setIiopopts(String iiopOpts) {
        this.iiopOpts = iiopOpts;
    }

    public String getIiopopts() {
        return this.iiopOpts;
    }

    public void setIdl(boolean idl) {
        this.idl = idl;
    }

    public boolean getIdl() {
        return this.idl;
    }

    public void setIdlopts(String idlOpts) {
        this.idlOpts = idlOpts;
    }

    public String getIdlopts() {
        return this.idlOpts;
    }

    public Vector<String> getFileList() {
        return this.compileList;
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

    public synchronized void setExtdirs(Path extDirs) {
        if (this.extDirs == null) {
            this.extDirs = extDirs;
        } else {
            this.extDirs.append(extDirs);
        }
    }

    public synchronized Path createExtdirs() {
        if (this.extDirs == null) {
            this.extDirs = new Path(this.getProject());
        }
        return this.extDirs.createPath();
    }

    public Path getExtdirs() {
        return this.extDirs;
    }

    public Vector<String> getCompileList() {
        return this.compileList;
    }

    public void setCompiler(String compiler) {
        if (!compiler.isEmpty()) {
            this.facade.setImplementation(compiler);
        }
    }

    public String getCompiler() {
        this.facade.setMagicValue(this.getProject().getProperty("build.rmic"));
        return this.facade.getImplementation();
    }

    public ImplementationSpecificArgument createCompilerArg() {
        ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }

    public String[] getCurrentCompilerArgs() {
        this.getCompiler();
        return this.facade.getArgs();
    }

    public void setExecutable(String ex) {
        this.executable = ex;
    }

    public String getExecutable() {
        return this.executable;
    }

    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }

    public void setListfiles(boolean list) {
        this.listFiles = list;
    }

    public void add(RmicAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one rmic adapter");
        }
        this.nestedAdapter = adapter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        try {
            this.compileList.clear();
            File outputDir = this.getOutputDir();
            if (outputDir == null) {
                throw new BuildException(ERROR_BASE_NOT_SET, this.getLocation());
            }
            if (!outputDir.exists()) {
                throw new BuildException(ERROR_NO_BASE_EXISTS + outputDir, this.getLocation());
            }
            if (!outputDir.isDirectory()) {
                throw new BuildException(ERROR_NOT_A_DIR + outputDir, this.getLocation());
            }
            if (this.verify) {
                this.log("Verify has been turned on.", 3);
            }
            RmicAdapter adapter = this.nestedAdapter != null ? this.nestedAdapter : RmicAdapterFactory.getRmic(this.getCompiler(), this, this.createCompilerClasspath());
            adapter.setRmic(this);
            Path classpath = adapter.getClasspath();
            this.loader = this.getProject().createClassLoader(classpath);
            if (this.classname == null) {
                DirectoryScanner ds = this.getDirectoryScanner(this.baseDir);
                this.scanDir(this.baseDir, ds.getIncludedFiles(), adapter.getMapper());
            } else {
                String path = this.classname.replace('.', File.separatorChar) + ".class";
                File f2 = new File(this.baseDir, path);
                if (f2.isFile()) {
                    this.scanDir(this.baseDir, new String[]{path}, adapter.getMapper());
                } else {
                    this.compileList.add(this.classname);
                }
            }
            int fileCount = this.compileList.size();
            if (fileCount > 0) {
                this.log("RMI Compiling " + fileCount + " class" + (fileCount > 1 ? "es" : "") + " to " + outputDir, 2);
                if (this.listFiles) {
                    this.compileList.forEach(this::log);
                }
                if (!adapter.execute()) {
                    throw new BuildException(ERROR_RMIC_FAILED, this.getLocation());
                }
            }
            if (null != this.sourceBase && !outputDir.equals(this.sourceBase) && fileCount > 0) {
                if (this.idl) {
                    this.log("Cannot determine sourcefiles in idl mode, ", 1);
                    this.log("sourcebase attribute will be ignored.", 1);
                } else {
                    this.compileList.forEach(f -> this.moveGeneratedFile(outputDir, this.sourceBase, (String)f, adapter));
                }
            }
        }
        finally {
            this.cleanup();
        }
    }

    protected void cleanup() {
        if (this.loader != null) {
            this.loader.cleanup();
            this.loader = null;
        }
    }

    private void moveGeneratedFile(File baseDir, File sourceBaseFile, String classname, RmicAdapter adapter) throws BuildException {
        String classFileName = classname.replace('.', File.separatorChar) + ".class";
        String[] generatedFiles = adapter.getMapper().mapFileName(classFileName);
        if (generatedFiles == null) {
            return;
        }
        for (String generatedFile : generatedFiles) {
            String sourceFileName;
            File oldFile;
            if (!generatedFile.endsWith(".class") || !(oldFile = new File(baseDir, sourceFileName = StringUtils.removeSuffix(generatedFile, ".class") + ".java")).exists()) continue;
            File newFile = new File(sourceBaseFile, sourceFileName);
            try {
                if (this.filtering) {
                    FILE_UTILS.copyFile(oldFile, newFile, new FilterSetCollection(this.getProject().getGlobalFilterSet()));
                } else {
                    FILE_UTILS.copyFile(oldFile, newFile);
                }
                oldFile.delete();
            }
            catch (IOException ioe) {
                throw new BuildException("Failed to copy " + oldFile + " to " + newFile + " due to " + ioe.getMessage(), ioe, this.getLocation());
            }
        }
    }

    protected void scanDir(File baseDir, String[] files, FileNameMapper mapper) {
        String[] newFiles = files;
        if (this.idl) {
            this.log("will leave uptodate test to rmic implementation in idl mode.", 3);
        } else if (this.iiop && this.iiopOpts != null && this.iiopOpts.contains("-always")) {
            this.log("no uptodate test as -always option has been specified", 3);
        } else {
            SourceFileScanner sfs = new SourceFileScanner(this);
            newFiles = sfs.restrict(files, baseDir, this.getOutputDir(), mapper);
        }
        Stream.of(newFiles).map(s -> s.replace(File.separatorChar, '.')).map(s -> s.substring(0, s.lastIndexOf(".class"))).forEach(this.compileList::add);
    }

    public boolean isValidRmiRemote(String classname) {
        try {
            Class<?> testClass = this.loader.loadClass(classname);
            return (!testClass.isInterface() || this.iiop || this.idl) && this.isValidRmiRemote(testClass);
        }
        catch (ClassNotFoundException e) {
            this.log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_NOT_FOUND, 1);
        }
        catch (NoClassDefFoundError e) {
            this.log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_NOT_DEFINED, 1);
        }
        catch (Throwable t) {
            this.log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_LOADING_CAUSED_EXCEPTION + t.getMessage(), 1);
        }
        return false;
    }

    public Class<?> getRemoteInterface(Class<?> testClass) {
        return Stream.of(testClass.getInterfaces()).filter(Remote.class::isAssignableFrom).findFirst().orElse(null);
    }

    private boolean isValidRmiRemote(Class<?> testClass) {
        return Remote.class.isAssignableFrom(testClass);
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public class ImplementationSpecificArgument
    extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
        public void setCompiler(String impl) {
            super.setImplementation(impl);
        }
    }
}

