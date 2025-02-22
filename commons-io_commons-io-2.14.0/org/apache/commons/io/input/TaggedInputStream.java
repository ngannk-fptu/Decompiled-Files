/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.UUID;
import org.apache.commons.io.TaggedIOException;
import org.apache.commons.io.input.ProxyInputStream;

public class TaggedInputStream
extends ProxyInputStream {
    private final Serializable tag = UUID.randomUUID();

    public TaggedInputStream(InputStream proxy) {
        super(proxy);
    }

    @Override
    protected void handleIOException(IOException e) throws IOException {
        throw new TaggedIOException(e, this.tag);
    }

    public boolean isCauseOf(Throwable exception) {
        return TaggedIOException.isTaggedWith(exception, this.tag);
    }

    public void throwIfCauseOf(Throwable throwable) throws IOException {
        TaggedIOException.throwCauseIfTaggedWith(throwable, this.tag);
    }
}

