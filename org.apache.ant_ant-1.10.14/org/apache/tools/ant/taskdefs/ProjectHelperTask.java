/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;

public class ProjectHelperTask
extends Task {
    private List<ProjectHelper> projectHelpers = new ArrayList<ProjectHelper>();

    public synchronized void addConfigured(ProjectHelper projectHelper) {
        this.projectHelpers.add(projectHelper);
    }

    @Override
    public void execute() throws BuildException {
        this.projectHelpers.stream().map(Object::getClass).forEach(ProjectHelperRepository.getInstance()::registerProjectHelper);
    }
}

