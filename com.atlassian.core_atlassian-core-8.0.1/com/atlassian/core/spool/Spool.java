/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import java.io.IOException;
import java.io.InputStream;

public interface Spool {
    public InputStream spool(InputStream var1) throws IOException;
}

