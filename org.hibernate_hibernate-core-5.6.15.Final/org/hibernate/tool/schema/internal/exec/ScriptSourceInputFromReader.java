/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.Reader;
import org.hibernate.tool.schema.internal.exec.AbstractScriptSourceInput;
import org.hibernate.tool.schema.spi.ScriptSourceInput;

public class ScriptSourceInputFromReader
extends AbstractScriptSourceInput
implements ScriptSourceInput {
    private final Reader reader;

    public ScriptSourceInputFromReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    protected Reader reader() {
        return this.reader;
    }

    @Override
    protected String getScriptDescription() {
        return "[injected ScriptSourceInputFromReader script]";
    }

    public String toString() {
        return "ScriptSourceInputFromReader()";
    }
}

