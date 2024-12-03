/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.document.Document
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.MapTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.core.StringToInstant
 *  software.amazon.awssdk.protocols.core.StringToValueConverter
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeParser
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor
 *  software.amazon.awssdk.utils.builder.Buildable
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.StringToInstant;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.json.internal.MarshallerUtil;
import software.amazon.awssdk.protocols.json.internal.unmarshall.HeaderUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshallerContext;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshallerRegistry;
import software.amazon.awssdk.protocols.json.internal.unmarshall.document.DocumentUnmarshaller;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkInternalApi
@ThreadSafe
public final class JsonProtocolUnmarshaller {
    public final StringToValueConverter.StringToValue<Instant> instantStringToValue;
    private final JsonUnmarshallerRegistry registry;
    private final JsonNodeParser parser;

    private JsonProtocolUnmarshaller(Builder builder) {
        this.parser = builder.parser;
        this.instantStringToValue = StringToInstant.create(builder.defaultTimestampFormats.isEmpty() ? new EnumMap(MarshallLocation.class) : new EnumMap(builder.defaultTimestampFormats));
        this.registry = JsonProtocolUnmarshaller.createUnmarshallerRegistry(this.instantStringToValue);
    }

    private static JsonUnmarshallerRegistry createUnmarshallerRegistry(StringToValueConverter.StringToValue<Instant> instantStringToValue) {
        return JsonUnmarshallerRegistry.builder().statusCodeUnmarshaller(MarshallingType.INTEGER, (context, json, f) -> context.response().statusCode()).headerUnmarshaller(MarshallingType.STRING, HeaderUnmarshaller.STRING).headerUnmarshaller(MarshallingType.INTEGER, HeaderUnmarshaller.INTEGER).headerUnmarshaller(MarshallingType.LONG, HeaderUnmarshaller.LONG).headerUnmarshaller(MarshallingType.SHORT, HeaderUnmarshaller.SHORT).headerUnmarshaller(MarshallingType.DOUBLE, HeaderUnmarshaller.DOUBLE).headerUnmarshaller(MarshallingType.BOOLEAN, HeaderUnmarshaller.BOOLEAN).headerUnmarshaller(MarshallingType.INSTANT, HeaderUnmarshaller.createInstantHeaderUnmarshaller(instantStringToValue)).headerUnmarshaller(MarshallingType.FLOAT, HeaderUnmarshaller.FLOAT).headerUnmarshaller(MarshallingType.LIST, HeaderUnmarshaller.LIST).payloadUnmarshaller(MarshallingType.STRING, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_STRING)).payloadUnmarshaller(MarshallingType.INTEGER, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_INTEGER)).payloadUnmarshaller(MarshallingType.LONG, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_LONG)).payloadUnmarshaller(MarshallingType.SHORT, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_SHORT)).payloadUnmarshaller(MarshallingType.FLOAT, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_FLOAT)).payloadUnmarshaller(MarshallingType.DOUBLE, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_DOUBLE)).payloadUnmarshaller(MarshallingType.BIG_DECIMAL, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_BIG_DECIMAL)).payloadUnmarshaller(MarshallingType.BOOLEAN, new SimpleTypeJsonUnmarshaller((StringToValueConverter.StringToValue)StringToValueConverter.TO_BOOLEAN)).payloadUnmarshaller(MarshallingType.SDK_BYTES, JsonProtocolUnmarshaller::unmarshallSdkBytes).payloadUnmarshaller(MarshallingType.INSTANT, new SimpleTypeJsonUnmarshaller(instantStringToValue)).payloadUnmarshaller(MarshallingType.SDK_POJO, JsonProtocolUnmarshaller::unmarshallStructured).payloadUnmarshaller(MarshallingType.LIST, JsonProtocolUnmarshaller::unmarshallList).payloadUnmarshaller(MarshallingType.MAP, JsonProtocolUnmarshaller::unmarshallMap).payloadUnmarshaller(MarshallingType.DOCUMENT, JsonProtocolUnmarshaller::unmarshallDocument).build();
    }

    private static SdkBytes unmarshallSdkBytes(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<SdkBytes> field) {
        if (jsonContent == null || jsonContent.isNull()) {
            return null;
        }
        if (jsonContent.isEmbeddedObject()) {
            return SdkBytes.fromByteArray((byte[])((byte[])jsonContent.asEmbeddedObject()));
        }
        return (SdkBytes)StringToValueConverter.TO_SDK_BYTES.convert(jsonContent.text(), field);
    }

    private static SdkPojo unmarshallStructured(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<SdkPojo> f) {
        if (jsonContent == null || jsonContent.isNull()) {
            return null;
        }
        return JsonProtocolUnmarshaller.unmarshallStructured((SdkPojo)f.constructor().get(), jsonContent, context);
    }

    private static Document unmarshallDocument(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<Document> field) {
        if (jsonContent == null) {
            return null;
        }
        return jsonContent.isNull() ? Document.fromNull() : JsonProtocolUnmarshaller.getDocumentFromJsonContent(jsonContent);
    }

    private static Document getDocumentFromJsonContent(JsonNode jsonContent) {
        return (Document)jsonContent.visit((JsonNodeVisitor)new DocumentUnmarshaller());
    }

    private static Map<String, ?> unmarshallMap(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<Map<String, ?>> field) {
        if (jsonContent == null || jsonContent.isNull()) {
            return null;
        }
        SdkField valueInfo = ((MapTrait)field.getTrait(MapTrait.class)).valueFieldInfo();
        HashMap map = new HashMap();
        jsonContent.asObject().forEach((fieldName, value) -> {
            JsonUnmarshaller<Object> unmarshaller = context.getUnmarshaller(valueInfo.location(), valueInfo.marshallingType());
            map.put(fieldName, unmarshaller.unmarshall(context, (JsonNode)value, (SdkField<Object>)valueInfo));
        });
        return map;
    }

    private static List<?> unmarshallList(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<List<?>> field) {
        if (jsonContent == null || jsonContent.isNull()) {
            return null;
        }
        return jsonContent.asArray().stream().map(item -> {
            SdkField memberInfo = ((ListTrait)field.getTrait(ListTrait.class)).memberFieldInfo();
            JsonUnmarshaller<Object> unmarshaller = context.getUnmarshaller(memberInfo.location(), memberInfo.marshallingType());
            return unmarshaller.unmarshall(context, (JsonNode)item, (SdkField<Object>)memberInfo);
        }).collect(Collectors.toList());
    }

    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo sdkPojo, SdkHttpFullResponse response) throws IOException {
        JsonNode jsonNode = this.hasJsonPayload(sdkPojo, response) ? this.parser.parse((InputStream)response.content().get()) : null;
        return this.unmarshall(sdkPojo, response, jsonNode);
    }

    private boolean hasJsonPayload(SdkPojo sdkPojo, SdkHttpFullResponse response) {
        return sdkPojo.sdkFields().stream().anyMatch(f -> this.isPayloadMemberOnUnmarshall((SdkField<?>)f) && !this.isExplicitBlobPayloadMember((SdkField<?>)f) && !this.isExplicitStringPayloadMember((SdkField<?>)f)) && response.content().isPresent();
    }

    private boolean isExplicitBlobPayloadMember(SdkField<?> f) {
        return JsonProtocolUnmarshaller.isExplicitPayloadMember(f) && f.marshallingType() == MarshallingType.SDK_BYTES;
    }

    private boolean isExplicitStringPayloadMember(SdkField<?> f) {
        return JsonProtocolUnmarshaller.isExplicitPayloadMember(f) && f.marshallingType() == MarshallingType.STRING;
    }

    private static boolean isExplicitPayloadMember(SdkField<?> f) {
        return f.containsTrait(PayloadTrait.class);
    }

    private boolean isPayloadMemberOnUnmarshall(SdkField<?> f) {
        return f.location() == MarshallLocation.PAYLOAD || MarshallerUtil.isInUri(f.location());
    }

    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo sdkPojo, SdkHttpFullResponse response, JsonNode jsonContent) {
        JsonUnmarshallerContext context = JsonUnmarshallerContext.builder().unmarshallerRegistry(this.registry).response(response).build();
        return JsonProtocolUnmarshaller.unmarshallStructured(sdkPojo, jsonContent, context);
    }

    private static <TypeT extends SdkPojo> TypeT unmarshallStructured(SdkPojo sdkPojo, JsonNode jsonContent, JsonUnmarshallerContext context) {
        for (SdkField field : sdkPojo.sdkFields()) {
            Optional responseContent;
            if (JsonProtocolUnmarshaller.isExplicitPayloadMember(field) && field.marshallingType() == MarshallingType.SDK_BYTES) {
                responseContent = context.response().content();
                if (responseContent.isPresent()) {
                    field.set((Object)sdkPojo, (Object)SdkBytes.fromInputStream((InputStream)((InputStream)responseContent.get())));
                    continue;
                }
                field.set((Object)sdkPojo, (Object)SdkBytes.fromByteArrayUnsafe((byte[])new byte[0]));
                continue;
            }
            if (JsonProtocolUnmarshaller.isExplicitPayloadMember(field) && field.marshallingType() == MarshallingType.STRING) {
                responseContent = context.response().content();
                if (responseContent.isPresent()) {
                    field.set((Object)sdkPojo, (Object)SdkBytes.fromInputStream((InputStream)((InputStream)responseContent.get())).asUtf8String());
                    continue;
                }
                field.set((Object)sdkPojo, (Object)"");
                continue;
            }
            JsonNode jsonFieldContent = JsonProtocolUnmarshaller.getJsonNode(jsonContent, field);
            JsonUnmarshaller<Object> unmarshaller = context.getUnmarshaller(field.location(), field.marshallingType());
            field.set((Object)sdkPojo, unmarshaller.unmarshall(context, jsonFieldContent, (SdkField<Object>)field));
        }
        return (TypeT)((SdkPojo)((Buildable)sdkPojo).build());
    }

    private static JsonNode getJsonNode(JsonNode jsonContent, SdkField<?> field) {
        if (jsonContent == null) {
            return null;
        }
        return JsonProtocolUnmarshaller.isFieldExplicitlyTransferredAsJson(field) ? jsonContent : (JsonNode)jsonContent.field(field.locationName()).orElse(null);
    }

    private static boolean isFieldExplicitlyTransferredAsJson(SdkField<?> field) {
        return JsonProtocolUnmarshaller.isExplicitPayloadMember(field) && !MarshallingType.DOCUMENT.equals(field.marshallingType());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private JsonNodeParser parser;
        private Map<MarshallLocation, TimestampFormatTrait.Format> defaultTimestampFormats;

        private Builder() {
        }

        public Builder parser(JsonNodeParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder defaultTimestampFormats(Map<MarshallLocation, TimestampFormatTrait.Format> formats) {
            this.defaultTimestampFormats = formats;
            return this;
        }

        public JsonProtocolUnmarshaller build() {
            return new JsonProtocolUnmarshaller(this);
        }
    }

    private static class SimpleTypeJsonUnmarshaller<T>
    implements JsonUnmarshaller<T> {
        private final StringToValueConverter.StringToValue<T> stringToValue;

        private SimpleTypeJsonUnmarshaller(StringToValueConverter.StringToValue<T> stringToValue) {
            this.stringToValue = stringToValue;
        }

        @Override
        public T unmarshall(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<T> field) {
            return (T)(jsonContent != null && !jsonContent.isNull() ? this.stringToValue.convert(jsonContent.text(), field) : null);
        }
    }
}

