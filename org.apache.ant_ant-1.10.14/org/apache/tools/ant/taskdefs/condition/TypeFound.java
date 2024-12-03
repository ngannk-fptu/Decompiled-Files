/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class TypeFound
extends ProjectComponent
implements Condition {
    private String name;
    private String uri;

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    protected boolean doesTypeExist(String typename) {
        boolean found;
        String componentName;
        ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
        AntTypeDefinition def = helper.getDefinition(componentName = ProjectHelper.genComponentName(this.uri, typename));
        if (def == null) {
            return false;
        }
        boolean bl = found = def.getExposedClass(this.getProject()) != null;
        if (!found) {
            String text = helper.diagnoseCreationFailure(componentName, "type");
            this.log(text, 3);
        }
        return found;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.name == null) {
            throw new BuildException("No type specified");
        }
        return this.doesTypeExist(this.name);
    }
}

