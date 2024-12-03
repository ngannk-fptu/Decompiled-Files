/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class Not
extends ConditionBase
implements Condition {
    @Override
    public boolean eval() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into <not>");
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into <not>");
        }
        return !this.getConditions().nextElement().eval();
    }
}

