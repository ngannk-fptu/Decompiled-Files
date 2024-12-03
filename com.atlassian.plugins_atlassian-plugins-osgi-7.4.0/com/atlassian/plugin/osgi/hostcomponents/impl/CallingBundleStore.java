/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.hostcomponents.impl;

import org.osgi.framework.Bundle;

public class CallingBundleStore {
    private static final ThreadLocal<Bundle> callingBundle = new ThreadLocal();

    public static Bundle get() {
        return callingBundle.get();
    }

    static void set(Bundle bundle) {
        if (bundle == null) {
            callingBundle.remove();
        } else {
            callingBundle.set(bundle);
        }
    }
}

