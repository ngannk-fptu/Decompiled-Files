/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 *  software.amazon.awssdk.utils.LookaheadInputStream
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.utils.LookaheadInputStream;

@SdkInternalApi
public final class XmlResponseParserUtils {
    private XmlResponseParserUtils() {
    }

    public static XmlElement parse(SdkPojo sdkPojo, SdkHttpFullResponse response) {
        try {
            Optional responseContent = response.content();
            if (!responseContent.isPresent() || response.isSuccessful() && !XmlResponseParserUtils.hasPayloadMembers(sdkPojo) || XmlResponseParserUtils.getBlobTypePayloadMemberToUnmarshal(sdkPojo).isPresent()) {
                return XmlElement.empty();
            }
            LookaheadInputStream content = new LookaheadInputStream((InputStream)responseContent.get());
            if (content.peek() == -1) {
                return XmlElement.empty();
            }
            return XmlDomParser.parse((InputStream)content);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch (RuntimeException e) {
            if (response.isSuccessful()) {
                throw e;
            }
            return XmlElement.empty();
        }
    }

    public static Optional<SdkField<?>> getBlobTypePayloadMemberToUnmarshal(SdkPojo sdkPojo) {
        return sdkPojo.sdkFields().stream().filter(e -> XmlResponseParserUtils.isExplicitPayloadMember(e)).filter(f -> f.marshallingType() == MarshallingType.SDK_BYTES).findFirst();
    }

    private static boolean isExplicitPayloadMember(SdkField<?> f) {
        return f.containsTrait(PayloadTrait.class);
    }

    private static boolean hasPayloadMembers(SdkPojo sdkPojo) {
        return sdkPojo.sdkFields().stream().anyMatch(f -> f.location() == MarshallLocation.PAYLOAD);
    }
}

