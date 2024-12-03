/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.exception.CorruptedFileException;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParseRecord;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.StatefulParser;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.sax.SecureContentHandler;
import org.apache.tika.sax.WriteLimiter;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class RecursiveParserWrapper
extends ParserDecorator {
    private static final long serialVersionUID = 9086536568120690938L;
    private final boolean catchEmbeddedExceptions;

    public RecursiveParserWrapper(Parser wrappedParser) {
        this(wrappedParser, true);
    }

    public RecursiveParserWrapper(Parser wrappedParser, boolean catchEmbeddedExceptions) {
        super(wrappedParser);
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
            ContentHandlerFactory factory;
            if (!(recursiveParserWrapperHandler instanceof AbstractRecursiveParserWrapperHandler)) {
                throw new IllegalStateException("ContentHandler must implement RecursiveParserWrapperHandler");
            }
            ParserState parserState = new ParserState((AbstractRecursiveParserWrapperHandler)recursiveParserWrapperHandler);
            EmbeddedParserDecorator decorator = new EmbeddedParserDecorator(this.getWrappedParser(), "/", "/", parserState);
            context.set(Parser.class, decorator);
            ContentHandler localHandler = parserState.recursiveParserWrapperHandler.getNewContentHandler();
            long started = System.currentTimeMillis();
            parserState.recursiveParserWrapperHandler.startDocument();
            TemporaryResources tmp = new TemporaryResources();
            int writeLimit = -1;
            boolean throwOnWriteLimitReached = true;
            if (recursiveParserWrapperHandler instanceof AbstractRecursiveParserWrapperHandler && (factory = ((AbstractRecursiveParserWrapperHandler)recursiveParserWrapperHandler).getContentHandlerFactory()) instanceof WriteLimiter) {
                writeLimit = ((WriteLimiter)((Object)factory)).getWriteLimit();
                throwOnWriteLimitReached = ((WriteLimiter)((Object)factory)).isThrowOnWriteLimitReached();
            }
            try {
                TikaInputStream tis = TikaInputStream.get(stream, tmp, metadata);
                RecursivelySecureContentHandler secureContentHandler = new RecursivelySecureContentHandler(localHandler, tis, writeLimit, throwOnWriteLimitReached, context);
                context.set(RecursivelySecureContentHandler.class, secureContentHandler);
                this.getWrappedParser().parse((InputStream)((Object)tis), secureContentHandler, metadata, context);
            }
            catch (Throwable e) {
                if (e instanceof EncryptedDocumentException) {
                    metadata.set(TikaCoreProperties.IS_ENCRYPTED, "true");
                }
                if (WriteLimitReachedException.isWriteLimitReached(e)) {
                    metadata.set(TikaCoreProperties.WRITE_LIMIT_REACHED, "true");
                    break block9;
                }
                String stackTrace = ExceptionUtils.getFilteredStackTrace(e);
                metadata.add(TikaCoreProperties.CONTAINER_EXCEPTION, stackTrace);
                throw e;
            }
            finally {
                tmp.dispose();
                long elapsedMillis = System.currentTimeMillis() - started;
                metadata.set(TikaCoreProperties.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                parserState.recursiveParserWrapperHandler.endDocument(localHandler, metadata);
                parserState.recursiveParserWrapperHandler.endDocument();
            }
        }
    }

    private String getResourceName(Metadata metadata, ParserState state) {
        String objectName = "";
        objectName = metadata.get("resourceName") != null ? metadata.get("resourceName") : (metadata.get("embeddedRelationshipId") != null ? metadata.get("embeddedRelationshipId") : "embedded-" + ++state.unknownCount);
        objectName = FilenameUtils.getName(objectName);
        return objectName;
    }

    static class RecursivelySecureContentHandler
    extends SecureContentHandler {
        private ContentHandler handler;
        private final int totalWriteLimit;
        private final boolean throwOnWriteLimitReached;
        private final ParseContext parseContext;
        private boolean writeLimitReached = false;
        private int totalChars = 0;

        public RecursivelySecureContentHandler(ContentHandler handler, TikaInputStream stream, int totalWriteLimit, boolean throwOnWriteLimitReached, ParseContext parseContext) {
            super(handler, stream);
            this.handler = handler;
            this.totalWriteLimit = totalWriteLimit;
            this.throwOnWriteLimitReached = throwOnWriteLimitReached;
            this.parseContext = parseContext;
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
            if (this.writeLimitReached) {
                return;
            }
            if (this.totalWriteLimit < 0) {
                super.characters(ch, start, length);
                return;
            }
            int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.characters(ch, start, availableLength);
            if (availableLength < length) {
                this.handleWriteLimitReached();
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            if (this.writeLimitReached) {
                return;
            }
            if (this.totalWriteLimit < 0) {
                super.ignorableWhitespace(ch, start, length);
                return;
            }
            int availableLength = Math.min(this.totalWriteLimit - this.totalChars, length);
            super.ignorableWhitespace(ch, start, availableLength);
            if (availableLength < length) {
                this.handleWriteLimitReached();
            }
        }

        private void handleWriteLimitReached() throws WriteLimitReachedException {
            this.writeLimitReached = true;
            if (this.throwOnWriteLimitReached) {
                throw new WriteLimitReachedException(this.totalWriteLimit);
            }
            ParseRecord parseRecord = this.parseContext.get(ParseRecord.class);
            if (parseRecord != null) {
                parseRecord.setWriteLimitReached(true);
            }
        }
    }

    private static class ParserState {
        private final AbstractRecursiveParserWrapperHandler recursiveParserWrapperHandler;
        private int unknownCount = 0;
        private int embeddedCount = 0;

        private ParserState(AbstractRecursiveParserWrapperHandler handler) {
            this.recursiveParserWrapperHandler = handler;
        }
    }

    private class EmbeddedParserDecorator
    extends StatefulParser {
        private static final long serialVersionUID = 207648200464263337L;
        private final ParserState parserState;
        private String location;
        private String embeddedIdPath;

        private EmbeddedParserDecorator(Parser parser, String location, String embeddedIdPath, ParserState parseState) {
            super(parser);
            this.location = null;
            this.embeddedIdPath = null;
            this.location = location;
            if (!this.location.endsWith("/")) {
                this.location = this.location + "/";
            }
            this.embeddedIdPath = embeddedIdPath;
            this.parserState = parseState;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void parse(InputStream stream, ContentHandler ignore, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
            block14: {
                if (this.parserState.recursiveParserWrapperHandler.hasHitMaximumEmbeddedResources()) {
                    return;
                }
                String objectName = RecursiveParserWrapper.this.getResourceName(metadata, this.parserState);
                String objectLocation = this.location + objectName;
                metadata.add(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, objectLocation);
                String idPath = this.embeddedIdPath.equals("/") ? this.embeddedIdPath + ++this.parserState.embeddedCount : this.embeddedIdPath + "/" + ++this.parserState.embeddedCount;
                metadata.add(TikaCoreProperties.EMBEDDED_ID_PATH, idPath);
                metadata.set(TikaCoreProperties.EMBEDDED_ID, this.parserState.embeddedCount);
                ContentHandler localHandler = this.parserState.recursiveParserWrapperHandler.getNewContentHandler();
                this.parserState.recursiveParserWrapperHandler.startEmbeddedDocument(localHandler, metadata);
                Parser preContextParser = context.get(Parser.class);
                context.set(Parser.class, new EmbeddedParserDecorator(this.getWrappedParser(), objectLocation, idPath, this.parserState));
                long started = System.currentTimeMillis();
                RecursivelySecureContentHandler secureContentHandler = context.get(RecursivelySecureContentHandler.class);
                ContentHandler preContextHandler = secureContentHandler.handler;
                secureContentHandler.updateContentHandler(localHandler);
                try {
                    super.parse(stream, secureContentHandler, metadata, context);
                }
                catch (SAXException e) {
                    if (WriteLimitReachedException.isWriteLimitReached(e)) {
                        metadata.add(TikaCoreProperties.WRITE_LIMIT_REACHED, "true");
                        throw e;
                    }
                    if (RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                        ParserUtils.recordParserFailure(this, e, metadata);
                        break block14;
                    }
                    throw e;
                }
                catch (CorruptedFileException e) {
                    throw e;
                }
                catch (TikaException e) {
                    if (e instanceof EncryptedDocumentException) {
                        metadata.set(TikaCoreProperties.IS_ENCRYPTED, true);
                    }
                    if (context.get(ZeroByteFileException.IgnoreZeroByteFileException.class) != null && e instanceof ZeroByteFileException) {
                        break block14;
                    }
                    if (RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                        ParserUtils.recordParserFailure(this, e, metadata);
                        break block14;
                    }
                    throw e;
                }
                finally {
                    context.set(Parser.class, preContextParser);
                    secureContentHandler.updateContentHandler(preContextHandler);
                    long elapsedMillis = System.currentTimeMillis() - started;
                    metadata.set(TikaCoreProperties.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                    this.parserState.recursiveParserWrapperHandler.endEmbeddedDocument(localHandler, metadata);
                }
            }
        }
    }
}

