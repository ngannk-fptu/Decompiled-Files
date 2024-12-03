/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.ReusableStringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.util.CloseableThreadLocal;

public abstract class Analyzer
implements Closeable {
    private final ReuseStrategy reuseStrategy;

    public Analyzer() {
        this(new GlobalReuseStrategy());
    }

    public Analyzer(ReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
    }

    protected abstract TokenStreamComponents createComponents(String var1, Reader var2);

    public final TokenStream tokenStream(String fieldName, Reader reader) throws IOException {
        TokenStreamComponents components = this.reuseStrategy.getReusableComponents(fieldName);
        Reader r = this.initReader(fieldName, reader);
        if (components == null) {
            components = this.createComponents(fieldName, r);
            this.reuseStrategy.setReusableComponents(fieldName, components);
        } else {
            components.setReader(r);
        }
        return components.getTokenStream();
    }

    public final TokenStream tokenStream(String fieldName, String text) throws IOException {
        TokenStreamComponents components = this.reuseStrategy.getReusableComponents(fieldName);
        ReusableStringReader strReader = components == null || components.reusableStringReader == null ? new ReusableStringReader() : components.reusableStringReader;
        strReader.setValue(text);
        Reader r = this.initReader(fieldName, strReader);
        if (components == null) {
            components = this.createComponents(fieldName, r);
            this.reuseStrategy.setReusableComponents(fieldName, components);
        } else {
            components.setReader(r);
        }
        components.reusableStringReader = strReader;
        return components.getTokenStream();
    }

    protected Reader initReader(String fieldName, Reader reader) {
        return reader;
    }

    public int getPositionIncrementGap(String fieldName) {
        return 0;
    }

    public int getOffsetGap(String fieldName) {
        return 1;
    }

    @Override
    public void close() {
        this.reuseStrategy.close();
    }

    public static class PerFieldReuseStrategy
    extends ReuseStrategy {
        @Override
        public TokenStreamComponents getReusableComponents(String fieldName) {
            Map componentsPerField = (Map)this.getStoredValue();
            return componentsPerField != null ? (TokenStreamComponents)componentsPerField.get(fieldName) : null;
        }

        @Override
        public void setReusableComponents(String fieldName, TokenStreamComponents components) {
            HashMap<String, TokenStreamComponents> componentsPerField = (HashMap<String, TokenStreamComponents>)this.getStoredValue();
            if (componentsPerField == null) {
                componentsPerField = new HashMap<String, TokenStreamComponents>();
                this.setStoredValue(componentsPerField);
            }
            componentsPerField.put(fieldName, components);
        }
    }

    public static final class GlobalReuseStrategy
    extends ReuseStrategy {
        @Override
        public TokenStreamComponents getReusableComponents(String fieldName) {
            return (TokenStreamComponents)this.getStoredValue();
        }

        @Override
        public void setReusableComponents(String fieldName, TokenStreamComponents components) {
            this.setStoredValue(components);
        }
    }

    public static abstract class ReuseStrategy
    implements Closeable {
        private CloseableThreadLocal<Object> storedValue = new CloseableThreadLocal();

        public abstract TokenStreamComponents getReusableComponents(String var1);

        public abstract void setReusableComponents(String var1, TokenStreamComponents var2);

        protected final Object getStoredValue() {
            try {
                return this.storedValue.get();
            }
            catch (NullPointerException npe) {
                if (this.storedValue == null) {
                    throw new AlreadyClosedException("this Analyzer is closed");
                }
                throw npe;
            }
        }

        protected final void setStoredValue(Object storedValue) {
            try {
                this.storedValue.set(storedValue);
            }
            catch (NullPointerException npe) {
                if (storedValue == null) {
                    throw new AlreadyClosedException("this Analyzer is closed");
                }
                throw npe;
            }
        }

        @Override
        public void close() {
            if (this.storedValue != null) {
                this.storedValue.close();
                this.storedValue = null;
            }
        }
    }

    public static class TokenStreamComponents {
        protected final Tokenizer source;
        protected final TokenStream sink;
        transient ReusableStringReader reusableStringReader;

        public TokenStreamComponents(Tokenizer source, TokenStream result) {
            this.source = source;
            this.sink = result;
        }

        public TokenStreamComponents(Tokenizer source) {
            this.source = source;
            this.sink = source;
        }

        protected void setReader(Reader reader) throws IOException {
            this.source.setReader(reader);
        }

        public TokenStream getTokenStream() {
            return this.sink;
        }

        public Tokenizer getTokenizer() {
            return this.source;
        }
    }
}

