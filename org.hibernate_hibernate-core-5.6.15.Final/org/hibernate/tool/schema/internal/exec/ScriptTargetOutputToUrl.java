/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import org.hibernate.internal.CoreLogging;
import org.hibernate.tool.schema.internal.exec.AbstractScriptTargetOutput;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.jboss.logging.Logger;

public class ScriptTargetOutputToUrl
extends AbstractScriptTargetOutput
implements ScriptTargetOutput {
    private static final Logger log = CoreLogging.logger(ScriptTargetOutputToUrl.class);
    private final URL url;
    private final String charsetName;
    private final boolean append;
    private Writer writer;

    public ScriptTargetOutputToUrl(URL url, String charsetName, boolean append) {
        this.url = url;
        this.charsetName = charsetName;
        this.append = append;
    }

    public ScriptTargetOutputToUrl(URL url, String charsetName) {
        this(url, charsetName, true);
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
        this.writer = ScriptTargetOutputToUrl.toWriter(this.url, this.charsetName, this.append);
    }

    @Override
    public void release() {
        try {
            this.writer().close();
        }
        catch (IOException e) {
            throw new SchemaManagementException("Unable to close file writer : " + e);
        }
    }

    private static Writer toWriter(URL url, String charsetName, boolean append) {
        log.debug((Object)("Attempting to resolve writer for URL : " + url));
        try {
            return ScriptTargetOutputToFile.toFileWriter(new File(url.toURI()), charsetName, append);
        }
        catch (URISyntaxException e) {
            throw new SchemaManagementException(String.format("Could not convert specified URL[%s] to a File reference", url), e);
        }
    }
}

