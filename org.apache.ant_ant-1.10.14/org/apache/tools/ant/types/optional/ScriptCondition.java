/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.optional.AbstractScriptComponent;

public class ScriptCondition
extends AbstractScriptComponent
implements Condition {
    private boolean value = false;

    @Override
    public boolean eval() throws BuildException {
        this.initScriptRunner();
        Object result = this.getRunner().evaluateScript("ant_condition");
        return result instanceof Boolean ? Boolean.TRUE.equals(result) : this.getValue();
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}

