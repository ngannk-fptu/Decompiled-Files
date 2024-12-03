/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  javax.annotation.Nonnull
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.diagnostics.internal.platform.plugin.BundleFinder;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiBundleFinder
implements BundleFinder {
    private static final Logger logger = LoggerFactory.getLogger(OsgiBundleFinder.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Optional<String> getBundleNameForClass(@Nonnull Class<?> clazz) {
        long startTime = System.currentTimeMillis();
        try {
            Optional<String> optional = Optional.ofNullable(FrameworkUtil.getBundle(clazz)).map(OsgiHeaderUtil::getPluginKey);
            return optional;
        }
        catch (Exception exception) {
            logger.debug("Failed to get a bundle for class [" + clazz + "]", (Throwable)exception);
            Optional<String> optional = Optional.empty();
            return optional;
        }
        finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Time taken in milliseconds to get bundle for class {}: {}", (Object)clazz.getName(), (Object)(System.currentTimeMillis() - startTime));
            }
        }
    }
}

