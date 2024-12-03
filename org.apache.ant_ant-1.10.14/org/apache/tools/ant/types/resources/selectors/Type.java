/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class Type
implements ResourceSelector {
    private static final String FILE_ATTR = "file";
    private static final String DIR_ATTR = "dir";
    private static final String ANY_ATTR = "any";
    public static final Type FILE = new Type(new FileDir("file"));
    public static final Type DIR = new Type(new FileDir("dir"));
    public static final Type ANY = new Type(new FileDir("any"));
    private FileDir type = null;

    public Type() {
    }

    public Type(FileDir fd) {
        this.setType(fd);
    }

    public void setType(FileDir fd) {
        this.type = fd;
    }

    @Override
    public boolean isSelected(Resource r) {
        if (this.type == null) {
            throw new BuildException("The type attribute is required.");
        }
        int i = this.type.getIndex();
        return i == 2 || (r.isDirectory() ? i == 1 : i == 0);
    }

    public static class FileDir
    extends EnumeratedAttribute {
        private static final String[] VALUES = new String[]{"file", "dir", "any"};

        public FileDir() {
        }

        public FileDir(String value) {
            this.setValue(value);
        }

        @Override
        public String[] getValues() {
            return VALUES;
        }
    }
}

