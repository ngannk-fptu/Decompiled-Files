/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl
implements SourceLocation {
    Class withinType;
    String fileName;
    int line;

    SourceLocationImpl(Class withinType, String fileName, int line) {
        this.withinType = withinType;
        this.fileName = fileName;
        this.line = line;
    }

    @Override
    public Class getWithinType() {
        return this.withinType;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public int getColumn() {
        return -1;
    }

    public String toString() {
        return this.getFileName() + ":" + this.getLine();
    }
}

