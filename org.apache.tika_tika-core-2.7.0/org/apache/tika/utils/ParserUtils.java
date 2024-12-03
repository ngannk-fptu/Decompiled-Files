/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.utils.RereadableInputStream;

public class ParserUtils {
    public static final Property EMBEDDED_PARSER = Property.internalText("X-TIKA:EXCEPTION:embedded_parser");

    public static Metadata cloneMetadata(Metadata m) {
        Metadata clone = new Metadata();
        for (String n : m.names()) {
            String[] vals;
            if (!m.isMultiValued(n)) {
                clone.set(n, m.get(n));
                continue;
            }
            for (String val : vals = m.getValues(n)) {
                clone.add(n, val);
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
        String className = ParserUtils.getParserClassname(parser);
        ParserUtils.recordParserDetails(className, metadata);
    }

    public static void recordParserDetails(String parserClassName, Metadata metadata) {
        String[] parsedBys = metadata.getValues(TikaCoreProperties.TIKA_PARSED_BY);
        if (parsedBys == null || parsedBys.length == 0) {
            metadata.add(TikaCoreProperties.TIKA_PARSED_BY, parserClassName);
        } else if (Arrays.stream(parsedBys).noneMatch(parserClassName::equals)) {
            metadata.add(TikaCoreProperties.TIKA_PARSED_BY, parserClassName);
        }
    }

    public static void recordParserFailure(Parser parser, Throwable failure, Metadata metadata) {
        String trace = ExceptionUtils.getStackTrace(failure);
        metadata.add(TikaCoreProperties.EMBEDDED_EXCEPTION, trace);
        metadata.add(EMBEDDED_PARSER, ParserUtils.getParserClassname(parser));
    }

    public static InputStream ensureStreamReReadable(InputStream stream, TemporaryResources tmp, Metadata metadata) throws IOException {
        if (stream instanceof RereadableInputStream) {
            return stream;
        }
        TikaInputStream tstream = TikaInputStream.cast(stream);
        if (tstream == null) {
            tstream = TikaInputStream.get(stream, tmp, metadata);
        }
        if (tstream.getInputStreamFactory() != null) {
            return tstream;
        }
        tstream.getFile();
        tstream.mark(-1);
        return tstream;
    }

    public static InputStream streamResetForReRead(InputStream stream, TemporaryResources tmp) throws IOException {
        if (stream instanceof RereadableInputStream) {
            ((RereadableInputStream)stream).rewind();
            return stream;
        }
        TikaInputStream tstream = (TikaInputStream)((Object)stream);
        if (tstream.getInputStreamFactory() != null) {
            return TikaInputStream.get(tstream.getInputStreamFactory(), tmp);
        }
        tstream.reset();
        tstream.mark(-1);
        return tstream;
    }
}

