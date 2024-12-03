/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ClassLoaderParentMatcher<T extends ClassLoader>
extends ElementMatcher.Junction.AbstractBase<T> {
    @MaybeNull
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
    private final ClassLoader classLoader;

    public ClassLoaderParentMatcher(@MaybeNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        for (ClassLoader current = this.classLoader; current != null; current = current.getParent()) {
            if (current != target) continue;
            return true;
        }
        return target == null;
    }

    public String toString() {
        return "isParentOf(" + this.classLoader + ')';
    }

    public boolean equals(@MaybeNull Object object) {
        block10: {
            block9: {
                ClassLoader classLoader;
                block8: {
                    ClassLoader classLoader2;
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    ClassLoader classLoader3 = ((ClassLoaderParentMatcher)object).classLoader;
                    classLoader = classLoader2 = this.classLoader;
                    if (classLoader3 == null) break block8;
                    if (classLoader == null) break block9;
                    if (!classLoader2.equals(classLoader3)) {
                        return false;
                    }
                    break block10;
                }
                if (classLoader == null) break block10;
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int n = this.getClass().hashCode() * 31;
        ClassLoader classLoader = this.classLoader;
        if (classLoader != null) {
            n = n + classLoader.hashCode();
        }
        return n;
    }
}

