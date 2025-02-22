/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.parser.ParserImpl;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Objects;

public class Parse {
    private LoadSettings settings;

    public Parse(LoadSettings settings) {
        Objects.requireNonNull(settings, "LoadSettings cannot be null");
        this.settings = settings;
    }

    public Iterable<Event> parseInputStream(InputStream yaml) {
        Objects.requireNonNull(yaml, "InputStream cannot be null");
        return () -> new ParserImpl(new StreamReader(new YamlUnicodeReader(yaml), this.settings), this.settings);
    }

    public Iterable<Event> parseReader(Reader yaml) {
        Objects.requireNonNull(yaml, "Reader cannot be null");
        return () -> new ParserImpl(new StreamReader(yaml, this.settings), this.settings);
    }

    public Iterable<Event> parseString(final String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return new Iterable(){

            public Iterator<Event> iterator() {
                return new ParserImpl(new StreamReader(new StringReader(yaml), Parse.this.settings), Parse.this.settings);
            }
        };
    }
}

