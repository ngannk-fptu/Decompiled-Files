/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;

public class GlobPatternMapper
implements FileNameMapper {
    protected String fromPrefix = null;
    protected String fromPostfix = null;
    protected int prefixLength;
    protected int postfixLength;
    protected String toPrefix = null;
    protected String toPostfix = null;
    private boolean fromContainsStar = false;
    private boolean toContainsStar = false;
    private boolean handleDirSep = false;
    private boolean caseSensitive = true;

    public void setHandleDirSep(boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }

    public boolean getHandleDirSep() {
        return this.handleDirSep;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    public void setFrom(String from) {
        if (from == null) {
            throw new BuildException("this mapper requires a 'from' attribute");
        }
        int index = from.lastIndexOf(42);
        if (index < 0) {
            this.fromPrefix = from;
            this.fromPostfix = "";
        } else {
            this.fromPrefix = from.substring(0, index);
            this.fromPostfix = from.substring(index + 1);
            this.fromContainsStar = true;
        }
        this.prefixLength = this.fromPrefix.length();
        this.postfixLength = this.fromPostfix.length();
    }

    @Override
    public void setTo(String to) {
        if (to == null) {
            throw new BuildException("this mapper requires a 'to' attribute");
        }
        int index = to.lastIndexOf(42);
        if (index < 0) {
            this.toPrefix = to;
            this.toPostfix = "";
        } else {
            this.toPrefix = to.substring(0, index);
            this.toPostfix = to.substring(index + 1);
            this.toContainsStar = true;
        }
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        if (sourceFileName == null) {
            return null;
        }
        String modName = this.modifyName(sourceFileName);
        if (this.fromPrefix == null || sourceFileName.length() < this.prefixLength + this.postfixLength || !this.fromContainsStar && !modName.equals(this.modifyName(this.fromPrefix)) || this.fromContainsStar && (!modName.startsWith(this.modifyName(this.fromPrefix)) || !modName.endsWith(this.modifyName(this.fromPostfix)))) {
            return null;
        }
        return new String[]{this.toPrefix + (this.toContainsStar ? this.extractVariablePart(sourceFileName) + this.toPostfix : "")};
    }

    protected String extractVariablePart(String name) {
        return name.substring(this.prefixLength, name.length() - this.postfixLength);
    }

    private String modifyName(String name) {
        if (!this.caseSensitive) {
            name = name.toLowerCase();
        }
        if (this.handleDirSep && name.contains("\\")) {
            name = name.replace('\\', '/');
        }
        return name;
    }
}

