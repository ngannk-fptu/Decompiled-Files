/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.AntlibDefinition;

public class PreSetDef
extends AntlibDefinition
implements TaskContainer {
    private UnknownElement nestedTask;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addTask(Task nestedTask) {
        if (this.nestedTask != null) {
            throw new BuildException("Only one nested element allowed");
        }
        if (!(nestedTask instanceof UnknownElement)) {
            throw new BuildException("addTask called with a task that is not an unknown element");
        }
        this.nestedTask = (UnknownElement)nestedTask;
    }

    @Override
    public void execute() {
        String componentName;
        if (this.nestedTask == null) {
            throw new BuildException("Missing nested element");
        }
        if (this.name == null) {
            throw new BuildException("Name not specified");
        }
        this.name = ProjectHelper.genComponentName(this.getURI(), this.name);
        ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
        AntTypeDefinition def = helper.getDefinition(componentName = ProjectHelper.genComponentName(this.nestedTask.getNamespace(), this.nestedTask.getTag()));
        if (def == null) {
            throw new BuildException("Unable to find typedef %s", componentName);
        }
        PreSetDefinition newDef = new PreSetDefinition(def, this.nestedTask);
        newDef.setName(this.name);
        helper.addDataTypeDefinition(newDef);
        this.log("defining preset " + this.name, 3);
    }

    public static class PreSetDefinition
    extends AntTypeDefinition {
        private AntTypeDefinition parent;
        private UnknownElement element;

        public PreSetDefinition(AntTypeDefinition parent, UnknownElement el) {
            if (parent instanceof PreSetDefinition) {
                PreSetDefinition p = (PreSetDefinition)parent;
                el.applyPreSet(p.element);
                parent = p.parent;
            }
            this.parent = parent;
            this.element = el;
        }

        @Override
        public void setClass(Class<?> clazz) {
            throw new BuildException("Not supported");
        }

        @Override
        public void setClassName(String className) {
            throw new BuildException("Not supported");
        }

        @Override
        public String getClassName() {
            return this.parent.getClassName();
        }

        @Override
        public void setAdapterClass(Class<?> adapterClass) {
            throw new BuildException("Not supported");
        }

        @Override
        public void setAdaptToClass(Class<?> adaptToClass) {
            throw new BuildException("Not supported");
        }

        @Override
        public void setClassLoader(ClassLoader classLoader) {
            throw new BuildException("Not supported");
        }

        @Override
        public ClassLoader getClassLoader() {
            return this.parent.getClassLoader();
        }

        @Override
        public Class<?> getExposedClass(Project project) {
            return this.parent.getExposedClass(project);
        }

        @Override
        public Class<?> getTypeClass(Project project) {
            return this.parent.getTypeClass(project);
        }

        @Override
        public void checkClass(Project project) {
            this.parent.checkClass(project);
        }

        public Object createObject(Project project) {
            return this.parent.create(project);
        }

        public UnknownElement getPreSets() {
            return this.element;
        }

        @Override
        public Object create(Project project) {
            return this;
        }

        @Override
        public boolean sameDefinition(AntTypeDefinition other, Project project) {
            return other != null && other.getClass() == this.getClass() && this.parent != null && this.parent.sameDefinition(((PreSetDefinition)other).parent, project) && this.element.similar(((PreSetDefinition)other).element);
        }

        @Override
        public boolean similarDefinition(AntTypeDefinition other, Project project) {
            return other != null && other.getClass().getName().equals(this.getClass().getName()) && this.parent != null && this.parent.similarDefinition(((PreSetDefinition)other).parent, project) && this.element.similar(((PreSetDefinition)other).element);
        }
    }
}

