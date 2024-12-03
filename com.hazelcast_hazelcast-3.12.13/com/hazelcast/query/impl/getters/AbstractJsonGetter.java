/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.com.fasterxml.jackson.core.JsonToken;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.json.internal.JsonPattern;
import com.hazelcast.json.internal.JsonSchemaHelper;
import com.hazelcast.json.internal.JsonSchemaNode;
import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.JsonGetterContext;
import com.hazelcast.query.impl.getters.JsonGetterContextCache;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.util.collection.WeightedEvictableList;
import java.io.IOException;
import java.util.List;

public abstract class AbstractJsonGetter
extends Getter {
    private static final int QUERY_CONTEXT_CACHE_MAX_SIZE = 40;
    private static final int QUERY_CONTEXT_CACHE_CLEANUP_SIZE = 3;
    private static final int PATTERN_TRY_COUNT = 2;
    private final JsonGetterContextCache contextCache = new JsonGetterContextCache(40, 3);

    AbstractJsonGetter(Getter parent) {
        super(parent);
    }

    public static JsonPathCursor getPath(String attributePath) {
        return JsonPathCursor.createCursor(attributePath);
    }

    public static Object convertFromJsonValue(JsonValue value) {
        if (value == null) {
            return null;
        }
        if (value.isNumber()) {
            if (value.toString().contains(".")) {
                return value.asDouble();
            }
            return value.asLong();
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isNull()) {
            return null;
        }
        if (value.isString()) {
            return value.asString();
        }
        if (value == NonTerminalJsonValue.INSTANCE) {
            return value;
        }
        throw new IllegalArgumentException("Unknown Json type: " + value);
    }

    abstract JsonParser createParser(Object var1) throws IOException;

    @Override
    Object getValue(Object obj) {
        throw new HazelcastException("Path agnostic value extraction is not supported");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    Object getValue(Object obj, String attributePath) throws Exception {
        JsonPathCursor pathCursor = AbstractJsonGetter.getPath(attributePath);
        try (JsonParser parser = this.createParser(obj);){
            Object token;
            parser.nextToken();
            while (pathCursor.getNext() != null) {
                if (pathCursor.isArray()) {
                    if (pathCursor.isAny()) {
                        MultiResult multiResult = this.getMultiValue(parser, pathCursor);
                        return multiResult;
                    }
                    token = parser.currentToken();
                    if (token != JsonToken.START_ARRAY) {
                        Object var6_8 = null;
                        return var6_8;
                    }
                    token = parser.nextToken();
                    int arrayIndex = pathCursor.getArrayIndex();
                    for (int j = 0; j < arrayIndex; ++j) {
                        if (token == JsonToken.END_ARRAY) {
                            Object var8_11 = null;
                            return var8_11;
                        }
                        parser.skipChildren();
                        token = parser.nextToken();
                    }
                    continue;
                }
                if (this.findAttribute(parser, pathCursor, false)) continue;
                token = null;
                return token;
            }
            token = AbstractJsonGetter.convertJsonTokenToValue(parser);
            return token;
        }
    }

    @Override
    Object getValue(Object obj, String attributePath, Object metadata) throws Exception {
        JsonPattern knownPattern;
        if (metadata == null) {
            return this.getValue(obj, attributePath);
        }
        JsonSchemaNode schemaNode = (JsonSchemaNode)metadata;
        NavigableJsonInputAdapter adapter = this.annotate(obj);
        JsonGetterContext queryContext = this.contextCache.getContext(attributePath);
        List<WeightedEvictableList.WeightedItem<JsonPattern>> patternsSnapshot = queryContext.getPatternListSnapshot();
        JsonPathCursor pathCursor = queryContext.newJsonPathCursor();
        for (int i = 0; i < 2 && i < patternsSnapshot.size(); ++i) {
            WeightedEvictableList.WeightedItem<JsonPattern> patternWeightedItem = patternsSnapshot.get(i);
            knownPattern = patternWeightedItem.getItem();
            JsonValue value = JsonSchemaHelper.findValueWithPattern(adapter, schemaNode, knownPattern, pathCursor);
            pathCursor.reset();
            if (value == null) continue;
            queryContext.voteFor(patternWeightedItem);
            return AbstractJsonGetter.convertFromJsonValue(value);
        }
        knownPattern = JsonSchemaHelper.createPattern(adapter, schemaNode, pathCursor);
        pathCursor.reset();
        if (knownPattern != null) {
            if (knownPattern.hasAny()) {
                return this.getValue(obj, attributePath);
            }
            queryContext.addOrVoteForPattern(knownPattern);
            return AbstractJsonGetter.convertFromJsonValue(JsonSchemaHelper.findValueWithPattern(adapter, schemaNode, knownPattern, pathCursor));
        }
        return null;
    }

    @Override
    Class getReturnType() {
        throw new IllegalArgumentException("Non applicable for Json getters");
    }

    @Override
    boolean isCacheable() {
        return false;
    }

    int getContextCacheSize() {
        return this.contextCache.getCacheSize();
    }

    protected abstract NavigableJsonInputAdapter annotate(Object var1);

    private boolean findAttribute(JsonParser parser, JsonPathCursor pathCursor, boolean multiValue) throws IOException {
        JsonToken token = parser.getCurrentToken();
        if (token != JsonToken.START_OBJECT) {
            return false;
        }
        while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {
            if (pathCursor.getCurrent().equals(parser.getCurrentName())) {
                parser.nextToken();
                return true;
            }
            if (multiValue) {
                parser.nextToken();
                continue;
            }
            parser.nextToken();
            parser.skipChildren();
        }
        return false;
    }

    private MultiResult getMultiValue(JsonParser parser, JsonPathCursor pathCursor) throws IOException {
        pathCursor.getNext();
        MultiResult<Object> multiResult = new MultiResult<Object>();
        JsonToken currentToken = parser.currentToken();
        if (currentToken != JsonToken.START_ARRAY) {
            return null;
        }
        block0: while ((currentToken = parser.nextToken()) != JsonToken.END_ARRAY) {
            if (pathCursor.getCurrent() == null) {
                if (currentToken.isScalarValue()) {
                    multiResult.add(AbstractJsonGetter.convertJsonTokenToValue(parser));
                    continue;
                }
                parser.skipChildren();
                continue;
            }
            if (currentToken == JsonToken.START_OBJECT) {
                while (this.findAttribute(parser, pathCursor, true)) {
                    if ((parser.currentToken() == JsonToken.START_OBJECT || pathCursor.hasNext()) && pathCursor.getNext() != null) continue;
                    AbstractJsonGetter.addToMultiResult(multiResult, parser);
                    continue block0;
                }
                continue;
            }
            if (currentToken != JsonToken.START_ARRAY) continue;
            parser.skipChildren();
        }
        return multiResult;
    }

    private static void addToMultiResult(MultiResult<Object> multiResult, JsonParser parser) throws IOException {
        if (parser.currentToken().isScalarValue()) {
            multiResult.add(AbstractJsonGetter.convertJsonTokenToValue(parser));
        }
        while (parser.getCurrentToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken().isStructStart()) {
                parser.skipChildren();
            }
            parser.nextToken();
        }
    }

    private static Object convertJsonTokenToValue(JsonParser parser) throws IOException {
        int token = parser.currentTokenId();
        switch (token) {
            case 6: {
                return parser.getValueAsString();
            }
            case 7: {
                return parser.getLongValue();
            }
            case 8: {
                return parser.getValueAsDouble();
            }
            case 9: {
                return true;
            }
            case 10: {
                return false;
            }
            case 11: {
                return null;
            }
        }
        return NonTerminalJsonValue.INSTANCE;
    }
}

