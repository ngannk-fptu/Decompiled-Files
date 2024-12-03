/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.apache.tika.exception.CorruptedFileException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class RecursiveParserWrapper
extends ParserDecorator {
    private static final long serialVersionUID = 9086536568120690938L;
    @Deprecated
    public static final Property TIKA_CONTENT = AbstractRecursiveParserWrapperHandler.TIKA_CONTENT;
    @Deprecated
    public static final Property PARSE_TIME_MILLIS = AbstractRecursiveParserWrapperHandler.PARSE_TIME_MILLIS;
    @Deprecated
    public static final Property WRITE_LIMIT_REACHED = AbstractRecursiveParserWrapperHandler.WRITE_LIMIT_REACHED;
    @Deprecated
    public static final Property EMBEDDED_RESOURCE_LIMIT_REACHED = AbstractRecursiveParserWrapperHandler.EMBEDDED_RESOURCE_LIMIT_REACHED;
    @Deprecated
    public static final Property EMBEDDED_EXCEPTION = AbstractRecursiveParserWrapperHandler.EMBEDDED_EXCEPTION;
    @Deprecated
    public static final Property EMBEDDED_RESOURCE_PATH = AbstractRecursiveParserWrapperHandler.EMBEDDED_RESOURCE_PATH;
    @Deprecated
    private ContentHandlerFactory contentHandlerFactory = null;
    private final boolean catchEmbeddedExceptions;
    @Deprecated
    private int maxEmbeddedResources = -1;
    @Deprecated
    private ParserState lastParseState = null;

    public RecursiveParserWrapper(Parser wrappedParser) {
        this(wrappedParser, true);
    }

    public RecursiveParserWrapper(Parser wrappedParser, boolean catchEmbeddedExceptions) {
        super(wrappedParser);
        this.catchEmbeddedExceptions = catchEmbeddedExceptions;
    }

    @Deprecated
    public RecursiveParserWrapper(Parser wrappedParser, ContentHandlerFactory contentHandlerFactory) {
        this(wrappedParser, contentHandlerFactory, true);
    }

    @Deprecated
    public RecursiveParserWrapper(Parser wrappedParser, ContentHandlerFactory contentHandlerFactory, boolean catchEmbeddedExceptions) {
        super(wrappedParser);
        this.contentHandlerFactory = contentHandlerFactory;
        this.catchEmbeddedExceptions = catchEmbeddedExceptions;
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.getWrappedParser().getSupportedTypes(context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler recursiveParserWrapperHandler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        block9: {
            ParserState parserState;
            if (recursiveParserWrapperHandler instanceof AbstractRecursiveParserWrapperHandler) {
                parserState = new ParserState((AbstractRecursiveParserWrapperHandler)recursiveParserWrapperHandler);
            } else {
                this.lastParseState = parserState = new ParserState(new RecursiveParserWrapperHandler(this.contentHandlerFactory, this.maxEmbeddedResources));
            }
            EmbeddedParserDecorator decorator = new EmbeddedParserDecorator(this.getWrappedParser(), "/", parserState);
            context.set(Parser.class, decorator);
            ContentHandler localHandler = parserState.recursiveParserWrapperHandler.getNewContentHandler();
            long started = System.currentTimeMillis();
            parserState.recursiveParserWrapperHandler.startDocument();
            TemporaryResources tmp = new TemporaryResources();
            int totalWriteLimit = -1;
            if (recursiveParserWrapperHandler instanceof AbstractRecursiveParserWrapperHandler) {
                totalWriteLimit = ((AbstractRecursiveParserWrapperHandler)recursiveParserWrapperHandler).getTotalWriteLimit();
            }
            try {
                TikaInputStream tis = TikaInputStream.get(stream, tmp);
                RecursivelySecureContentHandler secureContentHandler = new RecursivelySecureContentHandler(localHandler, tis, totalWriteLimit);
                context.set(RecursivelySecureContentHandler.class, secureContentHandler);
                this.getWrappedParser().parse(tis, secureContentHandler, metadata, context);
            }
            catch (Throwable e) {
                if (WriteLimitReachedException.isWriteLimitReached(e)) {
                    metadata.set(RecursiveParserWrapperHandler.WRITE_LIMIT_REACHED, "true");
                    break block9;
                }
                String stackTrace = ExceptionUtils.getFilteredStackTrace(e);
                metadata.add(RecursiveParserWrapperHandler.CONTAINER_EXCEPTION, stackTrace);
                throw e;
            }
            finally {
                tmp.dispose();
                long elapsedMillis = System.currentTimeMillis() - started;
                metadata.set(RecursiveParserWrapperHandler.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                parserState.recursiveParserWrapperHandler.endDocument(localHandler, metadata);
                parserState.recursiveParserWrapperHandler.endDocument();
            }
        }
    }

    @Deprecated
    public List<Metadata> getMetadata() {
        if (this.lastParseState != null) {
            return ((RecursiveParserWrapperHandler)this.lastParseState.recursiveParserWrapperHandler).getMetadataList();
        }
        throw new IllegalStateException("This is deprecated; please use a RecursiveParserWrapperHandler instead");
    }

    @Deprecated
    public void setMaxEmbeddedResources(int max) {
        this.maxEmbeddedResources = max;
    }

    @Deprecated
    public void reset() {
        if (this.lastParseState == null) {
            throw new IllegalStateException("This is deprecated; please use a RecursiveParserWrapperHandler instead");
        }
        this.lastParseState = new ParserState(new RecursiveParserWrapperHandler(this.contentHandlerFactory, this.maxEmbeddedResources));
    }

    private String getResourceName(Metadata metadata, ParserState state) {
        String objectName = "";
        objectName = metadata.get("resourceName") != null ? metadata.get("resourceName") : (metadata.get("embeddedRelationshipId") != null ? metadata.get("embeddedRelationshipId") : "embedded-" + ++state.unknownCount);
        objectName = FilenameUtils.getName(objectName);
        return objectName;
    }

    public static class WriteLimitReached
    extends SAXException {
        final int writeLimit;

        WriteLimitReached(int writeLimit) {
            this.writeLimit = writeLimit;
        }

        @Override
        public String getMessage() {
            return "Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).";
        }
    }

    private class RecursivelySecureContentHandler
    extends SecureContentHandler {
        private ContentHandler handler;
        private final int totalWriteLimit;
        private int totalChars;

        public RecursivelySecureContentHandler(ContentHandler handler, TikaInputStream stream, int totalWriteLimit) {
            super(handler, stream);
            this.totalChars = 0;
            this.handler = handler;
            this.totalWriteLimit = totalWriteLimit;
        }

        public void updateContentHandler(ContentHandler handler) {
            this.setContentHandler(handler);
            this.handler = handler;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
            this.handler.startElement(uri, localName, name, atts);
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            this.handler.endElement(uri, localName, name);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.totalWriteLimit < 0) {
                super.characters(ch, start, length);
                return;
            }
            int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.characters(ch, start, availableLength);
            this.totalChars += availableLength;
            if (availableLength < length) {
                throw new WriteLimitReached(this.totalWriteLimit);
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            if (this.totalWriteLimit < 0) {
                super.ignorableWhitespace(ch, start, length);
                return;
            }
            int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.ignorableWhitespace(ch, start, availableLength);
            if (availableLength < length) {
                throw new WriteLimitReached(this.totalWriteLimit);
            }
            this.totalChars += availableLength;
        }
    }

    private class ParserState {
        private int unknownCount = 0;
        private final AbstractRecursiveParserWrapperHandler recursiveParserWrapperHandler;

        private ParserState(AbstractRecursiveParserWrapperHandler handler) {
            this.recursiveParserWrapperHandler = handler;
        }
    }

    private class EmbeddedParserDecorator
    extends ParserDecorator {
        private static final long serialVersionUID = 207648200464263337L;
        private String location;
        private final ParserState parserState;

        private EmbeddedParserDecorator(Parser parser, String location, ParserState parseState) {
            super(parser);
            this.location = null;
            this.location = location;
            if (!this.location.endsWith("/")) {
                this.location = this.location + "/";
            }
            this.parserState = parseState;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void parse(InputStream stream, ContentHandler ignore, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
            block13: {
                if (this.parserState.recursiveParserWrapperHandler.hasHitMaximumEmbeddedResources()) {
                    return;
                }
                String objectName = RecursiveParserWrapper.this.getResourceName(metadata, this.parserState);
                String objectLocation = this.location + objectName;
                metadata.add(AbstractRecursiveParserWrapperHandler.EMBEDDED_RESOURCE_PATH, objectLocation);
                ContentHandler localHandler = this.parserState.recursiveParserWrapperHandler.getNewContentHandler();
                this.parserState.recursiveParserWrapperHandler.startEmbeddedDocument(localHandler, metadata);
                Parser preContextParser = context.get(Parser.class);
                context.set(Parser.class, new EmbeddedParserDecorator(this.getWrappedParser(), objectLocation, this.parserState));
                long started = System.currentTimeMillis();
                RecursivelySecureContentHandler secureContentHandler = context.get(RecursivelySecureContentHandler.class);
                ContentHandler preContextHandler = secureContentHandler.handler;
                secureContentHandler.updateContentHandler(localHandler);
                try {
                    super.parse(stream, secureContentHandler, metadata, context);
                }
                catch (SAXException e) {
                    boolean wlr = WriteLimitReachedException.isWriteLimitReached(e);
                    if (wlr) {
                        metadata.add(WRITE_LIMIT_REACHED, "true");
                        throw e;
                    }
                    if (RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                        ParserUtils.recordParserFailure(this, e, metadata);
                        break block13;
                    }
                    throw e;
                }
                catch (CorruptedFileException e) {
                    throw e;
                }
                catch (TikaException e) {
                    if (context.get(ZeroByteFileException.IgnoreZeroByteFileException.class) != null && e instanceof ZeroByteFileException) {
                        break block13;
                    }
                    if (RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                        ParserUtils.recordParserFailure(this, e, metadata);
                        break block13;
                    }
                    throw e;
                }
                finally {
                    context.set(Parser.class, preContextParser);
                    secureContentHandler.updateContentHandler(preContextHandler);
                    long elapsedMillis = System.currentTimeMillis() - started;
                    metadata.set(RecursiveParserWrapperHandler.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                    this.parserState.recursiveParserWrapperHandler.endEmbeddedDocument(localHandler, metadata);
                }
            }
        }
    }
}

