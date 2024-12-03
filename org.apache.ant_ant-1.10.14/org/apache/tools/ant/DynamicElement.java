/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildException;

public interface DynamicElement {
    public Object createDynamicElement(String var1) throws BuildException;
}

