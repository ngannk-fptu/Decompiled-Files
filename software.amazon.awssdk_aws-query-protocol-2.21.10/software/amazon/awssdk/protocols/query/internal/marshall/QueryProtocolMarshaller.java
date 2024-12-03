/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 *  software.amazon.awssdk.protocols.core.ProtocolUtils
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.core.ProtocolUtils;
import software.amazon.awssdk.protocols.query.internal.marshall.ListQueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.MapQueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerContext;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerRegistry;
import software.amazon.awssdk.protocols.query.internal.marshall.SimpleTypeQueryMarshaller;

@SdkInternalApi
public final class QueryProtocolMarshaller
implements ProtocolMarshaller<SdkHttpFullRequest> {
    private static final QueryMarshallerRegistry AWS_QUERY_MARSHALLER_REGISTRY = QueryProtocolMarshaller.commonRegistry().marshaller(MarshallingType.LIST, ListQueryMarshaller.awsQuery()).build();
    private static final QueryMarshallerRegistry EC2_QUERY_MARSHALLER_REGISTRY = QueryProtocolMarshaller.commonRegistry().marshaller(MarshallingType.LIST, ListQueryMarshaller.ec2Query()).build();
    private final SdkHttpFullRequest.Builder request;
    private final QueryMarshallerRegistry registry;
    private final URI endpoint;

    private QueryProtocolMarshaller(Builder builder) {
        this.endpoint = builder.endpoint;
        this.request = this.fillBasicRequestParams(builder.operationInfo);
        this.registry = builder.isEc2 ? EC2_QUERY_MARSHALLER_REGISTRY : AWS_QUERY_MARSHALLER_REGISTRY;
    }

    private SdkHttpFullRequest.Builder fillBasicRequestParams(OperationInfo operationInfo) {
        return ProtocolUtils.createSdkHttpRequest((OperationInfo)operationInfo, (URI)this.endpoint).encodedPath("").putRawQueryParameter("Action", operationInfo.operationIdentifier()).putRawQueryParameter("Version", operationInfo.apiVersion());
    }

    public SdkHttpFullRequest marshall(SdkPojo pojo) {
        QueryMarshallerContext context = QueryMarshallerContext.builder().request(this.request).protocolHandler(this).marshallerRegistry(this.registry).build();
        this.doMarshall(null, context, pojo);
        return this.request.build();
    }

    private void doMarshall(String path, QueryMarshallerContext context, SdkPojo pojo) {
        for (SdkField sdkField : pojo.sdkFields()) {
            Object val = sdkField.getValueOrDefault((Object)pojo);
            QueryMarshaller<Object> marshaller = this.registry.getMarshaller(sdkField.marshallingType(), val);
            marshaller.marshall(context, QueryProtocolMarshaller.resolvePath(path, sdkField), val, (SdkField<Object>)sdkField);
        }
    }

    private static String resolvePath(String path, SdkField<?> sdkField) {
        return path == null ? sdkField.locationName() : path + "." + sdkField.locationName();
    }

    private static QueryMarshallerRegistry.Builder commonRegistry() {
        return QueryMarshallerRegistry.builder().marshaller(MarshallingType.STRING, SimpleTypeQueryMarshaller.STRING).marshaller(MarshallingType.INTEGER, SimpleTypeQueryMarshaller.INTEGER).marshaller(MarshallingType.FLOAT, SimpleTypeQueryMarshaller.FLOAT).marshaller(MarshallingType.BOOLEAN, SimpleTypeQueryMarshaller.BOOLEAN).marshaller(MarshallingType.DOUBLE, SimpleTypeQueryMarshaller.DOUBLE).marshaller(MarshallingType.LONG, SimpleTypeQueryMarshaller.LONG).marshaller(MarshallingType.SHORT, SimpleTypeQueryMarshaller.SHORT).marshaller(MarshallingType.INSTANT, SimpleTypeQueryMarshaller.INSTANT).marshaller(MarshallingType.SDK_BYTES, SimpleTypeQueryMarshaller.SDK_BYTES).marshaller(MarshallingType.NULL, SimpleTypeQueryMarshaller.NULL).marshaller(MarshallingType.MAP, new MapQueryMarshaller()).marshaller(MarshallingType.SDK_POJO, (context, path, val, sdkField) -> context.protocolHandler().doMarshall(path, context, (SdkPojo)val));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OperationInfo operationInfo;
        private boolean isEc2;
        private URI endpoint;

        public Builder operationInfo(OperationInfo operationInfo) {
            this.operationInfo = operationInfo;
            return this;
        }

        public Builder isEc2(boolean ec2) {
            this.isEc2 = ec2;
            return this;
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public QueryProtocolMarshaller build() {
            return new QueryProtocolMarshaller(this);
        }
    }
}

