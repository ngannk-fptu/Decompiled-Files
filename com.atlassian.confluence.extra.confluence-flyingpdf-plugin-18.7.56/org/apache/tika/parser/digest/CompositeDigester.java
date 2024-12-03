/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.digest;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOExceptionWithCause;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DigestingParser;
import org.apache.tika.parser.ParseContext;

public class CompositeDigester
implements DigestingParser.Digester {
    private final DigestingParser.Digester[] digesters;

    public CompositeDigester(DigestingParser.Digester ... digesters) {
        this.digesters = digesters;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void digest(InputStream is, Metadata m, ParseContext parseContext) throws IOException {
        TemporaryResources tmp = new TemporaryResources();
        TikaInputStream tis = TikaInputStream.get(is, tmp);
        try {
            for (DigestingParser.Digester digester : this.digesters) {
                digester.digest(tis, m, parseContext);
            }
        }
        finally {
            try {
                tmp.dispose();
            }
            catch (TikaException e) {
                throw new IOExceptionWithCause(e);
            }
        }
    }
}

