/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonFactory
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonParseException
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonParser
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonParser$Feature
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonToken
 *  software.amazon.awssdk.thirdparty.jackson.core.json.JsonReadFeature
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.protocols.jsoncore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.BooleanJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.EmbeddedObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NullJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NumberJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.StringJsonNode;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParseException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonToken;
import software.amazon.awssdk.thirdparty.jackson.core.json.JsonReadFeature;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkProtectedApi
public final class JsonNodeParser {
    public static final JsonFactory DEFAULT_JSON_FACTORY = JsonFactory.builder().configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true).build();
    private final boolean removeErrorLocations;
    private final JsonFactory jsonFactory;

    private JsonNodeParser(Builder builder) {
        this.removeErrorLocations = builder.removeErrorLocations;
        this.jsonFactory = builder.jsonFactory;
    }

    public static JsonNodeParser create() {
        return JsonNodeParser.builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public JsonNode parse(InputStream content) {
        return (JsonNode)FunctionalUtils.invokeSafely(() -> {
            try (JsonParser parser = this.jsonFactory.createParser(content).configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);){
                JsonNode jsonNode = this.parse(parser);
                return jsonNode;
            }
        });
    }

    public JsonNode parse(byte[] content) {
        return (JsonNode)FunctionalUtils.invokeSafely(() -> {
            try (JsonParser parser = this.jsonFactory.createParser(content).configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);){
                JsonNode jsonNode = this.parse(parser);
                return jsonNode;
            }
        });
    }

    public JsonNode parse(String content) {
        return (JsonNode)FunctionalUtils.invokeSafely(() -> {
            try (JsonParser parser = this.jsonFactory.createParser(content).configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);){
                JsonNode jsonNode = this.parse(parser);
                return jsonNode;
            }
        });
    }

    private JsonNode parse(JsonParser parser) throws IOException {
        try {
            return this.parseToken(parser, parser.nextToken());
        }
        catch (Exception e) {
            this.removeErrorLocationsIfRequired(e);
            throw e;
        }
    }

    private void removeErrorLocationsIfRequired(Throwable exception) {
        if (this.removeErrorLocations) {
            this.removeErrorLocations(exception);
        }
    }

    private void removeErrorLocations(Throwable exception) {
        if (exception == null) {
            return;
        }
        if (exception instanceof JsonParseException) {
            ((JsonParseException)exception).clearLocation();
        }
        this.removeErrorLocations(exception.getCause());
    }

    private JsonNode parseToken(JsonParser parser, JsonToken token) throws IOException {
        if (token == null) {
            return null;
        }
        switch (token) {
            case VALUE_STRING: {
                return new StringJsonNode(parser.getText());
            }
            case VALUE_FALSE: {
                return new BooleanJsonNode(false);
            }
            case VALUE_TRUE: {
                return new BooleanJsonNode(true);
            }
            case VALUE_NULL: {
                return NullJsonNode.instance();
            }
            case VALUE_NUMBER_FLOAT: 
            case VALUE_NUMBER_INT: {
                return new NumberJsonNode(parser.getText());
            }
            case START_OBJECT: {
                return this.parseObject(parser);
            }
            case START_ARRAY: {
                return this.parseArray(parser);
            }
            case VALUE_EMBEDDED_OBJECT: {
                return new EmbeddedObjectJsonNode(parser.getEmbeddedObject());
            }
        }
        throw new IllegalArgumentException("Unexpected JSON token - " + token);
    }

    private JsonNode parseObject(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        LinkedHashMap<String, JsonNode> object = new LinkedHashMap<String, JsonNode>();
        while (currentToken != JsonToken.END_OBJECT) {
            String fieldName = parser.getText();
            object.put(fieldName, this.parseToken(parser, parser.nextToken()));
            currentToken = parser.nextToken();
        }
        return new ObjectJsonNode(object);
    }

    private JsonNode parseArray(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        ArrayList<JsonNode> array = new ArrayList<JsonNode>();
        while (currentToken != JsonToken.END_ARRAY) {
            array.add(this.parseToken(parser, currentToken));
            currentToken = parser.nextToken();
        }
        return new ArrayJsonNode(array);
    }

    public static final class Builder {
        private JsonFactory jsonFactory = DEFAULT_JSON_FACTORY;
        private boolean removeErrorLocations = false;

        private Builder() {
        }

        public Builder removeErrorLocations(boolean removeErrorLocations) {
            this.removeErrorLocations = removeErrorLocations;
            return this;
        }

        public Builder jsonFactory(JsonFactory jsonFactory) {
            this.jsonFactory = jsonFactory;
            return this;
        }

        public JsonNodeParser build() {
            return new JsonNodeParser(this);
        }
    }
}

