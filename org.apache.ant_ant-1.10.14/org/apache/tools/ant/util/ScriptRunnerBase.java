/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.PropertyResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.FileUtils;

public abstract class ScriptRunnerBase {
    private boolean keepEngine = false;
    private String language;
    private String script = "";
    private String encoding;
    private boolean compiled;
    private Project project;
    private ClassLoader scriptLoader;
    private final Map<String, Object> beans = new HashMap<String, Object>();

    public void addBeans(Map<String, ?> dictionary) {
        dictionary.forEach((k, v) -> {
            try {
                this.addBean((String)k, v);
            }
            catch (BuildException buildException) {
                // empty catch block
            }
        });
    }

    public void addBean(String key, Object bean) {
        if (!key.isEmpty() && Character.isJavaIdentifierStart(key.charAt(0)) && key.chars().skip(1L).allMatch(Character::isJavaIdentifierPart)) {
            this.beans.put(key, bean);
        }
    }

    protected Map<String, Object> getBeans() {
        return this.beans;
    }

    public abstract void executeScript(String var1);

    public abstract Object evaluateScript(String var1);

    public abstract boolean supportsLanguage();

    public abstract String getManagerName();

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setScriptClassLoader(ClassLoader classLoader) {
        this.scriptLoader = classLoader;
    }

    protected ClassLoader getScriptClassLoader() {
        return this.scriptLoader;
    }

    public void setKeepEngine(boolean keepEngine) {
        this.keepEngine = keepEngine;
    }

    public boolean getKeepEngine() {
        return this.keepEngine;
    }

    public final void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    public final boolean getCompiled() {
        return this.compiled;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSrc(File file) {
        String filename = file.getPath();
        if (!file.exists()) {
            throw new BuildException("file " + filename + " not found.");
        }
        try (InputStream in = Files.newInputStream(file.toPath(), new OpenOption[0]);){
            Charset charset = null == this.encoding ? Charset.defaultCharset() : Charset.forName(this.encoding);
            this.readSource(in, filename, charset);
        }
        catch (IOException e) {
            throw new BuildException("file " + filename + " not found.", e);
        }
    }

    private void readSource(InputStream in, String name, Charset charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));){
            this.script = this.script + FileUtils.safeReadFully(reader);
        }
        catch (IOException ex) {
            throw new BuildException("Failed to read " + name, ex);
        }
    }

    public void loadResource(Resource sourceResource) {
        if (sourceResource instanceof StringResource) {
            this.script = this.script + ((StringResource)sourceResource).getValue();
            return;
        }
        if (sourceResource instanceof PropertyResource) {
            this.script = this.script + ((PropertyResource)sourceResource).getValue();
            return;
        }
        String name = sourceResource.toLongString();
        try (InputStream in = sourceResource.getInputStream();){
            this.readSource(in, name, Charset.defaultCharset());
        }
        catch (IOException e) {
            throw new BuildException("Failed to open " + name, e);
        }
        catch (UnsupportedOperationException e) {
            throw new BuildException("Failed to open " + name + " - it is not readable", e);
        }
    }

    public void loadResources(ResourceCollection collection) {
        collection.forEach(this::loadResource);
    }

    public void addText(String text) {
        this.script = this.script + text;
    }

    public String getScript() {
        return this.script;
    }

    public void clearScript() {
        this.script = "";
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void bindToComponent(ProjectComponent component) {
        this.project = component.getProject();
        HashMap effectiveProperties = new HashMap();
        this.project.getPropertyNames().forEach(n -> effectiveProperties.put(n, this.project.getProperty((String)n)));
        this.addBeans(effectiveProperties);
        this.addBeans(this.project.getCopyOfTargets());
        this.addBeans(this.project.getCopyOfReferences());
        this.addBean("project", this.project);
        this.addBean("self", component);
    }

    public void bindToComponentMinimum(ProjectComponent component) {
        this.project = component.getProject();
        this.addBean("project", this.project);
        this.addBean("self", component);
    }

    protected void checkLanguage() {
        if (this.language == null) {
            throw new BuildException("script language must be specified");
        }
    }

    protected ClassLoader replaceContextLoader() {
        ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
        if (this.getScriptClassLoader() == null) {
            this.setScriptClassLoader(this.getClass().getClassLoader());
        }
        Thread.currentThread().setContextClassLoader(this.getScriptClassLoader());
        return origContextClassLoader;
    }

    protected void restoreContextLoader(ClassLoader origLoader) {
        Thread.currentThread().setContextClassLoader(origLoader);
    }
}

