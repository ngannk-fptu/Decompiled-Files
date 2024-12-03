/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.javacc;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.optional.javacc.JavaCC;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JJDoc
extends Task {
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String TEXT = "TEXT";
    private static final String ONE_TABLE = "ONE_TABLE";
    private final Map<String, Object> optionalAttrs = new Hashtable<String, Object>();
    private String outputFile = null;
    private boolean plainText = false;
    private static final String DEFAULT_SUFFIX_HTML = ".html";
    private static final String DEFAULT_SUFFIX_TEXT = ".txt";
    private File targetFile = null;
    private File javaccHome = null;
    private CommandlineJava cmdl = new CommandlineJava();
    private String maxMemory = null;

    public void setText(boolean plainText) {
        this.optionalAttrs.put(TEXT, plainText);
        this.plainText = plainText;
    }

    public void setOnetable(boolean oneTable) {
        this.optionalAttrs.put(ONE_TABLE, oneTable);
    }

    public void setOutputfile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setTarget(File target) {
        this.targetFile = target;
    }

    public void setJavacchome(File javaccHome) {
        this.javaccHome = javaccHome;
    }

    public void setMaxmemory(String max) {
        this.maxMemory = max;
    }

    public JJDoc() {
        this.cmdl.setVm(JavaEnvUtils.getJreExecutable("java"));
    }

    @Override
    public void execute() throws BuildException {
        File javaFile;
        this.optionalAttrs.forEach((name, value) -> this.cmdl.createArgument().setValue("-" + name + ":" + value.toString()));
        if (this.targetFile == null || !this.targetFile.isFile()) {
            throw new BuildException("Invalid target: %s", this.targetFile);
        }
        if (this.outputFile != null) {
            this.cmdl.createArgument().setValue("-OUTPUT_FILE:" + this.outputFile.replace('\\', '/'));
        }
        if ((javaFile = new File(this.createOutputFileName(this.targetFile, this.outputFile, this.plainText))).exists() && this.targetFile.lastModified() < javaFile.lastModified()) {
            this.log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        Path classpath = this.cmdl.createClasspath(this.getProject());
        File javaccJar = JavaCC.getArchiveFile(this.javaccHome);
        classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
        classpath.addJavaRuntime();
        this.cmdl.setClassname(JavaCC.getMainClass(classpath, 3));
        this.cmdl.setMaxmemory(this.maxMemory);
        Commandline.Argument arg = this.cmdl.createVmArgument();
        arg.setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        Execute process = new Execute(new LogStreamHandler(this, 2, 2), null);
        this.log(this.cmdl.describeCommand(), 3);
        process.setCommandline(this.cmdl.getCommandline());
        try {
            if (process.execute() != 0) {
                throw new BuildException("JJDoc failed.");
            }
        }
        catch (IOException e) {
            throw new BuildException("Failed to launch JJDoc", e);
        }
    }

    private String createOutputFileName(File destFile, String optionalOutputFile, boolean plain) {
        String suffix = DEFAULT_SUFFIX_HTML;
        String javaccFile = destFile.getAbsolutePath().replace('\\', '/');
        if (plain) {
            suffix = DEFAULT_SUFFIX_TEXT;
        }
        if (optionalOutputFile == null || optionalOutputFile.isEmpty()) {
            String currentSuffix;
            int suffixPos;
            int filePos = javaccFile.lastIndexOf(47);
            if (filePos >= 0) {
                javaccFile = javaccFile.substring(filePos + 1);
            }
            optionalOutputFile = (suffixPos = javaccFile.lastIndexOf(46)) == -1 ? javaccFile + suffix : ((currentSuffix = javaccFile.substring(suffixPos)).equals(suffix) ? javaccFile + suffix : javaccFile.substring(0, suffixPos) + suffix);
        } else {
            optionalOutputFile = optionalOutputFile.replace('\\', '/');
        }
        return (this.getProject().getBaseDir() + "/" + optionalOutputFile).replace('\\', '/');
    }
}

