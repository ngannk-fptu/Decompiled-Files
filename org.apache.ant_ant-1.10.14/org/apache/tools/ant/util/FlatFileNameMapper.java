/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.util.FileNameMapper;

public class FlatFileNameMapper
implements FileNameMapper {
    @Override
    public void setFrom(String from) {
    }

    @Override
    public void setTo(String to) {
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        String[] stringArray;
        if (sourceFileName == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = new File(sourceFileName).getName();
        }
        return stringArray;
    }
}

