/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.Mutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.EndpointTag;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
public final class PartitionEndpointKey {
    private final Set<EndpointTag> tags;

    private PartitionEndpointKey(DefaultBuilder builder) {
        this.tags = Collections.unmodifiableSet(new HashSet(Validate.paramNotNull(builder.tags, "tags")));
        Validate.noNullElements(builder.tags, "tags must not contain null.", new Object[0]);
    }

    public static Builder builder() {
        return new DefaultBuilder();
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
        PartitionEndpointKey that = (PartitionEndpointKey)o;
        return this.tags.equals(that.tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    public String toString() {
        return ToString.builder("PartitionEndpointKey").add("tags", this.tags).toString();
    }

    private static class DefaultBuilder
    implements Builder {
        private List<EndpointTag> tags = Collections.emptyList();

        private DefaultBuilder() {
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
        public PartitionEndpointKey build() {
            return new PartitionEndpointKey(this);
        }
    }

    @SdkPublicApi
    @Mutable
    public static interface Builder {
        public Builder tags(Collection<EndpointTag> var1);

        public Builder tags(EndpointTag ... var1);

        public PartitionEndpointKey build();
    }
}

