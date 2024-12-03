/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.Project;

public class PropertyOutputStream
extends ByteArrayOutputStream {
    private Project project;
    private String property;
    private boolean trim;

    public PropertyOutputStream(Project p, String s) {
        this(p, s, true);
    }

    public PropertyOutputStream(Project p, String s, boolean b) {
        this.project = p;
        this.property = s;
        this.trim = b;
    }

    @Override
    public void close() {
        if (this.project != null && this.property != null) {
            String s = new String(this.toByteArray());
            this.project.setNewProperty(this.property, this.trim ? s.trim() : s);
        }
    }
}

