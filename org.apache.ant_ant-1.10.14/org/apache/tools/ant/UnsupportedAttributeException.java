/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildException;

public class UnsupportedAttributeException
extends BuildException {
    private static final long serialVersionUID = 1L;
    private final String attribute;

    public UnsupportedAttributeException(String msg, String attribute) {
        super(msg);
        this.attribute = attribute;
    }

    public String getAttribute() {
        return this.attribute;
    }
}

