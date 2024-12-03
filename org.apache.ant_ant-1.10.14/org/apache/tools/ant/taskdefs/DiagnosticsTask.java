/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Diagnostics;
import org.apache.tools.ant.Task;

public class DiagnosticsTask
extends Task {
    private static final String[] ARGS = new String[0];

    @Override
    public void execute() throws BuildException {
        Diagnostics.main(ARGS);
    }
}

