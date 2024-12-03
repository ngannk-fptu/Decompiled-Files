/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.IOException;
import java.io.Writer;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;

public abstract class AbstractScriptTargetOutput
implements ScriptTargetOutput {
    protected abstract Writer writer();

    @Override
    public void prepare() {
    }

    @Override
    public void accept(String command) {
        try {
            this.writer().write(command);
            this.writer().write(System.lineSeparator());
            this.writer().flush();
        }
        catch (IOException e) {
            throw new CommandAcceptanceException("Could not write \"" + command + "\" to target script file", e);
        }
    }

    @Override
    public void release() {
    }
}

