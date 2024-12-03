/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.renderer.RenderRequest;
import org.apache.tika.renderer.RenderResults;

public interface Renderer
extends Serializable {
    public Set<MediaType> getSupportedTypes(ParseContext var1);

    public RenderResults render(InputStream var1, Metadata var2, ParseContext var3, RenderRequest ... var4) throws IOException, TikaException;
}

