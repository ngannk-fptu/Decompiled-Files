/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.unix.AbstractAccessTask;

public class Chown
extends AbstractAccessTask {
    private boolean haveOwner = false;

    public Chown() {
        super.setExecutable("chown");
    }

    public void setOwner(String owner) {
        this.createArg().setValue(owner);
        this.haveOwner = true;
    }

    @Override
    protected void checkConfiguration() {
        if (!this.haveOwner) {
            throw new BuildException("Required attribute owner not set in chown", this.getLocation());
        }
        super.checkConfiguration();
    }

    @Override
    public void setExecutable(String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }
}

