/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;

public class GenerationTargetToScript
implements GenerationTarget {
    private final ScriptTargetOutput scriptTarget;
    private final String delimiter;

    public GenerationTargetToScript(ScriptTargetOutput scriptTarget, String delimiter) {
        if (scriptTarget == null) {
            throw new SchemaManagementException("ScriptTargetOutput cannot be null");
        }
        this.scriptTarget = scriptTarget;
        this.delimiter = delimiter;
    }

    @Override
    public void prepare() {
        this.scriptTarget.prepare();
    }

    @Override
    public void accept(String command) {
        if (this.delimiter != null) {
            command = command + this.delimiter;
        }
        this.scriptTarget.accept(command);
    }

    @Override
    public void release() {
        this.scriptTarget.release();
    }
}

