/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.util.concurrent.Executor;
import org.apache.tomcat.util.compat.JreCompat;

public class VirtualThreadExecutor
implements Executor {
    private final JreCompat jreCompat = JreCompat.getInstance();
    private Object threadBuilder;

    public VirtualThreadExecutor(String namePrefix) {
        this.threadBuilder = this.jreCompat.createVirtualThreadBuilder(namePrefix);
    }

    @Override
    public void execute(Runnable command) {
        this.jreCompat.threadBuilderStart(this.threadBuilder, command);
    }
}

