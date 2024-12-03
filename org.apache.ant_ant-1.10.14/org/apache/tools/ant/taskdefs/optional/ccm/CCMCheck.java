/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.ccm.Continuus;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;

public class CCMCheck
extends Continuus {
    public static final String FLAG_COMMENT = "/comment";
    public static final String FLAG_TASK = "/task";
    private File file = null;
    private String comment = null;
    private String task = null;
    protected Vector<FileSet> filesets = new Vector();

    public File getFile() {
        return this.file;
    }

    public void setFile(File v) {
        this.log("working file " + v, 3);
        this.file = v;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String v) {
        this.comment = v;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String v) {
        this.task = v;
    }

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    @Override
    public void execute() throws BuildException {
        if (this.file == null && this.filesets.isEmpty()) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }
        if (this.file != null && this.file.exists() && this.file.isDirectory()) {
            throw new BuildException("CCMCheck cannot be generated for directories");
        }
        if (this.file != null && !this.filesets.isEmpty()) {
            throw new BuildException("Choose between file and fileset !");
        }
        if (this.getFile() != null) {
            this.doit();
            return;
        }
        for (FileSet fs : this.filesets) {
            File basedir = fs.getDir(this.getProject());
            DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            for (String srcFile : ds.getIncludedFiles()) {
                this.setFile(new File(basedir, srcFile));
                this.doit();
            }
        }
    }

    private void doit() {
        Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        int result = this.run(commandLine);
        if (Execute.isFailure(result)) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue(FLAG_COMMENT);
            cmd.createArgument().setValue(this.getComment());
        }
        if (this.getTask() != null) {
            cmd.createArgument().setValue(FLAG_TASK);
            cmd.createArgument().setValue(this.getTask());
        }
        if (this.getFile() != null) {
            cmd.createArgument().setValue(this.file.getAbsolutePath());
        }
    }
}

