/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 */
package org.eclipse.gemini.blueprint.extender.internal.util;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public abstract class BundleUtils {
    public static final String DM_CORE_ID = "spring.osgi.core.bundle.id";
    public static final String DM_CORE_TS = "spring.osgi.core.bundle.timestamp";

    public static Bundle getDMCoreBundle(BundleContext ctx) {
        return FrameworkUtil.getBundle(OsgiBundleXmlApplicationContext.class);
    }

    public static String createNamespaceFilter(BundleContext ctx) {
        Bundle bnd = BundleUtils.getDMCoreBundle(ctx);
        if (bnd != null) {
            return "(|(spring.osgi.core.bundle.id=" + bnd.getBundleId() + ")(" + DM_CORE_TS + "=" + bnd.getLastModified() + "))";
        }
        return "";
    }
}

