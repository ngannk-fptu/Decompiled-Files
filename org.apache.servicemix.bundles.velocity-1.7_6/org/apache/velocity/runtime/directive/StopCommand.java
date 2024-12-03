/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Evaluate;

public class StopCommand
extends Error {
    private static final long serialVersionUID = 2577683435802825964L;
    private Object stopMe;
    private boolean nearest = false;

    public StopCommand() {
        this.nearest = true;
    }

    public StopCommand(String message) {
        super(message);
    }

    public StopCommand(Object stopMe) {
        this.stopMe = stopMe;
    }

    public String getMessage() {
        if (this.stopMe != null) {
            return "StopCommand: " + this.stopMe;
        }
        return "StopCommand: " + super.getMessage();
    }

    public boolean isFor(Object that) {
        if (this.nearest) {
            this.stopMe = that;
            return true;
        }
        if (this.stopMe != null) {
            return that == this.stopMe;
        }
        return that instanceof Template || that instanceof RuntimeInstance || that instanceof Evaluate;
    }
}

