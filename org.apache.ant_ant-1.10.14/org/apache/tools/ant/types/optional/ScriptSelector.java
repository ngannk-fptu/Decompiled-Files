/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class ScriptSelector
extends BaseSelector {
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private ScriptRunnerBase runner;
    private File basedir;
    private String filename;
    private File file;
    private boolean selected;

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
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

    private void init() throws BuildException {
        if (this.runner != null) {
            return;
        }
        this.runner = this.helper.getScriptRunner();
    }

    public void setSrc(File file) {
        this.helper.setSrc(file);
    }

    public void addText(String text) {
        this.helper.addText(text);
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

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.init();
        this.setSelected(true);
        this.file = file;
        this.basedir = basedir;
        this.filename = filename;
        this.runner.addBean("basedir", basedir);
        this.runner.addBean("filename", filename);
        this.runner.addBean("file", file);
        this.runner.executeScript("ant_selector");
        return this.isSelected();
    }

    public File getBasedir() {
        return this.basedir;
    }

    public String getFilename() {
        return this.filename;
    }

    public File getFile() {
        return this.file;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setEncoding(String encoding) {
        this.helper.setEncoding(encoding);
    }
}

