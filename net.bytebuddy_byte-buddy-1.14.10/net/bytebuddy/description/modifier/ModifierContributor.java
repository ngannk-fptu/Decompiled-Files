/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import java.util.Arrays;
import java.util.Collection;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface ModifierContributor {
    public static final int EMPTY_MASK = 0;

    public int getMask();

    public int getRange();

    public boolean isDefault();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Resolver<T extends ModifierContributor> {
        private final Collection<? extends T> modifierContributors;

        protected Resolver(Collection<? extends T> modifierContributors) {
            this.modifierContributors = modifierContributors;
        }

        public static Resolver<ForType> of(ForType ... modifierContributor) {
            return Resolver.of(Arrays.asList(modifierContributor));
        }

        public static Resolver<ForField> of(ForField ... modifierContributor) {
            return Resolver.of(Arrays.asList(modifierContributor));
        }

        public static Resolver<ForMethod> of(ForMethod ... modifierContributor) {
            return Resolver.of(Arrays.asList(modifierContributor));
        }

        public static Resolver<ForParameter> of(ForParameter ... modifierContributor) {
            return Resolver.of(Arrays.asList(modifierContributor));
        }

        public static <S extends ModifierContributor> Resolver<S> of(Collection<? extends S> modifierContributors) {
            return new Resolver<S>(modifierContributors);
        }

        public int resolve() {
            return this.resolve(0);
        }

        public int resolve(int modifiers) {
            for (ModifierContributor modifierContributor : this.modifierContributors) {
                modifiers = modifiers & ~modifierContributor.getRange() | modifierContributor.getMask();
            }
            return modifiers;
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
            return ((Object)this.modifierContributors).equals(((Resolver)object).modifierContributors);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.modifierContributors).hashCode();
        }
    }

    public static interface ForParameter
    extends ModifierContributor {
        public static final int MASK = 36880;
    }

    public static interface ForMethod
    extends ModifierContributor {
        public static final int MASK = 7679;
    }

    public static interface ForField
    extends ModifierContributor {
        public static final int MASK = 151775;
    }

    public static interface ForType
    extends ModifierContributor {
        public static final int MASK = 161311;
    }
}

