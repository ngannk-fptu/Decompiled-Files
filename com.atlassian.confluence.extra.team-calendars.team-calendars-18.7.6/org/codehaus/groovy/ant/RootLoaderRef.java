/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.AntClassLoader
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.taskdefs.MatchingTask
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.Reference
 */
package org.codehaus.groovy.ant;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.codehaus.groovy.tools.LoaderConfiguration;
import org.codehaus.groovy.tools.RootLoader;

public class RootLoaderRef
extends MatchingTask {
    private String name;
    private Path taskClasspath;

    public void setRef(String n) {
        this.name = n;
    }

    public void execute() throws BuildException {
        if (this.taskClasspath == null || this.taskClasspath.size() == 0) {
            throw new BuildException("no classpath given");
        }
        Project project = this.getProject();
        AntClassLoader loader = new AntClassLoader((ClassLoader)this.makeRoot(), true);
        project.addReference(this.name, (Object)loader);
    }

    private RootLoader makeRoot() {
        String[] list = this.taskClasspath.list();
        LoaderConfiguration lc = new LoaderConfiguration();
        for (int i = 0; i < list.length; ++i) {
            if (list[i].matches(".*ant-[^/]*jar$") || list[i].matches(".*commons-logging-[^/]*jar$") || list[i].matches(".*xerces-[^/]*jar$")) continue;
            lc.addFile(list[i]);
        }
        return new RootLoader(lc);
    }

    public void setClasspath(Path classpath) {
        if (this.taskClasspath == null) {
            this.taskClasspath = classpath;
        } else {
            this.taskClasspath.append(classpath);
        }
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public Path createClasspath() {
        if (this.taskClasspath == null) {
            this.taskClasspath = new Path(this.getProject());
        }
        return this.taskClasspath.createPath();
    }
}

