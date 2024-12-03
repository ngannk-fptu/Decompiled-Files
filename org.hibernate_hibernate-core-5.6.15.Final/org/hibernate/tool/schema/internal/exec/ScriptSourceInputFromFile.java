/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.hibernate.tool.schema.internal.exec.AbstractScriptSourceInput;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.jboss.logging.Logger;

public class ScriptSourceInputFromFile
extends AbstractScriptSourceInput
implements ScriptSourceInput {
    private static final Logger log = Logger.getLogger(ScriptSourceInputFromFile.class);
    private final File file;
    private final String charsetName;
    private Reader reader;

    public ScriptSourceInputFromFile(File file, String charsetName) {
        this.file = file;
        this.charsetName = charsetName;
    }

    @Override
    protected Reader reader() {
        if (this.reader == null) {
            throw new SchemaManagementException("Illegal state - reader is null - not prepared");
        }
        return this.reader;
    }

    @Override
    public void prepare() {
        super.prepare();
        this.reader = ScriptSourceInputFromFile.toReader(this.file, this.charsetName);
    }

    @Override
    protected String getScriptDescription() {
        return this.file.getAbsolutePath();
    }

    private static Reader toReader(File file, String charsetName) {
        if (!file.exists()) {
            log.warnf("Specified schema generation script file [%s] did not exist for reading", (Object)file);
            return new Reader(){

                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    return -1;
                }

                @Override
                public void close() throws IOException {
                }
            };
        }
        try {
            return charsetName != null ? new InputStreamReader((InputStream)new FileInputStream(file), charsetName) : new InputStreamReader(new FileInputStream(file));
        }
        catch (IOException e) {
            throw new SchemaManagementException("Unable to open specified script target file [" + file + "] for reading", e);
        }
    }

    @Override
    public void release() {
        try {
            this.reader.close();
        }
        catch (IOException e) {
            log.warn((Object)"Unable to close file reader for generation script source");
        }
    }

    public String toString() {
        return "ScriptSourceInputFromFile(" + this.file.getAbsolutePath() + ")";
    }
}

