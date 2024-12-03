/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.IOException;
import java.io.Serializable;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedResourceHandler;
import org.apache.tika.io.TikaInputStream;

public interface ContainerExtractor
extends Serializable {
    public boolean isSupported(TikaInputStream var1) throws IOException;

    public void extract(TikaInputStream var1, ContainerExtractor var2, EmbeddedResourceHandler var3) throws IOException, TikaException;
}

