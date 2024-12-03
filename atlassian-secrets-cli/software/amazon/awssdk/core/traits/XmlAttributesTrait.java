/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.traits;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.Pair;

@SdkProtectedApi
public final class XmlAttributesTrait
implements Trait {
    private Map<String, AttributeAccessors> attributes = new LinkedHashMap<String, AttributeAccessors>();

    private XmlAttributesTrait(Pair<String, AttributeAccessors> ... attributePairs) {
        for (Pair<String, AttributeAccessors> pair : attributePairs) {
            this.attributes.put(pair.left(), pair.right());
        }
        this.attributes = Collections.unmodifiableMap(this.attributes);
    }

    public static XmlAttributesTrait create(Pair<String, AttributeAccessors> ... pairs) {
        return new XmlAttributesTrait(pairs);
    }

    public Map<String, AttributeAccessors> attributes() {
        return this.attributes;
    }

    public static final class AttributeAccessors {
        private final Function<Object, String> attributeGetter;

        private AttributeAccessors(Builder builder) {
            this.attributeGetter = builder.attributeGetter;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Function<Object, String> attributeGetter() {
            return this.attributeGetter;
        }

        public static final class Builder {
            private Function<Object, String> attributeGetter;

            private Builder() {
            }

            public Builder attributeGetter(Function<Object, String> attributeGetter) {
                this.attributeGetter = attributeGetter;
                return this;
            }

            public AttributeAccessors build() {
                return new AttributeAccessors(this);
            }
        }
    }
}

