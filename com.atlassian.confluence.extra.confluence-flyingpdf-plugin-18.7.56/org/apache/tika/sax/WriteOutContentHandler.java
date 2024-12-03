/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.UUID;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WriteOutContentHandler
extends ContentHandlerDecorator {
    private final Serializable tag = UUID.randomUUID();
    private final int writeLimit;
    private int writeCount = 0;

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

    public WriteOutContentHandler(OutputStream stream) {
        this(new OutputStreamWriter(stream, Charset.defaultCharset()));
    }

    public WriteOutContentHandler(int writeLimit) {
        this(new StringWriter(), writeLimit);
    }

    public WriteOutContentHandler() {
        this(100000);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.characters(ch, start, length);
            this.writeCount += length;
        } else {
            super.characters(ch, start, this.writeLimit - this.writeCount);
            this.writeCount = this.writeLimit;
            throw new WriteLimitReachedException("Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).", this.tag);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.ignorableWhitespace(ch, start, length);
            this.writeCount += length;
        } else {
            super.ignorableWhitespace(ch, start, this.writeLimit - this.writeCount);
            this.writeCount = this.writeLimit;
            throw new WriteLimitReachedException("Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).", this.tag);
        }
    }

    public boolean isWriteLimitReached(Throwable t) {
        if (t instanceof WriteLimitReachedException) {
            return this.tag.equals(((WriteLimitReachedException)t).tag);
        }
        return t.getCause() != null && this.isWriteLimitReached(t.getCause());
    }

    private static class WriteLimitReachedException
    extends SAXException {
        private static final long serialVersionUID = -1850581945459429943L;
        private final Serializable tag;

        public WriteLimitReachedException(String message, Serializable tag) {
            super(message);
            this.tag = tag;
        }
    }
}

