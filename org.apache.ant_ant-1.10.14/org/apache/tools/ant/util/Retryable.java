/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;

public interface Retryable {
    public static final int RETRY_FOREVER = -1;

    public void execute() throws IOException;
}

