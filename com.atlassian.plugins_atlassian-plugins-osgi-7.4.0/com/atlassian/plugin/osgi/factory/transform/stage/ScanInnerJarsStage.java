/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import java.util.jar.JarEntry;

public class ScanInnerJarsStage
implements TransformStage {
    protected static final String INNER_JARS_BASE_LOCATION = "META-INF/lib/";

    @Override
    public void execute(TransformContext context) {
        for (JarEntry jarEntry : context.getPluginJarEntries()) {
            if (!jarEntry.getName().startsWith(INNER_JARS_BASE_LOCATION) || !jarEntry.getName().endsWith(".jar")) continue;
            context.addBundleClasspathJar(jarEntry.getName());
        }
    }
}

