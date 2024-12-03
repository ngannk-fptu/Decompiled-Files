/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.function;

import java.io.IOException;
import org.apache.commons.io.function.Uncheck;

@FunctionalInterface
public interface IORunnable {
    default public Runnable asRunnable() {
        return () -> Uncheck.run(this);
    }

    public void run() throws IOException;
}

