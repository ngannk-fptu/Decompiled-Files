/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.PropertySet;

public class CallTarget
extends Task {
    private Ant callee;
    private boolean inheritAll = true;
    private boolean inheritRefs = false;
    private boolean targetSet = false;

    public void setInheritAll(boolean inherit) {
        this.inheritAll = inherit;
    }

    public void setInheritRefs(boolean inheritRefs) {
        this.inheritRefs = inheritRefs;
    }

    @Override
    public void init() {
        this.callee = new Ant(this);
        this.callee.init();
    }

    @Override
    public void execute() throws BuildException {
        if (this.callee == null) {
            this.init();
        }
        if (!this.targetSet) {
            throw new BuildException("Attribute target or at least one nested target is required.", this.getLocation());
        }
        this.callee.setAntfile(this.getProject().getProperty("ant.file"));
        this.callee.setInheritAll(this.inheritAll);
        this.callee.setInheritRefs(this.inheritRefs);
        this.callee.execute();
    }

    public Property createParam() {
        if (this.callee == null) {
            this.init();
        }
        return this.callee.createProperty();
    }

    public void addReference(Ant.Reference r) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addReference(r);
    }

    public void addPropertyset(PropertySet ps) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addPropertyset(ps);
    }

    public void setTarget(String target) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.setTarget(target);
        this.targetSet = true;
    }

    public void addConfiguredTarget(Ant.TargetElement t) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addConfiguredTarget(t);
        this.targetSet = true;
    }

    @Override
    public void handleOutput(String output) {
        if (this.callee != null) {
            this.callee.handleOutput(output);
        } else {
            super.handleOutput(output);
        }
    }

    @Override
    public int handleInput(byte[] buffer, int offset, int length) throws IOException {
        if (this.callee != null) {
            return this.callee.handleInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }

    @Override
    public void handleFlush(String output) {
        if (this.callee != null) {
            this.callee.handleFlush(output);
        } else {
            super.handleFlush(output);
        }
    }

    @Override
    public void handleErrorOutput(String output) {
        if (this.callee != null) {
            this.callee.handleErrorOutput(output);
        } else {
            super.handleErrorOutput(output);
        }
    }

    @Override
    public void handleErrorFlush(String output) {
        if (this.callee != null) {
            this.callee.handleErrorFlush(output);
        } else {
            super.handleErrorFlush(output);
        }
    }
}

