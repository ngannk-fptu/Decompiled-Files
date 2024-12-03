/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.pocketknife.internal.lifecycle.modules.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public final class BundleUtil {
    private static final Bundle NOT_FOUND_BUNDLE = null;

    private BundleUtil() {
    }

    public static Bundle findBundleForPlugin(BundleContext bundleContext, String pluginKey) {
        return BundleUtil.findBundleForPlugin(Lists.newArrayList((Object[])bundleContext.getBundles()), pluginKey);
    }

    private static Bundle findBundleForPlugin(Iterable<Bundle> bundles, final String pluginKey) {
        return (Bundle)Iterables.find(bundles, (Predicate)new Predicate<Bundle>(){

            public boolean apply(Bundle b) {
                return pluginKey.equals(b.getHeaders().get("Atlassian-Plugin-Key"));
            }
        }, (Object)NOT_FOUND_BUNDLE);
    }
}

