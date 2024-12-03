/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class DeclaringMethodMatcher<T extends TypeDefinition>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super MethodList<?>> matcher;

    public DeclaringMethodMatcher(ElementMatcher<? super MethodList<? extends MethodDescription>> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        return this.matcher.matches(target.getDeclaredMethods());
    }

    public String toString() {
        return "declaresMethods(" + this.matcher + ")";
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
        return this.matcher.equals(((DeclaringMethodMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

