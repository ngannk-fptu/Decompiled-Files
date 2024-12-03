/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.unix.AbstractAccessTask;

public class Chgrp
extends AbstractAccessTask {
    private boolean haveGroup = false;

    public Chgrp() {
        super.setExecutable("chgrp");
    }

    public void setGroup(String group) {
        this.createArg().setValue(group);
        this.haveGroup = true;
    }

    @Override
    protected void checkConfiguration() {
        if (!this.haveGroup) {
            throw new BuildException("Required attribute group not set in chgrp", this.getLocation());
        }
        super.checkConfiguration();
    }

    @Override
    public void setExecutable(String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }
}

