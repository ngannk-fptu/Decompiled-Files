/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.Serializable;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;

public interface ContentHandlerDecoratorFactory
extends Serializable {
    @Deprecated
    public ContentHandler decorate(ContentHandler var1, Metadata var2);

    public ContentHandler decorate(ContentHandler var1, Metadata var2, ParseContext var3);
}

