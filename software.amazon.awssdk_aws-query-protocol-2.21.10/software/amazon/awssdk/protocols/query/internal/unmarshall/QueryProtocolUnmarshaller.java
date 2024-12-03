/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.core.StringToInstant
 *  software.amazon.awssdk.protocols.core.StringToValueConverter
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.builder.Buildable
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.StringToInstant;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.query.internal.marshall.SimpleTypeQueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.ListQueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.MapQueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerContext;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerRegistry;
import software.amazon.awssdk.protocols.query.internal.unmarshall.SimpleTypeQueryUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkInternalApi
public final class QueryProtocolUnmarshaller
implements XmlErrorUnmarshaller {
    private static final QueryUnmarshallerRegistry UNMARSHALLER_REGISTRY = QueryUnmarshallerRegistry.builder().unmarshaller(MarshallingType.STRING, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_STRING)).unmarshaller(MarshallingType.INTEGER, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_INTEGER)).unmarshaller(MarshallingType.LONG, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_LONG)).unmarshaller(MarshallingType.SHORT, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_SHORT)).unmarshaller(MarshallingType.FLOAT, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_FLOAT)).unmarshaller(MarshallingType.DOUBLE, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_DOUBLE)).unmarshaller(MarshallingType.BOOLEAN, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_BOOLEAN)).unmarshaller(MarshallingType.DOUBLE, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_DOUBLE)).unmarshaller(MarshallingType.INSTANT, new SimpleTypeQueryUnmarshaller(StringToInstant.create(SimpleTypeQueryMarshaller.defaultTimestampFormats()))).unmarshaller(MarshallingType.SDK_BYTES, new SimpleTypeQueryUnmarshaller(StringToValueConverter.TO_SDK_BYTES)).unmarshaller(MarshallingType.LIST, new ListQueryUnmarshaller()).unmarshaller(MarshallingType.MAP, new MapQueryUnmarshaller()).unmarshaller(MarshallingType.NULL, (context, content, field) -> null).unmarshaller(MarshallingType.SDK_POJO, (context, content, field) -> context.protocolUnmarshaller().unmarshall(context, (SdkPojo)field.constructor().get(), (XmlElement)content.get(0))).build();
    private final boolean hasResultWrapper;

    private QueryProtocolUnmarshaller(Builder builder) {
        this.hasResultWrapper = builder.hasResultWrapper;
    }

    public <TypeT extends SdkPojo> Pair<TypeT, Map<String, String>> unmarshall(SdkPojo sdkPojo, SdkHttpFullResponse response) {
        if (this.responsePayloadIsBlob(sdkPojo)) {
            XmlElement document = XmlElement.builder().textContent(response.content().map(s -> (String)FunctionalUtils.invokeSafely(() -> IoUtils.toUtf8String((InputStream)s))).orElse("")).build();
            return Pair.of(this.unmarshall(sdkPojo, document, response), new HashMap());
        }
        XmlElement document = response.content().map(XmlDomParser::parse).orElseGet(XmlElement::empty);
        XmlElement resultRoot = this.hasResultWrapper ? document.getFirstChild() : document;
        return Pair.of(this.unmarshall(sdkPojo, resultRoot, response), this.parseMetadata(document));
    }

    private boolean responsePayloadIsBlob(SdkPojo sdkPojo) {
        return sdkPojo.sdkFields().stream().anyMatch(field -> field.marshallingType() == MarshallingType.SDK_BYTES && field.containsTrait(PayloadTrait.class));
    }

    @Override
    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo sdkPojo, XmlElement resultRoot, SdkHttpFullResponse response) {
        QueryUnmarshallerContext unmarshallerContext = QueryUnmarshallerContext.builder().registry(UNMARSHALLER_REGISTRY).protocolUnmarshaller(this).build();
        return (TypeT)this.unmarshall(unmarshallerContext, sdkPojo, resultRoot);
    }

    private Map<String, String> parseMetadata(XmlElement document) {
        XmlElement requestId;
        XmlElement responseMetadata = document.getElementByName("ResponseMetadata");
        HashMap<String, String> metadata = new HashMap<String, String>();
        if (responseMetadata != null) {
            responseMetadata.children().forEach(c -> metadata.put(this.metadataKeyName((XmlElement)c), c.textContent()));
        }
        if ((requestId = document.getElementByName("requestId")) != null) {
            metadata.put("AWS_REQUEST_ID", requestId.textContent());
        }
        return metadata;
    }

    private String metadataKeyName(XmlElement c) {
        return c.elementName().equals("RequestId") ? "AWS_REQUEST_ID" : c.elementName();
    }

    private SdkPojo unmarshall(QueryUnmarshallerContext context, SdkPojo sdkPojo, XmlElement root) {
        if (root != null) {
            for (SdkField field : sdkPojo.sdkFields()) {
                List<XmlElement> element;
                if (field.containsTrait(PayloadTrait.class) && field.marshallingType() == MarshallingType.SDK_BYTES) {
                    field.set((Object)sdkPojo, (Object)SdkBytes.fromUtf8String((String)root.textContent()));
                }
                if (CollectionUtils.isNullOrEmpty(element = root.getElementsByName(field.unmarshallLocationName()))) continue;
                QueryUnmarshaller<Object> unmarshaller = UNMARSHALLER_REGISTRY.getUnmarshaller(field.location(), field.marshallingType());
                Object unmarshalled = unmarshaller.unmarshall(context, element, (SdkField<Object>)field);
                field.set((Object)sdkPojo, unmarshalled);
            }
        }
        return (SdkPojo)((Buildable)sdkPojo).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean hasResultWrapper;

        private Builder() {
        }

        public Builder hasResultWrapper(boolean hasResultWrapper) {
            this.hasResultWrapper = hasResultWrapper;
            return this;
        }

        public QueryProtocolUnmarshaller build() {
            return new QueryProtocolUnmarshaller(this);
        }
    }
}

