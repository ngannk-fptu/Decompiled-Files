/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class HasSuperClassMatcher<T extends TypeDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super TypeDescription.Generic> matcher;

    public HasSuperClassMatcher(ElementMatcher<? super TypeDescription.Generic> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        if (target.isInterface()) {
            return this.matcher.matches(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class));
        }
        for (TypeDefinition typeDefinition : target) {
            if (!this.matcher.matches(typeDefinition.asGenericType())) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return "hasSuperClass(" + this.matcher + ")";
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
        return this.matcher.equals(((HasSuperClassMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

