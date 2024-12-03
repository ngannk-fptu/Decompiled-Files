/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.enumeration;

import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface EnumerationDescription
extends NamedElement {
    public String getValue();

    public TypeDescription getEnumerationType();

    public <T extends Enum<T>> T load(Class<T> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends AbstractBase {
        private final TypeDescription enumerationType;
        private final String value;

        public Latent(TypeDescription enumerationType, String value) {
            this.enumerationType = enumerationType;
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public TypeDescription getEnumerationType() {
            return this.enumerationType;
        }

        @Override
        public <T extends Enum<T>> T load(Class<T> type) {
            if (!this.enumerationType.represents(type)) {
                throw new IllegalArgumentException(type + " does not represent " + this.enumerationType);
            }
            return Enum.valueOf(type, this.value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedEnumeration
    extends AbstractBase {
        private final Enum<?> value;

        public ForLoadedEnumeration(Enum<?> value) {
            this.value = value;
        }

        public static List<EnumerationDescription> asList(Enum<?>[] enumerations) {
            ArrayList<EnumerationDescription> result = new ArrayList<EnumerationDescription>(enumerations.length);
            for (Enum<?> enumeration : enumerations) {
                result.add(new ForLoadedEnumeration(enumeration));
            }
            return result;
        }

        @Override
        public String getValue() {
            return this.value.name();
        }

        @Override
        public TypeDescription getEnumerationType() {
            return TypeDescription.ForLoadedType.of(this.value.getDeclaringClass());
        }

        @Override
        public <T extends Enum<T>> T load(Class<T> type) {
            return (T)(this.value.getDeclaringClass() == type ? this.value : Enum.valueOf(type, this.value.name()));
        }
    }

    public static abstract class AbstractBase
    implements EnumerationDescription {
        private transient /* synthetic */ int hashCode;

        public String getActualName() {
            return this.getValue();
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                AbstractBase abstractBase = this;
                n2 = n = abstractBase.getValue().hashCode() + 31 * abstractBase.getEnumerationType().hashCode();
            }
            if (n == 0) {
                n = this.hashCode;
            } else {
                this.hashCode = n;
            }
            return n;
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EnumerationDescription)) {
                return false;
            }
            EnumerationDescription enumerationDescription = (EnumerationDescription)other;
            return this.getEnumerationType().equals(enumerationDescription.getEnumerationType()) && this.getValue().equals(enumerationDescription.getValue());
        }

        public String toString() {
            return this.getValue();
        }
    }
}

