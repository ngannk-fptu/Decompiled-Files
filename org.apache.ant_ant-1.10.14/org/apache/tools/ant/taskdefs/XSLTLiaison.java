/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;

public interface XSLTLiaison {
    public static final String FILE_PROTOCOL_PREFIX = "file://";

    public void setStylesheet(File var1) throws Exception;

    public void addParam(String var1, String var2) throws Exception;

    public void transform(File var1, File var2) throws Exception;
}

