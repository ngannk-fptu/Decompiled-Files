/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;

public interface RmicAdapter {
    public void setRmic(Rmic var1);

    public boolean execute() throws BuildException;

    public FileNameMapper getMapper();

    public Path getClasspath();
}

