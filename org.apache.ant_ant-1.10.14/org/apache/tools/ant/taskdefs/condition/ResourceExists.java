/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Resource;

public class ResourceExists
extends ProjectComponent
implements Condition {
    private Resource resource;

    public void add(Resource r) {
        if (this.resource != null) {
            throw new BuildException("only one resource can be tested");
        }
        this.resource = r;
    }

    protected void validate() throws BuildException {
        if (this.resource == null) {
            throw new BuildException("resource is required");
        }
    }

    @Override
    public boolean eval() throws BuildException {
        this.validate();
        return this.resource.isExists();
    }
}

