/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

public class UpToDate
extends Task
implements Condition {
    private String property;
    private String value;
    private File sourceFile;
    private File targetFile;
    private List<FileSet> sourceFileSets = new Vector<FileSet>();
    private Union sourceResources = new Union();
    protected Mapper mapperElement = null;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String getValue() {
        return this.value != null ? this.value : "true";
    }

    public void setTargetFile(File file) {
        this.targetFile = file;
    }

    public void setSrcfile(File file) {
        this.sourceFile = file;
    }

    public void addSrcfiles(FileSet fs) {
        this.sourceFileSets.add(fs);
    }

    public Union createSrcResources() {
        return this.sourceResources;
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
    public boolean eval() {
        Resource[] r;
        if (this.sourceFileSets.isEmpty() && this.sourceResources.isEmpty() && this.sourceFile == null) {
            throw new BuildException("At least one srcfile or a nested <srcfiles> or <srcresources> element must be set.");
        }
        if (!(this.sourceFileSets.isEmpty() && this.sourceResources.isEmpty() || this.sourceFile == null)) {
            throw new BuildException("Cannot specify both the srcfile attribute and a nested <srcfiles> or <srcresources> element.");
        }
        if (this.targetFile == null && this.mapperElement == null) {
            throw new BuildException("The targetfile attribute or a nested mapper element must be set.");
        }
        if (this.targetFile != null && !this.targetFile.exists()) {
            this.log("The targetfile \"" + this.targetFile.getAbsolutePath() + "\" does not exist.", 3);
            return false;
        }
        if (this.sourceFile != null && !this.sourceFile.exists()) {
            throw new BuildException("%s not found.", this.sourceFile.getAbsolutePath());
        }
        boolean upToDate = true;
        if (this.sourceFile != null) {
            if (this.mapperElement == null) {
                upToDate = this.targetFile.lastModified() >= this.sourceFile.lastModified();
            } else {
                SourceFileScanner sfs = new SourceFileScanner(this);
                boolean bl = upToDate = sfs.restrict(new String[]{this.sourceFile.getAbsolutePath()}, null, null, this.mapperElement.getImplementation()).length == 0;
            }
            if (!upToDate) {
                this.log(this.sourceFile.getAbsolutePath() + " is newer than (one of) its target(s).", 3);
            }
        }
        for (FileSet fs : this.sourceFileSets) {
            if (this.scanDir(fs.getDir(this.getProject()), fs.getDirectoryScanner(this.getProject()).getIncludedFiles())) continue;
            upToDate = false;
            break;
        }
        if (upToDate && (r = this.sourceResources.listResources()).length > 0) {
            upToDate = ResourceUtils.selectOutOfDateSources(this, r, this.getMapper(), this.getProject()).length == 0;
        }
        return upToDate;
    }

    @Override
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute is required.", this.getLocation());
        }
        boolean upToDate = this.eval();
        if (upToDate) {
            this.getProject().setNewProperty(this.property, this.getValue());
            if (this.mapperElement == null) {
                this.log("File \"" + this.targetFile.getAbsolutePath() + "\" is up-to-date.", 3);
            } else {
                this.log("All target files are up-to-date.", 3);
            }
        }
    }

    protected boolean scanDir(File srcDir, String[] files) {
        SourceFileScanner sfs = new SourceFileScanner(this);
        FileNameMapper mapper = this.getMapper();
        File dir = srcDir;
        if (this.mapperElement == null) {
            dir = null;
        }
        return sfs.restrict(files, srcDir, dir, mapper).length == 0;
    }

    private FileNameMapper getMapper() {
        if (this.mapperElement == null) {
            MergingMapper mm = new MergingMapper();
            mm.setTo(this.targetFile.getAbsolutePath());
            return mm;
        }
        return this.mapperElement.getImplementation();
    }
}

