/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.RuntimeJsonMappingException;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MappingIterator<T>
implements Iterator<T> {
    protected static final MappingIterator<?> EMPTY_ITERATOR = new MappingIterator(null, null, null, null, false, null);
    protected final JavaType _type;
    protected final DeserializationContext _context;
    protected final JsonDeserializer<T> _deserializer;
    protected JsonParser _parser;
    protected final boolean _closeParser;
    protected boolean _hasNextChecked;
    protected final T _updatedValue;

    protected MappingIterator(JavaType type, JsonParser jp, DeserializationContext ctxt, JsonDeserializer<?> deser) {
        this(type, jp, ctxt, deser, true, null);
    }

    protected MappingIterator(JavaType type, JsonParser jp, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean closeParser, Object valueToUpdate) {
        JsonStreamContext sc;
        this._type = type;
        this._parser = jp;
        this._context = ctxt;
        this._deserializer = deser;
        if (jp != null && jp.getCurrentToken() == JsonToken.START_ARRAY && !(sc = jp.getParsingContext()).inRoot()) {
            jp.clearCurrentToken();
        }
        this._closeParser = closeParser;
        this._updatedValue = valueToUpdate == null ? null : valueToUpdate;
    }

    protected static <T> MappingIterator<T> emptyIterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public boolean hasNext() {
        try {
            return this.hasNextValue();
        }
        catch (JsonMappingException e) {
            throw new RuntimeJsonMappingException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public T next() {
        try {
            return this.nextValue();
        }
        catch (JsonMappingException e) {
            throw new RuntimeJsonMappingException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNextValue() throws IOException {
        if (this._parser == null) {
            return false;
        }
        if (!this._hasNextChecked) {
            JsonToken t = this._parser.getCurrentToken();
            this._hasNextChecked = true;
            if (t == null) {
                t = this._parser.nextToken();
                if (t == null) {
                    JsonParser jp = this._parser;
                    this._parser = null;
                    if (this._closeParser) {
                        jp.close();
                    }
                    return false;
                }
                if (t == JsonToken.END_ARRAY) {
                    return false;
                }
            }
        }
        return true;
    }

    public T nextValue() throws IOException {
        T result;
        if (!this._hasNextChecked && !this.hasNextValue()) {
            throw new NoSuchElementException();
        }
        if (this._parser == null) {
            throw new NoSuchElementException();
        }
        this._hasNextChecked = false;
        if (this._updatedValue == null) {
            result = this._deserializer.deserialize(this._parser, this._context);
        } else {
            this._deserializer.deserialize(this._parser, this._context, this._updatedValue);
            result = this._updatedValue;
        }
        this._parser.clearCurrentToken();
        return result;
    }
}

