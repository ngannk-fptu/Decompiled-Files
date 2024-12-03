/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.inline;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class InliningImplementationMatcher
implements LatentMatcher<MethodDescription> {
    private final LatentMatcher<? super MethodDescription> ignoredMethods;
    private final ElementMatcher<? super MethodDescription> predefinedMethodSignatures;

    protected InliningImplementationMatcher(LatentMatcher<? super MethodDescription> ignoredMethods, ElementMatcher<? super MethodDescription> predefinedMethodSignatures) {
        this.ignoredMethods = ignoredMethods;
        this.predefinedMethodSignatures = predefinedMethodSignatures;
    }

    protected static LatentMatcher<MethodDescription> of(LatentMatcher<? super MethodDescription> ignoredMethods, TypeDescription originalType) {
        ElementMatcher.Junction predefinedMethodSignatures = ElementMatchers.none();
        for (MethodDescription methodDescription : originalType.getDeclaredMethods()) {
            ElementMatcher.Junction signature = methodDescription.isConstructor() ? ElementMatchers.isConstructor() : ElementMatchers.named(methodDescription.getName());
            signature = signature.and(ElementMatchers.returns(methodDescription.getReturnType().asErasure()));
            signature = signature.and(ElementMatchers.takesArguments(methodDescription.getParameters().asTypeList().asErasures()));
            predefinedMethodSignatures = predefinedMethodSignatures.or(signature);
        }
        return new InliningImplementationMatcher(ignoredMethods, predefinedMethodSignatures);
    }

    @Override
    public ElementMatcher<? super MethodDescription> resolve(TypeDescription typeDescription) {
        return ElementMatchers.not(this.ignoredMethods.resolve(typeDescription)).and(ElementMatchers.isVirtual().and(ElementMatchers.not(ElementMatchers.isFinal())).or(ElementMatchers.isDeclaredBy(typeDescription))).or(ElementMatchers.isDeclaredBy(typeDescription).and(ElementMatchers.not(this.predefinedMethodSignatures)));
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
        if (!this.ignoredMethods.equals(((InliningImplementationMatcher)object).ignoredMethods)) {
            return false;
        }
        return this.predefinedMethodSignatures.equals(((InliningImplementationMatcher)object).predefinedMethodSignatures);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.ignoredMethods.hashCode()) * 31 + this.predefinedMethodSignatures.hashCode();
    }
}

