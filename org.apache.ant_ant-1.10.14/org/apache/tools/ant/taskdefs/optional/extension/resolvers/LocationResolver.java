/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class LocationResolver
implements ExtensionResolver {
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public File resolve(Extension extension, Project project) throws BuildException {
        if (null == this.location) {
            throw new BuildException("No location specified for resolver");
        }
        return project.resolveFile(this.location);
    }

    public String toString() {
        return "Location[" + this.location + "]";
    }
}

