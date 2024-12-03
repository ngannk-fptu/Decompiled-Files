/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ClassFileVersionMatcher<T extends TypeDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ClassFileVersion classFileVersion;
    private final boolean atMost;

    public ClassFileVersionMatcher(ClassFileVersion classFileVersion, boolean atMost) {
        this.classFileVersion = classFileVersion;
        this.atMost = atMost;
    }

    @Override
    protected boolean doMatch(T target) {
        ClassFileVersion classFileVersion = target.getClassFileVersion();
        return classFileVersion != null && (this.atMost ? classFileVersion.isAtMost(this.classFileVersion) : classFileVersion.isAtLeast(this.classFileVersion));
    }

    public String toString() {
        return "hasClassFileVersion(at " + (this.atMost ? "most" : "least") + " " + this.classFileVersion + ")";
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
        if (this.atMost != ((ClassFileVersionMatcher)object).atMost) {
            return false;
        }
        return this.classFileVersion.equals(((ClassFileVersionMatcher)object).classFileVersion);
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.classFileVersion.hashCode()) * 31 + this.atMost;
    }
}

