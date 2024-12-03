/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerCreator;

public class ScriptRunnerHelper {
    private ClasspathUtils.Delegate cpDelegate = null;
    private File srcFile;
    private String encoding;
    private String language;
    private String text;
    private ScriptManager manager = ScriptManager.auto;
    private boolean compiled = false;
    private boolean setBeans = true;
    private ProjectComponent projectComponent;
    private ClassLoader scriptLoader = null;
    private Union resources = new Union();

    public void setProjectComponent(ProjectComponent component) {
        this.projectComponent = component;
    }

    public ScriptRunnerBase getScriptRunner() {
        ScriptRunnerBase runner = this.getRunner();
        runner.setCompiled(this.compiled);
        if (this.encoding != null) {
            runner.setEncoding(this.encoding);
        }
        if (this.srcFile != null) {
            runner.setSrc(this.srcFile);
        }
        if (this.text != null) {
            runner.addText(this.text);
        }
        if (this.resources != null) {
            runner.loadResources(this.resources);
        }
        if (this.setBeans) {
            runner.bindToComponent(this.projectComponent);
        } else {
            runner.bindToComponentMinimum(this.projectComponent);
        }
        return runner;
    }

    public Path createClasspath() {
        return this.getClassPathDelegate().createClasspath();
    }

    public void setClasspath(Path classpath) {
        this.getClassPathDelegate().setClasspath(classpath);
    }

    public void setClasspathRef(Reference r) {
        this.getClassPathDelegate().setClasspathref(r);
    }

    public void setSrc(File file) {
        this.srcFile = file;
    }

    public File getSrc() {
        return this.srcFile;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void addText(String text) {
        this.text = text;
    }

    @Deprecated
    public void setManager(String manager) {
        this.setManager(manager == null ? null : ScriptManager.valueOf(manager));
    }

    public void setManager(ScriptManager manager) {
        this.manager = manager == null ? ScriptManager.auto : manager;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    public boolean getCompiled() {
        return this.compiled;
    }

    public void setSetBeans(boolean setBeans) {
        this.setBeans = setBeans;
    }

    public void setClassLoader(ClassLoader loader) {
        this.scriptLoader = loader;
    }

    private synchronized ClassLoader generateClassLoader() {
        if (this.scriptLoader != null) {
            return this.scriptLoader;
        }
        if (this.cpDelegate == null) {
            this.scriptLoader = this.getClass().getClassLoader();
            return this.scriptLoader;
        }
        this.scriptLoader = this.cpDelegate.getClassLoader();
        return this.scriptLoader;
    }

    private ClasspathUtils.Delegate getClassPathDelegate() {
        if (this.cpDelegate == null) {
            if (this.projectComponent == null) {
                throw new IllegalStateException("Can't access classpath without a project component");
            }
            this.cpDelegate = ClasspathUtils.getDelegate(this.projectComponent);
        }
        return this.cpDelegate;
    }

    private ScriptRunnerBase getRunner() {
        return new ScriptRunnerCreator(this.projectComponent.getProject()).createRunner(this.manager, this.language, this.generateClassLoader());
    }

    public void add(ResourceCollection resource) {
        this.resources.add(resource);
    }
}

