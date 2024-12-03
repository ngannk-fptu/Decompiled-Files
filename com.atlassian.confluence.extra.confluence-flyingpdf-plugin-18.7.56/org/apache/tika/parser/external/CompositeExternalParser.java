/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.external;

import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.external.ExternalParsersFactory;

public class CompositeExternalParser
extends CompositeParser {
    private static final long serialVersionUID = 6962436916649024024L;

    public CompositeExternalParser() throws IOException, TikaException {
        this(new MediaTypeRegistry());
    }

    public CompositeExternalParser(MediaTypeRegistry registry) throws IOException, TikaException {
        super(registry, ExternalParsersFactory.create());
    }
}

