/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.SingleCheckExecutor;

public class DefaultExecutor
implements Executor {
    private static final SingleCheckExecutor SUB_EXECUTOR = new SingleCheckExecutor();

    @Override
    public void executeTargets(Project project, String[] targetNames) throws BuildException {
        BuildException thrownException = null;
        for (String targetName : targetNames) {
            try {
                project.executeTarget(targetName);
            }
            catch (BuildException ex) {
                if (project.isKeepGoingMode()) {
                    thrownException = ex;
                    continue;
                }
                throw ex;
            }
        }
        if (thrownException != null) {
            throw thrownException;
        }
    }

    @Override
    public Executor getSubProjectExecutor() {
        return SUB_EXECUTOR;
    }
}

