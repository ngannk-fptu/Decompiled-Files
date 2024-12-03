/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import org.hibernate.tool.schema.internal.exec.AbstractScriptSourceInput;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputFromFile;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.jboss.logging.Logger;

public class ScriptSourceInputFromUrl
extends AbstractScriptSourceInput
implements ScriptSourceInput {
    private static final Logger log = Logger.getLogger(ScriptSourceInputFromFile.class);
    private final URL url;
    private final String charsetName;
    private Reader reader;

    public ScriptSourceInputFromUrl(URL url, String charsetName) {
        this.url = url;
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
        try {
            this.reader = this.charsetName != null ? new InputStreamReader(this.url.openStream(), this.charsetName) : new InputStreamReader(this.url.openStream());
        }
        catch (IOException e) {
            throw new SchemaManagementException("Unable to open specified script source url [" + this.url + "] for reading");
        }
    }

    @Override
    protected String getScriptDescription() {
        return this.url.toExternalForm();
    }

    @Override
    public void release() {
        try {
            this.reader().close();
        }
        catch (IOException e) {
            log.warn((Object)"Unable to close file reader for generation script source");
        }
    }

    public String toString() {
        return "ScriptSourceInputFromUrl(" + this.url.toExternalForm() + ")";
    }
}

