/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.arns;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class ArnResource
implements ToCopyableBuilder<Builder, ArnResource> {
    private final String resourceType;
    private final String resource;
    private final String qualifier;

    private ArnResource(DefaultBuilder builder) {
        this.resourceType = builder.resourceType;
        this.resource = (String)Validate.paramNotBlank((CharSequence)builder.resource, (String)"resource");
        this.qualifier = builder.qualifier;
    }

    public Optional<String> resourceType() {
        return Optional.ofNullable(this.resourceType);
    }

    public String resource() {
        return this.resource;
    }

    public Optional<String> qualifier() {
        return Optional.ofNullable(this.qualifier);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static ArnResource fromString(String resource) {
        Character splitter = StringUtils.findFirstOccurrence((String)resource, (char[])new char[]{':', '/'});
        if (splitter == null) {
            return ArnResource.builder().resource(resource).build();
        }
        int resourceTypeColonIndex = resource.indexOf(splitter.charValue());
        Builder builder = ArnResource.builder().resourceType(resource.substring(0, resourceTypeColonIndex));
        int resourceColonIndex = resource.indexOf(splitter.charValue(), resourceTypeColonIndex);
        int qualifierColonIndex = resource.indexOf(splitter.charValue(), resourceColonIndex + 1);
        if (qualifierColonIndex < 0) {
            builder.resource(resource.substring(resourceTypeColonIndex + 1));
        } else {
            builder.resource(resource.substring(resourceTypeColonIndex + 1, qualifierColonIndex));
            builder.qualifier(resource.substring(qualifierColonIndex + 1));
        }
        return builder.build();
    }

    public String toString() {
        return this.resourceType + ":" + this.resource + ":" + this.qualifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ArnResource that = (ArnResource)o;
        if (!Objects.equals(this.resourceType, that.resourceType)) {
            return false;
        }
        if (!Objects.equals(this.resource, that.resource)) {
            return false;
        }
        return Objects.equals(this.qualifier, that.qualifier);
    }

    public int hashCode() {
        int result = this.resourceType != null ? this.resourceType.hashCode() : 0;
        result = 31 * result + (this.resource != null ? this.resource.hashCode() : 0);
        result = 31 * result + (this.qualifier != null ? this.qualifier.hashCode() : 0);
        return result;
    }

    public Builder toBuilder() {
        return ArnResource.builder().resource(this.resource).resourceType(this.resourceType).qualifier(this.qualifier);
    }

    public static final class DefaultBuilder
    implements Builder {
        private String resourceType;
        private String resource;
        private String qualifier;

        private DefaultBuilder() {
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        @Override
        public Builder resourceType(String resourceType) {
            this.setResourceType(resourceType);
            return this;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        @Override
        public Builder resource(String resource) {
            this.setResource(resource);
            return this;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public Builder qualifier(String qualifier) {
            this.setQualifier(qualifier);
            return this;
        }

        @Override
        public ArnResource build() {
            return new ArnResource(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ArnResource> {
        public Builder resourceType(String var1);

        public Builder resource(String var1);

        public Builder qualifier(String var1);

        public ArnResource build();
    }
}

