/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Basename
extends Task {
    private File file;
    private String property;
    private String suffix;

    public void setFile(File file) {
        this.file = file;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute required", this.getLocation());
        }
        if (this.file == null) {
            throw new BuildException("file attribute required", this.getLocation());
        }
        this.getProject().setNewProperty(this.property, this.removeExtension(this.file.getName(), this.suffix));
    }

    private String removeExtension(String s, String ext) {
        if (ext == null || !s.endsWith(ext)) {
            return s;
        }
        int clipFrom = s.length() - ext.length();
        if (ext.charAt(0) != '.' && clipFrom > 0 && s.charAt(clipFrom - 1) == '.') {
            --clipFrom;
        }
        return s.substring(0, clipFrom);
    }
}

