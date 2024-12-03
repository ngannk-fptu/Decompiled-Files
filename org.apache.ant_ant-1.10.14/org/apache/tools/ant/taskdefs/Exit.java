/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class Exit
extends Task {
    private String message;
    private Object ifCondition;
    private Object unlessCondition;
    private NestedCondition nestedCondition;
    private Integer status;

    public void setMessage(String value) {
        this.message = value;
    }

    public void setIf(Object c) {
        this.ifCondition = c;
    }

    public void setIf(String c) {
        this.setIf((Object)c);
    }

    public void setUnless(Object c) {
        this.unlessCondition = c;
    }

    public void setUnless(String c) {
        this.setUnless((Object)c);
    }

    public void setStatus(int i) {
        this.status = i;
    }

    @Override
    public void execute() throws BuildException {
        boolean fail;
        boolean bl = this.nestedConditionPresent() ? this.testNestedCondition() : (fail = this.testIfCondition() && this.testUnlessCondition());
        if (fail) {
            String text = null;
            if (this.message != null && !this.message.trim().isEmpty()) {
                text = this.message.trim();
            } else {
                if (!this.isNullOrEmpty(this.ifCondition) && this.testIfCondition()) {
                    text = "if=" + this.ifCondition;
                }
                if (!this.isNullOrEmpty(this.unlessCondition) && this.testUnlessCondition()) {
                    text = text == null ? "" : text + " and ";
                    text = text + "unless=" + this.unlessCondition;
                }
                if (this.nestedConditionPresent()) {
                    text = "condition satisfied";
                } else if (text == null) {
                    text = "No message";
                }
            }
            this.log("failing due to " + text, 4);
            throw this.status == null ? new BuildException(text) : new ExitStatusException(text, this.status);
        }
    }

    private boolean isNullOrEmpty(Object value) {
        return value == null || "".equals(value);
    }

    public void addText(String msg) {
        if (this.message == null) {
            this.message = "";
        }
        this.message = this.message + this.getProject().replaceProperties(msg);
    }

    public ConditionBase createCondition() {
        if (this.nestedCondition != null) {
            throw new BuildException("Only one nested condition is allowed.");
        }
        this.nestedCondition = new NestedCondition();
        return this.nestedCondition;
    }

    private boolean testIfCondition() {
        return PropertyHelper.getPropertyHelper(this.getProject()).testIfCondition(this.ifCondition);
    }

    private boolean testUnlessCondition() {
        return PropertyHelper.getPropertyHelper(this.getProject()).testUnlessCondition(this.unlessCondition);
    }

    private boolean testNestedCondition() {
        boolean result = this.nestedConditionPresent();
        if (result && this.ifCondition != null || this.unlessCondition != null) {
            throw new BuildException("Nested conditions not permitted in conjunction with if/unless attributes");
        }
        return result && this.nestedCondition.eval();
    }

    private boolean nestedConditionPresent() {
        return this.nestedCondition != null;
    }

    private static class NestedCondition
    extends ConditionBase
    implements Condition {
        private NestedCondition() {
        }

        @Override
        public boolean eval() {
            if (this.countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return this.getConditions().nextElement().eval();
        }
    }
}

