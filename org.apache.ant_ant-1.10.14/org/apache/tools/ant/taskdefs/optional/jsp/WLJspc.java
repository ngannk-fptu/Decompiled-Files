/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;

public class WLJspc
extends MatchingTask {
    private File destinationDirectory;
    private File sourceDirectory;
    private String destinationPackage;
    private Path compileClasspath;
    private String pathToPackage = "";
    private List<String> filesToDo = new Vector<String>();

    @Override
    public void execute() throws BuildException {
        if (!this.destinationDirectory.isDirectory()) {
            throw new BuildException("destination directory %s is not valid", this.destinationDirectory.getPath());
        }
        if (!this.sourceDirectory.isDirectory()) {
            throw new BuildException("src directory %s is not valid", this.sourceDirectory.getPath());
        }
        if (this.destinationPackage == null) {
            throw new BuildException("package attribute must be present.", this.getLocation());
        }
        this.pathToPackage = this.destinationPackage.replace('.', File.separatorChar);
        DirectoryScanner ds = super.getDirectoryScanner(this.sourceDirectory);
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        this.compileClasspath = this.compileClasspath.concatSystemClasspath();
        Java helperTask = new Java(this);
        helperTask.setFork(true);
        helperTask.setClassname("weblogic.jspc");
        helperTask.setTaskName(this.getTaskName());
        String[] args = new String[12];
        int j = 0;
        args[j++] = "-d";
        args[j++] = this.destinationDirectory.getAbsolutePath().trim();
        args[j++] = "-docroot";
        args[j++] = this.sourceDirectory.getAbsolutePath().trim();
        args[j++] = "-keepgenerated";
        args[j++] = "-compilerclass";
        args[j++] = "sun.tools.javac.Main";
        args[j++] = "-classpath";
        args[j++] = this.compileClasspath.toString();
        this.scanDir(ds.getIncludedFiles());
        this.log("Compiling " + this.filesToDo.size() + " JSP files");
        for (String filename : this.filesToDo) {
            File jspFile = new File(filename);
            args[j] = "-package";
            String parents = jspFile.getParent();
            if (parents == null || parents.isEmpty()) {
                args[j + 1] = this.destinationPackage;
            } else {
                parents = this.replaceString(parents, File.separator, "_.");
                args[j + 1] = this.destinationPackage + "._" + parents;
            }
            args[j + 2] = this.sourceDirectory + File.separator + filename;
            helperTask.clearArgs();
            for (int x = 0; x < j + 3; ++x) {
                helperTask.createArg().setValue(args[x]);
            }
            helperTask.setClasspath(this.compileClasspath);
            if (helperTask.executeJava() == 0) continue;
            this.log(filename + " failed to compile", 1);
        }
    }

    public void setClasspath(Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        } else {
            this.compileClasspath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath;
    }

    public void setSrc(File dirName) {
        this.sourceDirectory = dirName;
    }

    public void setDest(File dirName) {
        this.destinationDirectory = dirName;
    }

    public void setPackage(String packageName) {
        this.destinationPackage = packageName;
    }

    protected void scanDir(String[] files) {
        long now = Instant.now().toEpochMilli();
        for (String file : files) {
            String pack;
            File srcFile = new File(this.sourceDirectory, file);
            File jspFile = new File(file);
            String parents = jspFile.getParent();
            if (parents == null || parents.isEmpty()) {
                pack = this.pathToPackage;
            } else {
                parents = this.replaceString(parents, File.separator, "_/");
                pack = this.pathToPackage + File.separator + "_" + parents;
            }
            String filePath = pack + File.separator + "_";
            int startingIndex = file.lastIndexOf(File.separator) != -1 ? file.lastIndexOf(File.separator) + 1 : 0;
            int endingIndex = file.indexOf(".jsp");
            if (endingIndex == -1) {
                this.log("Skipping " + file + ". Not a JSP", 3);
                continue;
            }
            filePath = filePath + file.substring(startingIndex, endingIndex);
            filePath = filePath + ".class";
            File classFile = new File(this.destinationDirectory, filePath);
            if (srcFile.lastModified() > now) {
                this.log("Warning: file modified in the future: " + file, 1);
            }
            if (srcFile.lastModified() <= classFile.lastModified()) continue;
            this.filesToDo.add(file);
            this.log("Recompiling File " + file, 3);
        }
    }

    protected String replaceString(String inpString, String escapeChars, String replaceChars) {
        StringBuilder localString = new StringBuilder();
        StringTokenizer st = new StringTokenizer(inpString, escapeChars, true);
        int numTokens = st.countTokens();
        for (int i = 0; i < numTokens; ++i) {
            String test = st.nextToken();
            test = test.equals(escapeChars) ? replaceChars : test;
            localString.append(test);
        }
        return localString.toString();
    }
}

