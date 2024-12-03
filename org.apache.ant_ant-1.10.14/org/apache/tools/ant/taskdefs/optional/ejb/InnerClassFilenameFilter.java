/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FilenameFilter;

public class InnerClassFilenameFilter
implements FilenameFilter {
    private String baseClassName;

    InnerClassFilenameFilter(String baseclass) {
        int extidx = baseclass.lastIndexOf(".class");
        if (extidx == -1) {
            extidx = baseclass.length() - 1;
        }
        this.baseClassName = baseclass.substring(0, extidx);
    }

    @Override
    public boolean accept(File dir, String filename) {
        return filename.lastIndexOf(46) == filename.lastIndexOf(".class") && filename.indexOf(this.baseClassName + "$") == 0;
    }
}

