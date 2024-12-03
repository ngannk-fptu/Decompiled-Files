/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

public interface Condition {
    public boolean eval() throws BuildException;
}

