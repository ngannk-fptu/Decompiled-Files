/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.endpoints.authscheme;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;

@SdkProtectedApi
public final class SigV4aAuthScheme
implements EndpointAuthScheme {
    private final String signingName;
    private final List<String> signingRegionSet;
    private final Boolean disableDoubleEncoding;

    private SigV4aAuthScheme(Builder b) {
        this.signingName = b.signingName;
        this.signingRegionSet = b.signingRegionSet;
        this.disableDoubleEncoding = b.disableDoubleEncoding;
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

    public List<String> signingRegionSet() {
        return this.signingRegionSet;
    }

    @Override
    public String name() {
        return "sigv4a";
    }

    @Override
    public String schemeId() {
        return "aws.auth#sigv4a";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SigV4aAuthScheme that = (SigV4aAuthScheme)o;
        if (this.disableDoubleEncoding != null ? !this.disableDoubleEncoding.equals(that.disableDoubleEncoding) : that.disableDoubleEncoding != null) {
            return false;
        }
        if (this.signingName != null ? !this.signingName.equals(that.signingName) : that.signingName != null) {
            return false;
        }
        return this.signingRegionSet != null ? this.signingRegionSet.equals(that.signingRegionSet) : that.signingRegionSet == null;
    }

    public int hashCode() {
        int result = this.signingName != null ? this.signingName.hashCode() : 0;
        result = 31 * result + (this.signingRegionSet != null ? this.signingRegionSet.hashCode() : 0);
        result = 31 * result + (this.disableDoubleEncoding != null ? this.disableDoubleEncoding.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<String> signingRegionSet = new ArrayList<String>();
        private String signingName;
        private Boolean disableDoubleEncoding;

        public Builder addSigningRegion(String signingRegion) {
            this.signingRegionSet.add(signingRegion);
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

        public SigV4aAuthScheme build() {
            return new SigV4aAuthScheme(this);
        }
    }
}

