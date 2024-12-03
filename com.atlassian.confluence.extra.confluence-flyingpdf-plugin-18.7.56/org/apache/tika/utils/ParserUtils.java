/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.utils.ExceptionUtils;

public class ParserUtils {
    public static final String X_PARSED_BY = "X-Parsed-By";
    public static final Property EMBEDDED_PARSER = Property.internalText("X-TIKA:EXCEPTION:embedded_parser");
    public static final Property EMBEDDED_EXCEPTION = Property.internalText("X-TIKA:EXCEPTION:embedded_exception");

    public static Metadata cloneMetadata(Metadata m) {
        Metadata clone = new Metadata();
        for (String n : m.names()) {
            if (!m.isMultiValued(n)) {
                clone.set(n, m.get(n));
                continue;
            }
            String[] vals = m.getValues(n);
            for (int i = 0; i < vals.length; ++i) {
                clone.add(n, vals[i]);
            }
        }
        return clone;
    }

    public static String getParserClassname(Parser parser) {
        if (parser instanceof ParserDecorator) {
            return ((ParserDecorator)parser).getWrappedParser().getClass().getName();
        }
        return parser.getClass().getName();
    }

    public static void recordParserDetails(Parser parser, Metadata metadata) {
        metadata.add(X_PARSED_BY, ParserUtils.getParserClassname(parser));
    }

    public static void recordParserFailure(Parser parser, Throwable failure, Metadata metadata) {
        String trace = ExceptionUtils.getStackTrace(failure);
        metadata.add(EMBEDDED_EXCEPTION, trace);
        metadata.add(EMBEDDED_PARSER, ParserUtils.getParserClassname(parser));
    }
}

