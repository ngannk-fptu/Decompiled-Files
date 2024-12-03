/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class SubTypeMatcher<T extends TypeDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final TypeDescription typeDescription;

    public SubTypeMatcher(TypeDescription typeDescription) {
        this.typeDescription = typeDescription;
    }

    @Override
    protected boolean doMatch(T target) {
        return target.isAssignableTo(this.typeDescription);
    }

    public String toString() {
        return "isSubTypeOf(" + this.typeDescription + ')';
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
        return this.typeDescription.equals(((SubTypeMatcher)object).typeDescription);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.typeDescription.hashCode();
    }
}

