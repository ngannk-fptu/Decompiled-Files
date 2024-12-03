/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkInternalApi
public class AwsXmlUnmarshallingContext {
    private final SdkHttpFullResponse sdkHttpFullResponse;
    private final XmlElement parsedXml;
    private final ExecutionAttributes executionAttributes;
    private final Boolean isResponseSuccess;
    private final XmlElement parsedErrorXml;

    private AwsXmlUnmarshallingContext(Builder builder) {
        this.sdkHttpFullResponse = builder.sdkHttpFullResponse;
        this.parsedXml = builder.parsedXml;
        this.executionAttributes = builder.executionAttributes;
        this.isResponseSuccess = builder.isResponseSuccess;
        this.parsedErrorXml = builder.parsedErrorXml;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SdkHttpFullResponse sdkHttpFullResponse() {
        return this.sdkHttpFullResponse;
    }

    public XmlElement parsedRootXml() {
        return this.parsedXml;
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionAttributes;
    }

    public Boolean isResponseSuccess() {
        return this.isResponseSuccess;
    }

    public XmlElement parsedErrorXml() {
        return this.parsedErrorXml;
    }

    public Builder toBuilder() {
        return AwsXmlUnmarshallingContext.builder().sdkHttpFullResponse(this.sdkHttpFullResponse).parsedXml(this.parsedXml).executionAttributes(this.executionAttributes).isResponseSuccess(this.isResponseSuccess).parsedErrorXml(this.parsedErrorXml);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsXmlUnmarshallingContext that = (AwsXmlUnmarshallingContext)o;
        if (this.sdkHttpFullResponse != null ? !this.sdkHttpFullResponse.equals(that.sdkHttpFullResponse) : that.sdkHttpFullResponse != null) {
            return false;
        }
        if (this.parsedXml != null ? !this.parsedXml.equals(that.parsedXml) : that.parsedXml != null) {
            return false;
        }
        if (this.executionAttributes != null ? !this.executionAttributes.equals((Object)that.executionAttributes) : that.executionAttributes != null) {
            return false;
        }
        if (this.isResponseSuccess != null ? !this.isResponseSuccess.equals(that.isResponseSuccess) : that.isResponseSuccess != null) {
            return false;
        }
        return this.parsedErrorXml != null ? this.parsedErrorXml.equals(that.parsedErrorXml) : that.parsedErrorXml == null;
    }

    public int hashCode() {
        int result = this.sdkHttpFullResponse != null ? this.sdkHttpFullResponse.hashCode() : 0;
        result = 31 * result + (this.parsedXml != null ? this.parsedXml.hashCode() : 0);
        result = 31 * result + (this.executionAttributes != null ? this.executionAttributes.hashCode() : 0);
        result = 31 * result + (this.isResponseSuccess != null ? this.isResponseSuccess.hashCode() : 0);
        result = 31 * result + (this.parsedErrorXml != null ? this.parsedErrorXml.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private SdkHttpFullResponse sdkHttpFullResponse;
        private XmlElement parsedXml;
        private ExecutionAttributes executionAttributes;
        private Boolean isResponseSuccess;
        private XmlElement parsedErrorXml;

        private Builder() {
        }

        public Builder sdkHttpFullResponse(SdkHttpFullResponse sdkHttpFullResponse) {
            this.sdkHttpFullResponse = sdkHttpFullResponse;
            return this;
        }

        public Builder parsedXml(XmlElement parsedXml) {
            this.parsedXml = parsedXml;
            return this;
        }

        public Builder executionAttributes(ExecutionAttributes executionAttributes) {
            this.executionAttributes = executionAttributes;
            return this;
        }

        public Builder isResponseSuccess(Boolean isResponseSuccess) {
            this.isResponseSuccess = isResponseSuccess;
            return this;
        }

        public Builder parsedErrorXml(XmlElement parsedErrorXml) {
            this.parsedErrorXml = parsedErrorXml;
            return this;
        }

        public AwsXmlUnmarshallingContext build() {
            return new AwsXmlUnmarshallingContext(this);
        }
    }
}

