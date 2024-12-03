/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.util.CloseableThreadLocal;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Modifier;

public abstract class Analyzer
implements Closeable {
    private CloseableThreadLocal<Object> tokenStreams = new CloseableThreadLocal();

    protected Analyzer() {
        assert (this.assertFinal());
    }

    private boolean assertFinal() {
        try {
            Class<?> clazz = this.getClass();
            if (!clazz.desiredAssertionStatus()) {
                return true;
            }
            assert (clazz.isAnonymousClass() || (clazz.getModifiers() & 0x12) != 0 || Modifier.isFinal(clazz.getMethod("tokenStream", String.class, Reader.class).getModifiers()) && Modifier.isFinal(clazz.getMethod("reusableTokenStream", String.class, Reader.class).getModifiers())) : "Analyzer implementation classes or at least their tokenStream() and reusableTokenStream() implementations must be final";
            return true;
        }
        catch (NoSuchMethodException nsme) {
            return false;
        }
    }

    public abstract TokenStream tokenStream(String var1, Reader var2);

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        return this.tokenStream(fieldName, reader);
    }

    protected Object getPreviousTokenStream() {
        try {
            return this.tokenStreams.get();
        }
        catch (NullPointerException npe) {
            if (this.tokenStreams == null) {
                throw new AlreadyClosedException("this Analyzer is closed");
            }
            throw npe;
        }
    }

    protected void setPreviousTokenStream(Object obj) {
        try {
            this.tokenStreams.set(obj);
        }
        catch (NullPointerException npe) {
            if (this.tokenStreams == null) {
                throw new AlreadyClosedException("this Analyzer is closed");
            }
            throw npe;
        }
    }

    public int getPositionIncrementGap(String fieldName) {
        return 0;
    }

    public int getOffsetGap(Fieldable field) {
        if (field.isTokenized()) {
            return 1;
        }
        return 0;
    }

    public void close() {
        this.tokenStreams.close();
        this.tokenStreams = null;
    }
}

