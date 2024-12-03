/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import java.time.Instant;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapterFactory;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class JspC
extends MatchingTask {
    private Path classpath;
    private Path compilerClasspath;
    private Path src;
    private File destDir;
    private String packageName;
    private String compilerName = "jasper";
    private String iepluginid;
    private boolean mapped;
    private int verbose = 0;
    protected Vector<String> compileList = new Vector();
    Vector<File> javaFiles = new Vector();
    protected boolean failOnError = true;
    private File uriroot;
    private File webinc;
    private File webxml;
    protected WebAppParameter webApp;
    private static final String FAIL_MSG = "Compile failed, messages should have been provided.";

    public void setSrcDir(Path srcDir) {
        if (this.src == null) {
            this.src = srcDir;
        } else {
            this.src.append(srcDir);
        }
    }

    public Path getSrcDir() {
        return this.src;
    }

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setPackage(String pkg) {
        this.packageName = pkg;
    }

    public String getPackage() {
        return this.packageName;
    }

    public void setVerbose(int i) {
        this.verbose = i;
    }

    public int getVerbose() {
        return this.verbose;
    }

    public void setFailonerror(boolean fail) {
        this.failOnError = fail;
    }

    public boolean getFailonerror() {
        return this.failOnError;
    }

    public String getIeplugin() {
        return this.iepluginid;
    }

    public void setIeplugin(String iepluginid) {
        this.iepluginid = iepluginid;
    }

    public boolean isMapped() {
        return this.mapped;
    }

    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    public void setUribase(File uribase) {
        this.log("Uribase is currently an unused parameter", 1);
    }

    public File getUribase() {
        return this.uriroot;
    }

    public void setUriroot(File uriroot) {
        this.uriroot = uriroot;
    }

    public File getUriroot() {
        return this.uriroot;
    }

    public void setClasspath(Path cp) {
        if (this.classpath == null) {
            this.classpath = cp;
        } else {
            this.classpath.append(cp);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public void setCompilerclasspath(Path cp) {
        if (this.compilerClasspath == null) {
            this.compilerClasspath = cp;
        } else {
            this.compilerClasspath.append(cp);
        }
    }

    public Path getCompilerclasspath() {
        return this.compilerClasspath;
    }

    public Path createCompilerclasspath() {
        if (this.compilerClasspath == null) {
            this.compilerClasspath = new Path(this.getProject());
        }
        return this.compilerClasspath.createPath();
    }

    public void setWebxml(File webxml) {
        this.webxml = webxml;
    }

    public File getWebxml() {
        return this.webxml;
    }

    public void setWebinc(File webinc) {
        this.webinc = webinc;
    }

    public File getWebinc() {
        return this.webinc;
    }

    public void addWebApp(WebAppParameter webappParam) throws BuildException {
        if (this.webApp != null) {
            throw new BuildException("Only one webapp can be specified");
        }
        this.webApp = webappParam;
    }

    public WebAppParameter getWebApp() {
        return this.webApp;
    }

    public void setCompiler(String compiler) {
        this.compilerName = compiler;
    }

    public Vector<String> getCompileList() {
        return this.compileList;
    }

    @Override
    public void execute() throws BuildException {
        if (this.destDir == null) {
            throw new BuildException("destdir attribute must be set!", this.getLocation());
        }
        if (!this.destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", this.getLocation());
        }
        File dest = this.getActualDestDir();
        try (AntClassLoader al = this.getProject().createClassLoader(this.compilerClasspath);){
            JspCompilerAdapter compiler = JspCompilerAdapterFactory.getCompiler(this.compilerName, this, al);
            if (this.webApp != null) {
                this.doCompilation(compiler);
                return;
            }
            if (this.src == null) {
                throw new BuildException("srcdir attribute must be set!", this.getLocation());
            }
            String[] list = this.src.list();
            if (list.length == 0) {
                throw new BuildException("srcdir attribute must be set!", this.getLocation());
            }
            if (compiler.implementsOwnDependencyChecking()) {
                this.doCompilation(compiler);
                return;
            }
            JspMangler mangler = compiler.createMangler();
            this.resetFileLists();
            int filecount = 0;
            for (String fileName : list) {
                File srcDir = this.getProject().resolveFile(fileName);
                if (!srcDir.exists()) {
                    throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", this.getLocation());
                }
                DirectoryScanner ds = this.getDirectoryScanner(srcDir);
                String[] files = ds.getIncludedFiles();
                filecount = files.length;
                this.scanDir(srcDir, dest, mangler, files);
            }
            this.log("compiling " + this.compileList.size() + " files", 3);
            if (!this.compileList.isEmpty()) {
                this.log("Compiling " + this.compileList.size() + " source file" + (this.compileList.size() == 1 ? "" : "s") + " to " + dest);
                this.doCompilation(compiler);
            } else if (filecount == 0) {
                this.log("there were no files to compile", 2);
            } else {
                this.log("all files are up to date", 3);
            }
        }
    }

    private File getActualDestDir() {
        if (this.packageName == null) {
            return this.destDir;
        }
        return new File(this.destDir.getPath() + File.separatorChar + this.packageName.replace('.', File.separatorChar));
    }

    private void doCompilation(JspCompilerAdapter compiler) throws BuildException {
        compiler.setJspc(this);
        if (!compiler.execute()) {
            if (this.failOnError) {
                throw new BuildException(FAIL_MSG, this.getLocation());
            }
            this.log(FAIL_MSG, 0);
        }
    }

    protected void resetFileLists() {
        this.compileList.removeAllElements();
    }

    protected void scanDir(File srcDir, File dest, JspMangler mangler, String[] files) {
        long now = Instant.now().toEpochMilli();
        for (String filename : files) {
            File srcFile = new File(srcDir, filename);
            File javaFile = this.mapToJavaFile(mangler, srcFile, srcDir, dest);
            if (javaFile == null) continue;
            if (srcFile.lastModified() > now) {
                this.log("Warning: file modified in the future: " + filename, 1);
            }
            if (!this.isCompileNeeded(srcFile, javaFile)) continue;
            this.compileList.addElement(srcFile.getAbsolutePath());
            this.javaFiles.addElement(javaFile);
        }
    }

    private boolean isCompileNeeded(File srcFile, File javaFile) {
        boolean shouldCompile = false;
        if (!javaFile.exists()) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because java file " + javaFile.getPath() + " does not exist", 3);
        } else if (srcFile.lastModified() > javaFile.lastModified()) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because it is out of date with respect to " + javaFile.getPath(), 3);
        } else if (javaFile.length() == 0L) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because java file " + javaFile.getPath() + " is empty", 3);
        }
        return shouldCompile;
    }

    protected File mapToJavaFile(JspMangler mangler, File srcFile, File srcDir, File dest) {
        if (!srcFile.getName().endsWith(".jsp")) {
            return null;
        }
        String javaFileName = mangler.mapJspToJavaName(srcFile);
        return new File(dest, javaFileName);
    }

    public void deleteEmptyJavaFiles() {
        if (this.javaFiles != null) {
            for (File file : this.javaFiles) {
                if (!file.exists() || file.length() != 0L) continue;
                this.log("deleting empty output file " + file);
                file.delete();
            }
        }
    }

    public static class WebAppParameter {
        private File directory;

        public File getDirectory() {
            return this.directory;
        }

        public void setBaseDir(File directory) {
            this.directory = directory;
        }
    }
}

