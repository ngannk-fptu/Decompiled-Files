/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import com.hazelcast.org.snakeyaml.engine.v2.composer.Composer;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.parser.ParserImpl;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class Compose {
    private LoadSettings settings;

    public Compose(LoadSettings settings) {
        Objects.requireNonNull(settings, "LoadSettings cannot be null");
        this.settings = settings;
    }

    public Optional<Node> composeReader(Reader yaml) {
        Objects.requireNonNull(yaml, "Reader cannot be null");
        return new Composer(new ParserImpl(new StreamReader(yaml, this.settings), this.settings), this.settings).getSingleNode();
    }

    public Optional<Node> composeInputStream(InputStream yaml) {
        Objects.requireNonNull(yaml, "InputStream cannot be null");
        return new Composer(new ParserImpl(new StreamReader(new YamlUnicodeReader(yaml), this.settings), this.settings), this.settings).getSingleNode();
    }

    public Optional<Node> composeString(String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return new Composer(new ParserImpl(new StreamReader(new StringReader(yaml), this.settings), this.settings), this.settings).getSingleNode();
    }

    public Iterable<Node> composeAllFromReader(Reader yaml) {
        Objects.requireNonNull(yaml, "Reader cannot be null");
        return () -> new Composer(new ParserImpl(new StreamReader(yaml, this.settings), this.settings), this.settings);
    }

    public Iterable<Node> composeAllFromInputStream(InputStream yaml) {
        Objects.requireNonNull(yaml, "InputStream cannot be null");
        return () -> new Composer(new ParserImpl(new StreamReader(new YamlUnicodeReader(yaml), this.settings), this.settings), this.settings);
    }

    public Iterable<Node> composeAllFromString(final String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return new Iterable(){

            public Iterator<Node> iterator() {
                return new Composer(new ParserImpl(new StreamReader(new StringReader(yaml), Compose.this.settings), Compose.this.settings), Compose.this.settings);
            }
        };
    }
}

