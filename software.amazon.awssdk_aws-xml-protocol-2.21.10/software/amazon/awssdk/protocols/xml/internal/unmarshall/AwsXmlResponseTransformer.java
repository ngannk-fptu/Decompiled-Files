/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 *  software.amazon.awssdk.awscore.DefaultAwsResponseMetadata
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkStandardLogger
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.util.HashMap;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.awscore.DefaultAwsResponseMetadata;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlUnmarshallingContext;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;

@SdkInternalApi
public final class AwsXmlResponseTransformer<T extends AwsResponse>
implements Function<AwsXmlUnmarshallingContext, T> {
    private static final String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";
    private final XmlProtocolUnmarshaller unmarshaller;
    private final Function<SdkHttpFullResponse, SdkPojo> pojoSupplier;

    public AwsXmlResponseTransformer(XmlProtocolUnmarshaller unmarshaller, Function<SdkHttpFullResponse, SdkPojo> pojoSupplier) {
        this.unmarshaller = unmarshaller;
        this.pojoSupplier = pojoSupplier;
    }

    @Override
    public T apply(AwsXmlUnmarshallingContext context) {
        return this.unmarshallResponse(context.sdkHttpFullResponse(), context.parsedRootXml());
    }

    private T unmarshallResponse(SdkHttpFullResponse response, XmlElement parsedXml) {
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Unmarshalling parsed service response XML.");
        AwsResponse result = (AwsResponse)this.unmarshaller.unmarshall(this.pojoSupplier.apply(response), parsedXml, response);
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Done unmarshalling parsed service response.");
        AwsResponseMetadata responseMetadata = this.generateResponseMetadata((SdkHttpResponse)response);
        return (T)result.toBuilder().responseMetadata(responseMetadata).build();
    }

    private AwsResponseMetadata generateResponseMetadata(SdkHttpResponse response) {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("AWS_REQUEST_ID", response.firstMatchingHeader(X_AMZN_REQUEST_ID_HEADER).orElse(null));
        response.forEachHeader((key, value) -> {
            String cfr_ignored_0 = (String)metadata.put((String)key, value.get(0));
        });
        return DefaultAwsResponseMetadata.create(metadata);
    }
}

