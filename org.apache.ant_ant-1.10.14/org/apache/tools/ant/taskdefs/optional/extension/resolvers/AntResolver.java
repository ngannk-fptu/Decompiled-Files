/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class AntResolver
implements ExtensionResolver {
    private File antfile;
    private File destfile;
    private String target;

    public void setAntfile(File antfile) {
        this.antfile = antfile;
    }

    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public File resolve(Extension extension, Project project) throws BuildException {
        this.validate();
        Ant ant = new Ant();
        ant.setProject(project);
        ant.setInheritAll(false);
        ant.setAntfile(this.antfile.getName());
        try {
            File dir = this.antfile.getParentFile().getCanonicalFile();
            ant.setDir(dir);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
        if (null != this.target) {
            ant.setTarget(this.target);
        }
        ant.execute();
        return this.destfile;
    }

    private void validate() {
        if (null == this.antfile) {
            String message = "Must specify Buildfile";
            throw new BuildException("Must specify Buildfile");
        }
        if (null == this.destfile) {
            String message = "Must specify destination file";
            throw new BuildException("Must specify destination file");
        }
    }

    public String toString() {
        return "Ant[" + this.antfile + "==>" + this.destfile + "]";
    }
}

