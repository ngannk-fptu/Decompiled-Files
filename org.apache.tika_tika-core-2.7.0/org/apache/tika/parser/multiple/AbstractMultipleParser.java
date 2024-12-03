/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.multiple;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AbstractMultipleParser
extends AbstractParser {
    protected static final String METADATA_POLICY_CONFIG_KEY = "metadataPolicy";
    private static final long serialVersionUID = 5383668090329836559L;
    private final MetadataPolicy policy;
    private final Collection<? extends Parser> parsers;
    private final Set<MediaType> offeredTypes;
    private MediaTypeRegistry registry;

    public AbstractMultipleParser(MediaTypeRegistry registry, Collection<? extends Parser> parsers, Map<String, Param> params) {
        this(registry, AbstractMultipleParser.getMetadataPolicy(params), parsers);
    }

    public AbstractMultipleParser(MediaTypeRegistry registry, MetadataPolicy policy, Parser ... parsers) {
        this(registry, policy, Arrays.asList(parsers));
    }

    public AbstractMultipleParser(MediaTypeRegistry registry, MetadataPolicy policy, Collection<? extends Parser> parsers) {
        this.policy = policy;
        this.parsers = parsers;
        this.registry = registry;
        this.offeredTypes = new HashSet<MediaType>();
        for (Parser parser : parsers) {
            this.offeredTypes.addAll(parser.getSupportedTypes(new ParseContext()));
        }
    }

    protected static MetadataPolicy getMetadataPolicy(Map<String, Param> params) {
        if (params.containsKey(METADATA_POLICY_CONFIG_KEY)) {
            return (MetadataPolicy)((Object)params.get(METADATA_POLICY_CONFIG_KEY).getValue());
        }
        throw new IllegalArgumentException("Required parameter 'metadataPolicy' not supplied");
    }

    protected static Metadata mergeMetadata(Metadata newMetadata, Metadata lastMetadata, MetadataPolicy policy) {
        if (policy == MetadataPolicy.DISCARD_ALL) {
            return newMetadata;
        }
        block5: for (String n : lastMetadata.names()) {
            if (n.equals(TikaCoreProperties.TIKA_PARSED_BY.getName()) || n.equals(ParserUtils.EMBEDDED_PARSER.getName()) || n.equals(TikaCoreProperties.EMBEDDED_EXCEPTION.getName())) continue;
            Object[] newVals = newMetadata.getValues(n);
            Object[] oldVals = lastMetadata.getValues(n);
            if (newVals == null || newVals.length == 0) {
                for (Object val : oldVals) {
                    newMetadata.add(n, (String)val);
                }
                continue;
            }
            if (Arrays.deepEquals(oldVals, newVals)) continue;
            switch (policy) {
                case FIRST_WINS: {
                    newMetadata.remove(n);
                    for (Object val : oldVals) {
                        newMetadata.add(n, (String)val);
                    }
                    continue block5;
                }
                case LAST_WINS: {
                    continue block5;
                }
                case KEEP_ALL: {
                    ArrayList<Object> vals = new ArrayList<Object>(Arrays.asList(oldVals));
                    newMetadata.remove(n);
                    for (Object oldVal : oldVals) {
                        newMetadata.add(n, (String)oldVal);
                    }
                    for (Object newVal : newVals) {
                        if (vals.contains(newVal)) continue;
                        newMetadata.add(n, (String)newVal);
                        vals.add(newVal);
                    }
                    continue block5;
                }
            }
        }
        return newMetadata;
    }

    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }

    public void setMediaTypeRegistry(MediaTypeRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.offeredTypes;
    }

    public MetadataPolicy getMetadataPolicy() {
        return this.policy;
    }

    public List<Parser> getAllParsers() {
        return Collections.unmodifiableList(new ArrayList<Parser>(this.parsers));
    }

    protected void parserPrepare(Parser parser, Metadata metadata, ParseContext context) {
    }

    protected abstract boolean parserCompleted(Parser var1, Metadata var2, ContentHandler var3, ParseContext var4, Exception var5);

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        this.parse(stream, handler, null, metadata, context);
    }

    public void parse(InputStream stream, ContentHandlerFactory handlers, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        this.parse(stream, null, handlers, metadata, context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private void parse(InputStream stream, ContentHandler handler, ContentHandlerFactory handlerFactory, Metadata originalMetadata, ParseContext context) throws IOException, SAXException, TikaException {
        void var11_14;
        Metadata lastMetadata;
        Metadata metadata = lastMetadata = ParserUtils.cloneMetadata(originalMetadata);
        TemporaryResources tmp = new TemporaryResources();
        try {
            InputStream taggedStream = ParserUtils.ensureStreamReReadable(stream, tmp, originalMetadata);
            for (Parser parser : this.parsers) {
                if (handlerFactory != null) {
                    handler = handlerFactory.getNewContentHandler();
                }
                ParserUtils.recordParserDetails(parser, originalMetadata);
                metadata = ParserUtils.cloneMetadata(originalMetadata);
                this.parserPrepare(parser, metadata, context);
                Exception failure = null;
                try {
                    parser.parse(taggedStream, handler, metadata, context);
                }
                catch (Exception e) {
                    ParserUtils.recordParserFailure(parser, e, originalMetadata);
                    ParserUtils.recordParserFailure(parser, e, metadata);
                    failure = e;
                }
                boolean tryNext = this.parserCompleted(parser, metadata, handler, context, failure);
                metadata = AbstractMultipleParser.mergeMetadata(metadata, lastMetadata, this.policy);
                if (!tryNext) {
                    if (failure != null) {
                        if (failure instanceof IOException) {
                            throw (IOException)failure;
                        }
                        if (failure instanceof SAXException) {
                            throw (SAXException)failure;
                        }
                        if (failure instanceof TikaException) {
                            throw (TikaException)failure;
                        }
                        throw new TikaException("Unexpected RuntimeException from " + parser, failure);
                    }
                    break;
                }
                lastMetadata = ParserUtils.cloneMetadata(metadata);
                taggedStream = ParserUtils.streamResetForReRead(taggedStream, tmp);
            }
        }
        finally {
            tmp.dispose();
        }
        String[] stringArray = metadata.names();
        int n = stringArray.length;
        boolean bl = false;
        while (var11_14 < n) {
            String n2 = stringArray[var11_14];
            originalMetadata.remove(n2);
            for (String val : metadata.getValues(n2)) {
                originalMetadata.add(n2, val);
            }
            ++var11_14;
        }
    }

    public static enum MetadataPolicy {
        DISCARD_ALL,
        FIRST_WINS,
        LAST_WINS,
        KEEP_ALL;

    }
}

