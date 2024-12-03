/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodParameterTypesMatcher<T extends ParameterList<?>>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super List<? extends TypeDescription.Generic>> matcher;

    public MethodParameterTypesMatcher(ElementMatcher<? super List<? extends TypeDescription.Generic>> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        return this.matcher.matches(target.asTypeList());
    }

    public String toString() {
        return "hasTypes(" + this.matcher + ")";
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
        return this.matcher.equals(((MethodParameterTypesMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

