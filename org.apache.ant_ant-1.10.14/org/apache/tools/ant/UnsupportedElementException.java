/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildException;

public class UnsupportedElementException
extends BuildException {
    private static final long serialVersionUID = 1L;
    private final String element;

    public UnsupportedElementException(String msg, String element) {
        super(msg);
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }
}

