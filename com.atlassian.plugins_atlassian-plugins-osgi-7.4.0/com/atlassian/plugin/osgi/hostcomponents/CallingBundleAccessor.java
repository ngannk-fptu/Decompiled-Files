/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.hostcomponents;

import com.atlassian.plugin.osgi.hostcomponents.impl.CallingBundleStore;
import org.osgi.framework.Bundle;

public class CallingBundleAccessor {
    public static Bundle getCallingBundle() {
        return CallingBundleStore.get();
    }
}

