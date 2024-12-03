/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.spi;

import com.atlassian.annotations.PublicApi;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PublicApi
public interface StreamsCommentHandler {
    public Either<PostReplyError, URI> postReply(Iterable<String> var1, String var2);

    public Either<PostReplyError, URI> postReply(URI var1, Iterable<String> var2, String var3);

    public static class PostReplyErrorDeserializer
    extends JsonDeserializer<PostReplyError> {
        @Override
        public PostReplyError deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            PostReplyError.Type errorType = new ObjectMapper().readValue(node.toString(), PostReplyError.Type.class);
            String causeMessage = node.get("causeMessage").asText();
            return new PostReplyError(errorType, new Throwable(causeMessage));
        }
    }

    @JsonDeserialize(using=PostReplyErrorDeserializer.class)
    public static class PostReplyError {
        private static final Logger log = LoggerFactory.getLogger(PostReplyError.class);
        final Type type;
        final Option<Throwable> cause;

        public PostReplyError(Type type) {
            this(type, null);
        }

        public PostReplyError(Type type, Throwable cause) {
            this.type = (Type)((Object)Preconditions.checkNotNull((Object)((Object)type), (Object)"type"));
            this.cause = Option.option((Object)cause);
        }

        public Type getType() {
            return this.type;
        }

        public Option<Throwable> getCause() {
            return this.cause;
        }

        public String asJsonString() {
            if (this.getCause().isDefined()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = null;
                try {
                    node = mapper.readTree(this.getType().asJsonString());
                    ((ObjectNode)node).put("causeMessage", ((Throwable)this.getCause().get()).getMessage());
                    return mapper.writeValueAsString(node);
                }
                catch (IOException e) {
                    log.debug("An error occurred when serializing PostReplyError to JSON.", (Throwable)e);
                    return this.getType().asJsonString();
                }
            }
            return this.getType().asJsonString();
        }

        static class PostReplyErrorTypeDeserializer
        extends JsonDeserializer<Type> {
            PostReplyErrorTypeDeserializer() {
            }

            @Override
            public Type deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                String subCode = node.get("subCode").asText();
                return Stream.of(Type.values()).filter(enumValue -> enumValue.getSubCode().equals(subCode)).findFirst().orElseThrow(() -> new IllegalArgumentException("SubCode " + subCode + " is not recognized"));
            }
        }

        static class PostReplyErrorTypeSerializer
        extends JsonSerializer<Type> {
            PostReplyErrorTypeSerializer() {
            }

            @Override
            public void serialize(Type type, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("subCode");
                jsonGenerator.writeString(type.subCode);
                jsonGenerator.writeEndObject();
            }
        }

        @JsonSerialize(using=PostReplyErrorTypeSerializer.class)
        @JsonDeserialize(using=PostReplyErrorTypeDeserializer.class)
        public static enum Type {
            DELETED_OR_PERMISSION_DENIED(404, "comment.deleted.or.denied"),
            UNAUTHORIZED(401, "unauthorized"),
            FORBIDDEN(403, "forbidden"),
            CONFLICT(409, "conflict"),
            REMOTE_POST_REPLY_ERROR(500, "remote.error"),
            UNKNOWN_ERROR(500, "unknown.error");

            private final int statusCode;
            private final String subCode;

            private Type(int statusCode, String subCode) {
                this.statusCode = statusCode;
                this.subCode = "streams.comment.action." + subCode;
            }

            public int getStatusCode() {
                return this.statusCode;
            }

            public String getSubCode() {
                return this.subCode;
            }

            public String asJsonString() {
                try {
                    return new ObjectMapper().writeValueAsString((Object)this);
                }
                catch (IOException e) {
                    log.debug("An error occurred when serializing PostReplyError.Type to JSON.", (Throwable)e);
                    return "";
                }
            }
        }
    }
}

