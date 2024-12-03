/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public class ExtensionPoint
extends Target {
    private static final String NO_CHILDREN_ALLOWED = "you must not nest child elements into an extension-point";

    public ExtensionPoint() {
    }

    public ExtensionPoint(Target other) {
        super(other);
    }

    @Override
    public final void addTask(Task task) {
        throw new BuildException(NO_CHILDREN_ALLOWED);
    }

    @Override
    public final void addDataType(RuntimeConfigurable r) {
        throw new BuildException(NO_CHILDREN_ALLOWED);
    }
}

