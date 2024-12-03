/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class JsonUnmarshallerContextImpl
extends JsonUnmarshallerContext {
    private JsonToken currentToken;
    private JsonToken nextToken;
    private final JsonParser jsonParser;
    private String currentHeader;
    private final Stack<JsonFieldTokenPair> stack = new Stack();
    private String currentField;
    private String lastParsedParentElement;
    private Map<String, String> metadata = new HashMap<String, String>();
    private final HttpResponse httpResponse;
    private final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> unmarshallerMap;
    private final Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customUnmarshallerMap;

    public JsonUnmarshallerContextImpl(JsonParser jsonParser, Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> mapper, HttpResponse httpResponse) {
        this(jsonParser, mapper, Collections.emptyMap(), httpResponse);
    }

    public JsonUnmarshallerContextImpl(JsonParser jsonParser, Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> mapper, Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customUnmarshallerMap, HttpResponse httpResponse) {
        this.jsonParser = jsonParser;
        this.unmarshallerMap = mapper;
        this.customUnmarshallerMap = customUnmarshallerMap;
        this.httpResponse = httpResponse;
    }

    @Override
    public String getHeader(String header) {
        if (this.httpResponse == null) {
            return null;
        }
        return this.httpResponse.getHeaders().get(header);
    }

    @Override
    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    @Override
    public int getCurrentDepth() {
        int depth = this.stack.size();
        if (this.currentField != null) {
            ++depth;
        }
        return depth;
    }

    @Override
    public String readText() throws IOException {
        if (this.isInsideResponseHeader()) {
            return this.getHeader(this.currentHeader);
        }
        return this.readCurrentJsonTokenValue();
    }

    private String readCurrentJsonTokenValue() throws IOException {
        switch (this.currentToken) {
            case VALUE_STRING: {
                String text = this.jsonParser.getText();
                return text;
            }
            case VALUE_FALSE: {
                return "false";
            }
            case VALUE_TRUE: {
                return "true";
            }
            case VALUE_NULL: {
                return null;
            }
            case VALUE_NUMBER_FLOAT: 
            case VALUE_NUMBER_INT: {
                return this.jsonParser.getNumberValue().toString();
            }
            case FIELD_NAME: {
                return this.jsonParser.getText();
            }
        }
        throw new RuntimeException("We expected a VALUE token but got: " + (Object)((Object)this.currentToken));
    }

    @Override
    public boolean isInsideResponseHeader() {
        return this.currentToken == null && this.nextToken == null;
    }

    @Override
    public boolean isStartOfDocument() {
        return this.jsonParser == null || this.jsonParser.getCurrentToken() == null;
    }

    @Override
    public boolean testExpression(String expression) {
        if (expression.equals(".")) {
            return true;
        }
        if (this.currentField != null) {
            return this.currentField.equals(expression);
        }
        return !this.stack.isEmpty() && this.stack.peek().getField().equals(expression);
    }

    @Override
    public String getCurrentParentElement() {
        String parentElement = this.currentField != null ? this.currentField : (!this.stack.isEmpty() ? this.stack.peek().getField() : "");
        return parentElement;
    }

    @Override
    public boolean testExpression(String expression, int stackDepth) {
        if (expression.equals(".")) {
            return true;
        }
        return this.testExpression(expression) && stackDepth == this.getCurrentDepth();
    }

    @Override
    public JsonToken nextToken() throws IOException {
        JsonToken token;
        this.currentToken = token = this.nextToken != null ? this.nextToken : this.jsonParser.nextToken();
        this.nextToken = null;
        this.updateContext();
        return token;
    }

    @Override
    public JsonToken peek() throws IOException {
        if (this.nextToken != null) {
            return this.nextToken;
        }
        this.nextToken = this.jsonParser.nextToken();
        return this.nextToken;
    }

    @Override
    public JsonParser getJsonParser() {
        return this.jsonParser;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public void setCurrentHeader(String currentHeader) {
        this.currentHeader = currentHeader;
    }

    @Override
    public <T> Unmarshaller<T, JsonUnmarshallerContext> getUnmarshaller(Class<T> type) {
        return this.unmarshallerMap.get(type);
    }

    @Override
    public <T> Unmarshaller<T, JsonUnmarshallerContext> getUnmarshaller(Class<T> type, JsonUnmarshallerContext.UnmarshallerType unmarshallerType) {
        return this.customUnmarshallerMap.get((Object)unmarshallerType);
    }

    @Override
    public JsonToken getCurrentToken() {
        return this.currentToken;
    }

    private void updateContext() throws IOException {
        this.lastParsedParentElement = null;
        if (this.currentToken == null) {
            return;
        }
        if (this.currentToken == JsonToken.START_OBJECT || this.currentToken == JsonToken.START_ARRAY) {
            if (this.currentField != null) {
                this.stack.push(new JsonFieldTokenPair(this.currentField, this.currentToken));
                this.currentField = null;
            } else if (this.currentToken == JsonToken.START_ARRAY) {
                this.stack.push(new JsonFieldTokenPair("ARRAY", this.currentToken));
            }
        } else if (this.currentToken == JsonToken.END_OBJECT || this.currentToken == JsonToken.END_ARRAY) {
            if (!this.stack.isEmpty()) {
                boolean curlyBracketsMatch;
                boolean squareBracketsMatch = this.currentToken == JsonToken.END_ARRAY && this.stack.peek().getToken() == JsonToken.START_ARRAY;
                boolean bl = curlyBracketsMatch = this.currentToken == JsonToken.END_OBJECT && this.stack.peek().getToken() == JsonToken.START_OBJECT;
                if (squareBracketsMatch || curlyBracketsMatch) {
                    this.lastParsedParentElement = this.stack.pop().getField();
                }
            }
            this.currentField = null;
        } else if (this.currentToken == JsonToken.FIELD_NAME) {
            String t;
            this.currentField = t = this.jsonParser.getText();
        }
    }

    public String toString() {
        StringBuilder stackString = new StringBuilder();
        for (JsonFieldTokenPair jsonFieldTokenPair : this.stack) {
            stackString.append("/").append(jsonFieldTokenPair.getField());
        }
        if (this.currentField != null) {
            stackString.append("/").append(this.currentField);
        }
        return stackString.length() == 0 ? "/" : stackString.toString();
    }

    @Override
    public String getLastParsedParentElement() {
        return this.lastParsedParentElement;
    }

    private static class JsonFieldTokenPair {
        private final String field;
        private final JsonToken jsonToken;

        public JsonFieldTokenPair(String fieldString, JsonToken token) {
            this.field = fieldString;
            this.jsonToken = token;
        }

        public String getField() {
            return this.field;
        }

        public JsonToken getToken() {
            return this.jsonToken;
        }

        public String toString() {
            return this.field + ": " + this.jsonToken.asString();
        }
    }
}

