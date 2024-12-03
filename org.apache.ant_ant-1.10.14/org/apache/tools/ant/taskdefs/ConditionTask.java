/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class ConditionTask
extends ConditionBase {
    private String property = null;
    private Object value = "true";
    private Object alternative = null;

    public ConditionTask() {
        super("condition");
    }

    public void setProperty(String p) {
        this.property = p;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValue(String v) {
        this.setValue((Object)v);
    }

    public void setElse(Object alt) {
        this.alternative = alt;
    }

    public void setElse(String e) {
        this.setElse((Object)e);
    }

    public void execute() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into <%s>", this.getTaskName());
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into <%s>", this.getTaskName());
        }
        if (this.property == null) {
            throw new BuildException("The property attribute is required.");
        }
        Condition c = this.getConditions().nextElement();
        if (c.eval()) {
            this.log("Condition true; setting " + this.property + " to " + this.value, 4);
            PropertyHelper.getPropertyHelper(this.getProject()).setNewProperty(this.property, this.value);
        } else if (this.alternative != null) {
            this.log("Condition false; setting " + this.property + " to " + this.alternative, 4);
            PropertyHelper.getPropertyHelper(this.getProject()).setNewProperty(this.property, this.alternative);
        } else {
            this.log("Condition false; not setting " + this.property, 4);
        }
    }
}

