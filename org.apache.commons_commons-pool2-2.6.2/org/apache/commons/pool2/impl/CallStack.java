/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;

public interface CallStack {
    public boolean printStackTrace(PrintWriter var1);

    public void fillInStackTrace();

    public void clear();
}

