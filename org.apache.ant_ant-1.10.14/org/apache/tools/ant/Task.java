/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.IOException;
import java.util.Collections;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.dispatch.DispatchUtils;

public abstract class Task
extends ProjectComponent {
    @Deprecated
    protected Target target;
    @Deprecated
    protected String taskName;
    @Deprecated
    protected String taskType;
    @Deprecated
    protected RuntimeConfigurable wrapper;
    private boolean invalid;
    private UnknownElement replacement;

    public void setOwningTarget(Target target) {
        this.target = target;
    }

    public Target getOwningTarget() {
        return this.target;
    }

    public void setTaskName(String name) {
        this.taskName = name;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskType(String type) {
        this.taskType = type;
    }

    public void init() throws BuildException {
    }

    public void execute() throws BuildException {
    }

    public RuntimeConfigurable getRuntimeConfigurableWrapper() {
        if (this.wrapper == null) {
            this.wrapper = new RuntimeConfigurable(this, this.getTaskName());
        }
        return this.wrapper;
    }

    public void setRuntimeConfigurableWrapper(RuntimeConfigurable wrapper) {
        this.wrapper = wrapper;
    }

    public void maybeConfigure() throws BuildException {
        if (this.invalid) {
            this.getReplacement();
        } else if (this.wrapper != null) {
            this.wrapper.maybeConfigure(this.getProject());
        }
    }

    public void reconfigure() {
        if (this.wrapper != null) {
            this.wrapper.reconfigure(this.getProject());
        }
    }

    protected void handleOutput(String output) {
        this.log(output, 2);
    }

    protected void handleFlush(String output) {
        this.handleOutput(output);
    }

    protected int handleInput(byte[] buffer, int offset, int length) throws IOException {
        return this.getProject().defaultInput(buffer, offset, length);
    }

    protected void handleErrorOutput(String output) {
        this.log(output, 1);
    }

    protected void handleErrorFlush(String output) {
        this.handleErrorOutput(output);
    }

    @Override
    public void log(String msg) {
        this.log(msg, 2);
    }

    @Override
    public void log(String msg, int msgLevel) {
        if (this.getProject() == null) {
            super.log(msg, msgLevel);
        } else {
            this.getProject().log(this, msg, msgLevel);
        }
    }

    public void log(Throwable t, int msgLevel) {
        if (t != null) {
            this.log(t.getMessage(), t, msgLevel);
        }
    }

    public void log(String msg, Throwable t, int msgLevel) {
        if (this.getProject() == null) {
            super.log(msg, msgLevel);
        } else {
            this.getProject().log(this, msg, t, msgLevel);
        }
    }

    public final void perform() {
        if (this.invalid) {
            UnknownElement ue = this.getReplacement();
            Task task = ue.getTask();
            task.perform();
        } else {
            this.getProject().fireTaskStarted(this);
            Throwable reason = null;
            try {
                this.maybeConfigure();
                DispatchUtils.execute(this);
            }
            catch (BuildException ex) {
                if (ex.getLocation() == Location.UNKNOWN_LOCATION) {
                    ex.setLocation(this.getLocation());
                }
                reason = ex;
                throw ex;
            }
            catch (Exception ex) {
                reason = ex;
                BuildException be = new BuildException(ex);
                be.setLocation(this.getLocation());
                throw be;
            }
            catch (Error ex) {
                reason = ex;
                throw ex;
            }
            finally {
                this.getProject().fireTaskFinished(this, reason);
            }
        }
    }

    final void markInvalid() {
        this.invalid = true;
    }

    protected final boolean isInvalid() {
        return this.invalid;
    }

    private UnknownElement getReplacement() {
        if (this.replacement == null) {
            this.replacement = new UnknownElement(this.taskType);
            this.replacement.setProject(this.getProject());
            this.replacement.setTaskType(this.taskType);
            this.replacement.setTaskName(this.taskName);
            this.replacement.setLocation(this.getLocation());
            this.replacement.setOwningTarget(this.target);
            this.replacement.setRuntimeConfigurableWrapper(this.wrapper);
            this.wrapper.setProxy(this.replacement);
            this.replaceChildren(this.wrapper, this.replacement);
            this.target.replaceChild(this, this.replacement);
            this.replacement.maybeConfigure();
        }
        return this.replacement;
    }

    private void replaceChildren(RuntimeConfigurable wrapper, UnknownElement parentElement) {
        for (RuntimeConfigurable childWrapper : Collections.list(wrapper.getChildren())) {
            UnknownElement childElement = new UnknownElement(childWrapper.getElementTag());
            parentElement.addChild(childElement);
            childElement.setProject(this.getProject());
            childElement.setRuntimeConfigurableWrapper(childWrapper);
            childWrapper.setProxy(childElement);
            this.replaceChildren(childWrapper, childElement);
        }
    }

    public String getTaskType() {
        return this.taskType;
    }

    protected RuntimeConfigurable getWrapper() {
        return this.wrapper;
    }

    public final void bindToOwner(Task owner) {
        this.setProject(owner.getProject());
        this.setOwningTarget(owner.getOwningTarget());
        this.setTaskName(owner.getTaskName());
        this.setDescription(owner.getDescription());
        this.setLocation(owner.getLocation());
        this.setTaskType(owner.getTaskType());
    }
}

