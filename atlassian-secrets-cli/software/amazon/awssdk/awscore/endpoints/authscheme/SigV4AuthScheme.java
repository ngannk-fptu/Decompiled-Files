/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.endpoints.authscheme;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;

@SdkProtectedApi
public final class SigV4AuthScheme
implements EndpointAuthScheme {
    private final String signingRegion;
    private final String signingName;
    private final Boolean disableDoubleEncoding;

    private SigV4AuthScheme(Builder b) {
        this.signingRegion = b.signingRegion;
        this.signingName = b.signingName;
        this.disableDoubleEncoding = b.disableDoubleEncoding;
    }

    @Override
    public String name() {
        return "sigv4";
    }

    @Override
    public String schemeId() {
        return "aws.auth#sigv4";
    }

    public String signingRegion() {
        return this.signingRegion;
    }

    public String signingName() {
        return this.signingName;
    }

    public boolean disableDoubleEncoding() {
        return this.disableDoubleEncoding == null ? false : this.disableDoubleEncoding;
    }

    public boolean isDisableDoubleEncodingSet() {
        return this.disableDoubleEncoding != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SigV4AuthScheme that = (SigV4AuthScheme)o;
        if (this.disableDoubleEncoding != null ? !this.disableDoubleEncoding.equals(that.disableDoubleEncoding) : that.disableDoubleEncoding != null) {
            return false;
        }
        if (this.signingRegion != null ? !this.signingRegion.equals(that.signingRegion) : that.signingRegion != null) {
            return false;
        }
        return this.signingName != null ? this.signingName.equals(that.signingName) : that.signingName == null;
    }

    public int hashCode() {
        int result = this.signingRegion != null ? this.signingRegion.hashCode() : 0;
        result = 31 * result + (this.signingName != null ? this.signingName.hashCode() : 0);
        result = 31 * result + (this.disableDoubleEncoding != null ? this.disableDoubleEncoding.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String signingRegion;
        private String signingName;
        private Boolean disableDoubleEncoding;

        public Builder signingRegion(String signingRegion) {
            this.signingRegion = signingRegion;
            return this;
        }

        public Builder signingName(String signingName) {
            this.signingName = signingName;
            return this;
        }

        public Builder disableDoubleEncoding(Boolean disableDoubleEncoding) {
            this.disableDoubleEncoding = disableDoubleEncoding;
            return this;
        }

        public SigV4AuthScheme build() {
            return new SigV4AuthScheme(this);
        }
    }
}

