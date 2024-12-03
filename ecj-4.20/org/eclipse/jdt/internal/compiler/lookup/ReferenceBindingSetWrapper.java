/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

final class ReferenceBindingSetWrapper {
    final ReferenceBinding referenceBinding;
    private int hashCode;

    ReferenceBindingSetWrapper(ReferenceBinding referenceBinding) {
        this.referenceBinding = referenceBinding;
        this.hashCode = referenceBinding.identityHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ReferenceBindingSetWrapper) {
            ReferenceBindingSetWrapper other = (ReferenceBindingSetWrapper)obj;
            return ReferenceBindingSetWrapper.identityEqual(this.referenceBinding, other.referenceBinding);
        }
        return false;
    }

    private static boolean identityEqual(Object o1, Object o2) {
        return o1 == o2;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.referenceBinding.toString();
    }
}

