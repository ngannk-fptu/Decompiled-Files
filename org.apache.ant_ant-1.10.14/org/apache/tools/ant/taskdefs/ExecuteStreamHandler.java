/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ExecuteStreamHandler {
    public void setProcessInputStream(OutputStream var1) throws IOException;

    public void setProcessErrorStream(InputStream var1) throws IOException;

    public void setProcessOutputStream(InputStream var1) throws IOException;

    public void start() throws IOException;

    public void stop();
}

