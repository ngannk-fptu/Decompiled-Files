/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.util.FileNameMapper;

public class MergingMapper
implements FileNameMapper {
    protected String[] mergedFile = null;

    public MergingMapper() {
    }

    public MergingMapper(String to) {
        this.setTo(to);
    }

    @Override
    public void setFrom(String from) {
    }

    @Override
    public void setTo(String to) {
        this.mergedFile = new String[]{to};
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        return this.mergedFile;
    }
}

