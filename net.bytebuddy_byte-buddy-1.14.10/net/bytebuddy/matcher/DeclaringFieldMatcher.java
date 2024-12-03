/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class DeclaringFieldMatcher<T extends TypeDefinition>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super FieldList<?>> matcher;

    public DeclaringFieldMatcher(ElementMatcher<? super FieldList<? extends FieldDescription>> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        return this.matcher.matches(target.getDeclaredFields());
    }

    public String toString() {
        return "declaresFields(" + this.matcher + ")";
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
        return this.matcher.equals(((DeclaringFieldMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

