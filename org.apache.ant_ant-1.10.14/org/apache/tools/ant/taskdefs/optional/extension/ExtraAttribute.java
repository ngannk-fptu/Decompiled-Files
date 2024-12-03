/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;

public class ExtraAttribute {
    private String name;
    private String value;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String getName() {
        return this.name;
    }

    String getValue() {
        return this.value;
    }

    public void validate() throws BuildException {
        if (null == this.name) {
            throw new BuildException("Missing name from parameter.");
        }
        if (null == this.value) {
            throw new BuildException("Missing value from parameter " + this.name + ".");
        }
    }
}

