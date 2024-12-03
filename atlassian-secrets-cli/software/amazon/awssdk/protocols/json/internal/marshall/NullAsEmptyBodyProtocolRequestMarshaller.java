/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;

@SdkInternalApi
public class NullAsEmptyBodyProtocolRequestMarshaller
implements ProtocolMarshaller<SdkHttpFullRequest> {
    private final ProtocolMarshaller<SdkHttpFullRequest> delegate;

    public NullAsEmptyBodyProtocolRequestMarshaller(ProtocolMarshaller<SdkHttpFullRequest> delegate) {
        this.delegate = delegate;
    }

    @Override
    public SdkHttpFullRequest marshall(SdkPojo pojo) {
        return this.delegate.marshall(pojo);
    }
}

