/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

public class Cab
extends MatchingTask {
    private static final int DEFAULT_RESULT = -99;
    private File cabFile;
    private File baseDir;
    private boolean doCompress = true;
    private boolean doVerbose = false;
    private String cmdOptions;
    private boolean filesetAdded = false;
    protected String archiveType = "cab";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    public void setCabfile(File cabFile) {
        this.cabFile = cabFile;
    }

    public void setBasedir(File baseDir) {
        this.baseDir = baseDir;
    }

    public void setCompress(boolean compress) {
        this.doCompress = compress;
    }

    public void setVerbose(boolean verbose) {
        this.doVerbose = verbose;
    }

    public void setOptions(String options) {
        this.cmdOptions = options;
    }

    public void addFileset(FileSet fileset) {
        if (this.filesetAdded) {
            throw new BuildException("Only one nested fileset allowed");
        }
        this.filesetAdded = true;
        this.fileset = fileset;
    }

    protected void checkConfiguration() throws BuildException {
        if (this.baseDir == null && !this.filesetAdded) {
            throw new BuildException("basedir attribute or one nested fileset is required!", this.getLocation());
        }
        if (this.baseDir != null && !this.baseDir.exists()) {
            throw new BuildException("basedir does not exist!", this.getLocation());
        }
        if (this.baseDir != null && this.filesetAdded) {
            throw new BuildException("Both basedir attribute and a nested fileset is not allowed");
        }
        if (this.cabFile == null) {
            throw new BuildException("cabfile attribute must be set!", this.getLocation());
        }
    }

    protected ExecTask createExec() throws BuildException {
        return new ExecTask(this);
    }

    protected boolean isUpToDate(Vector<String> files) {
        long cabModified = this.cabFile.lastModified();
        return files.stream().map(f -> FILE_UTILS.resolveFile(this.baseDir, (String)f)).mapToLong(File::lastModified).allMatch(t -> t < cabModified);
    }

    protected File createListFile(Vector<String> files) throws IOException {
        File listFile = FILE_UTILS.createTempFile(this.getProject(), "ant", "", null, true, true);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile));){
            for (String f : files) {
                String s = String.format("\"%s\"", f);
                writer.write(s);
                writer.newLine();
            }
        }
        return listFile;
    }

    protected void appendFiles(Vector<String> files, DirectoryScanner ds) {
        Collections.addAll(files, ds.getIncludedFiles());
    }

    protected Vector<String> getFileList() throws BuildException {
        Vector<String> files = new Vector<String>();
        if (this.baseDir != null) {
            this.appendFiles(files, super.getDirectoryScanner(this.baseDir));
        } else {
            this.baseDir = this.fileset.getDir();
            this.appendFiles(files, this.fileset.getDirectoryScanner(this.getProject()));
        }
        return files;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        Vector<String> files = this.getFileList();
        if (this.isUpToDate(files)) {
            return;
        }
        this.log("Building " + this.archiveType + ": " + this.cabFile.getAbsolutePath());
        if (!Os.isFamily("windows")) {
            this.log("Using listcab/libcabinet", 3);
            StringBuilder sb = new StringBuilder();
            files.forEach(f -> sb.append((String)f).append("\n"));
            sb.append("\n").append(this.cabFile.getAbsolutePath()).append("\n");
            try {
                Process p = Execute.launch(this.getProject(), new String[]{"listcab"}, null, this.baseDir != null ? this.baseDir : this.getProject().getBaseDir(), true);
                OutputStream out = p.getOutputStream();
                LogOutputStream outLog = new LogOutputStream(this, 3);
                LogOutputStream errLog = new LogOutputStream(this, 0);
                StreamPumper outPump = new StreamPumper(p.getInputStream(), outLog);
                StreamPumper errPump = new StreamPumper(p.getErrorStream(), errLog);
                new Thread(outPump).start();
                new Thread(errPump).start();
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
                int result = -99;
                try {
                    result = p.waitFor();
                    outPump.waitFor();
                    outLog.close();
                    errPump.waitFor();
                    errLog.close();
                }
                catch (InterruptedException ie) {
                    this.log("Thread interrupted: " + ie);
                }
                if (!Execute.isFailure(result)) return;
                this.log("Error executing listcab; error code: " + result);
                return;
            }
            catch (IOException ex) {
                throw new BuildException("Problem creating " + this.cabFile + " " + ex.getMessage(), this.getLocation());
            }
        }
        try {
            File listFile = this.createListFile(files);
            ExecTask exec = this.createExec();
            File outFile = null;
            exec.setFailonerror(true);
            exec.setDir(this.baseDir);
            if (!this.doVerbose) {
                outFile = FILE_UTILS.createTempFile(this.getProject(), "ant", "", null, true, true);
                exec.setOutput(outFile);
            }
            exec.setExecutable("cabarc");
            exec.createArg().setValue("-r");
            exec.createArg().setValue("-p");
            if (!this.doCompress) {
                exec.createArg().setValue("-m");
                exec.createArg().setValue("none");
            }
            if (this.cmdOptions != null) {
                exec.createArg().setLine(this.cmdOptions);
            }
            exec.createArg().setValue("n");
            exec.createArg().setFile(this.cabFile);
            exec.createArg().setValue("@" + listFile.getAbsolutePath());
            exec.execute();
            if (outFile != null) {
                outFile.delete();
            }
            listFile.delete();
            return;
        }
        catch (IOException ioe) {
            throw new BuildException("Problem creating " + this.cabFile + " " + ioe.getMessage(), this.getLocation());
        }
    }
}

