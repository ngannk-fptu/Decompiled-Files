/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.taskdefs.condition.And;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Or;

public class Target
implements TaskContainer {
    private String name;
    private String ifString = "";
    private String unlessString = "";
    private Condition ifCondition;
    private Condition unlessCondition;
    private List<String> dependencies = null;
    private List<Object> children = new ArrayList<Object>();
    private Location location = Location.UNKNOWN_LOCATION;
    private Project project;
    private String description = null;

    public Target() {
    }

    public Target(Target other) {
        this.name = other.name;
        this.ifString = other.ifString;
        this.unlessString = other.unlessString;
        this.ifCondition = other.ifCondition;
        this.unlessCondition = other.unlessCondition;
        this.dependencies = other.dependencies;
        this.location = other.location;
        this.project = other.project;
        this.description = other.description;
        this.children = other.children;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setDepends(String depS) {
        for (String dep : Target.parseDepends(depS, this.getName(), "depends")) {
            this.addDependency(dep);
        }
    }

    public static List<String> parseDepends(String depends, String targetName, String attributeName) {
        if (depends.isEmpty()) {
            return new ArrayList<String>();
        }
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(depends, ",", true);
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();
            if (token.isEmpty() || ",".equals(token)) {
                throw new BuildException("Syntax Error: " + attributeName + " attribute of target \"" + targetName + "\" contains an empty string.");
            }
            list.add(token);
            if (!tok.hasMoreTokens()) continue;
            token = tok.nextToken();
            if (tok.hasMoreTokens() && ",".equals(token)) continue;
            throw new BuildException("Syntax Error: " + attributeName + " attribute for target \"" + targetName + "\" ends with a \",\" character");
        }
        return list;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void addTask(Task task) {
        this.children.add(task);
    }

    public void addDataType(RuntimeConfigurable r) {
        this.children.add(r);
    }

    public Task[] getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>(this.children.size());
        for (Object o : this.children) {
            if (!(o instanceof Task)) continue;
            tasks.add((Task)o);
        }
        return tasks.toArray(new Task[0]);
    }

    public void addDependency(String dependency) {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<String>(2);
        }
        this.dependencies.add(dependency);
    }

    public Enumeration<String> getDependencies() {
        return this.dependencies == null ? Collections.emptyEnumeration() : Collections.enumeration(this.dependencies);
    }

    public boolean dependsOn(String other) {
        Project p = this.getProject();
        Hashtable<String, Target> t = p == null ? null : p.getTargets();
        return p != null && p.topoSort(this.getName(), t, false).contains(t.get(other));
    }

    public void setIf(String property) {
        this.ifString = property == null ? "" : property;
        this.setIf(() -> {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(this.getProject());
            Object o = propertyHelper.parseProperties(this.ifString);
            return propertyHelper.testIfCondition(o);
        });
    }

    public String getIf() {
        return this.ifString.isEmpty() ? null : this.ifString;
    }

    public void setIf(Condition condition) {
        if (this.ifCondition == null) {
            this.ifCondition = condition;
        } else {
            And andCondition = new And();
            andCondition.setProject(this.getProject());
            andCondition.setLocation(this.getLocation());
            andCondition.add(this.ifCondition);
            andCondition.add(condition);
            this.ifCondition = andCondition;
        }
    }

    public void setUnless(String property) {
        this.unlessString = property == null ? "" : property;
        this.setUnless(() -> {
            Object o;
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(this.getProject());
            return !propertyHelper.testUnlessCondition(o = propertyHelper.parseProperties(this.unlessString));
        });
    }

    public String getUnless() {
        return this.unlessString.isEmpty() ? null : this.unlessString;
    }

    public void setUnless(Condition condition) {
        if (this.unlessCondition == null) {
            this.unlessCondition = condition;
        } else {
            Or orCondition = new Or();
            orCondition.setProject(this.getProject());
            orCondition.setLocation(this.getLocation());
            orCondition.add(this.unlessCondition);
            orCondition.add(condition);
            this.unlessCondition = orCondition;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() throws BuildException {
        if (this.ifCondition != null && !this.ifCondition.eval()) {
            this.project.log(this, "Skipped because property '" + this.project.replaceProperties(this.ifString) + "' not set.", 3);
            return;
        }
        if (this.unlessCondition != null && this.unlessCondition.eval()) {
            this.project.log(this, "Skipped because property '" + this.project.replaceProperties(this.unlessString) + "' set.", 3);
            return;
        }
        LocalProperties localProperties = LocalProperties.get(this.getProject());
        localProperties.enterScope();
        try {
            for (int i = 0; i < this.children.size(); ++i) {
                Object o = this.children.get(i);
                if (o instanceof Task) {
                    Task task = (Task)o;
                    task.perform();
                    continue;
                }
                ((RuntimeConfigurable)o).maybeConfigure(this.project);
            }
        }
        finally {
            localProperties.exitScope();
        }
    }

    public final void performTasks() {
        RuntimeException thrown = null;
        this.project.fireTargetStarted(this);
        try {
            this.execute();
        }
        catch (RuntimeException exc) {
            thrown = exc;
            throw exc;
        }
        finally {
            this.project.fireTargetFinished(this, thrown);
        }
    }

    void replaceChild(Task el, RuntimeConfigurable o) {
        int index;
        while ((index = this.children.indexOf(el)) >= 0) {
            this.children.set(index, o);
        }
    }

    void replaceChild(Task el, Task o) {
        int index;
        while ((index = this.children.indexOf(el)) >= 0) {
            this.children.set(index, o);
        }
    }
}

