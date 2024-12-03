/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.util.AttributeSource;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Modifier;

public abstract class TokenStream
extends AttributeSource
implements Closeable {
    protected TokenStream() {
        assert (this.assertFinal());
    }

    protected TokenStream(AttributeSource input) {
        super(input);
        assert (this.assertFinal());
    }

    protected TokenStream(AttributeSource.AttributeFactory factory) {
        super(factory);
        assert (this.assertFinal());
    }

    private boolean assertFinal() {
        try {
            Class<?> clazz = this.getClass();
            if (!clazz.desiredAssertionStatus()) {
                return true;
            }
            assert (clazz.isAnonymousClass() || (clazz.getModifiers() & 0x12) != 0 || Modifier.isFinal(clazz.getMethod("incrementToken", new Class[0]).getModifiers())) : "TokenStream implementation classes or at least their incrementToken() implementation must be final";
            return true;
        }
        catch (NoSuchMethodException nsme) {
            return false;
        }
    }

    public abstract boolean incrementToken() throws IOException;

    public void end() throws IOException {
    }

    public void reset() throws IOException {
    }

    public void close() throws IOException {
    }
}

