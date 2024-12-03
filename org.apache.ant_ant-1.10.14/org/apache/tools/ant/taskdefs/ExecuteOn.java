/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

public class ExecuteOn
extends ExecTask {
    protected Vector<AbstractFileSet> filesets = new Vector();
    private Union resources = null;
    private boolean relative = false;
    private boolean parallel = false;
    private boolean forwardSlash = false;
    protected String type = "file";
    protected Commandline.Marker srcFilePos = null;
    private boolean skipEmpty = false;
    protected Commandline.Marker targetFilePos = null;
    protected Mapper mapperElement = null;
    protected FileNameMapper mapper = null;
    protected File destDir = null;
    private int maxParallel = -1;
    private boolean addSourceFile = true;
    private boolean verbose = false;
    private boolean ignoreMissing = true;
    private boolean force = false;
    protected boolean srcIsFirst = true;

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    public void addDirset(DirSet set) {
        this.filesets.addElement(set);
    }

    public void addFilelist(FileList list) {
        this.add(list);
    }

    public void add(ResourceCollection rc) {
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public void setType(FileDirBoth type) {
        this.type = type.getValue();
    }

    public void setSkipEmptyFilesets(boolean skip) {
        this.skipEmpty = skip;
    }

    public void setDest(File destDir) {
        this.destDir = destDir;
    }

    public void setForwardslash(boolean forwardSlash) {
        this.forwardSlash = forwardSlash;
    }

    public void setMaxParallel(int max) {
        this.maxParallel = max;
    }

    public void setAddsourcefile(boolean b) {
        this.addSourceFile = b;
    }

    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    public void setIgnoremissing(boolean b) {
        this.ignoreMissing = b;
    }

    public void setForce(boolean b) {
        this.force = b;
    }

    public Commandline.Marker createSrcfile() {
        if (this.srcFilePos != null) {
            throw new BuildException(this.getTaskType() + " doesn't support multiple srcfile elements.", this.getLocation());
        }
        this.srcFilePos = this.cmdl.createMarker();
        return this.srcFilePos;
    }

    public Commandline.Marker createTargetfile() {
        if (this.targetFilePos != null) {
            throw new BuildException(this.getTaskType() + " doesn't support multiple targetfile elements.", this.getLocation());
        }
        this.targetFilePos = this.cmdl.createMarker();
        this.srcIsFirst = this.srcFilePos != null;
        return this.targetFilePos;
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.mapperElement = new Mapper(this.getProject());
        return this.mapperElement;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    @Override
    protected void checkConfiguration() {
        if ("execon".equals(this.getTaskName())) {
            this.log("!! execon is deprecated. Use apply instead. !!");
        }
        super.checkConfiguration();
        if (this.filesets.isEmpty() && this.resources == null) {
            throw new BuildException("no resources specified", this.getLocation());
        }
        if (this.targetFilePos != null && this.mapperElement == null) {
            throw new BuildException("targetfile specified without mapper", this.getLocation());
        }
        if (this.destDir != null && this.mapperElement == null) {
            throw new BuildException("dest specified without mapper", this.getLocation());
        }
        if (this.mapperElement != null) {
            this.mapper = this.mapperElement.getImplementation();
        }
    }

    @Override
    protected ExecuteStreamHandler createHandler() throws BuildException {
        return this.redirectorElement == null ? super.createHandler() : new PumpStreamHandler();
    }

    @Override
    protected void setupRedirector() {
        super.setupRedirector();
        this.redirector.setAppendProperties(true);
    }

    @Override
    protected void runExec(Execute exe) throws BuildException {
        int totalFiles = 0;
        int totalDirs = 0;
        boolean haveExecuted = false;
        try {
            Vector<String> fileNames = new Vector<String>();
            Vector<File> baseDirs = new Vector<File>();
            for (AbstractFileSet fs : this.filesets) {
                String currentType = this.type;
                if (fs instanceof DirSet && !"dir".equals(this.type)) {
                    this.log("Found a nested dirset but type is " + this.type + ". Temporarily switching to type=\"dir\" on the assumption that you really did mean <dirset> not <fileset>.", 4);
                    currentType = "dir";
                }
                File base = fs.getDir(this.getProject());
                DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                if (!"dir".equals(currentType)) {
                    for (String string : this.getFiles(base, ds)) {
                        ++totalFiles;
                        fileNames.add(string);
                        baseDirs.add(base);
                    }
                }
                if (!"file".equals(currentType)) {
                    for (String string : this.getDirs(base, ds)) {
                        ++totalDirs;
                        fileNames.add(string);
                        baseDirs.add(base);
                    }
                }
                if (fileNames.isEmpty() && this.skipEmpty) {
                    this.logSkippingFileset(currentType, ds, base);
                    continue;
                }
                if (this.parallel) continue;
                for (String srcFile : fileNames) {
                    String[] command = this.getCommandline(srcFile, base);
                    this.log(Commandline.describeCommand(command), 3);
                    exe.setCommandline(command);
                    if (this.redirectorElement != null) {
                        this.setupRedirector();
                        this.redirectorElement.configure(this.redirector, srcFile);
                    }
                    if (this.redirectorElement != null || haveExecuted) {
                        exe.setStreamHandler(this.redirector.createHandler());
                    }
                    this.runExecute(exe);
                    haveExecuted = true;
                }
                fileNames.clear();
                baseDirs.clear();
            }
            if (this.resources != null) {
                for (Resource res : this.resources) {
                    FileResource fr;
                    if (!res.isExists() && this.ignoreMissing) continue;
                    File base = null;
                    String name = res.getName();
                    FileProvider fp = res.as(FileProvider.class);
                    if (fp != null && (base = (fr = ResourceUtils.asFileResource(fp)).getBaseDir()) == null) {
                        name = fr.getFile().getAbsolutePath();
                    }
                    if (this.restrict(new String[]{name}, base).length == 0) continue;
                    if (!(res.isDirectory() && res.isExists() || "dir".equals(this.type))) {
                        ++totalFiles;
                    } else {
                        if (!res.isDirectory() || "file".equals(this.type)) continue;
                        ++totalDirs;
                    }
                    baseDirs.add(base);
                    fileNames.add(name);
                    if (this.parallel) continue;
                    String[] command = this.getCommandline(name, base);
                    this.log(Commandline.describeCommand(command), 3);
                    exe.setCommandline(command);
                    if (this.redirectorElement != null) {
                        this.setupRedirector();
                        this.redirectorElement.configure(this.redirector, name);
                    }
                    if (this.redirectorElement != null || haveExecuted) {
                        exe.setStreamHandler(this.redirector.createHandler());
                    }
                    this.runExecute(exe);
                    haveExecuted = true;
                    fileNames.clear();
                    baseDirs.clear();
                }
            }
            if (!(!this.parallel || fileNames.isEmpty() && this.skipEmpty)) {
                this.runParallel(exe, fileNames, baseDirs);
                haveExecuted = true;
            }
            if (haveExecuted) {
                this.log("Applied " + this.cmdl.getExecutable() + " to " + totalFiles + " file" + (totalFiles != 1 ? "s" : "") + " and " + totalDirs + " director" + (totalDirs != 1 ? "ies" : "y") + ".", this.verbose ? 2 : 3);
            }
        }
        catch (IOException e) {
            throw new BuildException("Execute failed: " + e, e, this.getLocation());
        }
        finally {
            this.logFlush();
            this.redirector.setAppendProperties(false);
            this.redirector.setProperties();
        }
    }

    private void logSkippingFileset(String currentType, DirectoryScanner ds, File base) {
        int includedCount = (!"dir".equals(currentType) ? ds.getIncludedFilesCount() : 0) + (!"file".equals(currentType) ? ds.getIncludedDirsCount() : 0);
        this.log("Skipping fileset for directory " + base + ". It is " + (includedCount > 0 ? "up to date." : "empty."), this.verbose ? 2 : 3);
    }

    protected String[] getCommandline(String[] srcFiles, File[] baseDirs) {
        char fileSeparator = File.separatorChar;
        ArrayList<String> targets = new ArrayList<String>();
        if (this.targetFilePos != null) {
            HashSet<String> addedFiles = new HashSet<String>();
            for (String srcFile : srcFiles) {
                String[] subTargets = this.mapper.mapFileName(srcFile);
                if (subTargets == null) continue;
                for (String subTarget : subTargets) {
                    String name = this.relative ? subTarget : new File(this.destDir, subTarget).getAbsolutePath();
                    if (this.forwardSlash && fileSeparator != '/') {
                        name = name.replace(fileSeparator, '/');
                    }
                    if (addedFiles.contains(name)) continue;
                    targets.add(name);
                    addedFiles.add(name);
                }
            }
        }
        String[] targetFiles = targets.toArray(new String[0]);
        if (!this.addSourceFile) {
            srcFiles = new String[]{};
        }
        String[] orig = this.cmdl.getCommandline();
        String[] result = new String[orig.length + srcFiles.length + targetFiles.length];
        int srcIndex = orig.length;
        if (this.srcFilePos != null) {
            srcIndex = this.srcFilePos.getPosition();
        }
        if (this.targetFilePos != null) {
            int targetIndex = this.targetFilePos.getPosition();
            if (srcIndex < targetIndex || srcIndex == targetIndex && this.srcIsFirst) {
                System.arraycopy(orig, 0, result, 0, srcIndex);
                System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length, targetIndex - srcIndex);
                ExecuteOn.insertTargetFiles(targetFiles, result, targetIndex + srcFiles.length, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(orig, targetIndex, result, targetIndex + srcFiles.length + targetFiles.length, orig.length - targetIndex);
            } else {
                System.arraycopy(orig, 0, result, 0, targetIndex);
                ExecuteOn.insertTargetFiles(targetFiles, result, targetIndex, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(orig, targetIndex, result, targetIndex + targetFiles.length, srcIndex - targetIndex);
                System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length + targetFiles.length, orig.length - srcIndex);
                srcIndex += targetFiles.length;
            }
        } else {
            System.arraycopy(orig, 0, result, 0, srcIndex);
            System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length, orig.length - srcIndex);
        }
        for (int i = 0; i < srcFiles.length; ++i) {
            String src = this.relative ? srcFiles[i] : new File(baseDirs[i], srcFiles[i]).getAbsolutePath();
            if (this.forwardSlash && fileSeparator != '/') {
                src = src.replace(fileSeparator, '/');
            }
            if (!(this.srcFilePos == null || this.srcFilePos.getPrefix().isEmpty() && this.srcFilePos.getSuffix().isEmpty())) {
                src = this.srcFilePos.getPrefix() + src + this.srcFilePos.getSuffix();
            }
            result[srcIndex + i] = src;
        }
        return result;
    }

    protected String[] getCommandline(String srcFile, File baseDir) {
        return this.getCommandline(new String[]{srcFile}, new File[]{baseDir});
    }

    protected String[] getFiles(File baseDir, DirectoryScanner ds) {
        return this.restrict(ds.getIncludedFiles(), baseDir);
    }

    protected String[] getDirs(File baseDir, DirectoryScanner ds) {
        return this.restrict(ds.getIncludedDirectories(), baseDir);
    }

    protected String[] getFilesAndDirs(FileList list) {
        return this.restrict(list.getFiles(this.getProject()), list.getDir(this.getProject()));
    }

    private String[] restrict(String[] s, File baseDir) {
        return this.mapper == null || this.force ? s : new SourceFileScanner(this).restrict(s, baseDir, this.destDir, this.mapper);
    }

    protected void runParallel(Execute exe, Vector<String> fileNames, Vector<File> baseDirs) throws IOException, BuildException {
        String[] s = fileNames.toArray(new String[0]);
        File[] b = baseDirs.toArray(new File[0]);
        if (this.maxParallel <= 0 || s.length == 0) {
            String[] command = this.getCommandline(s, b);
            this.log(Commandline.describeCommand(command), 3);
            exe.setCommandline(command);
            if (this.redirectorElement != null) {
                this.setupRedirector();
                this.redirectorElement.configure(this.redirector, null);
                exe.setStreamHandler(this.redirector.createHandler());
            }
            this.runExecute(exe);
        } else {
            int stillToDo = fileNames.size();
            int currentOffset = 0;
            while (stillToDo > 0) {
                int currentAmount = Math.min(stillToDo, this.maxParallel);
                String[] cs = new String[currentAmount];
                System.arraycopy(s, currentOffset, cs, 0, currentAmount);
                File[] cb = new File[currentAmount];
                System.arraycopy(b, currentOffset, cb, 0, currentAmount);
                String[] command = this.getCommandline(cs, cb);
                this.log(Commandline.describeCommand(command), 3);
                exe.setCommandline(command);
                if (this.redirectorElement != null) {
                    this.setupRedirector();
                    this.redirectorElement.configure(this.redirector, null);
                }
                if (this.redirectorElement != null || currentOffset > 0) {
                    exe.setStreamHandler(this.redirector.createHandler());
                }
                this.runExecute(exe);
                stillToDo -= currentAmount;
                currentOffset += currentAmount;
            }
        }
    }

    private static void insertTargetFiles(String[] targetFiles, String[] arguments, int insertPosition, String prefix, String suffix) {
        if (prefix.isEmpty() && suffix.isEmpty()) {
            System.arraycopy(targetFiles, 0, arguments, insertPosition, targetFiles.length);
        } else {
            for (int i = 0; i < targetFiles.length; ++i) {
                arguments[insertPosition + i] = prefix + targetFiles[i] + suffix;
            }
        }
    }

    public static class FileDirBoth
    extends EnumeratedAttribute {
        public static final String FILE = "file";
        public static final String DIR = "dir";

        @Override
        public String[] getValues() {
            return new String[]{FILE, DIR, "both"};
        }
    }
}

