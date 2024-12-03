/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;

public interface CallStack {
    public void clear();

    public void fillInStackTrace();

    public boolean printStackTrace(PrintWriter var1);
}

