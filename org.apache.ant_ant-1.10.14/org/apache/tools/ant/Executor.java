/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public interface Executor {
    public void executeTargets(Project var1, String[] var2) throws BuildException;

    public Executor getSubProjectExecutor();
}

