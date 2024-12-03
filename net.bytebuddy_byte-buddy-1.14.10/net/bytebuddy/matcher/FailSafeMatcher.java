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
public class FailSafeMatcher<T>
extends ElementMatcher.Junction.AbstractBase<T> {
    private final ElementMatcher<? super T> matcher;
    private final boolean fallback;

    public FailSafeMatcher(ElementMatcher<? super T> matcher, boolean fallback) {
        this.matcher = matcher;
        this.fallback = fallback;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        try {
            return this.matcher.matches(target);
        }
        catch (Exception ignored) {
            return this.fallback;
        }
    }

    public String toString() {
        return "failSafe(try(" + this.matcher + ") or " + this.fallback + ")";
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (this.fallback != ((FailSafeMatcher)object).fallback) {
            return false;
        }
        return this.matcher.equals(((FailSafeMatcher)object).matcher);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.fallback;
    }
}

