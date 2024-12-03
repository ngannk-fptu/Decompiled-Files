/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource.loader;

import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ClassUtils;

public class ResourceLoaderFactory {
    public static ResourceLoader getLoader(RuntimeServices rs, String loaderClassName) {
        ResourceLoader loader = null;
        try {
            loader = (ResourceLoader)ClassUtils.getNewInstance(loaderClassName);
            rs.getLog().debug("ResourceLoader instantiated: " + loader.getClass().getName());
            return loader;
        }
        catch (Exception e) {
            String msg = "Problem instantiating the template loader: " + loaderClassName + ".\n" + "Look at your properties file and make sure the\n" + "name of the template loader is correct.";
            rs.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
    }
}

