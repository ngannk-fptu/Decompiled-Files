/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.tool.schema.internal.exec.AbstractScriptTargetOutput;
import org.hibernate.tool.schema.spi.SchemaManagementException;

public class ScriptTargetOutputToStdout
extends AbstractScriptTargetOutput {
    private Writer writer;

    @Override
    protected Writer writer() {
        if (this.writer == null) {
            throw new SchemaManagementException("Illegal state : writer null - not prepared");
        }
        return this.writer;
    }

    @Override
    @AllowSysOut
    public void prepare() {
        super.prepare();
        this.writer = new OutputStreamWriter(System.out);
    }

    @Override
    public void accept(String command) {
        super.accept(command);
    }

    @Override
    public void release() {
        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (IOException e) {
                throw new SchemaManagementException("Unable to close file writer : " + e.toString());
            }
            finally {
                this.writer = null;
            }
        }
    }
}

