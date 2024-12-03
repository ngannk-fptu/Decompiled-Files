/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.unix;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;

public abstract class AbstractAccessTask
extends ExecuteOn {
    public AbstractAccessTask() {
        super.setParallel(true);
        super.setSkipEmptyFilesets(true);
    }

    public void setFile(File src) {
        FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
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

