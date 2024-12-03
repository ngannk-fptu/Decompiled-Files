/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.ExceptionUtils;

public class ClasspathResourceLoader
extends ResourceLoader {
    public void init(ExtendedProperties configuration) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("ClasspathResourceLoader : initialization complete.");
        }
    }

    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        InputStream result = null;
        if (StringUtils.isEmpty((String)name)) {
            throw new ResourceNotFoundException("No template name provided");
        }
        try {
            result = ClassUtils.getResourceAsStream(this.getClass(), name);
        }
        catch (Exception fnfe) {
            throw (ResourceNotFoundException)ExceptionUtils.createWithCause(ResourceNotFoundException.class, "problem with template: " + name, fnfe);
        }
        if (result == null) {
            String msg = "ClasspathResourceLoader Error: cannot find resource " + name;
            throw new ResourceNotFoundException(msg);
        }
        return result;
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}

