/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.net;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailTask;

@Deprecated
public class MimeMail
extends EmailTask {
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The " + this.getTaskName() + " task is deprecated. Use the mail task instead.");
        super.execute();
    }
}

