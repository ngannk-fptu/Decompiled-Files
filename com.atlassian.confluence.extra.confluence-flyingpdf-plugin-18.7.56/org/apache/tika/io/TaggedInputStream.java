/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.UUID;
import org.apache.tika.io.ProxyInputStream;
import org.apache.tika.io.TaggedIOException;

public class TaggedInputStream
extends ProxyInputStream {
    private final Serializable tag = UUID.randomUUID();

    public TaggedInputStream(InputStream proxy) {
        super(proxy);
    }

    public static TaggedInputStream get(InputStream proxy) {
        if (proxy instanceof TaggedInputStream) {
            return (TaggedInputStream)proxy;
        }
        return new TaggedInputStream(proxy);
    }

    public boolean isCauseOf(IOException exception) {
        if (exception instanceof TaggedIOException) {
            TaggedIOException tagged = (TaggedIOException)exception;
            return this.tag.equals(tagged.getTag());
        }
        return false;
    }

    public void throwIfCauseOf(Exception exception) throws IOException {
        TaggedIOException tagged;
        if (exception instanceof TaggedIOException && this.tag.equals((tagged = (TaggedIOException)exception).getTag())) {
            throw tagged.getCause();
        }
    }

    @Override
    protected void handleIOException(IOException e) throws IOException {
        throw new TaggedIOException(e, this.tag);
    }

    public String toString() {
        return "Tika Tagged InputStream wrapping " + this.in;
    }
}

