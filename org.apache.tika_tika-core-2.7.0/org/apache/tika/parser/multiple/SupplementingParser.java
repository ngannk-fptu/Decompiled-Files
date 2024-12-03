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

public class SupplementingParser
extends AbstractMultipleParser {
    public static final List<AbstractMultipleParser.MetadataPolicy> allowedPolicies = Arrays.asList(AbstractMultipleParser.MetadataPolicy.FIRST_WINS, AbstractMultipleParser.MetadataPolicy.LAST_WINS, AbstractMultipleParser.MetadataPolicy.KEEP_ALL);
    private static final long serialVersionUID = 313179254565350994L;

    public SupplementingParser(MediaTypeRegistry registry, Collection<? extends Parser> parsers, Map<String, Param> params) {
        super(registry, parsers, params);
    }

    public SupplementingParser(MediaTypeRegistry registry, AbstractMultipleParser.MetadataPolicy policy, Parser ... parsers) {
        this(registry, policy, Arrays.asList(parsers));
    }

    public SupplementingParser(MediaTypeRegistry registry, AbstractMultipleParser.MetadataPolicy policy, Collection<? extends Parser> parsers) {
        super(registry, policy, parsers);
        if (!allowedPolicies.contains((Object)policy)) {
            throw new IllegalArgumentException("Unsupported policy for SupplementingParser: " + (Object)((Object)policy));
        }
    }

    @Override
    protected boolean parserCompleted(Parser parser, Metadata metadata, ContentHandler handler, ParseContext context, Exception exception) {
        if (exception == null) {
            return true;
        }
        return true;
    }
}

