/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

public class Chmod
extends ExecuteOn {
    private FileSet defaultSet = new FileSet();
    private boolean defaultSetDefined = false;
    private boolean havePerm = false;

    public Chmod() {
        super.setExecutable("chmod");
        super.setParallel(true);
        super.setSkipEmptyFilesets(true);
    }

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        this.defaultSet.setProject(project);
    }

    public void setFile(File src) {
        FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
    }

    @Override
    public void setDir(File src) {
        this.defaultSet.setDir(src);
    }

    public void setPerm(String perm) {
        this.createArg().setValue(perm);
        this.havePerm = true;
    }

    public PatternSet.NameEntry createInclude() {
        this.defaultSetDefined = true;
        return this.defaultSet.createInclude();
    }

    public PatternSet.NameEntry createExclude() {
        this.defaultSetDefined = true;
        return this.defaultSet.createExclude();
    }

    public PatternSet createPatternSet() {
        this.defaultSetDefined = true;
        return this.defaultSet.createPatternSet();
    }

    public void setIncludes(String includes) {
        this.defaultSetDefined = true;
        this.defaultSet.setIncludes(includes);
    }

    public void setExcludes(String excludes) {
        this.defaultSetDefined = true;
        this.defaultSet.setExcludes(excludes);
    }

    public void setDefaultexcludes(boolean useDefaultExcludes) {
        this.defaultSetDefined = true;
        this.defaultSet.setDefaultexcludes(useDefaultExcludes);
    }

    @Override
    protected void checkConfiguration() {
        if (!this.havePerm) {
            throw new BuildException("Required attribute perm not set in chmod", this.getLocation());
        }
        if (this.defaultSetDefined && this.defaultSet.getDir(this.getProject()) != null) {
            this.addFileset(this.defaultSet);
        }
        super.checkConfiguration();
    }

    @Override
    public void execute() throws BuildException {
        if (this.defaultSetDefined || this.defaultSet.getDir(this.getProject()) == null) {
            try {
                super.execute();
            }
            finally {
                if (this.defaultSetDefined && this.defaultSet.getDir(this.getProject()) != null) {
                    this.filesets.removeElement(this.defaultSet);
                }
            }
        }
        if (this.isValidOs()) {
            Execute execute = this.prepareExec();
            Commandline cloned = (Commandline)this.cmdl.clone();
            cloned.createArgument().setValue(this.defaultSet.getDir(this.getProject()).getPath());
            try {
                execute.setCommandline(cloned.getCommandline());
                this.runExecute(execute);
            }
            catch (IOException e) {
                throw new BuildException("Execute failed: " + e, e, this.getLocation());
            }
            finally {
                this.logFlush();
            }
        }
    }

    @Override
    public void setExecutable(String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }

    @Override
    public void setCommand(Commandline cmdl) {
        throw new BuildException(this.getTaskType() + " doesn't support the command attribute", this.getLocation());
    }

    @Override
    public void setSkipEmptyFilesets(boolean skip) {
        throw new BuildException(this.getTaskType() + " doesn't support the skipemptyfileset attribute", this.getLocation());
    }

    @Override
    public void setAddsourcefile(boolean b) {
        throw new BuildException(this.getTaskType() + " doesn't support the addsourcefile attribute", this.getLocation());
    }

    @Override
    protected boolean isValidOs() {
        return this.getOs() == null && this.getOsFamily() == null ? Os.isFamily("unix") : super.isValidOs();
    }
}

