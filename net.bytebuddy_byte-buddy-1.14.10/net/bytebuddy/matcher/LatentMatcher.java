/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LatentMatcher<T> {
    public ElementMatcher<? super T> resolve(TypeDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Disjunction<S>
    implements LatentMatcher<S> {
        private final List<? extends LatentMatcher<? super S>> matchers;

        public Disjunction(LatentMatcher<? super S> ... matcher) {
            this(Arrays.asList(matcher));
        }

        public Disjunction(List<? extends LatentMatcher<? super S>> matchers) {
            this.matchers = matchers;
        }

        @Override
        public ElementMatcher<? super S> resolve(TypeDescription typeDescription) {
            ElementMatcher.Junction<Object> matcher = ElementMatchers.none();
            for (LatentMatcher<S> latentMatcher : this.matchers) {
                matcher = matcher.or(latentMatcher.resolve(typeDescription));
            }
            return matcher;
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
            return ((Object)this.matchers).equals(((Disjunction)object).matchers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Conjunction<S>
    implements LatentMatcher<S> {
        private final List<? extends LatentMatcher<? super S>> matchers;

        public Conjunction(LatentMatcher<? super S> ... matcher) {
            this(Arrays.asList(matcher));
        }

        public Conjunction(List<? extends LatentMatcher<? super S>> matchers) {
            this.matchers = matchers;
        }

        @Override
        public ElementMatcher<? super S> resolve(TypeDescription typeDescription) {
            ElementMatcher.Junction<Object> matcher = ElementMatchers.any();
            for (LatentMatcher<S> latentMatcher : this.matchers) {
                matcher = matcher.and(latentMatcher.resolve(typeDescription));
            }
            return matcher;
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
            return ((Object)this.matchers).equals(((Conjunction)object).matchers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForRecordComponentToken
    implements LatentMatcher<RecordComponentDescription> {
        private final RecordComponentDescription.Token token;

        public ForRecordComponentToken(RecordComponentDescription.Token token) {
            this.token = token;
        }

        @Override
        public ElementMatcher<? super RecordComponentDescription> resolve(TypeDescription typeDescription) {
            return ElementMatchers.named(this.token.getName());
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
            return this.token.equals(((ForRecordComponentToken)object).token);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.token.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForMethodToken
    implements LatentMatcher<MethodDescription> {
        private final MethodDescription.Token token;

        public ForMethodToken(MethodDescription.Token token) {
            this.token = token;
        }

        @Override
        public ElementMatcher<? super MethodDescription> resolve(TypeDescription typeDescription) {
            return new ResolvedMatcher(this.token.asSignatureToken(typeDescription));
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
            return this.token.equals(((ForMethodToken)object).token);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.token.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ResolvedMatcher
        extends ElementMatcher.Junction.ForNonNullValues<MethodDescription> {
            private final MethodDescription.SignatureToken signatureToken;

            protected ResolvedMatcher(MethodDescription.SignatureToken signatureToken) {
                this.signatureToken = signatureToken;
            }

            @Override
            public boolean doMatch(MethodDescription target) {
                return target.asSignatureToken().equals(this.signatureToken);
            }

            @Override
            public boolean equals(@MaybeNull Object object) {
                if (!super.equals(object)) {
                    return false;
                }
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.signatureToken.equals(((ResolvedMatcher)object).signatureToken);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.signatureToken.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForFieldToken
    implements LatentMatcher<FieldDescription> {
        private final FieldDescription.Token token;

        public ForFieldToken(FieldDescription.Token token) {
            this.token = token;
        }

        @Override
        public ElementMatcher<? super FieldDescription> resolve(TypeDescription typeDescription) {
            return new ResolvedMatcher(this.token.asSignatureToken(typeDescription));
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
            return this.token.equals(((ForFieldToken)object).token);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.token.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ResolvedMatcher
        extends ElementMatcher.Junction.ForNonNullValues<FieldDescription> {
            private final FieldDescription.SignatureToken signatureToken;

            protected ResolvedMatcher(FieldDescription.SignatureToken signatureToken) {
                this.signatureToken = signatureToken;
            }

            @Override
            protected boolean doMatch(FieldDescription target) {
                return target.asSignatureToken().equals(this.signatureToken);
            }

            @Override
            public boolean equals(@MaybeNull Object object) {
                if (!super.equals(object)) {
                    return false;
                }
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.signatureToken.equals(((ResolvedMatcher)object).signatureToken);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.signatureToken.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Resolved<S>
    implements LatentMatcher<S> {
        private final ElementMatcher<? super S> matcher;

        public Resolved(ElementMatcher<? super S> matcher) {
            this.matcher = matcher;
        }

        @Override
        public ElementMatcher<? super S> resolve(TypeDescription typeDescription) {
            return this.matcher;
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
            return this.matcher.equals(((Resolved)object).matcher);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.matcher.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForSelfDeclaredMethod implements LatentMatcher<MethodDescription>
    {
        DECLARED(false),
        NOT_DECLARED(true);

        private final boolean inverted;

        private ForSelfDeclaredMethod(boolean inverted) {
            this.inverted = inverted;
        }

        @Override
        public ElementMatcher<? super MethodDescription> resolve(TypeDescription typeDescription) {
            return this.inverted ? ElementMatchers.not(ElementMatchers.isDeclaredBy(typeDescription)) : ElementMatchers.isDeclaredBy(typeDescription);
        }
    }
}

