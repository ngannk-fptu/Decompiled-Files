/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;

public class SingleCheckExecutor
implements Executor {
    @Override
    public void executeTargets(Project project, String[] targetNames) throws BuildException {
        project.executeSortedTargets(project.topoSort(targetNames, project.getTargets(), false));
    }

    @Override
    public Executor getSubProjectExecutor() {
        return this;
    }
}

