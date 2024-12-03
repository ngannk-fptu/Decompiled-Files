/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TypeAdapter;

public class AugmentReference
extends Task
implements TypeAdapter {
    private String id;

    @Override
    public void checkProxyClass(Class<?> proxyClass) {
    }

    @Override
    public synchronized Object getProxy() {
        if (this.getProject() == null) {
            throw new IllegalStateException(this.getTaskName() + "Project owner unset");
        }
        this.hijackId();
        if (this.getProject().hasReference(this.id)) {
            Object result = this.getProject().getReference(this.id);
            this.log("project reference " + this.id + "=" + result, 4);
            return result;
        }
        throw new BuildException("Unknown reference \"" + this.id + "\"");
    }

    @Override
    public void setProxy(Object o) {
        throw new UnsupportedOperationException();
    }

    private synchronized void hijackId() {
        if (this.id == null) {
            RuntimeConfigurable wrapper = this.getWrapper();
            this.id = wrapper.getId();
            if (this.id == null) {
                throw new BuildException(this.getTaskName() + " attribute 'id' unset");
            }
            wrapper.setAttribute("id", null);
            wrapper.removeAttribute("id");
            wrapper.setElementTag("augmented reference \"" + this.id + "\"");
        }
    }

    @Override
    public void execute() {
        this.restoreWrapperId();
    }

    private synchronized void restoreWrapperId() {
        if (this.id != null) {
            this.log("restoring augment wrapper " + this.id, 4);
            RuntimeConfigurable wrapper = this.getWrapper();
            wrapper.setAttribute("id", this.id);
            wrapper.setElementTag(this.getTaskName());
            this.id = null;
        }
    }
}

