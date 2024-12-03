/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DigestingParser
extends ParserDecorator {
    private final Digester digester;

    public DigestingParser(Parser parser, Digester digester) {
        super(parser);
        this.digester = digester;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        TemporaryResources tmp = new TemporaryResources();
        TikaInputStream tis = TikaInputStream.get(stream, tmp);
        try {
            if (this.digester != null) {
                this.digester.digest((InputStream)((Object)tis), metadata, context);
            }
            super.parse((InputStream)((Object)tis), handler, metadata, context);
        }
        finally {
            tmp.dispose();
        }
    }

    public static interface Encoder {
        public String encode(byte[] var1);
    }

    public static interface Digester {
        public void digest(InputStream var1, Metadata var2, ParseContext var3) throws IOException;
    }
}

