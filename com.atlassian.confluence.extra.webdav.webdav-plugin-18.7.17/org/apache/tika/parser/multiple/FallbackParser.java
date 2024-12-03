/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.multiple;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.Param;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.xml.sax.ContentHandler;

public class FallbackParser
extends AbstractMultipleParser {
    public static final List<AbstractMultipleParser.MetadataPolicy> allowedPolicies = Arrays.asList(AbstractMultipleParser.MetadataPolicy.values());
    private static final long serialVersionUID = 5844409020977206167L;

    public FallbackParser(MediaTypeRegistry registry, Collection<? extends Parser> parsers, Map<String, Param> params) {
        super(registry, parsers, params);
    }

    public FallbackParser(MediaTypeRegistry registry, AbstractMultipleParser.MetadataPolicy policy, Collection<? extends Parser> parsers) {
        super(registry, policy, parsers);
    }

    public FallbackParser(MediaTypeRegistry registry, AbstractMultipleParser.MetadataPolicy policy, Parser ... parsers) {
        super(registry, policy, parsers);
    }

    @Override
    protected boolean parserCompleted(Parser parser, Metadata metadata, ContentHandler handler, ParseContext context, Exception exception) {
        return exception != null;
    }
}

