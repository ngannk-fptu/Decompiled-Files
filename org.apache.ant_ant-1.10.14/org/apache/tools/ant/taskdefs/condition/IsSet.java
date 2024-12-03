/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class IsSet
extends ProjectComponent
implements Condition {
    private String property;

    public void setProperty(String p) {
        this.property = p;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.property == null) {
            throw new BuildException("No property specified for isset condition");
        }
        return this.getProject().getProperty(this.property) != null;
    }
}

