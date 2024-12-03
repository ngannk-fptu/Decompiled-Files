/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.Project;

public interface ParseNextProperty {
    public Project getProject();

    public Object parseNextProperty(String var1, ParsePosition var2);
}

