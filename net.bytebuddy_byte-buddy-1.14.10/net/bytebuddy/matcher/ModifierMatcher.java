/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ModifierMatcher<T extends ModifierReviewable>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final Mode mode;

    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> of(Mode mode) {
        return mode.getMatcher();
    }

    public ModifierMatcher(Mode mode) {
        this.mode = mode;
    }

    @Override
    protected boolean doMatch(T target) {
        return (this.mode.getModifiers() & target.getModifiers()) != 0;
    }

    public String toString() {
        return this.mode.getDescription();
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
        return this.mode.equals((Object)((ModifierMatcher)object).mode);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.mode.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mode {
        PUBLIC(1, "isPublic()"),
        PROTECTED(4, "isProtected()"),
        PRIVATE(2, "isPrivate()"),
        FINAL(16, "isFinal()"),
        STATIC(8, "isStatic()"),
        SYNCHRONIZED(32, "isSynchronized()"),
        NATIVE(256, "isNative()"),
        STRICT(2048, "isStrict()"),
        VAR_ARGS(128, "isVarArgs()"),
        SYNTHETIC(4096, "isSynthetic()"),
        BRIDGE(64, "isBridge()"),
        ABSTRACT(1024, "isAbstract()"),
        INTERFACE(512, "isInterface()"),
        ANNOTATION(8192, "isAnnotation()"),
        VOLATILE(64, "isVolatile()"),
        TRANSIENT(128, "isTransient()"),
        MANDATED(32768, "isMandated()"),
        ENUMERATION(16384, "isEnum()");

        private final int modifiers;
        private final String description;
        private final ModifierMatcher<?> matcher;

        private Mode(int modifiers, String description) {
            this.modifiers = modifiers;
            this.description = description;
            this.matcher = new ModifierMatcher(this);
        }

        protected String getDescription() {
            return this.description;
        }

        protected int getModifiers() {
            return this.modifiers;
        }

        protected ModifierMatcher<?> getMatcher() {
            return this.matcher;
        }
    }
}

