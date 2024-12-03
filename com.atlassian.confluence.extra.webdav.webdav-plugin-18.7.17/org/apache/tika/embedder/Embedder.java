/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.embedder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;

public interface Embedder
extends Serializable {
    public Set<MediaType> getSupportedEmbedTypes(ParseContext var1);

    public void embed(Metadata var1, InputStream var2, OutputStream var3, ParseContext var4) throws IOException, TikaException;
}

