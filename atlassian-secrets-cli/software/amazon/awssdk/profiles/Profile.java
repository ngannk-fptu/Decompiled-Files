/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.profiles;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class Profile
implements ToCopyableBuilder<Builder, Profile> {
    private final String name;
    private final Map<String, String> properties;

    private Profile(BuilderImpl builder) {
        this.name = Validate.paramNotNull(builder.name, "name");
        this.properties = Validate.paramNotNull(builder.properties, "properties");
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String name() {
        return this.name;
    }

    public Optional<String> property(String propertyKey) {
        return Optional.ofNullable(this.properties.get(propertyKey));
    }

    public Optional<Boolean> booleanProperty(String propertyKey) {
        return this.property(propertyKey).map(property -> this.parseBooleanProperty(propertyKey, (String)property));
    }

    private Boolean parseBooleanProperty(String propertyKey, String property) {
        if (property.equalsIgnoreCase("true")) {
            return true;
        }
        if (property.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalStateException("Profile property '" + propertyKey + "' must be set to 'true', 'false' or unset, but was set to '" + property + "'.");
    }

    public Map<String, String> properties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public Builder toBuilder() {
        return Profile.builder().name(this.name).properties(this.properties);
    }

    public String toString() {
        return ToString.builder("Profile").add("name", this.name).add("properties", this.properties.keySet()).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Profile profile2 = (Profile)o;
        return Objects.equals(this.name, profile2.name) && Objects.equals(this.properties, profile2.properties);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.properties());
        return hashCode;
    }

    private static final class BuilderImpl
    implements Builder {
        private String name;
        private Map<String, String> properties;

        private BuilderImpl() {
        }

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public void setName(String name) {
            this.name(name);
        }

        @Override
        public Builder properties(Map<String, String> properties) {
            this.properties = Collections.unmodifiableMap(new LinkedHashMap<String, String>(properties));
            return this;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties(properties);
        }

        @Override
        public Profile build() {
            return new Profile(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, Profile> {
        public Builder name(String var1);

        public Builder properties(Map<String, String> var1);

        @Override
        public Profile build();
    }
}

