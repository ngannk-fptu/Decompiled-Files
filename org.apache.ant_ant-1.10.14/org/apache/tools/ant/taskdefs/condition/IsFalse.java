/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class IsFalse
extends ProjectComponent
implements Condition {
    private Boolean value = null;

    public void setValue(boolean value) {
        this.value = value ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.value == null) {
            throw new BuildException("Nothing to test for falsehood");
        }
        return this.value == false;
    }
}

