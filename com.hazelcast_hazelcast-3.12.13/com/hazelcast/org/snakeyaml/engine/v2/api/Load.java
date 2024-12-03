/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import com.hazelcast.org.snakeyaml.engine.v2.composer.Composer;
import com.hazelcast.org.snakeyaml.engine.v2.constructor.BaseConstructor;
import com.hazelcast.org.snakeyaml.engine.v2.constructor.StandardConstructor;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.parser.ParserImpl;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class Load {
    private LoadSettings settings;
    private BaseConstructor constructor;

    public Load(LoadSettings settings) {
        this(settings, new StandardConstructor(settings));
    }

    public Load(LoadSettings settings, BaseConstructor constructor) {
        Objects.requireNonNull(settings, "LoadSettings cannot be null");
        Objects.requireNonNull(constructor, "BaseConstructor cannot be null");
        this.settings = settings;
        this.constructor = constructor;
    }

    private Composer createComposer(StreamReader streamReader) {
        return new Composer(new ParserImpl(streamReader, this.settings), this.settings);
    }

    protected Composer createComposer(InputStream yamlStream) {
        return this.createComposer(new StreamReader(new YamlUnicodeReader(yamlStream), this.settings));
    }

    protected Composer createComposer(String yaml) {
        return this.createComposer(new StreamReader(yaml, this.settings));
    }

    protected Composer createComposer(Reader yamlReader) {
        return this.createComposer(new StreamReader(yamlReader, this.settings));
    }

    protected Object loadOne(Composer composer) {
        Optional<Node> nodeOptional = composer.getSingleNode();
        return this.constructor.constructSingleDocument(nodeOptional);
    }

    public Object loadFromInputStream(InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        return this.loadOne(this.createComposer(yamlStream));
    }

    public Object loadFromReader(Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        return this.loadOne(this.createComposer(yamlReader));
    }

    public Object loadFromString(String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return this.loadOne(this.createComposer(yaml));
    }

    private Iterable<Object> loadAll(Composer composer) {
        YamlIterator result = new YamlIterator(composer, this.constructor);
        return new YamlIterable(result);
    }

    public Iterable<Object> loadAllFromInputStream(InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        Composer composer = this.createComposer(new StreamReader(new YamlUnicodeReader(yamlStream), this.settings));
        return this.loadAll(composer);
    }

    public Iterable<Object> loadAllFromReader(Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        Composer composer = this.createComposer(new StreamReader(yamlReader, this.settings));
        return this.loadAll(composer);
    }

    public Iterable<Object> loadAllFromString(String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        Composer composer = this.createComposer(new StreamReader(yaml, this.settings));
        return this.loadAll(composer);
    }

    private static class YamlIterator
    implements Iterator<Object> {
        private Composer composer;
        private BaseConstructor constructor;

        public YamlIterator(Composer composer, BaseConstructor constructor) {
            this.composer = composer;
            this.constructor = constructor;
        }

        @Override
        public boolean hasNext() {
            return this.composer.hasNext();
        }

        @Override
        public Object next() {
            Node node = this.composer.next();
            return this.constructor.constructSingleDocument(Optional.of(node));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing is not supported.");
        }
    }

    private static class YamlIterable
    implements Iterable<Object> {
        private Iterator<Object> iterator;

        public YamlIterable(Iterator<Object> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<Object> iterator() {
            return this.iterator;
        }
    }
}

