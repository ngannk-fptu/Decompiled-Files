/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ApiName {
    private final String name;
    private final String version;

    private ApiName(BuilderImpl b) {
        this.name = Validate.notNull(b.name, "name must not be null", new Object[0]);
        this.version = Validate.notNull(b.version, "version must not be null", new Object[0]);
    }

    public String name() {
        return this.name;
    }

    public String version() {
        return this.version;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static final class BuilderImpl
    implements Builder {
        private String name;
        private String version;

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
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public void setVersion(String version) {
            this.version(version);
        }

        @Override
        public ApiName build() {
            return new ApiName(this);
        }
    }

    public static interface Builder {
        public Builder name(String var1);

        public Builder version(String var1);

        public ApiName build();
    }
}

