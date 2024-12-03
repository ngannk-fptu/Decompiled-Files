/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public abstract class AbstractScriptComponent
extends ProjectComponent {
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private ScriptRunnerBase runner = null;

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }

    public ScriptRunnerBase getRunner() {
        this.initScriptRunner();
        return this.runner;
    }

    public void setSrc(File file) {
        this.helper.setSrc(file);
    }

    public void addText(String text) {
        this.helper.addText(text);
    }

    @Deprecated
    public void setManager(String manager) {
        this.helper.setManager(manager);
    }

    public void setManager(ScriptManager manager) {
        this.helper.setManager(manager);
    }

    public void setLanguage(String language) {
        this.helper.setLanguage(language);
    }

    protected void initScriptRunner() {
        if (this.runner != null) {
            return;
        }
        this.helper.setProjectComponent(this);
        this.runner = this.helper.getScriptRunner();
    }

    public void setClasspath(Path classpath) {
        this.helper.setClasspath(classpath);
    }

    public Path createClasspath() {
        return this.helper.createClasspath();
    }

    public void setClasspathRef(Reference r) {
        this.helper.setClasspathRef(r);
    }

    protected void executeScript(String execName) {
        this.getRunner().executeScript(execName);
    }

    public void setSetBeans(boolean setBeans) {
        this.helper.setSetBeans(setBeans);
    }

    public void setEncoding(String encoding) {
        this.helper.setEncoding(encoding);
    }
}

