/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.extender;

import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.osgi.framework.BundleContext;

public interface OsgiApplicationContextCreator {
    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext var1) throws Exception;
}

