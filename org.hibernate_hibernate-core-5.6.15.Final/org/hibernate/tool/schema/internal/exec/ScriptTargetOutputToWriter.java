/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.Writer;
import org.hibernate.tool.schema.internal.exec.AbstractScriptTargetOutput;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;

public class ScriptTargetOutputToWriter
extends AbstractScriptTargetOutput
implements ScriptTargetOutput {
    private final Writer writer;

    public ScriptTargetOutputToWriter(Writer writer) {
        if (writer == null) {
            throw new SchemaManagementException("Writer cannot be null");
        }
        this.writer = writer;
    }

    @Override
    protected Writer writer() {
        return this.writer;
    }
}

