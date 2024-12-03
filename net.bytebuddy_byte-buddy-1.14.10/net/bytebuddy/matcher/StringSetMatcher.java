/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.Set;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class StringSetMatcher
extends ElementMatcher.Junction.ForNonNullValues<String> {
    private final Set<String> values;

    public StringSetMatcher(Set<String> values) {
        this.values = values;
    }

    @Override
    protected boolean doMatch(String target) {
        return this.values.contains(target);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder().append("in(");
        boolean first = true;
        for (String value : this.values) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(value);
        }
        return stringBuilder.append(")").toString();
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
        return ((Object)this.values).equals(((StringSetMatcher)object).values);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + ((Object)this.values).hashCode();
    }
}

