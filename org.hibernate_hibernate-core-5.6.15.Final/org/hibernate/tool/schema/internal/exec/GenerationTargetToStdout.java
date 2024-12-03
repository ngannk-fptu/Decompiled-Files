/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;

public class GenerationTargetToStdout
implements GenerationTarget {
    private final String delimiter;

    public GenerationTargetToStdout(String delimiter) {
        this.delimiter = delimiter;
    }

    public GenerationTargetToStdout() {
        this(null);
    }

    @Override
    public void prepare() {
    }

    @Override
    @AllowSysOut
    public void accept(String command) {
        if (this.delimiter != null) {
            command = command + this.delimiter;
        }
        System.out.println(command);
    }

    @Override
    public void release() {
    }
}

