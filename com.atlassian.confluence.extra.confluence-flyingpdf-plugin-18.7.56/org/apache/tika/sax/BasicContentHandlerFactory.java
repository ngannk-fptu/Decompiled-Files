/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.sax.ToTextContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class BasicContentHandlerFactory
implements ContentHandlerFactory {
    private final HANDLER_TYPE type;
    private final int writeLimit;

    public static HANDLER_TYPE parseHandlerType(String handlerTypeName, HANDLER_TYPE defaultType) {
        String lcHandlerTypeName;
        if (handlerTypeName == null) {
            return defaultType;
        }
        switch (lcHandlerTypeName = handlerTypeName.toLowerCase(Locale.ROOT)) {
            case "xml": {
                return HANDLER_TYPE.XML;
            }
            case "text": {
                return HANDLER_TYPE.TEXT;
            }
            case "txt": {
                return HANDLER_TYPE.TEXT;
            }
            case "html": {
                return HANDLER_TYPE.HTML;
            }
            case "body": {
                return HANDLER_TYPE.BODY;
            }
            case "ignore": {
                return HANDLER_TYPE.IGNORE;
            }
        }
        return defaultType;
    }

    public BasicContentHandlerFactory(HANDLER_TYPE type, int writeLimit) {
        this.type = type;
        this.writeLimit = writeLimit;
    }

    @Override
    public ContentHandler getNewContentHandler() {
        if (this.type == HANDLER_TYPE.BODY) {
            return new BodyContentHandler(this.writeLimit);
        }
        if (this.type == HANDLER_TYPE.IGNORE) {
            return new DefaultHandler();
        }
        if (this.writeLimit > -1) {
            switch (this.type) {
                case TEXT: {
                    return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
                }
                case HTML: {
                    return new WriteOutContentHandler(new ToHTMLContentHandler(), this.writeLimit);
                }
                case XML: {
                    return new WriteOutContentHandler(new ToXMLContentHandler(), this.writeLimit);
                }
            }
            return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
        }
        switch (this.type) {
            case TEXT: {
                return new ToTextContentHandler();
            }
            case HTML: {
                return new ToHTMLContentHandler();
            }
            case XML: {
                return new ToXMLContentHandler();
            }
        }
        return new ToTextContentHandler();
    }

    @Override
    public ContentHandler getNewContentHandler(OutputStream os, String encoding) throws UnsupportedEncodingException {
        return this.getNewContentHandler(os, Charset.forName(encoding));
    }

    @Override
    public ContentHandler getNewContentHandler(OutputStream os, Charset charset) {
        if (this.type == HANDLER_TYPE.IGNORE) {
            return new DefaultHandler();
        }
        try {
            if (this.writeLimit > -1) {
                switch (this.type) {
                    case BODY: {
                        return new WriteOutContentHandler(new BodyContentHandler(new OutputStreamWriter(os, charset)), this.writeLimit);
                    }
                    case TEXT: {
                        return new WriteOutContentHandler(new ToTextContentHandler(os, charset.name()), this.writeLimit);
                    }
                    case HTML: {
                        return new WriteOutContentHandler(new ToHTMLContentHandler(os, charset.name()), this.writeLimit);
                    }
                    case XML: {
                        return new WriteOutContentHandler(new ToXMLContentHandler(os, charset.name()), this.writeLimit);
                    }
                }
                return new WriteOutContentHandler(new ToTextContentHandler(os, charset.name()), this.writeLimit);
            }
            switch (this.type) {
                case BODY: {
                    return new BodyContentHandler(new OutputStreamWriter(os, charset));
                }
                case TEXT: {
                    return new ToTextContentHandler(os, charset.name());
                }
                case HTML: {
                    return new ToHTMLContentHandler(os, charset.name());
                }
                case XML: {
                    return new ToXMLContentHandler(os, charset.name());
                }
            }
            return new ToTextContentHandler(os, charset.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("couldn't find charset for name: " + charset);
        }
    }

    public HANDLER_TYPE getType() {
        return this.type;
    }

    public int getWriteLimit() {
        return this.writeLimit;
    }

    public static enum HANDLER_TYPE {
        BODY,
        IGNORE,
        TEXT,
        HTML,
        XML;

    }
}

