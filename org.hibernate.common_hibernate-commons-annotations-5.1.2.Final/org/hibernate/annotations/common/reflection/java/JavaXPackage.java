/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import org.hibernate.annotations.common.reflection.XPackage;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXAnnotatedElement;

final class JavaXPackage
extends JavaXAnnotatedElement
implements XPackage {
    public JavaXPackage(Package pkg, JavaReflectionManager factory) {
        super(pkg, factory);
    }

    @Override
    public String getName() {
        return ((Package)this.toAnnotatedElement()).getName();
    }
}

