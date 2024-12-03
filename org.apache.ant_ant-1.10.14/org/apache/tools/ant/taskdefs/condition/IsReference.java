/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Reference;

public class IsReference
extends ProjectComponent
implements Condition {
    private Reference ref;
    private String type;

    public void setRefid(Reference r) {
        this.ref = r;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.ref == null) {
            throw new BuildException("No reference specified for isreference condition");
        }
        String key = this.ref.getRefId();
        if (!this.getProject().hasReference(key)) {
            return false;
        }
        if (this.type == null) {
            return true;
        }
        Class<?> typeClass = this.getProject().getDataTypeDefinitions().get(this.type);
        if (typeClass == null) {
            typeClass = this.getProject().getTaskDefinitions().get(this.type);
        }
        return typeClass != null && typeClass.isAssignableFrom(this.getProject().getReference(key).getClass());
    }
}

