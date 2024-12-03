/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.protocols.xml;

import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@NotThreadSafe
@SdkProtectedApi
public final class XmlOperationMetadata {
    private boolean hasStreamingSuccessResponse;

    public XmlOperationMetadata() {
    }

    private XmlOperationMetadata(Builder b) {
        this.hasStreamingSuccessResponse = b.hasStreamingSuccessResponse;
    }

    public boolean isHasStreamingSuccessResponse() {
        return this.hasStreamingSuccessResponse;
    }

    public XmlOperationMetadata withHasStreamingSuccessResponse(boolean hasStreamingSuccessResponse) {
        this.hasStreamingSuccessResponse = hasStreamingSuccessResponse;
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean hasStreamingSuccessResponse;

        public Builder hasStreamingSuccessResponse(boolean hasStreamingSuccessResponse) {
            this.hasStreamingSuccessResponse = hasStreamingSuccessResponse;
            return this;
        }

        public XmlOperationMetadata build() {
            return new XmlOperationMetadata(this);
        }
    }
}

