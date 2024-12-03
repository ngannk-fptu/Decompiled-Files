/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class IsFailure
implements Condition {
    private int code;

    public void setCode(int c) {
        this.code = c;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public boolean eval() {
        return Execute.isFailure(this.code);
    }
}

