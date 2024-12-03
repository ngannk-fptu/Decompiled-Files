/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.EmptyParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParseRecord;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class CompositeParser
extends AbstractParser {
    private static final long serialVersionUID = 2192845797749627824L;
    private MediaTypeRegistry registry;
    private List<Parser> parsers;
    private Parser fallback = new EmptyParser();

    public CompositeParser(MediaTypeRegistry registry, List<Parser> parsers, Collection<Class<? extends Parser>> excludeParsers) {
        if (excludeParsers == null || excludeParsers.isEmpty()) {
            this.parsers = parsers;
        } else {
            this.parsers = new ArrayList<Parser>();
            for (Parser p : parsers) {
                if (this.isExcluded(excludeParsers, p.getClass())) continue;
                this.parsers.add(p);
            }
        }
        this.registry = registry;
    }

    public CompositeParser(MediaTypeRegistry registry, List<Parser> parsers) {
        this(registry, parsers, null);
    }

    public CompositeParser(MediaTypeRegistry registry, Parser ... parsers) {
        this(registry, Arrays.asList(parsers));
    }

    public CompositeParser() {
        this(new MediaTypeRegistry(), new Parser[0]);
    }

    public Map<MediaType, Parser> getParsers(ParseContext context) {
        HashMap<MediaType, Parser> map = new HashMap<MediaType, Parser>();
        for (Parser parser : this.parsers) {
            for (MediaType type : parser.getSupportedTypes(context)) {
                map.put(this.registry.normalize(type), parser);
            }
        }
        return map;
    }

    private boolean isExcluded(Collection<Class<? extends Parser>> excludeParsers, Class<? extends Parser> p) {
        return excludeParsers.contains(p) || this.assignableFrom(excludeParsers, p);
    }

    private boolean assignableFrom(Collection<Class<? extends Parser>> excludeParsers, Class<? extends Parser> p) {
        for (Class<? extends Parser> e : excludeParsers) {
            if (!e.isAssignableFrom(p)) continue;
            return true;
        }
        return false;
    }

    public Map<MediaType, List<Parser>> findDuplicateParsers(ParseContext context) {
        HashMap<MediaType, Parser> types = new HashMap<MediaType, Parser>();
        HashMap<MediaType, List<Parser>> duplicates = new HashMap<MediaType, List<Parser>>();
        for (Parser parser : this.parsers) {
            for (MediaType type : parser.getSupportedTypes(context)) {
                MediaType canonicalType = this.registry.normalize(type);
                if (types.containsKey(canonicalType)) {
                    ArrayList<Parser> list = (ArrayList<Parser>)duplicates.get(canonicalType);
                    if (list == null) {
                        list = new ArrayList<Parser>();
                        list.add((Parser)types.get(canonicalType));
                        duplicates.put(canonicalType, list);
                    }
                    list.add(parser);
                    continue;
                }
                types.put(canonicalType, parser);
            }
        }
        return duplicates;
    }

    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }

    public void setMediaTypeRegistry(MediaTypeRegistry registry) {
        this.registry = registry;
    }

    public List<Parser> getAllComponentParsers() {
        return Collections.unmodifiableList(this.parsers);
    }

    public Map<MediaType, Parser> getParsers() {
        return this.getParsers(new ParseContext());
    }

    public void setParsers(Map<MediaType, Parser> parsers) {
        this.parsers = new ArrayList<Parser>(parsers.size());
        for (Map.Entry<MediaType, Parser> entry : parsers.entrySet()) {
            this.parsers.add(ParserDecorator.withTypes(entry.getValue(), Collections.singleton(entry.getKey())));
        }
    }

    public Parser getFallback() {
        return this.fallback;
    }

    public void setFallback(Parser fallback) {
        this.fallback = fallback;
    }

    protected Parser getParser(Metadata metadata) {
        return this.getParser(metadata, new ParseContext());
    }

    protected Parser getParser(Metadata metadata, ParseContext context) {
        MediaType type;
        Map<MediaType, Parser> map = this.getParsers(context);
        String contentTypeString = metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE);
        if (contentTypeString == null) {
            contentTypeString = metadata.get("Content-Type");
        }
        if ((type = MediaType.parse(contentTypeString)) != null) {
            type = this.registry.normalize(type);
        }
        while (type != null) {
            Parser parser = map.get(type);
            if (parser != null) {
                return parser;
            }
            type = this.registry.getSupertype(type);
        }
        return this.fallback;
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.getParsers(context).keySet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        Parser parser = this.getParser(metadata, context);
        TemporaryResources tmp = new TemporaryResources();
        ParseRecord parserRecord = context.get(ParseRecord.class);
        if (parserRecord == null) {
            parserRecord = new ParseRecord();
            context.set(ParseRecord.class, parserRecord);
        }
        try {
            TikaInputStream taggedStream = TikaInputStream.get(stream, tmp, metadata);
            TaggedContentHandler taggedHandler = handler != null ? new TaggedContentHandler(handler) : null;
            String parserClassname = ParserUtils.getParserClassname(parser);
            parserRecord.addParserClass(parserClassname);
            ParserUtils.recordParserDetails(parserClassname, metadata);
            parserRecord.beforeParse();
            try {
                parser.parse((InputStream)((Object)taggedStream), taggedHandler, metadata, context);
            }
            catch (SecurityException e) {
                throw e;
            }
            catch (IOException e) {
                taggedStream.throwIfCauseOf(e);
                throw new TikaException("TIKA-198: Illegal IOException from " + parser, e);
            }
            catch (SAXException e) {
                WriteLimitReachedException.throwIfWriteLimitReached(e);
                if (taggedHandler != null) {
                    taggedHandler.throwIfCauseOf(e);
                }
                throw new TikaException("TIKA-237: Illegal SAXException from " + parser, e);
            }
            catch (RuntimeException e) {
                throw new TikaException("Unexpected RuntimeException from " + parser, e);
            }
        }
        finally {
            tmp.dispose();
            parserRecord.afterParse();
            if (parserRecord.getDepth() == 0) {
                metadata.set(TikaCoreProperties.TIKA_PARSED_BY_FULL_SET, parserRecord.getParsers());
                this.recordEmbeddedMetadata(metadata, context);
            }
        }
    }

    private void recordEmbeddedMetadata(Metadata metadata, ParseContext context) {
        ParseRecord record = context.get(ParseRecord.class);
        if (record == null) {
            return;
        }
        for (Exception e : record.getExceptions()) {
            metadata.add(TikaCoreProperties.EMBEDDED_EXCEPTION, ExceptionUtils.getStackTrace(e));
        }
        for (String msg : record.getWarnings()) {
            metadata.add(TikaCoreProperties.EMBEDDED_WARNING, msg);
        }
        if (record.isWriteLimitReached()) {
            metadata.set(TikaCoreProperties.WRITE_LIMIT_REACHED, true);
        }
        for (Metadata m : record.getMetadataList()) {
            for (String n : m.names()) {
                for (String v : m.getValues(n)) {
                    metadata.add(n, v);
                }
            }
        }
    }
}

