/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.Bundle
 */
package com.atlassian.activeobjects.osgi;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.osgi.framework.Bundle;

class LoadClassFromBundleFunction
implements Function<String, Class> {
    private final Bundle bundle;

    LoadClassFromBundleFunction(Bundle bundle) {
        this.bundle = (Bundle)Preconditions.checkNotNull((Object)bundle);
    }

    public Class<?> apply(String className) {
        try {
            return this.bundle.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("How did this happen? We're loading class '" + className + "'from the " + this.bundle, e);
        }
    }
}

