/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.dispatch;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.dispatch.Dispatchable;

public abstract class DispatchTask
extends Task
implements Dispatchable {
    private String action;

    @Override
    public String getActionParameterName() {
        return "action";
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}

