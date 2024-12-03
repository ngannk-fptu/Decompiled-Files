/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class RecordMatcher<T extends TypeDefinition>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    @Override
    protected boolean doMatch(T target) {
        return target.isRecord();
    }

    public String toString() {
        return "isRecord()";
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
        return this.getClass() == object.getClass();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

