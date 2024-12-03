/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;

public class BindTargets
extends Task {
    private String extensionPoint;
    private final List<String> targets = new ArrayList<String>();
    private ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint;

    public void setExtensionPoint(String extensionPoint) {
        this.extensionPoint = extensionPoint;
    }

    public void setOnMissingExtensionPoint(String onMissingExtensionPoint) {
        try {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.valueOf(onMissingExtensionPoint);
        }
        catch (IllegalArgumentException e) {
            throw new BuildException("Invalid onMissingExtensionPoint: " + onMissingExtensionPoint);
        }
    }

    public void setOnMissingExtensionPoint(ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint) {
        this.onMissingExtensionPoint = onMissingExtensionPoint;
    }

    public void setTargets(String target) {
        Stream.of(target.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(this.targets::add);
    }

    @Override
    public void execute() throws BuildException {
        if (this.extensionPoint == null) {
            throw new BuildException("extensionPoint required", this.getLocation());
        }
        if (this.getOwningTarget() == null || !this.getOwningTarget().getName().isEmpty()) {
            throw new BuildException("bindtargets only allowed as a top-level task");
        }
        if (this.onMissingExtensionPoint == null) {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.FAIL;
        }
        ProjectHelper helper = (ProjectHelper)this.getProject().getReference("ant.projectHelper");
        for (String target : this.targets) {
            helper.getExtensionStack().add(new String[]{this.extensionPoint, target, this.onMissingExtensionPoint.name()});
        }
    }
}

