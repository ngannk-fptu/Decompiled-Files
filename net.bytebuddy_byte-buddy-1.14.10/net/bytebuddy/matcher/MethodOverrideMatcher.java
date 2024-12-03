/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodOverrideMatcher<T extends MethodDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super TypeDescription.Generic> matcher;

    public MethodOverrideMatcher(ElementMatcher<? super TypeDescription.Generic> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        HashSet<TypeDescription> duplicates = new HashSet<TypeDescription>();
        for (TypeDefinition typeDefinition : target.getDeclaringType()) {
            if (!this.matches((MethodDescription)target, typeDefinition) && !this.matches((MethodDescription)target, typeDefinition.getInterfaces(), (Set<TypeDescription>)duplicates)) continue;
            return true;
        }
        return false;
    }

    private boolean matches(MethodDescription target, List<? extends TypeDefinition> typeDefinitions, Set<TypeDescription> duplicates) {
        for (TypeDefinition typeDefinition : typeDefinitions) {
            if (!duplicates.add(typeDefinition.asErasure()) || !this.matches(target, typeDefinition) && !this.matches(target, typeDefinition.getInterfaces(), duplicates)) continue;
            return true;
        }
        return false;
    }

    private boolean matches(MethodDescription target, TypeDefinition typeDefinition) {
        for (MethodDescription methodDescription : (MethodList)typeDefinition.getDeclaredMethods().filter(ElementMatchers.isVirtual())) {
            if (!methodDescription.asSignatureToken().equals(target.asSignatureToken())) continue;
            if (!this.matcher.matches(typeDefinition.asGenericType())) break;
            return true;
        }
        return false;
    }

    public String toString() {
        return "isOverriddenFrom(" + this.matcher + ")";
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
        return this.matcher.equals(((MethodOverrideMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

