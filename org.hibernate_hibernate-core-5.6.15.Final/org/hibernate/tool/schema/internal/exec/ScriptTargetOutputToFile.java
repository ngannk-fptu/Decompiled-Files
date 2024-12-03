/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.hibernate.internal.CoreLogging;
import org.hibernate.tool.schema.internal.exec.AbstractScriptTargetOutput;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.jboss.logging.Logger;

public class ScriptTargetOutputToFile
extends AbstractScriptTargetOutput
implements ScriptTargetOutput {
    private static final Logger log = CoreLogging.logger(ScriptTargetOutputToFile.class);
    private final File file;
    private final String charsetName;
    private final boolean append;
    private Writer writer;

    public ScriptTargetOutputToFile(File file, String charsetName, boolean append) {
        this.file = file;
        this.charsetName = charsetName;
        this.append = append;
    }

    public ScriptTargetOutputToFile(File file, String charsetName) {
        this(file, charsetName, true);
    }

    @Override
    protected Writer writer() {
        if (this.writer == null) {
            throw new SchemaManagementException("Illegal state : writer null - not prepared");
        }
        return this.writer;
    }

    @Override
    public void prepare() {
        super.prepare();
        this.writer = ScriptTargetOutputToFile.toFileWriter(this.file, this.charsetName, this.append);
    }

    @Override
    public void release() {
        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (IOException e) {
                throw new SchemaManagementException("Unable to close file writer : " + e);
            }
            finally {
                this.writer = null;
            }
        }
    }

    static Writer toFileWriter(File file, String charsetName, boolean append) {
        try {
            if (!file.exists()) {
                log.debug((Object)("Attempting to create non-existent script target file : " + file.getAbsolutePath()));
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
        }
        catch (Exception e) {
            log.debug((Object)("Exception calling File#createNewFile : " + e));
        }
        try {
            return charsetName != null ? new OutputStreamWriter((OutputStream)new FileOutputStream(file, append), charsetName) : new OutputStreamWriter(new FileOutputStream(file, append));
        }
        catch (IOException e) {
            throw new SchemaManagementException("Unable to open specified script target file for writing : " + file, e);
        }
    }
}

