/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.apache.tools.ant.types.DataType;

public class Description
extends DataType {
    public void addText(String text) {
        ProjectHelper ph = (ProjectHelper)this.getProject().getReference("ant.projectHelper");
        if (!(ph instanceof ProjectHelperImpl)) {
            return;
        }
        String currentDescription = this.getProject().getDescription();
        if (currentDescription == null) {
            this.getProject().setDescription(text);
        } else {
            this.getProject().setDescription(currentDescription + text);
        }
    }

    public static String getDescription(Project project) {
        List targets = (List)project.getReference("ant.targets");
        if (targets == null) {
            return null;
        }
        StringBuilder description = new StringBuilder();
        for (Target t : targets) {
            Description.concatDescriptions(project, t, description);
        }
        return description.toString();
    }

    private static void concatDescriptions(Project project, Target t, StringBuilder description) {
        if (t == null) {
            return;
        }
        for (Task task : Description.findElementInTarget(t, "description")) {
            UnknownElement ue;
            String descComp;
            if (!(task instanceof UnknownElement) || (descComp = (ue = (UnknownElement)task).getWrapper().getText().toString()) == null) continue;
            description.append(project.replaceProperties(descComp));
        }
    }

    private static List<Task> findElementInTarget(Target t, String name) {
        return Stream.of(t.getTasks()).filter(task -> name.equals(task.getTaskName())).collect(Collectors.toList());
    }
}

