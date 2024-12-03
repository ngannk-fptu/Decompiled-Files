/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.util.ClassLoaderReference;
import org.aspectj.weaver.WeakClassLoaderReference;

public class BcelWeakClassLoaderReference
extends WeakClassLoaderReference
implements ClassLoaderReference {
    public BcelWeakClassLoaderReference(ClassLoader loader) {
        super(loader);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BcelWeakClassLoaderReference)) {
            return false;
        }
        BcelWeakClassLoaderReference other = (BcelWeakClassLoaderReference)obj;
        return other.hashcode == this.hashcode;
    }
}

