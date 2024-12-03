/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParseRecord;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WriteOutContentHandler
extends ContentHandlerDecorator {
    private final int writeLimit;
    private int writeCount = 0;
    private boolean throwOnWriteLimitReached = true;
    private ParseContext parseContext = null;
    private boolean writeLimitReached;

    public WriteOutContentHandler(ContentHandler handler, int writeLimit) {
        super(handler);
        this.writeLimit = writeLimit;
    }

    public WriteOutContentHandler(Writer writer, int writeLimit) {
        this(new ToTextContentHandler(writer), writeLimit);
    }

    public WriteOutContentHandler(Writer writer) {
        this(writer, -1);
    }

    @Deprecated
    public WriteOutContentHandler(OutputStream stream) {
        this(new OutputStreamWriter(stream, Charset.defaultCharset()));
    }

    public WriteOutContentHandler(int writeLimit) {
        this(new StringWriter(), writeLimit);
    }

    public WriteOutContentHandler() {
        this(100000);
    }

    public WriteOutContentHandler(ContentHandler handler, int writeLimit, boolean throwOnWriteLimitReached, ParseContext parseContext) {
        super(handler);
        this.writeLimit = writeLimit;
        this.throwOnWriteLimitReached = throwOnWriteLimitReached;
        this.parseContext = parseContext;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.writeLimitReached) {
            return;
        }
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.characters(ch, start, length);
            this.writeCount += length;
        } else {
            super.characters(ch, start, this.writeLimit - this.writeCount);
            this.handleWriteLimitReached();
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.writeLimitReached) {
            return;
        }
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.ignorableWhitespace(ch, start, length);
            this.writeCount += length;
        } else {
            super.ignorableWhitespace(ch, start, this.writeLimit - this.writeCount);
            this.handleWriteLimitReached();
        }
    }

    private void handleWriteLimitReached() throws WriteLimitReachedException {
        this.writeLimitReached = true;
        this.writeCount = this.writeLimit;
        if (this.throwOnWriteLimitReached) {
            throw new WriteLimitReachedException(this.writeLimit);
        }
        ParseRecord parseRecord = this.parseContext.get(ParseRecord.class);
        if (parseRecord != null) {
            parseRecord.setWriteLimitReached(true);
        }
    }
}

