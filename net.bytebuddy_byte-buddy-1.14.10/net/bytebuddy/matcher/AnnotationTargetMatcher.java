/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.lang.annotation.ElementType;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class AnnotationTargetMatcher<T extends AnnotationDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementType elementType;

    public AnnotationTargetMatcher(ElementType elementType) {
        this.elementType = elementType;
    }

    @Override
    protected boolean doMatch(T target) {
        return target.isSupportedOn(this.elementType);
    }

    public String toString() {
        return "targetsElement(" + (Object)((Object)this.elementType) + ")";
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
        return this.elementType.equals((Object)((AnnotationTargetMatcher)object).elementType);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.elementType.hashCode();
    }
}

