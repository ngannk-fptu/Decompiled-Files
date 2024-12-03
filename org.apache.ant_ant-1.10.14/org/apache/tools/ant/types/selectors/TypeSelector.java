/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;

public class TypeSelector
extends BaseExtendSelector {
    public static final String TYPE_KEY = "type";
    private String type = null;

    @Override
    public String toString() {
        return "{typeselector type: " + this.type + "}";
    }

    public void setType(FileType fileTypes) {
        this.type = fileTypes.getValue();
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (TYPE_KEY.equalsIgnoreCase(paramname)) {
                    FileType t = new FileType();
                    t.setValue(parameter.getValue());
                    this.setType(t);
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.type == null) {
            this.setError("The type attribute is required");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        if (file.isDirectory()) {
            return this.type.equals("dir");
        }
        return this.type.equals("file");
    }

    public static class FileType
    extends EnumeratedAttribute {
        public static final String FILE = "file";
        public static final String DIR = "dir";

        @Override
        public String[] getValues() {
            return new String[]{FILE, DIR};
        }
    }
}

