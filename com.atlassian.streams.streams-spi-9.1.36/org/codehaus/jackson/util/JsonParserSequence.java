/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.util.JsonParserDelegate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JsonParserSequence
extends JsonParserDelegate {
    protected final JsonParser[] _parsers;
    protected int _nextParser;

    protected JsonParserSequence(JsonParser[] parsers) {
        super(parsers[0]);
        this._parsers = parsers;
        this._nextParser = 1;
    }

    public static JsonParserSequence createFlattened(JsonParser first, JsonParser second) {
        if (!(first instanceof JsonParserSequence) && !(second instanceof JsonParserSequence)) {
            return new JsonParserSequence(new JsonParser[]{first, second});
        }
        ArrayList<JsonParser> p = new ArrayList<JsonParser>();
        if (first instanceof JsonParserSequence) {
            ((JsonParserSequence)first).addFlattenedActiveParsers(p);
        } else {
            p.add(first);
        }
        if (second instanceof JsonParserSequence) {
            ((JsonParserSequence)second).addFlattenedActiveParsers(p);
        } else {
            p.add(second);
        }
        return new JsonParserSequence(p.toArray(new JsonParser[p.size()]));
    }

    protected void addFlattenedActiveParsers(List<JsonParser> result) {
        int len = this._parsers.length;
        for (int i = this._nextParser - 1; i < len; ++i) {
            JsonParser p = this._parsers[i];
            if (p instanceof JsonParserSequence) {
                ((JsonParserSequence)p).addFlattenedActiveParsers(result);
                continue;
            }
            result.add(p);
        }
    }

    @Override
    public void close() throws IOException {
        do {
            this.delegate.close();
        } while (this.switchToNext());
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        JsonToken t = this.delegate.nextToken();
        if (t != null) {
            return t;
        }
        while (this.switchToNext()) {
            t = this.delegate.nextToken();
            if (t == null) continue;
            return t;
        }
        return null;
    }

    public int containedParsersCount() {
        return this._parsers.length;
    }

    protected boolean switchToNext() {
        if (this._nextParser >= this._parsers.length) {
            return false;
        }
        this.delegate = this._parsers[this._nextParser++];
        return true;
    }
}

