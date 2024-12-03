/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.inline;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface MethodNameTransformer {
    public String transform(MethodDescription var1);

    @HashCodeAndEqualsPlugin.Enhance
    public static class Prefixing
    implements MethodNameTransformer {
        private static final String DEFAULT_PREFIX = "original";
        private final String prefix;

        public Prefixing() {
            this(DEFAULT_PREFIX);
        }

        public Prefixing(String prefix) {
            this.prefix = prefix;
        }

        public String transform(MethodDescription methodDescription) {
            return this.prefix + methodDescription.getInternalName();
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.prefix.equals(((Prefixing)object).prefix);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.prefix.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Suffixing
    implements MethodNameTransformer {
        private static final String DEFAULT_SUFFIX = "original$";
        private final String suffix;

        public static MethodNameTransformer withRandomSuffix() {
            return new Suffixing(DEFAULT_SUFFIX + RandomString.make());
        }

        public Suffixing(String suffix) {
            this.suffix = suffix;
        }

        public String transform(MethodDescription methodDescription) {
            return methodDescription.getInternalName() + "$" + this.suffix;
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.suffix.equals(((Suffixing)object).suffix);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.suffix.hashCode();
        }
    }
}

