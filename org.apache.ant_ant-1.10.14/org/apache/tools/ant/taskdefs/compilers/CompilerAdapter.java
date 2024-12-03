/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;

public interface CompilerAdapter {
    public void setJavac(Javac var1);

    public boolean execute() throws BuildException;
}

