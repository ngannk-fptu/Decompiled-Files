/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.ArrayList;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class CollectionErasureMatcher<T extends Iterable<? extends TypeDefinition>>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super Iterable<? extends TypeDescription>> matcher;

    public CollectionErasureMatcher(ElementMatcher<? super Iterable<? extends TypeDescription>> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        ArrayList<TypeDescription> typeDescriptions = new ArrayList<TypeDescription>();
        for (TypeDefinition typeDefinition : target) {
            typeDescriptions.add(typeDefinition.asErasure());
        }
        return this.matcher.matches(typeDescriptions);
    }

    public String toString() {
        return "erasures(" + this.matcher + ')';
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
        return this.matcher.equals(((CollectionErasureMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

