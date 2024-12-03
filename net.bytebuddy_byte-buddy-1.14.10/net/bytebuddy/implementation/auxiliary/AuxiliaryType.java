/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.auxiliary;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface AuxiliaryType {
    @SuppressFBWarnings(value={"MS_MUTABLE_ARRAY", "MS_OOI_PKGPROTECT"}, justification="The array is not modified by class contract.")
    public static final ModifierContributor.ForType[] DEFAULT_TYPE_MODIFIER = new ModifierContributor.ForType[]{SyntheticState.SYNTHETIC};

    public DynamicType make(String var1, ClassFileVersion var2, MethodAccessorFactory var3);

    public String getSuffix();

    @Retention(value=RetentionPolicy.CLASS)
    @Target(value={ElementType.TYPE})
    public static @interface SignatureRelevant {
    }

    public static interface NamingStrategy {
        public String name(TypeDescription var1, AuxiliaryType var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class SuffixingRandom
        implements NamingStrategy {
            private final String suffix;
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
            private final RandomString randomString;

            public SuffixingRandom(String suffix) {
                this.suffix = suffix;
                this.randomString = new RandomString();
            }

            public String name(TypeDescription instrumentedType, AuxiliaryType auxiliaryType) {
                return instrumentedType.getName() + "$" + this.suffix + "$" + this.randomString.nextString();
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
                return this.suffix.equals(((SuffixingRandom)object).suffix);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.suffix.hashCode();
            }
        }

        public static class Suffixing
        implements NamingStrategy {
            private final String suffix;

            public Suffixing(String suffix) {
                this.suffix = suffix;
            }

            public String name(TypeDescription instrumentedType, AuxiliaryType auxiliaryType) {
                return instrumentedType.getName() + "$" + this.suffix + "$" + auxiliaryType.getSuffix();
            }
        }

        public static class Enumerating
        implements NamingStrategy {
            private final String suffix;

            public Enumerating(String suffix) {
                this.suffix = suffix;
            }

            public String name(TypeDescription instrumentedType, AuxiliaryType auxiliaryType) {
                return instrumentedType.getName() + "$" + this.suffix + "$" + RandomString.hashOf(auxiliaryType);
            }
        }
    }
}

