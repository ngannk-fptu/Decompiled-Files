/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.Mutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.regions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.Mutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.EndpointTag;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
public final class ServiceEndpointKey {
    private final Region region;
    private final Set<EndpointTag> tags;

    private ServiceEndpointKey(DefaultBuilder builder) {
        this.region = (Region)Validate.paramNotNull((Object)builder.region, (String)"region");
        this.tags = Collections.unmodifiableSet(new LinkedHashSet((Collection)Validate.paramNotNull((Object)builder.tags, (String)"tags")));
        Validate.noNullElements((Iterable)builder.tags, (String)"tags must not contain null.", (Object[])new Object[0]);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Region region() {
        return this.region;
    }

    public Set<EndpointTag> tags() {
        return this.tags;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ServiceEndpointKey that = (ServiceEndpointKey)o;
        if (!this.region.equals(that.region)) {
            return false;
        }
        return this.tags.equals(that.tags);
    }

    public int hashCode() {
        int result = this.region.hashCode();
        result = 31 * result + this.tags.hashCode();
        return result;
    }

    public String toString() {
        return ToString.builder((String)"ServiceEndpointKey").add("region", (Object)this.region).add("tags", this.tags).toString();
    }

    private static class DefaultBuilder
    implements Builder {
        private Region region;
        private List<EndpointTag> tags = Collections.emptyList();

        private DefaultBuilder() {
        }

        @Override
        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Region getRegion() {
            return this.region;
        }

        public void setRegion(Region region) {
            this.region = region;
        }

        @Override
        public Builder tags(Collection<EndpointTag> tags) {
            this.tags = new ArrayList<EndpointTag>(tags);
            return this;
        }

        @Override
        public Builder tags(EndpointTag ... tags) {
            this.tags = Arrays.asList(tags);
            return this;
        }

        public List<EndpointTag> getTags() {
            return Collections.unmodifiableList(this.tags);
        }

        public void setTags(Collection<EndpointTag> tags) {
            this.tags = new ArrayList<EndpointTag>(tags);
        }

        @Override
        public ServiceEndpointKey build() {
            return new ServiceEndpointKey(this);
        }
    }

    @SdkPublicApi
    @Mutable
    public static interface Builder {
        public Builder region(Region var1);

        public Builder tags(Collection<EndpointTag> var1);

        public Builder tags(EndpointTag ... var1);

        public ServiceEndpointKey build();
    }
}

