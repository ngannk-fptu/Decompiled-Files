/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.google.common.io.CharStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.fugue.Either;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerChain
implements Transformer {
    private static final Logger log = LoggerFactory.getLogger(TransformerChain.class);
    private final Iterable<Transformer> transformers;

    public TransformerChain(Iterable<Transformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        Objects.requireNonNull(input, "input cannot be null");
        Either data = Either.left((Object)input);
        for (Transformer transformer : this.transformers) {
            log.debug("Performing transform on: {}", (Object)transformer);
            data = Either.right((Object)transformer.transform(TransformerChain.asReader((Either<Reader, String>)data), conversionContext));
        }
        return TransformerChain.asString((Either<Reader, String>)data);
    }

    private static Reader asReader(Either<Reader, String> either) {
        return (Reader)either.fold(reader -> reader, StringReader::new);
    }

    private static String asString(Either<Reader, String> data) throws XhtmlException {
        if (data.isLeft()) {
            try {
                return CharStreams.toString((Readable)((Readable)data.left().get()));
            }
            catch (IOException e) {
                throw new XhtmlException(e);
            }
        }
        return (String)data.right().get();
    }
}

