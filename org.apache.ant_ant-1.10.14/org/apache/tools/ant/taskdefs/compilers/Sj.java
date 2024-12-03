/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;

public class Sj
extends DefaultCompilerAdapter {
    @Override
    public boolean execute() throws BuildException {
        this.attributes.log("Using symantec java compiler", 3);
        Commandline cmd = this.setupJavacCommand();
        String exec = this.getJavac().getExecutable();
        cmd.setExecutable(exec == null ? "sj" : exec);
        int firstFileName = cmd.size() - this.compileList.length;
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
    }

    @Override
    protected String getNoDebugArgument() {
        return null;
    }
}

