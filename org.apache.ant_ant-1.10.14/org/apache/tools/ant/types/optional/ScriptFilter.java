/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.TokenFilter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class ScriptFilter
extends TokenFilter.ChainableReaderFilter {
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private ScriptRunnerBase runner = null;
    private String token;

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }

    public void setLanguage(String language) {
        this.helper.setLanguage(language);
    }

    private void init() throws BuildException {
        if (this.runner != null) {
            return;
        }
        this.runner = this.helper.getScriptRunner();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public String filter(String token) {
        this.init();
        this.setToken(token);
        this.runner.executeScript("ant_filter");
        return this.getToken();
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

    public void setClasspath(Path classpath) {
        this.helper.setClasspath(classpath);
    }

    public Path createClasspath() {
        return this.helper.createClasspath();
    }

    public void setClasspathRef(Reference r) {
        this.helper.setClasspathRef(r);
    }

    public void setSetBeans(boolean setBeans) {
        this.helper.setSetBeans(setBeans);
    }

    public void setEncoding(String encoding) {
        this.helper.setEncoding(encoding);
    }
}

