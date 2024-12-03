/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.util.FileNameMapper;

public class IdentityMapper
implements FileNameMapper {
    @Override
    public void setFrom(String from) {
    }

    @Override
    public void setTo(String to) {
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        if (sourceFileName == null) {
            return null;
        }
        return new String[]{sourceFileName};
    }
}

