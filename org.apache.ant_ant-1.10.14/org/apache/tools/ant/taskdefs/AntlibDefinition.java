/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntlibDefinition
extends Task {
    private String uri = "";
    private ClassLoader antlibClassLoader;

    public void setURI(String uri) throws BuildException {
        if ("antlib:org.apache.tools.ant".equals(uri)) {
            uri = "";
        }
        if (uri.startsWith("ant:")) {
            throw new BuildException("Attempt to use a reserved URI %s", uri);
        }
        this.uri = uri;
    }

    public String getURI() {
        return this.uri;
    }

    public void setAntlibClassLoader(ClassLoader classLoader) {
        this.antlibClassLoader = classLoader;
    }

    public ClassLoader getAntlibClassLoader() {
        return this.antlibClassLoader;
    }
}

